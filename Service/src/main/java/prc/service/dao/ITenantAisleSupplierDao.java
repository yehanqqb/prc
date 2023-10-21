package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.config.RedisCache;
import prc.service.mapper.ITenantAisleSupplierMapper;
import prc.service.model.entity.ITenantAisleSupplier;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ITenantAisleSupplierDao extends ServiceImpl<ITenantAisleSupplierMapper, ITenantAisleSupplier> {
    @Autowired
    private RedisCache redisCache;

    public ITenantAisleSupplier findByTenantIdAndSupplierId(Integer tenantId, Integer supplierId, Integer aisleId) {
        ITenantAisleSupplier iTenantAisleSupplier = redisCache.getCacheObject(String.format(Constants.TENANT_SUPPLIER_AISLE, tenantId, supplierId, aisleId));
        if (Objects.isNull(iTenantAisleSupplier)) {
            iTenantAisleSupplier = getBaseMapper().selectOne(new LambdaQueryWrapper<ITenantAisleSupplier>()
                    .eq(ITenantAisleSupplier::getTenantId, tenantId)
                    .eq(ITenantAisleSupplier::getAisleId, aisleId)
                    .eq(ITenantAisleSupplier::getSupplierId, supplierId)
            );
            redisCache.setCacheObject(String.format(Constants.TENANT_SUPPLIER_AISLE, tenantId, supplierId, aisleId), iTenantAisleSupplier, 2, TimeUnit.MINUTES);
        }
        return iTenantAisleSupplier;
    }
}

