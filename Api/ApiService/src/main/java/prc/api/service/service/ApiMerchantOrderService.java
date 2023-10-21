package prc.api.service.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.api.service.dto.PayMerchantDto;
import prc.service.channel.payment.ChannelPayment;
import prc.service.channel.payment.ConcurrentPaymentService;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.common.utils.*;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.PaymentBeforeDto;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.dto.TenantSupplierAisleDto;
import prc.service.model.dto.TenantSupplierOrderBeforeDto;
import prc.service.model.entity.*;
import prc.service.model.enumeration.PayStatus;
import prc.service.model.vo.ChannelPayVo;
import prc.service.model.vo.ChannelPaymentVo;
import prc.service.mq.SendMonitoring;
import prc.service.mq.SendMonitoringCopy;
import prc.service.mq.SendReceive;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ApiMerchantOrderService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ITenantMerchantDao iTenantMerchantDao;
    @Autowired
    private ITenantDao iTenantDao;
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;
    @Autowired
    private ITenantAisleDao iTenantAisleDao;
    @Autowired
    private ISDictDao isDictDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ConcurrentPaymentService concurrentPaymentService;
    @Autowired
    private IUPaymentDao iuPaymentDao;
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private SendReceive sendReceive;
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private SendMonitoringCopy sendMonitoringCopy;
    @Autowired
    private SendMonitoring sendMonitoring;
    @Autowired
    private ISPayTypeDao isPayTypeDao;


    public RetResult createTemporaryOrder(PayMerchantDto payMerchantDto) {
        iTenantDao.findByIdAndExist(payMerchantDto.getTenantId());
        ITenantMerchant merchantInfo = iTenantMerchantDao.getMerchantByTenantIdAndMerchant(payMerchantDto.getTenantId(), payMerchantDto.getMerchantId());

        String ip = NetWorkUtil.getIpAddress(request);
        /*if (!merchantInfo.getWhiteIp().contains(ip)) {
            log.info("sorce ip is " + ip);
            throw new BizException("you ip is not white ip,please use white ip");
        }*/

        checkSign(payMerchantDto, merchantInfo);

        Long merchantOrderInfoCount = iuMerchantOrderDao.getBaseMapper().selectCount(
                new LambdaQueryWrapper<IUMerchantOrder>()
                        .eq(IUMerchantOrder::getOrderId, payMerchantDto.getOrderId())
        );

        if (merchantOrderInfoCount > 0) {
            throw new BizException("orderId is exist");
        }


        Set<TenantAisleDto> tenantAislesInfo = iTenantAisleDao.findAisleByTenantId(payMerchantDto.getTenantId(), true);

        Set<TenantAisleDto> foundTenantAislesInfo = tenantAislesInfo.stream()
                .filter(item -> item.getIsAisle().getPayType().contains(payMerchantDto.getPayType())
                        && item.getIsAisle().getFix() == payMerchantDto.isFix())
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(foundTenantAislesInfo)) {
            throw new BizException("payType is not exist");
        }

        String treadId = IdUtil.fastUUID();
        ISPayType isPayType = isPayTypeDao.findByKey(payMerchantDto.getPayType());
        Date expiresTime = DateUtil.beforeSeconds(new Date(), -isPayType.getMate());
        treadId = AESUtil.encryption(treadId + "," + expiresTime.getTime() + "," + payMerchantDto.getPayType());

        redisCache.setCacheObject(treadId, payMerchantDto, isPayType.getMate(), TimeUnit.SECONDS);
        return RetResponse.makeOKRsp(isDictDao.getCashierUrl(treadId));
    }

    public JSONObject before(String tradeId, String userKey) {
        IUPayment iuPayment = redisCache.getCacheObject(String.format(Constants.TRADE_PAY, tradeId));
        iuPayment.setUserKey(userKey);
        ChannelPayment channelPayment = SpringUtil.getBean(iuPayment.getPayBean());
        return channelPayment.getBefore(iuPayment);
    }

    public JSONObject after(String tradeId, String userKey) {
        IUPayment iuPayment = redisCache.getCacheObject(String.format(Constants.TRADE_PAY, tradeId));
        iuPayment.setUserKey(userKey);
        ChannelPayment channelPayment = SpringUtil.getBean(iuPayment.getPayBean());
        return channelPayment.getAfter(iuPayment);
    }

    public ChannelPayVo tradePay(String tradeId, String userKey) {
        PayMerchantDto payMerchantDto = redisCache.getCacheObject(tradeId);
        if (Objects.isNull(payMerchantDto)) {
            throw new BizException("trade id not exist");
        }
        /*if (!payMerchantDto.getUserIp().equalsIgnoreCase(NetWorkUtil.getIpAddress(request))) {
            throw new BizException("ip black");
        }*/

        ChannelPayVo vo = redisCache.getCacheObject(String.format(Constants.PAY_ORDER, tradeId));
        if (Objects.nonNull(vo)) {
            return vo;
        }


        PaymentBeforeDto paymentBeforeDto = new PaymentBeforeDto();
        paymentBeforeDto.setMoney(BigDecimal.valueOf(payMerchantDto.getMoney()));
        paymentBeforeDto.setTenantId(payMerchantDto.getTenantId());
        paymentBeforeDto.setUserKey(userKey);
        paymentBeforeDto.setPayType(payMerchantDto.getPayType());

        IUPayment payment = concurrentPaymentService.getPaymentParent(paymentBeforeDto);
        payment.setUserKey(userKey);
        payment.setMerchantId(payMerchantDto.getMerchantId());
        payment.setMerchantOrderId(payMerchantDto.getOrderId());


        Date start = new Date();
        // getBean 走入到具体的产码通道
        ChannelPayment paymentService = SpringUtil.getBean(payment.getPayBean());
        ChannelPaymentVo payUrlData = paymentService.getChannelPayUrl(payment);

        payment.setProxyIp(payUrlData.getProxyIp());
        payment.setRemark(payUrlData.getRemark());
        payment.setWait(new Date().getTime() - start.getTime());
        payUrlData.setCreateTime(new Date().getTime());

        ChannelPayVo channelPayVo = new ChannelPayVo();
        if (payUrlData.isStatus()) {
            payment.setPayUrl(payUrlData.getPayUrl());
            payment.setQueryJson(JSON.parseObject(payUrlData.getQuery().toJSONString()));
            payment.setPaymentNo(payUrlData.getPaymentNo());
            payment.setPayStatus(PayStatus.ING);
            iuPaymentDao.saveOrUpdate(payment);


            IUMerchantOrder iuMerchantOrder = new IUMerchantOrder();
            iuMerchantOrder.setOrderId(payMerchantDto.getOrderId());
            iuMerchantOrder.setMerchantId(payMerchantDto.getMerchantId());
            iuMerchantOrder.setNotify(false);
            iuMerchantOrder.setNotifyUrl(payMerchantDto.getNotifyUrl());
            iuMerchantOrder.setMoney(BigDecimal.valueOf(payMerchantDto.getMoney()));
            iuMerchantOrder.setTenantId(payMerchantDto.getTenantId());
            iuMerchantOrder.setPayType(payMerchantDto.getPayType());
            iuMerchantOrder.setPaymentId(payment.getPaymentId());
            iuMerchantOrder.setPayStatus(PayStatus.ING);
            iuMerchantOrderDao.saveOrUpdate(iuMerchantOrder);


            UpdateWrapper<IUSupplierOrder> iuSupplierOrderUpdateWrapper = Wrappers.update();
            iuSupplierOrderUpdateWrapper.lambda().eq(IUSupplierOrder::getOrderId, payment.getSupplierOrderId());
            IUSupplierOrder iuSupplierOrder = new IUSupplierOrder();
            iuSupplierOrder.setPayStatus(PayStatus.ING);
            iuSupplierOrderDao.update(iuSupplierOrder, iuSupplierOrderUpdateWrapper);

            channelPayVo.setStatus(true);

            if (payment.getPrimary()) {
                sendMonitoring.send(payment);
            } else {
                sendMonitoringCopy.send(payment);
            }

        } else {
            payUrlData.setPayUrl(null);
            payment.setPayStatus(PayStatus.CREATE_ERROR);
            TenantSupplierAisleDto supplier = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(payment.getTenantId(), payment.getSupplierId());
            if (supplier.getITenantSupplier().getRepetition()) {
                iuPaymentDao.saveOrUpdate(payment);

                TenantSupplierOrderBeforeDto tenantSupplierOrderBeforeDto = new TenantSupplierOrderBeforeDto();
                tenantSupplierOrderBeforeDto.setSupplierInfo(supplier);
                tenantSupplierOrderBeforeDto.setIuPayment(payment);
                tenantSupplierOrderBeforeDto.setRetry(true);
                tenantSupplierOrderBeforeDto.setIuSupplierOrder(iuSupplierOrderDao.getOne(new LambdaQueryWrapper<IUSupplierOrder>().eq(IUSupplierOrder::getOrderId, payment.getSupplierOrderId())));
                sendReceive.send(tenantSupplierOrderBeforeDto);
            } else {
                paymentService.monitoringError(payment);
            }
        }


        channelPayVo.setCreateTime(payUrlData.getCreateTime());
        channelPayVo.setPayUrl(payUrlData.getPayUrl());
        channelPayVo.setProductNo(payment.getProductNo());
        channelPayVo.setTradeId(tradeId);
        if (channelPayVo.isStatus()) {
            redisCache.setCacheObject(String.format(Constants.PAY_ORDER, tradeId), channelPayVo, 10, TimeUnit.MINUTES);
            redisCache.setCacheObject(String.format(Constants.TRADE_PAY, tradeId), payment, 10, TimeUnit.MINUTES);
        }
        return channelPayVo;
    }

    private void checkSign(PayMerchantDto payMerchantDto, ITenantMerchant merchantInfo) {
        String mSign = SignUtil.getSign(payMerchantDto, merchantInfo.getSecret(), Lists.newArrayList("sign"));
        if (!mSign.equalsIgnoreCase(payMerchantDto.getSign())) {
            log.info("签名错误-{}", JSON.toJSONString(payMerchantDto));
            throw new BizException("签名错误");
        }
    }

    public Integer queryStatusByOrderId(String orderId) {
        IUMerchantOrder iumerchantOrder = iuMerchantOrderDao.getOne(new LambdaQueryWrapper<IUMerchantOrder>().eq(IUMerchantOrder::getOrderId, orderId));
        if (Objects.isNull(iumerchantOrder)) {
            return -1;
        }
        if (iumerchantOrder.getPayStatus().equals(PayStatus.SUCCESS)) {
            return 0;
        }
        return -1;
    }

    public Set<String> queryPayTypeByTenantId(Integer tenantId) {
        Set<TenantAisleDto> aisles = iTenantAisleDao.findAisleByTenantId(tenantId, true);
        return aisles.stream().map(item -> item.getIsAisle().getPayType()).flatMap(List::stream).collect(Collectors.toSet());
    }
}