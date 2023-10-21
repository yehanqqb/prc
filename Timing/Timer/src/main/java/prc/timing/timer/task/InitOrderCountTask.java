package prc.timing.timer.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import prc.service.common.constant.Constants;
import prc.service.common.constant.ConstantsCodeCity;
import prc.service.config.RedisCache;
import prc.service.dao.ISDictDao;
import prc.service.dao.ITenantSupplierDao;
import prc.service.model.dto.ProvinceCountCacheDto;
import prc.service.model.entity.ITenantSupplier;
import prc.service.model.enumeration.Operator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
@Slf4j
public class InitOrderCountTask {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISDictDao isDictDao;

    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;


    @Scheduled(fixedRate = 1000)
    public void exec() {
        List<ITenantSupplier> suppliers = iTenantSupplierDao.getBaseMapper().selectList(new LambdaQueryWrapper<ITenantSupplier>()
                .eq(ITenantSupplier::getStatus, true));

        suppliers.forEach(item -> redisCache.setCacheObject(String.format(Constants.SUPPLIER_ORDER_COUNT, item.getId()), initItem(item)));
    }

    private Integer initItem(ITenantSupplier supplier) {
        isDictDao.getMoneyList().forEach(item->slow(true, supplier, item));
        return isDictDao.getMoneyList().stream().map(item -> slow(false, supplier, item)).mapToInt(Integer::intValue).sum();
    }

    private Integer slow(boolean slow, ITenantSupplier supplier, Integer money) {
        AtomicInteger count = new AtomicInteger(0);
        Arrays.stream(Operator.values()).forEach(operator -> {
            List<ProvinceCountCacheDto> provinceCaches = Lists.newArrayList();
            String provinceKey = null;
            if (slow) {
                provinceKey = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, supplier.getTenantId(), supplier.getId(), operator.getKey(), money);
            } else {
                provinceKey = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_NUMBER, supplier.getTenantId(), supplier.getId(), operator.getKey(), money);
            }
            new ConstantsCodeCity().getProvinceName().keySet().forEach(item2 -> {
                String key = null;
                if (slow) {
                    key = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, supplier.getTenantId(), supplier.getId(), operator.getKey(), money, item2);
                } else {
                    key = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, supplier.getTenantId(), supplier.getId(), operator.getKey(), money, item2);
                }
                ProvinceCountCacheDto paymentCacheDto = new ProvinceCountCacheDto();
                paymentCacheDto.setProvinceId(item2);
                paymentCacheDto.setNumber(redisCache.size(key).intValue());
                provinceCaches.add(paymentCacheDto);
                count.getAndAdd(paymentCacheDto.getNumber());
            });
            redisCache.setCacheObject(provinceKey, provinceCaches);
        });
        return count.get();
    }
}
