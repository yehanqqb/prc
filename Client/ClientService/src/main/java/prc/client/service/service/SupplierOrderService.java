package prc.client.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import prc.client.service.model.dto.SupplierOrderDto;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.dao.ITenantSupplierDao;
import prc.service.dao.IUPaymentDao;
import prc.service.dao.IUSupplierOrderDao;
import prc.service.model.dto.TenantSupplierAisleDto;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.PayStatus;
import prc.service.service.NotifyService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SupplierOrderService {
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private ThreadPoolTaskExecutor apiExecutor;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private IUPaymentDao iuPaymentDao;


    public Map<String, Object> getTitle(PageReq<SupplierOrderDto> pageReq) {
        PageReq<IUSupplierOrder> pagePes = new PageReq<IUSupplierOrder>();
        BeanUtil.copyBeanProp(pagePes, pageReq);
        pagePes.setCommon(BeanUtil.convertToBean(pageReq.getCommon(), IUSupplierOrder.class));
        QueryWrapper<IUSupplierOrder> query = pagePes.createMapper();
        return iuSupplierOrderDao.getMap(pagePes.createTitle(query));
    }


    public PageRes<IUSupplierOrder> page(PageReq<SupplierOrderDto> pageReq) {
        PageReq<IUSupplierOrder> pagePes = new PageReq<IUSupplierOrder>();
        BeanUtil.copyBeanProp(pagePes, pageReq);
        pagePes.setCommon(BeanUtil.convertToBean(pageReq.getCommon(), IUSupplierOrder.class));
        LambdaQueryWrapper<IUSupplierOrder> query = pagePes.createMapper().lambda();

        Page<IUSupplierOrder> pageRes = iuSupplierOrderDao.getBaseMapper().selectPage(pagePes.createPage(), query);

        return new PageRes<IUSupplierOrder>().trans(pageRes);
    }

    public boolean reissue(List<Integer> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<IUSupplierOrder> iuMerchantOrders = iuSupplierOrderDao.getBaseMapper().selectBatchIds(Sets.newHashSet(ids));
            Map<String, String> mapToNo = iuPaymentDao.getBaseMapper().selectList(new LambdaQueryWrapper<IUPayment>().in(IUPayment::getPaymentId, iuMerchantOrders.stream().map(IUSupplierOrder::getPaymentId).collect(Collectors.toSet())))
                    .stream().filter(item -> {
                        if (StringUtils.isEmpty(item.getPaymentNo())) {
                            return false;
                        } else {
                            return true;
                        }
                    }).collect(Collectors.toMap(IUPayment::getPaymentId, IUPayment::getPaymentNo));

            iuMerchantOrders.forEach(item -> {
                CompletableFuture.runAsync(() -> {

                    TenantSupplierAisleDto iSupplier = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(item.getTenantId(), item.getSupplierId());


                    item.setPayStatus(PayStatus.SUCCESS);
                    item.setFinishStatus(FinishStatus.SUCCESS);
                    item.setFinishDate(new Date());
                    item.setRemark("批量成功");
                    notifyService.notifySupplierSuccess(item, iSupplier.getITenantSupplier(), mapToNo.get(item.getPaymentId()));


                    if (!StringUtils.isEmpty(item.getPaymentId())) {
                        IUPayment iuPayment = new IUPayment();
                        iuPayment.setPayStatus(PayStatus.SUCCESS);
                        iuPayment.setFinishStatus(FinishStatus.SUCCESS);
                        iuPayment.setFinishTime(new Date());
                        iuPayment.setRemark("批量成功");
                        iuPayment.setSupplierNotify(true);

                        UpdateWrapper<IUPayment> iuPaymentUpdateWrapper = Wrappers.update();
                        iuPaymentUpdateWrapper.lambda().eq(IUPayment::getPaymentId, item.getPaymentId());

                        iuPaymentDao.update(iuPayment, iuPaymentUpdateWrapper);
                        iuPaymentDao.getBaseMapper().updateOtherSlowTrue(item.getOrderId(), item.getPaymentId());

                    }
                }, apiExecutor);
            });
        }
        return true;
    }


    public boolean batchError(List<Integer> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<IUSupplierOrder> iuSupplierOrders = iuSupplierOrderDao.getBaseMapper().selectBatchIds(Sets.newHashSet(ids));
            iuSupplierOrders.stream().filter(item -> !item.getPayStatus().equals(PayStatus.ING) && !item.getPayStatus().equals(PayStatus.SUCCESS)).forEach(item -> {
                CompletableFuture.runAsync(() -> {
                    TenantSupplierAisleDto iSupplier = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(item.getTenantId(), item.getSupplierId());
                    item.setPayStatus(PayStatus.BANK);
                    notifyService.notifySupplierError(item, iSupplier.getITenantSupplier());

                    UpdateWrapper<IUPayment> iuPaymentUpdateWrapper = Wrappers.update();
                    iuPaymentUpdateWrapper.lambda().eq(IUPayment::getPaymentId, item.getPaymentId());
                    iuPaymentDao.remove(iuPaymentUpdateWrapper);
                }, apiExecutor);
            });
        }
        return true;
    }

    public boolean batchErrorIng(List<Integer> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<IUSupplierOrder> iuSupplierOrders = iuSupplierOrderDao.getBaseMapper().selectBatchIds(Sets.newHashSet(ids));
            iuSupplierOrders.stream().filter(item -> !item.getPayStatus().equals(PayStatus.SUCCESS)).forEach(item -> {
                CompletableFuture.runAsync(() -> {
                    TenantSupplierAisleDto iSupplier = iTenantSupplierDao.getSupplierByTenantIdAndSupplier(item.getTenantId(), item.getSupplierId());
                    item.setPayStatus(PayStatus.BANK);
                    notifyService.notifySupplierError(item, iSupplier.getITenantSupplier());

                    UpdateWrapper<IUPayment> iuPaymentUpdateWrapper = Wrappers.update();
                    iuPaymentUpdateWrapper.lambda().eq(IUPayment::getPaymentId, item.getPaymentId());
                    iuPaymentDao.remove(iuPaymentUpdateWrapper);
                }, apiExecutor);
            });
        }
        return true;
    }
}
