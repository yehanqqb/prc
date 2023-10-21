package prc.api.service.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import prc.api.service.dto.TreadSupplierDto;
import prc.api.service.vo.QueryVo;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.common.utils.NetWorkUtil;
import prc.service.common.utils.SignUtil;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.dto.TenantSupplierAisleDto;
import prc.service.model.dto.TenantSupplierOrderBeforeDto;
import prc.service.model.entity.ITenantSupplier;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.entity.IUSupplierOrderLog;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;
import prc.service.mq.SendReceive;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ApiSupplierOrderService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private ITenantDao iTenantDao;
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SendReceive apiSendMqService;
    @Autowired
    private IUSupplierOrderLogDao iuSupplierOrderLogDao;

    @Autowired
    private ThreadPoolTaskExecutor systemExecutor;

    public RetResult createTemporaryOrder(TreadSupplierDto treadSupplierDto, boolean checkSign) {
        iTenantDao.findByIdAndExist(treadSupplierDto.getTenantId());
        TenantSupplierAisleDto supplierInfo = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(treadSupplierDto.getTenantId(), treadSupplierDto.getSupplierId());
        if (!supplierInfo.getITenantSupplier().getStatus()) {
            throw new BizException("this is close");
        }


        String ip = NetWorkUtil.getIpAddress(request);
        if (!supplierInfo.getITenantSupplier().getProduceIps().contains(ip)) {
            log.info("sorce ip is " + ip);
            throw new BizException("you ip is not white ip,please use white ip");
        }

        if (checkSign) {
            checkSign(treadSupplierDto, supplierInfo.getITenantSupplier());
        }

        String blackKey = String.format(Constants.BLACK_TELNET, treadSupplierDto.getTenantId());
        List<String> notProductNos = redisCache.getCacheObject(blackKey);
        if (Objects.nonNull(notProductNos)) {
            if (notProductNos.contains(treadSupplierDto.getProductNo())) {
                throw new BizException("productNo on the blacklist");
            }
        }


        Long supplierOrderInfoCount = iuSupplierOrderDao.getBaseMapper().selectCount(
                new LambdaQueryWrapper<IUSupplierOrder>()
                        .eq(IUSupplierOrder::getOrderId, treadSupplierDto.getOrderId())
        );

        if (supplierOrderInfoCount > 0) {
            throw new BizException("orderId is exist");
        }

        if (!treadSupplierDto.isSlow()) {
            Integer supplierOrderCount = redisCache.getCacheObject(String.format(Constants.SUPPLIER_ORDER_COUNT, supplierInfo.getITenantSupplier().getId()));
            if (supplierOrderCount >= supplierInfo.getITenantSupplier().getMaxCount()) {
                throw new BizException("order count repertory is adequate.please await submit");
            }
        }


        if (!supplierInfo.getITenantSupplier().getRepetitionNo()) {
            if (iuSupplierOrderDao.getBaseMapper().selectCount(new LambdaQueryWrapper<IUSupplierOrder>()
                    .eq(IUSupplierOrder::getProductNo, treadSupplierDto.getProductNo())
                    .in(IUSupplierOrder::getPayStatus, Lists.newArrayList(PayStatus.CREATE_ING, PayStatus.ING, PayStatus.WAIT))
            ) >= 1) {
                throw new BizException("order count one");
            }
        }

        Set<TenantAisleDto> filterChannel = supplierInfo.getTenantAisleDto().stream()
                .filter(item -> item.getIsAisle().getOperators().contains(Objects.requireNonNull(Operator.getByCode(treadSupplierDto.getOperator())).getId()))
                .filter(item -> item.getIsAisle().getRechargeMoney().contains(BigDecimal.valueOf(treadSupplierDto.getMoney())))
                .filter(item -> item.getIsAisle().getSlow().equals(treadSupplierDto.isSlow()))
                .collect(Collectors.toSet());


        if (filterChannel.isEmpty()) {
            throw new BizException("money or operator nonsupport");
        }

        Long orderCount = iuSupplierOrderDao.count(new LambdaQueryWrapper<IUSupplierOrder>()
                .eq(IUSupplierOrder::getOrderId, treadSupplierDto.getOrderId())
        );

        if (orderCount > 0) {
            throw new BizException("orderId is exist");
        }

        IUSupplierOrder iuSupplierOrder = new IUSupplierOrder();
        iuSupplierOrder.setTenantId(treadSupplierDto.getTenantId());
        iuSupplierOrder.setOrderId(treadSupplierDto.getOrderId());
        iuSupplierOrder.setProductNo(treadSupplierDto.getProductNo());
        iuSupplierOrder.setSupplierId(treadSupplierDto.getSupplierId());
        iuSupplierOrder.setMoney(BigDecimal.valueOf(treadSupplierDto.getMoney()));
        iuSupplierOrder.setNotify(Boolean.FALSE);
        iuSupplierOrder.setOperator(Operator.getByCode(treadSupplierDto.getOperator()));
        iuSupplierOrder.setNotifyUrl(treadSupplierDto.getNotifyUrl());
        iuSupplierOrder.setSlow(treadSupplierDto.isSlow());
        iuSupplierOrder.setPayStatus(PayStatus.CREATE_ING);
        iuSupplierOrder.setFinishStatus(FinishStatus.WAIT);
        iuSupplierOrder.setExt(treadSupplierDto.getExt());
        iuSupplierOrderDao.save(iuSupplierOrder);

        TenantSupplierOrderBeforeDto tenantSupplierOrderBeforeDto = new TenantSupplierOrderBeforeDto();
        tenantSupplierOrderBeforeDto.setIuSupplierOrder(iuSupplierOrder);
        tenantSupplierOrderBeforeDto.setSupplierInfo(supplierInfo);

        CompletableFuture.runAsync(() -> {
            IUSupplierOrderLog iuSupplierOrderLog = new IUSupplierOrderLog();
            iuSupplierOrderLog.setOrderId(iuSupplierOrder.getOrderId());
            iuSupplierOrderLog.setProductNo(iuSupplierOrder.getProductNo());
            iuSupplierOrderLog.setIdentityId(iuSupplierOrder.getSupplierId());
            iuSupplierOrderLog.setTenantId(iuSupplierOrder.getTenantId());
            iuSupplierOrderLog.setReq(JSON.parseObject(JSON.toJSONString(treadSupplierDto)));
            iuSupplierOrderLog.setType(IUSupplierOrderLog.TYPE.REQ);
            iuSupplierOrderLogDao.save(iuSupplierOrderLog);
        }, systemExecutor);
        apiSendMqService.send(tenantSupplierOrderBeforeDto);
        return RetResponse.makeOKRsp();
    }

    public QueryVo queryStatusByOrderId(String orderId) {

        IUSupplierOrder order = iuSupplierOrderDao.getOne(new LambdaQueryWrapper<IUSupplierOrder>().eq(IUSupplierOrder::getOrderId, orderId));
        if (Objects.isNull(order)) {
            throw new BizException(-1, "not exist");
        }
        QueryVo queryVo = new QueryVo();
        if (order.getFinishStatus().equals(FinishStatus.SUCCESS)) {
            queryVo.setFinishStatus(0);
        } else {
            queryVo.setFinishStatus(-1);
        }
        if (order.getPayStatus().equals(PayStatus.SUCCESS)) {
            queryVo.setPayStatus(0);
        } else {
            queryVo.setPayStatus(-1);
        }
        return queryVo;
    }

    private void checkSign(TreadSupplierDto treadSupplierDto, ITenantSupplier supplierInfo) {
        String mSign = SignUtil.getSign(treadSupplierDto, supplierInfo.getSecret(), Lists.newArrayList("sign"));
        if (!mSign.equalsIgnoreCase(treadSupplierDto.getSign())) {
            log.info("sign is error-{}", JSON.toJSONString(treadSupplierDto));
            throw new BizException("sign is error");
        }
    }
}
