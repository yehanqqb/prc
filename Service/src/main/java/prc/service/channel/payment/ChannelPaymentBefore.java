package prc.service.channel.payment;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.utils.AgentUtil;
import prc.service.common.utils.NetWorkUtil;
import prc.service.common.utils.RandomStrategyUtil;
import prc.service.common.utils.SpringUtil;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.*;
import prc.service.model.entity.*;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.mq.SendReceive;
import prc.service.service.NotifyService;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public abstract class ChannelPaymentBefore implements ChannelPayment {
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;
    @Autowired
    private ITenantMerchantDao iTenantMerchantDao;
    @Autowired
    private IUPaymentDao iuPaymentDao;
    @Autowired
    private SendReceive sendReceive;
    @Autowired
    private ITenantAisleDao iTenantAisleDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ITenantAisleSupplierDao iTenantAisleSupplierDao;
    @Autowired
    private HttpServletRequest request;

    @Override
    public IUPayment getPayment(PaymentBeforeDto paymentBeforeDto, TenantAisleDto tenantAisleDto) {
        List<TenantSupplierAisleDto> supplierInfos = iTenantSupplierDao.getSupplierByTenantIdAndAisleId(paymentBeforeDto.getTenantId(), tenantAisleDto.getIsAisle().getId());

        Map<Integer, ITenantAisleSupplier> supplierRadioIdMap = iTenantAisleSupplierDao.getBaseMapper().selectList(new LambdaQueryWrapper<ITenantAisleSupplier>()
                .eq(ITenantAisleSupplier::getAisleId, tenantAisleDto.getIsAisle().getId())
                .eq(ITenantAisleSupplier::getTenantId, paymentBeforeDto.getTenantId())
        ).stream().collect(Collectors.toMap(ITenantAisleSupplier::getSupplierId, fs -> fs));

        return execute(
                tenantAisleDto.getIsAisle().getSlow(),
                supplierInfos,
                paymentBeforeDto.getTenantId(),
                paymentBeforeDto.getMoney(),
                supplierRadioIdMap,
                tenantAisleDto,
                paymentBeforeDto.getPayType()
        );
    }

    private IUPayment execute(boolean slow,
                              List<TenantSupplierAisleDto> supplierInfos,
                              Integer tenantId,
                              BigDecimal money,
                              Map<Integer, ITenantAisleSupplier> supplierRadioIdMap,
                              TenantAisleDto tenantAisleDto,
                              String payType
    ) {
        List<PaymentSupplierDto> cache = Lists.newCopyOnWriteArrayList();
        Map<Integer, PaymentSupplierDto> cacheMap = Maps.newConcurrentMap();

        supplierInfos.forEach(item -> {
            if (slow) {
                Arrays.stream(Operator.values()).forEach(item2 -> {
                    String provinceKey = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, tenantId, item.getITenantSupplier().getId(), item2.getKey(), money.intValue());
                    if (checkNumber(provinceKey)) {
                        PaymentSupplierDto dto = cacheMap.get(item.getITenantSupplier().getId());
                        if (Objects.isNull(dto)) {
                            dto = new PaymentSupplierDto();
                            dto.setExistOperator(Lists.newArrayList());
                        }
                        dto.setSupplierId(item.getITenantSupplier().getId());
                        dto.setRadio(supplierRadioIdMap.get(item.getITenantSupplier().getId()).getRadio());
                        List<Operator> existOperatot = dto.getExistOperator();
                        existOperatot.add(item2);
                        dto.setExistOperator(existOperatot);


                        dto.setBankLong(tenantAisleDto.getIsAisle().getBankLong());
                        cache.add(dto);
                        cacheMap.put(item.getITenantSupplier().getId(), dto);
                    }
                });
            } else {
                Arrays.stream(Operator.values()).forEach(item2 -> {
                    String provinceKey = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, tenantId, item.getITenantSupplier().getId(), item2.getKey(), money.intValue());
                    if (checkNumber(provinceKey)) {
                        PaymentSupplierDto dto = cacheMap.get(item.getITenantSupplier().getId());
                        if (Objects.isNull(dto)) {
                            dto = new PaymentSupplierDto();
                            dto.setExistOperator(Lists.newArrayList());
                        }
                        dto.setSupplierId(item.getITenantSupplier().getId());
                        dto.setRadio(supplierRadioIdMap.get(item.getITenantSupplier().getId()).getRadio());
                        List<Operator> existOperatot = dto.getExistOperator();
                        existOperatot.add(item2);
                        dto.setExistOperator(existOperatot);

                        dto.setBankLong(tenantAisleDto.getIsAisle().getBankLong());

                        cache.add(dto);
                        cacheMap.put(item.getITenantSupplier().getId(), dto);
                    }
                });
            }
        });

        if (CollectionUtils.isEmpty(cache)) {
            throw new BizException("order is insufficient");
        }

        List<Pair<PaymentSupplierDto, Integer>> list = cache.stream().map(item -> new Pair<>(item, item.getRadio().intValue())).collect(Collectors.toList());
        RandomStrategyUtil<PaymentSupplierDto, Integer> strategy = new RandomStrategyUtil<>(list);
        PaymentSupplierDto paymentSupplierDto = strategy.random();

        String provinceKey;

        if (slow) {
            provinceKey = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, tenantId, paymentSupplierDto.getSupplierId(), paymentSupplierDto.getExistOperator().get(0).getKey(), money.intValue());
        } else {
            provinceKey = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, tenantId, paymentSupplierDto.getSupplierId(), paymentSupplierDto.getExistOperator().get(0).getKey(), money.intValue());

        }
        List<ProvinceCountCacheDto> provincePayments = redisCache.getCacheObject(provinceKey);
        if (CollectionUtils.isEmpty(provincePayments)) {
            throw new BizException("order is insufficient");
        }
        List<ProvinceCountCacheDto> cacheExist = provincePayments.stream().filter(item2 -> item2.getNumber() > 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cacheExist)) {
            throw new BizException("order is insufficient");
        }


        Random random = new Random();
        ProvinceCountCacheDto cacheDto = cacheExist.get(random.nextInt(cache.size()));


        String key = null;
        if (slow) {
            key = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, tenantId, paymentSupplierDto.getSupplierId(), paymentSupplierDto.getExistOperator().get(0).getKey(), money.intValue(), cacheDto.getProvinceId());
        } else {
            key = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, tenantId, paymentSupplierDto.getSupplierId(), paymentSupplierDto.getExistOperator().get(0).getKey(), money.intValue(), cacheDto.getProvinceId());
        }

        IUPayment payment = (IUPayment) redisCache.rPop(key);
        if (Objects.isNull(payment)) {
            throw new BizException("order is pop insufficient");
        }

        IUSupplierOrder supplierOrderInfo = iuSupplierOrderDao.getOne(new LambdaQueryWrapper<IUSupplierOrder>()
                .eq(IUSupplierOrder::getOrderId, payment.getSupplierOrderId())
        );
        if (Objects.isNull(supplierOrderInfo)) {
            iuPaymentDao.removeById(payment.getId());
            throw new BizException("order is supplier insufficient");
        }

        if (!supplierOrderInfo.getPayStatus().equals(PayStatus.WAIT)) {
            throw new BizException("order is supplier insufficient");
        }
        // before
        Map<Integer, TenantSupplierAisleDto> supplierInfoIdMap = supplierInfos.stream().collect(Collectors.toMap(item -> {
            return item.getITenantSupplier().getId();
        }, fs -> fs));

        enhanceVerify(payment,
                supplierInfoIdMap.get(supplierOrderInfo.getSupplierId()),
                supplierOrderInfo,
                tenantAisleDto);

        // after
        if (Boolean.FALSE.equals(payment.getSlow())) {
            if (new Date().getTime() - payment.getCreateTime().getTime() >= (paymentSupplierDto.getBankLong() - 15) * 1000) {

                supplierOrderInfo.setFinishStatus(FinishStatus.BANK);
                supplierOrderInfo.setPayStatus(PayStatus.NOT_PAY);
                supplierOrderInfo.setRemark("过期");
                notifyService.notifySupplierError(supplierOrderInfo, supplierInfoIdMap.get(supplierOrderInfo.getSupplierId()).getITenantSupplier());

                log.info("快充拉不起-{}-{}", (new Date().getTime() - payment.getCreateTime().getTime()), (paymentSupplierDto.getBankLong() - 15) * 1000);
                throw new BizException("order is supplier back long insufficient");
            } else {
                // 拉起
                String zKey = String.format(Constants.FAST_RECHARGE_WAIT_SET, tenantId, paymentSupplierDto.getSupplierId());
                redisCache.removeSet(zKey, payment);
            }
        } else {
            if (Math.abs(DateUtil.between(supplierOrderInfo.getCreateTime(), new Date(), DateUnit.HOUR, false)) >= 23) {
                // 拉不起
                log.info("慢充已失败{}-{}", DateUtil.between(supplierOrderInfo.getCreateTime(), new Date(), DateUnit.HOUR, false), payment.getPaymentId());
                throw new BizException("order is supplier back long insufficient");
            }

        }

        payment.setAisleId(tenantAisleDto.getIsAisle().getId());
        payment.setMonitorBean(tenantAisleDto.getIsAisle().getMonitorBean());
        payment.setPayStatus(PayStatus.ING);
        payment.setAgent(AgentUtil.getAgent(request.getHeader("User-Agent")));
        payment.setPayBean(tenantAisleDto.getIsAisle().getPayBeanName());
        payment.setPayType(payType);
        payment.setPayTime(new Date());
        payment.setUserIp(NetWorkUtil.getIpAddress(request));
        payment.setPrimary(tenantAisleDto.getIsAisle().getPrimary());

        return payment;
    }


    private void enhanceVerify(IUPayment iuPayment,
                               TenantSupplierAisleDto tenantSupplierAisleDto,
                               IUSupplierOrder iuSupplierOrder,
                               TenantAisleDto tenantAisleDto
    ) {
        // No.1
        String blackKey = String.format(Constants.BLACK_TELNET, iuPayment.getTenantId());
        List<String> notProductNos = redisCache.getCacheObject(blackKey);
        if (!CollectionUtils.isEmpty(notProductNos) && notProductNos.contains(iuPayment.getProductNo())) {
            iuSupplierOrder.setFinishStatus(FinishStatus.BANK);
            iuSupplierOrder.setPayStatus(PayStatus.NOT_PAY);
            iuSupplierOrder.setRemark("黑名单");
            notifyService.notifySupplierError(iuSupplierOrder, tenantSupplierAisleDto.getITenantSupplier());

            iuPayment.setPayStatus(PayStatus.NOT_PAY);
            iuPayment.setRemark("黑名单");
            iuPaymentDao.saveOrUpdate(iuPayment);
            throw new BizException("productNo on the blacklist");
        }
        // No.2
        if (tenantAisleDto.getITenantAisle().getNotProvince().contains(iuPayment.getProvinceName())) {
            iuSupplierOrder.setFinishStatus(FinishStatus.BANK);
            iuSupplierOrder.setPayStatus(PayStatus.NOT_PAY);
            iuSupplierOrder.setRemark("屏蔽省份");
            notifyService.notifySupplierError(iuSupplierOrder, tenantSupplierAisleDto.getITenantSupplier());

            iuPayment.setPayStatus(PayStatus.NOT_PAY);
            iuPayment.setRemark("屏蔽省份");
            iuPaymentDao.saveOrUpdate(iuPayment);
            throw new BizException("productNo on the blacklist");
        }
    }


    private void slowSendRetry(IUSupplierOrder iuSupplierOrder,
                               TenantSupplierAisleDto supplierInfo,
                               IUPayment iuPayment) {

        TenantSupplierOrderBeforeDto tenantSupplierOrderBeforeDto = new TenantSupplierOrderBeforeDto();
        tenantSupplierOrderBeforeDto.setSupplierInfo(supplierInfo);
        tenantSupplierOrderBeforeDto.setIuPayment(iuPayment);
        tenantSupplierOrderBeforeDto.setRetry(true);
        tenantSupplierOrderBeforeDto.setIuSupplierOrder(iuSupplierOrder);
        sendReceive.send(tenantSupplierOrderBeforeDto);

    }

    private boolean checkNumber(String provinceKey) {
        List<ProvinceCountCacheDto> provincePayments = redisCache.getCacheObject(provinceKey);
        if (CollectionUtils.isEmpty(provincePayments)) {
            return false;
        }
        List<ProvinceCountCacheDto> cache = provincePayments.stream().filter(item -> item.getNumber() > 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cache)) {
            return false;
        }
        return true;
    }

    @Override
    public void monitoringSuccess(IUPayment iuPayment) {
        // 通知供货商成功通知商户
        IUSupplierOrder supplierOrder = iuSupplierOrderDao.getOne(new LambdaQueryWrapper<IUSupplierOrder>().eq(IUSupplierOrder::getOrderId, iuPayment.getSupplierOrderId()));
        TenantSupplierAisleDto supplierInfo = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(supplierOrder.getTenantId(), supplierOrder.getSupplierId());

        IUMerchantOrder merchantOrder = iuMerchantOrderDao.getOne(new LambdaQueryWrapper<IUMerchantOrder>().eq(IUMerchantOrder::getOrderId, iuPayment.getMerchantOrderId()));

        ITenantMerchant merchantInfo = iTenantMerchantDao.getMerchantByTenantIdAndMerchant(merchantOrder.getTenantId(), merchantOrder.getMerchantId());


        supplierOrder.setPayStatus(PayStatus.SUCCESS);
        supplierOrder.setFinishStatus(iuPayment.getFinishStatus());
        merchantOrder.setPayStatus(PayStatus.SUCCESS);
        if (supplierOrder.getFinishStatus().equals(FinishStatus.SUCCESS)) {
            supplierOrder.setFinishDate(new Date());
            try {
                if (notifyService.notifySupplierSuccess(supplierOrder, supplierInfo.getITenantSupplier(), iuPayment.getPaymentNo()).get()) {

                    notifyService.notifyMerchantSuccess(merchantOrder, merchantInfo);
                    iuPayment.setMerchantNotify(true);
                    iuPayment.setSupplierNotify(true);
                    iuPaymentDao.saveOrUpdate(iuPayment);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            iuSupplierOrderDao.saveOrUpdate(supplierOrder);
            notifyService.notifyMerchantSuccess(merchantOrder, merchantInfo);
            iuPayment.setMerchantNotify(true);
            iuPayment.setSupplierNotify(false);
            iuPaymentDao.saveOrUpdate(iuPayment);
        }
    }

    @Override
    public void monitoringError(IUPayment iuPayment) {
        iuMerchantOrderDao.getBaseMapper().updateStatus(iuPayment.getMerchantOrderId(), PayStatus.ERROR.getId());
        IUSupplierOrder supplierOrder = iuSupplierOrderDao.getOne(new LambdaQueryWrapper<IUSupplierOrder>().eq(IUSupplierOrder::getOrderId, iuPayment.getSupplierOrderId()));
        TenantSupplierAisleDto supplierInfo = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(supplierOrder.getTenantId(), supplierOrder.getSupplierId());


        if (Boolean.TRUE.equals(iuPayment.getSlow())) {
            UpdateWrapper<IUSupplierOrder> iuSupplierOrderUpdateWrapper = Wrappers.update();
            iuSupplierOrderUpdateWrapper.lambda().eq(IUSupplierOrder::getOrderId, iuPayment.getSupplierOrderId());
            IUSupplierOrder iuSupplierOrder = new IUSupplierOrder();
            iuSupplierOrder.setPayStatus(PayStatus.ERROR);
            iuSupplierOrderDao.update(iuSupplierOrder, iuSupplierOrderUpdateWrapper);

            TenantSupplierOrderBeforeDto tenantSupplierOrderBeforeDto = new TenantSupplierOrderBeforeDto();
            tenantSupplierOrderBeforeDto.setSupplierInfo(supplierInfo);
            tenantSupplierOrderBeforeDto.setIuPayment(iuPayment);
            tenantSupplierOrderBeforeDto.setRetry(true);
            tenantSupplierOrderBeforeDto.setIuSupplierOrder(supplierOrder);
            sendReceive.send(tenantSupplierOrderBeforeDto);
        } else {

            supplierOrder.setPayStatus(PayStatus.ERROR);
            supplierOrder.setFinishStatus(FinishStatus.BANK);
            try {
                if (notifyService.notifySupplierError(supplierOrder, supplierInfo.getITenantSupplier()).get()) {
                    iuPayment.setMerchantNotify(false);
                    iuPayment.setSupplierNotify(true);
                    iuPaymentDao.saveOrUpdate(iuPayment);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ChannelMonitoringVo monitoringChannelParent(ChannelPayment channelPayment, IUPayment iuPayment, ISAisle aisle) {
        try {
            Thread.sleep(aisle.getMountSleep() * 1000);
            long mountLong = getPayMonitoringLong(iuPayment, aisle);
            long start = new Date().getTime();
            int mor = 0;
            ChannelMonitoringVo vo = new ChannelMonitoringVo();
            while (new Date().getTime() - start <= mountLong || mor == 0) {
                if (new Date().getTime() - start > mountLong) {
                    mor = 1;
                }

                if (mor == 0) {
                    Thread.sleep(aisle.getMountSleep() * 1000);
                }

                vo = channelPayment.monitoringChannel(iuPayment);
                if (vo.isOrderStatus()) {
                    return vo;
                } else {
                    if (vo.isPayStatus()) {
                        return vo;
                    }
                }
                if (new Date().getTime() - start + aisle.getMountSleep() * 1000 <= mountLong) {
                    Thread.sleep(aisle.getMountSleep() * 1000);
                } else {
                    break;
                }
            }
            // 监控超时
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[监控异常--paymentId:{}--{}]", iuPayment.getPaymentId(), e.getMessage());
            throw new BizException("[监控异常]");
        }
    }

    private long getPayMonitoringLong(IUPayment iuPayment, ISAisle aisle) {
        if (Boolean.TRUE.equals(iuPayment.getSlow())) {
            // 慢充不管，直接用
            return aisle.getMonitoringLong() * 1000;
        } else {
            // 库存等待时间 + 拉单时间
            return aisle.getMonitoringLong() * 1000 - (new Date().getTime() - iuPayment.getCreateTime().getTime()) - iuPayment.getWait();
        }
    }

    @Override
    public JSONObject getBefore(IUPayment iuPayment) {
        return null;
    }

    @Override
    public JSONObject getAfter(IUPayment iuPayment) {
        return null;
    }
}
