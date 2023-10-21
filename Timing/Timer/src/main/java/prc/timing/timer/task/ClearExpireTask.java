package prc.timing.timer.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.entity.*;
import prc.service.model.enumeration.PayStatus;
import prc.service.service.NotifyService;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@Slf4j
public class ClearExpireTask {
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private IUPaymentDao iuPaymentDao;
    @Autowired
    private ITenantAisleSupplierDao iTenantAisleSupplierDao;
    @Autowired
    private ISAisleDao isAisleDao;


    @Scheduled(fixedRate = 1000)
    @Transactional
    public void execute() {
        List<ITenantAisleSupplier> suppliers = iTenantAisleSupplierDao.getBaseMapper().selectList(
                new LambdaQueryWrapper<ITenantAisleSupplier>()
                        .eq(ITenantAisleSupplier::getSlow, false)
        );

        Set<IUPayment> payments = suppliers.stream().map(this::clearExpire).flatMap(List::stream).collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(payments)) {

            Set<String> supplierOrderIds = payments.stream().map(IUPayment::getSupplierOrderId).collect(Collectors.toSet());
            Map<String, IUSupplierOrder> supplierOrderIdMap = iuSupplierOrderDao.getBaseMapper().selectList(new LambdaQueryWrapper<IUSupplierOrder>().in(IUSupplierOrder::getOrderId, supplierOrderIds))
                    .stream().collect(Collectors.toMap(IUSupplierOrder::getOrderId, fs -> fs));

            payments.forEach(item -> {
                IUSupplierOrder supplierOrder = supplierOrderIdMap.get(item.getSupplierOrderId());
                if (Objects.isNull(supplierOrder)) {
                    return;
                }
                supplierOrder.setPayStatus(PayStatus.TIME_OUT);
                notifyService.notifySupplierError(supplierOrder, iTenantSupplierDao.getSupplierById(supplierOrder.getSupplierId()));
            });

            Set<Integer> paymentIds = payments.stream().map(IUPayment::getId).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(paymentIds)) {
                iuPaymentDao.getBaseMapper().deleteBatchIds(paymentIds);
            }
        }

    }

    /**
     * 清理已经无法使用的订单
     */
    public List<IUPayment> clearExpire(ITenantAisleSupplier iTenantSupplier) {
        //计算当前时间
        Long nowTime = new Date().getTime();
        Long waitTime = isAisleDao.findById(iTenantSupplier.getAisleId()).getBankLong() * 1000;
        long max = nowTime - waitTime;
        //0到 60 - 2 秒前的订单全部移除
        String zKey = String.format(Constants.FAST_RECHARGE_WAIT_SET, iTenantSupplier.getTenantId(), iTenantSupplier.getSupplierId());
        Set<Object> delPayment = redisCache.zDelKey(zKey, 0L, max - 20000);
        List<IUPayment> finishPayment = Lists.newCopyOnWriteArrayList();
        for (Object tenantPayment : delPayment) {
            IUPayment payment = (IUPayment) tenantPayment;
            String key = null;
            if (Boolean.TRUE.equals(payment.getSlow())) {
                key = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, payment.getTenantId(), payment.getSupplierId(), payment.getOperator().getKey(), payment.getMoney().intValue(), payment.getProvinceId());
            } else {
                key = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, payment.getTenantId(), payment.getSupplierId(), payment.getOperator().getKey(), payment.getMoney().intValue(), payment.getProvinceId());
            }
            redisCache.removeList(key, payment);
            finishPayment.add(payment);
        }
        return finishPayment;
    }
}
