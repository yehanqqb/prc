package prc.client.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import prc.client.service.model.dto.MerchantOrderDto;
import prc.client.service.model.dto.PaymentOrderDto;
import prc.service.channel.payment.ChannelPayment;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.common.utils.SpringUtil;
import prc.service.dao.*;
import prc.service.model.dto.TenantSupplierAisleDto;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.PayStatus;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.service.NotifyService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PaymentOrderService {
    @Autowired
    private IUPaymentDao iuPaymentDao;
    @Autowired
    private ThreadPoolTaskExecutor apiExecutor;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private ITenantMerchantDao iTenantMerchantDao;
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;

    public Map<String, Object> getTitle(PageReq<PaymentOrderDto> pageReq) {
        PageReq<IUPayment> pagePes = new PageReq<IUPayment>();
        BeanUtil.copyBeanProp(pagePes, pageReq);
        pagePes.setCommon(BeanUtil.convertToBean(pageReq.getCommon(), IUPayment.class));
        return iuPaymentDao.getMap(pagePes.createTitle(pagePes.createMapper()));
    }


    public PageRes<IUPayment> page(PageReq<PaymentOrderDto> pageReq) {
        PageReq<IUPayment> pagePes = new PageReq<IUPayment>();
        BeanUtil.copyBeanProp(pagePes, pageReq);
        pagePes.setCommon(BeanUtil.convertToBean(pageReq.getCommon(), IUPayment.class));
        LambdaQueryWrapper<IUPayment> query = pagePes.createMapper().lambda();
        Page<IUPayment> pageRes = iuPaymentDao.getBaseMapper().selectPage(pagePes.createPage(), query);
        return new PageRes<IUPayment>().trans(pageRes);
    }

    // 查单
    public ChannelMonitoringVo look(Integer paymentId) {
        IUPayment payment = iuPaymentDao.getById(paymentId);
        Assert.notNull(payment, "支付单不存在");
        ChannelPayment channelPayment = SpringUtil.getBean(payment.getMonitorBean());
        return channelPayment.monitoringChannel(payment);
    }

    public boolean reissue(List<Integer> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<IUPayment> iuPayments = iuPaymentDao.getBaseMapper().selectBatchIds(Sets.newHashSet(ids));
            iuPayments.forEach(item -> {
                CompletableFuture.runAsync(() -> {
                    IUSupplierOrder supplierOrder = null;
                    if (!StringUtils.isEmpty(item.getSupplierOrderId())) {
                        supplierOrder = iuSupplierOrderDao.getBaseMapper().selectOne(new LambdaQueryWrapper<IUSupplierOrder>().eq(IUSupplierOrder::getOrderId, item.getSupplierOrderId()));

                    }
                    if (Objects.nonNull(supplierOrder)) {
                        supplierOrder.setPayStatus(PayStatus.SUCCESS);
                        supplierOrder.setFinishStatus(FinishStatus.SUCCESS);
                        supplierOrder.setFinishDate(new Date());
                        supplierOrder.setRemark("批量成功");
                    }

                    TenantSupplierAisleDto iSupplier = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(item.getTenantId(), item.getSupplierId());
                    IUMerchantOrder merchantOrder = iuMerchantOrderDao.getBaseMapper().selectOne(new LambdaQueryWrapper<IUMerchantOrder>().eq(IUMerchantOrder::getOrderId, item.getMerchantOrderId()));
                    ITenantMerchant iMerchant = null;
                    if (Objects.nonNull(merchantOrder)) {
                        iMerchant = iTenantMerchantDao.getMerchantByTenantIdAndMerchant(item.getTenantId(), item.getMerchantId());
                        merchantOrder.setPayStatus(PayStatus.SUCCESS);
                        merchantOrder.setRemark("批量成功");
                    }


                    item.setPayStatus(PayStatus.SUCCESS);
                    item.setFinishStatus(FinishStatus.SUCCESS);
                    item.setFinishTime(new Date());
                    item.setRemark("批量成功");

                    try {
                        if (Objects.nonNull(supplierOrder)) {
                            if (notifyService.notifySupplierSuccess(supplierOrder, iSupplier.getITenantSupplier(), item.getPaymentNo()).get()) {
                                if (Objects.nonNull(iMerchant)) {
                                    notifyService.notifyMerchantSuccess(merchantOrder, iMerchant);
                                    item.setMerchantNotify(true);
                                }
                                item.setSupplierNotify(true);
                                iuPaymentDao.saveOrUpdate(item);
                                iuPaymentDao.getBaseMapper().updateOtherSlowTrue(item.getSupplierOrderId(), item.getPaymentId());
                            }
                        } else {
                            if (Objects.nonNull(iMerchant)) {
                                notifyService.notifyMerchantSuccess(merchantOrder, iMerchant);
                                item.setMerchantNotify(true);
                            }
                            item.setSupplierNotify(true);
                            iuPaymentDao.saveOrUpdate(item);
                            iuPaymentDao.getBaseMapper().updateOtherSlowTrue(item.getSupplierOrderId(), item.getPaymentId());
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }, apiExecutor);
            });
        }
        return true;
    }
}
