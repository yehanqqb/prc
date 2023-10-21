package prc.timing.timer.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import prc.service.dao.ITenantSupplierDao;
import prc.service.dao.IUOrderErrorNotifyDao;
import prc.service.dao.IUSupplierOrderDao;
import prc.service.model.entity.ITenantSupplier;
import prc.service.model.entity.IUOrderErrorNotify;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.service.NotifyService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@Slf4j
public class NotifyErrorTask {
    @Autowired
    private IUOrderErrorNotifyDao iuOrderErrorNotifyDao;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;

    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void notifySupplier() {
        List<IUOrderErrorNotify> iuOrderErrorNotifies = iuOrderErrorNotifyDao.getBaseMapper().selectList(new LambdaQueryWrapper<IUOrderErrorNotify>().eq(IUOrderErrorNotify::isEnd, false));

        Set<String> orderIds = iuOrderErrorNotifies.stream().map(IUOrderErrorNotify::getOrderId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(orderIds)) {
            return;
        }

        List<IUSupplierOrder> supplierOrders = iuSupplierOrderDao.getBaseMapper().selectList(
                new LambdaQueryWrapper<IUSupplierOrder>()
                        .in(IUSupplierOrder::getOrderId, orderIds)
        );


        if (CollectionUtils.isEmpty(supplierOrders)) {
            return;
        }

        Map<String, IUSupplierOrder> supplierOrderByIdMap =
                supplierOrders.stream().collect(Collectors.toMap(IUSupplierOrder::getOrderId, fs -> fs));

        Set<Integer> supplierIds =
                supplierOrders.stream().map(IUSupplierOrder::getSupplierId).collect(Collectors.toSet());


        Map<Integer, ITenantSupplier> supplierIdMap = iTenantSupplierDao.getBaseMapper()
                .selectList(new LambdaQueryWrapper<ITenantSupplier>().in(!CollectionUtils.isEmpty(supplierIds), ITenantSupplier::getId, supplierIds))
                .stream().collect(Collectors.toMap(ITenantSupplier::getId, fs -> fs));

        Set<String> updateOrderIds = orderIds.stream().map(item -> {
            IUSupplierOrder order = supplierOrderByIdMap.get(item);
            if (Objects.isNull(order)) {
                return null;
            }
            try {
                if (notifyService.notifySupplierError(order, supplierIdMap.get(order.getSupplierId())).get()) {
                    return order.getOrderId();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        IUOrderErrorNotify update = new IUOrderErrorNotify();
        update.setEnd(true);
        UpdateWrapper<IUOrderErrorNotify> updateWrapper = Wrappers.update();
        updateWrapper.lambda().in(!CollectionUtils.isEmpty(updateOrderIds), IUOrderErrorNotify::getOrderId, updateOrderIds);

        iuOrderErrorNotifyDao.getBaseMapper().update(update, updateWrapper);
    }
}
