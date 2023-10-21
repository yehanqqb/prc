package prc.service.channel.payment;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.utils.AgentUtil;
import prc.service.common.utils.NetWorkUtil;
import prc.service.common.utils.RandomStrategyUtil;
import prc.service.common.utils.SpringUtil;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.*;
import prc.service.model.entity.ITenantAisleSupplier;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.service.NotifyService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConcurrentPaymentService {
    @Autowired
    private ITenantAisleDao iTenantAisleDao;
    @Autowired
    private IUPaymentDao iuPaymentDao;

    public IUPayment getPaymentParent(PaymentBeforeDto paymentBeforeDto) {
        Set<TenantAisleDto> aisles = iTenantAisleDao.findAisleByTenantId(paymentBeforeDto.getTenantId(), true);
        TenantAisleDto tenantAisleDto = aisles.stream().filter(item -> item.getIsAisle().getPayType().contains(paymentBeforeDto.getPayType())).findFirst().get();

        ChannelPayment channelPayment = SpringUtil.getBean(tenantAisleDto.getIsAisle().getPayBeanName());
        return channelPayment.getPayment(paymentBeforeDto, tenantAisleDto);
    }

    public void monitoring(IUPayment iuPayment) {
        TenantAisleDto aisle = iTenantAisleDao.findAisleByTenantId(iuPayment.getTenantId(), true)
                .stream().filter(item -> item.getIsAisle().getId().equals(iuPayment.getAisleId())).findFirst().get();

        Date start = new Date();
        ChannelPayment channelPayment = SpringUtil.getBean(iuPayment.getMonitorBean());
        ChannelPaymentBefore channelPaymentBefore = SpringUtil.getBean(aisle.getIsAisle().getPayBeanName());

        ChannelMonitoringVo vo = channelPaymentBefore.monitoringChannelParent(channelPayment, iuPayment, aisle.getIsAisle());

        if (!iuPaymentDao.getById(iuPayment.getId()).getPayStatus().equals(PayStatus.ING)) {
            throw new BizException("监控异常，已手动更改的状态");
        }

        if (!StringUtils.isEmpty(vo.getPaymentNo())) {
            iuPayment.setPaymentNo(vo.getPaymentNo());
        }

        if (vo.isOrderStatus()) {
            // 返销
            iuPayment.setPayStatus(PayStatus.ERROR);
            iuPayment.setFinishStatus(FinishStatus.ERROR);
            // 先保存
            channelPayment.monitoringError(iuPayment);
        } else {
            if (vo.isPayStatus()) {
                // 支付成功的
                iuPayment.setPayStatus(PayStatus.SUCCESS);
                iuPayment.setMonitor(new Date().getTime() - start.getTime());
                if (vo.isFinishStatus()) {
                    iuPayment.setFinishTime(new Date());
                    iuPayment.setFinishStatus(FinishStatus.SUCCESS);
                }
                iuPaymentDao.saveOrUpdate(iuPayment);
                channelPayment.monitoringSuccess(iuPayment);
            } else {
                // 支付失败的
                iuPayment.setPayStatus(PayStatus.ERROR);
                iuPayment.setMonitor(new Date().getTime() - start.getTime());
                iuPaymentDao.saveOrUpdate(iuPayment);
                channelPayment.monitoringError(iuPayment);
            }
        }
    }
}
