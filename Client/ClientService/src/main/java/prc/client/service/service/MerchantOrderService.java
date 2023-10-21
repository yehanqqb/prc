package prc.client.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.client.service.model.dto.MerchantOrderDto;
import prc.client.service.model.dto.SupplierOrderDto;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.dao.ITenantMerchantDao;
import prc.service.dao.IUMerchantOrderDao;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.enumeration.PayStatus;
import prc.service.service.NotifyService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class MerchantOrderService {
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private ITenantMerchantDao iTenantMerchantDao;
    @Autowired
    private ThreadPoolTaskExecutor apiExecutor;


    public PageRes<IUMerchantOrder> page(PageReq<MerchantOrderDto> pageReq) {
        PageReq<IUMerchantOrder> pagePes = new PageReq<IUMerchantOrder>();
        BeanUtil.copyBeanProp(pagePes, pageReq);
        pagePes.setCommon(BeanUtil.convertToBean(pageReq.getCommon(), IUMerchantOrder.class));
        LambdaQueryWrapper<IUMerchantOrder> query = pagePes.createMapper().lambda();

        Page<IUMerchantOrder> pageRes = iuMerchantOrderDao.getBaseMapper().selectPage(pagePes.createPage(), query);
        return new PageRes<IUMerchantOrder>().trans(pageRes);
    }

    public Map<String, Object> getTitle(PageReq<MerchantOrderDto> pageReq) {
        PageReq<IUMerchantOrder> pagePes = new PageReq<IUMerchantOrder>();
        BeanUtil.copyBeanProp(pagePes, pageReq);
        pagePes.setCommon(BeanUtil.convertToBean(pageReq.getCommon(), IUMerchantOrder.class));
        return iuMerchantOrderDao.getMap(pagePes.createTitle(pagePes.createMapper()));
    }

    public boolean reissue(List<Integer> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<IUMerchantOrder> iuMerchantOrders = iuMerchantOrderDao.getBaseMapper().selectBatchIds(Sets.newHashSet(ids));
            iuMerchantOrders.forEach(item -> {
                CompletableFuture.runAsync(() -> {
                    ITenantMerchant iMerchant = iTenantMerchantDao.getMerchantByTenantIdAndMerchant(item.getTenantId(), item.getMerchantId());
                    item.setPayStatus(PayStatus.SUCCESS);
                    item.setRemark("批量成功");
                    notifyService.notifyMerchantSuccess(item, iMerchant);
                }, apiExecutor);
            });
        }
        return true;
    }
}
