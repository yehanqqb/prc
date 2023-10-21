package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ITenantSupplierMapper;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.dto.TenantAisleSupplierDto;
import prc.service.model.dto.TenantSupplierAisleDto;
import prc.service.model.entity.ITenantAisleSupplier;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.ITenantSupplier;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ITenantSupplierDao extends ServiceImpl<ITenantSupplierMapper, ITenantSupplier> {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ITenantAisleDao iTenantAisleDao;
    @Autowired
    private ITenantAisleSupplierDao iTenantAisleSupplierDao;


    public TenantSupplierAisleDto getSupplierByTenantIdAndSupplier(Integer tenantId, Integer supplierId) {
        ITenantSupplier supplierInfo = getSupplierById(supplierId);
        List<ITenantAisleSupplier> supplierAisle = iTenantAisleSupplierDao.getBaseMapper().selectList(new LambdaQueryWrapper<ITenantAisleSupplier>()
                .eq(ITenantAisleSupplier::getTenantId, tenantId)
                .eq(ITenantAisleSupplier::getSupplierId, supplierInfo.getId())
        );

        Set<Integer> supplierAisleIds = supplierAisle.stream().map(ITenantAisleSupplier::getAisleId).collect(Collectors.toSet());

        Set<TenantAisleDto> tenantAisleDtos = iTenantAisleDao.findAisleByTenantId(tenantId, false)
                .stream()
                .filter(item -> supplierAisleIds.contains(item.getITenantAisle().getAisleId()))
                .collect(Collectors.toSet());
        TenantSupplierAisleDto tenantAisleDtosNew = new TenantSupplierAisleDto();
        tenantAisleDtosNew.setITenantSupplier(supplierInfo);
        tenantAisleDtosNew.setTenantAisleDto(tenantAisleDtos);
        return tenantAisleDtosNew;
    }

    public List<TenantSupplierAisleDto> getSupplierByTenantIdAndAisleId(Integer tenantId, Integer aisleId) {
        Set<TenantSupplierAisleDto> iTenantSuppliers = baseMapper.selectList(new LambdaQueryWrapper<ITenantSupplier>()
                .eq(ITenantSupplier::getTenantId, tenantId)).stream().map(item -> getSupplierByTenantIdAndSupplier(tenantId, item.getId())).collect(Collectors.toSet());

        List<ITenantAisleSupplier> supplierAisle = iTenantAisleSupplierDao.getBaseMapper().selectList(new LambdaQueryWrapper<ITenantAisleSupplier>()
                .eq(ITenantAisleSupplier::getTenantId, tenantId)
                .eq(ITenantAisleSupplier::getAisleId, aisleId)
        );

        Set<Integer> supplierIds = supplierAisle.stream().map(ITenantAisleSupplier::getSupplierId).collect(Collectors.toSet());

        return iTenantSuppliers.stream().filter(item -> supplierIds.contains(item.getITenantSupplier().getId())).collect(Collectors.toList());
    }

    public TenantSupplierAisleDto getSupplierByTenantIdAndAisleId(Integer tenantId, Integer aisleId, Integer supplierId) {
        return getSupplierByTenantIdAndAisleId(tenantId, aisleId).stream().filter(item -> item.getITenantSupplier().getId().equals(supplierId)).findFirst().get();
    }


    public ITenantSupplier getSupplierById(Integer supplierId) {
        ITenantSupplier supplierInfo = redisCache.getCacheObject(String.format(Constants.SUPPLIER, supplierId));
        if (Objects.isNull(supplierInfo)) {
            supplierInfo = baseMapper.selectById(supplierId);
        }
        if (Objects.isNull(supplierInfo)) {
            throw new BizException("supplier not exist");
        }
        redisCache.setCacheObject(String.format(Constants.SUPPLIER, supplierId), supplierInfo, 5, TimeUnit.MINUTES);
        return supplierInfo;
    }

    public List<ITenantSupplier> getSupplierByTenantId(Integer tenantId) {
        List<ITenantSupplier> supplierInfo = redisCache.getCacheObject(String.format(Constants.TENANT_SUPPLIER, tenantId));
        if (Objects.isNull(supplierInfo)) {
            supplierInfo = baseMapper.selectList(new LambdaQueryWrapper<ITenantSupplier>().eq(ITenantSupplier::getTenantId, tenantId));
        }
        if (Objects.isNull(supplierInfo)) {
            return Lists.newArrayList();
        }
        redisCache.setCacheObject(String.format(Constants.TENANT_SUPPLIER, tenantId), supplierInfo, 2, TimeUnit.MINUTES);
        return supplierInfo;
    }

    public Set<TenantAisleSupplierDto> getSupplierAisleByTenantIdAndAisleId(Integer tenantId, Integer aisleId) {
        List<ITenantAisleSupplier> supplierAisle = iTenantAisleSupplierDao.getBaseMapper().selectList(new LambdaQueryWrapper<ITenantAisleSupplier>()
                .eq(ITenantAisleSupplier::getTenantId, tenantId)
                .eq(ITenantAisleSupplier::getAisleId, aisleId)
        );

        return supplierAisle.stream().map(item -> {
            TenantAisleSupplierDto dto = new TenantAisleSupplierDto();
            dto.setITenantAisleSupplier(item);
            dto.setITenantSupplier(getSupplierById(item.getSupplierId()));
            return dto;
        }).collect(Collectors.toSet());
    }
}
