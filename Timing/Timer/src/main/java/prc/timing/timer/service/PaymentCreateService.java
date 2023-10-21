package prc.timing.timer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import prc.service.channel.before.ChannelBefore;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.utils.IdUtil;
import prc.service.common.utils.SpringUtil;
import prc.service.config.RedisCache;
import prc.service.dao.IUPaymentDao;
import prc.service.dao.IUSupplierOrderDao;
import prc.service.model.dto.*;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.PayStatus;
import prc.service.model.vo.ChannelBeforeVo;
import prc.service.proxy.factory.ProvinceFactory;
import prc.service.service.NotifyService;
import prc.service.service.ProxyService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PaymentCreateService {
    @Autowired
    private ProxyService QgProxyService;

    @Autowired
    private ThreadPoolTaskExecutor systemExecutor;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private IUPaymentDao iuPaymentDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;

    @Transactional
    public void createPayment(TenantSupplierOrderBeforeDto rechargeDto) {
        if (Objects.isNull(rechargeDto.getIuSupplierOrder().getProvinceId())) {
            refreshProvince(rechargeDto);
        }

        if (rechargeDto.getIuSupplierOrder().getSlow()) {
            slowEnhance(rechargeDto, 0);
        } else {
            fastEnhance(rechargeDto);
        }

        after(rechargeDto);

    }

    public void save(IUSupplierOrder iuSupplierOrder, IUPayment iuPayment) {
        String key = null;
        if (Boolean.TRUE.equals(iuPayment.getSlow())) {
            key = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, iuPayment.getTenantId(), iuPayment.getSupplierId(), iuPayment.getOperator().getKey(), iuPayment.getMoney().intValue(), iuPayment.getProvinceId());
        } else {
            key = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, iuPayment.getTenantId(), iuPayment.getSupplierId(), iuPayment.getOperator().getKey(), iuPayment.getMoney().intValue(), iuPayment.getProvinceId());
        }
        redisCache.lPush(key, iuPayment);
        if (Boolean.FALSE.equals(iuPayment.getSlow())) {
            String zKey = String.format(Constants.FAST_RECHARGE_WAIT_SET, iuPayment.getTenantId(), iuSupplierOrder.getSupplierId());
            redisCache.zAdd(zKey, iuPayment, iuPayment.getCreateTime().getTime());
        }
    }

    public void after(TenantSupplierOrderBeforeDto rechargeDto) {
        IUPayment payment = new IUPayment();

        List<TenantAisleDto> channels = rechargeDto.getSupplierInfo().getTenantAisleDto().stream().filter(item -> item.getITenantAisle().getNotProvince().contains(rechargeDto.getIuSupplierOrder().getProvinceName())).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(channels)) {
            rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.CREATE_ERROR);
            notifyService.notifySupplierError(rechargeDto.getIuSupplierOrder(), rechargeDto.getSupplierInfo().getITenantSupplier());
            throw new BizException("supplier error is not province close");
        }


        if (channels.size() == 1 && channels.get(0).getIsAisle().getBefore().equals(Boolean.TRUE)) {
            ChannelBeforeDto channelBaseDto = new ChannelBeforeDto();
            channelBaseDto.setIuSupplierOrder(rechargeDto.getIuSupplierOrder());
            channelBaseDto.setPayment(rechargeDto.getIuPayment());

            ChannelBefore channelBefore = SpringUtil.getBean(channels.get(0).getIsAisle().getBeforeBeanName());

            ChannelBeforeVo beforeVo = channelBefore.createPay(channelBaseDto);

            if (!beforeVo.isSuccess()) {
                if (rechargeDto.isRetry()) {
                    rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.ERROR);
                } else {
                    rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.CREATE_ERROR);
                }
                rechargeDto.getIuSupplierOrder().setRemark(beforeVo.getRemark());
                notifyService.notifySupplierError(rechargeDto.getIuSupplierOrder(), rechargeDto.getSupplierInfo().getITenantSupplier());
                throw new BizException("supplier before is tryCount");
            }
            payment.setAisleId(channels.get(0).getIsAisle().getId());
            payment.setMonitorBean(channels.get(0).getIsAisle().getMonitorBean());
        }

        // payment add


        payment.setFinishStatus(FinishStatus.WAIT);
        payment.setMoney(rechargeDto.getIuSupplierOrder().getMoney());
        payment.setOperator(rechargeDto.getIuSupplierOrder().getOperator());
        payment.setPaymentId(IdUtil.getId("prc", "", 5));
        payment.setPayStatus(PayStatus.WAIT);
        payment.setProductNo(rechargeDto.getIuSupplierOrder().getProductNo());
        payment.setProvinceId(rechargeDto.getIuSupplierOrder().getProvinceId());
        payment.setProvinceName(rechargeDto.getIuSupplierOrder().getProvinceName());
        payment.setSlow(rechargeDto.getIuSupplierOrder().getSlow());
        payment.setSupplierId(rechargeDto.getIuSupplierOrder().getSupplierId());
        payment.setSupplierOrderId(rechargeDto.getIuSupplierOrder().getOrderId());
        payment.setTenantId(rechargeDto.getIuSupplierOrder().getTenantId());
        iuPaymentDao.save(payment);

        rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.WAIT);
        rechargeDto.getIuSupplierOrder().setPaymentId(payment.getPaymentId());
        iuSupplierOrderDao.saveOrUpdate(rechargeDto.getIuSupplierOrder());


        save(rechargeDto.getIuSupplierOrder(), payment);

    }

    public void fastEnhance(TenantSupplierOrderBeforeDto rechargeDto) {

    }

    public void slowEnhance(TenantSupplierOrderBeforeDto rechargeDto, Integer tryCount) {
        if (tryCount >= 5) {
            rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.CREATE_ERROR);
            notifyService.notifySupplierError(rechargeDto.getIuSupplierOrder(), rechargeDto.getSupplierInfo().getITenantSupplier());
            throw new BizException("supplier error is tryCount");
        }

        if (rechargeDto.isRetry()) {
            Long paymentCount = iuPaymentDao.getBaseMapper().selectCount(new LambdaQueryWrapper<IUPayment>()
                    .eq(IUPayment::getSupplierOrderId, rechargeDto.getIuSupplierOrder().getOrderId())
            );

            if (paymentCount >= rechargeDto.getSupplierInfo().getITenantSupplier().getRepetitionCount()) {
                rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.ERROR);
                notifyService.notifySupplierError(rechargeDto.getIuSupplierOrder(), rechargeDto.getSupplierInfo().getITenantSupplier());
                throw new BizException("supplier retry count max");
            }

            paymentCount = iuPaymentDao.getBaseMapper().selectCount(new LambdaQueryWrapper<IUPayment>()
                    .eq(IUPayment::getSupplierOrderId, rechargeDto.getIuSupplierOrder().getOrderId())
                    .eq(IUPayment::getPayStatus, PayStatus.WAIT)
            );
            if (paymentCount > 0) {
                throw new BizException("payment retry exist wait");
            }

            String key;
            if (Boolean.TRUE.equals(rechargeDto.getIuSupplierOrder().getSlow())) {
                key = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, rechargeDto.getIuSupplierOrder().getTenantId(), rechargeDto.getIuSupplierOrder().getSupplierId(), rechargeDto.getIuSupplierOrder().getOperator().getKey(), rechargeDto.getIuSupplierOrder().getMoney().intValue(), rechargeDto.getIuSupplierOrder().getProvinceId());
            } else {
                key = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, rechargeDto.getIuSupplierOrder().getTenantId(), rechargeDto.getIuSupplierOrder().getSupplierId(), rechargeDto.getIuSupplierOrder().getOperator().getKey(), rechargeDto.getIuSupplierOrder().getMoney().intValue(), rechargeDto.getIuSupplierOrder().getProvinceId());
            }
            List<IUPayment> payments = redisCache.getCacheList(key);
            payments.forEach(item -> {
                if (item.getSupplierOrderId().equals(rechargeDto.getIuSupplierOrder().getOrderId())) {
                    redisCache.removeList(key, item);
                }
            });

        }

        // add new payment

    }


    public void refreshProvince(TenantSupplierOrderBeforeDto rechargeDto) {

        if (!rechargeDto.getIuSupplierOrder().getExt().isInquireProvince()) {
            rechargeDto.getIuSupplierOrder().setProvinceId(-1);
            rechargeDto.getIuSupplierOrder().setProvinceName("未知");

        } else {

            ProvinceFactory provinceFactory = new ProvinceFactory(QgProxyService, systemExecutor);
            ProvinceNameCodeDto province = provinceFactory.getProvince(rechargeDto.getIuSupplierOrder().getProductNo());
            if (province.getProvinceId() == -1) {
                rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.CREATE_ERROR);
                notifyService.notifySupplierError(rechargeDto.getIuSupplierOrder(), rechargeDto.getSupplierInfo().getITenantSupplier());
                throw new BizException("supplier error is not province notify");
            }

            rechargeDto.getIuSupplierOrder().setProvinceId(province.getProvinceId());
            rechargeDto.getIuSupplierOrder().setProvinceName(province.getProvinceName());
            if (!province.getOperator().equals(rechargeDto.getIuSupplierOrder().getOperator())) {
                rechargeDto.getIuSupplierOrder().setPayStatus(PayStatus.CREATE_ERROR);
                notifyService.notifySupplierError(rechargeDto.getIuSupplierOrder(), rechargeDto.getSupplierInfo().getITenantSupplier());
                throw new BizException("two operator");
            }

        }
    }
}
