package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import org.apache.commons.compress.utils.Lists;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ITenantAisleMapper;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ITenant;
import prc.service.model.entity.ITenantAisle;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ITenantAisleDao extends ServiceImpl<ITenantAisleMapper, ITenantAisle> {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISAisleDao isAisleDao;

    public Set<TenantAisleDto> findAisleByTenantId(Integer tenantId, Boolean except) {
        Set<TenantAisleDto> tenantAisleInfoData = redisCache.getCacheObject(String.format(Constants.TENANT_AISLE, tenantId));
        Set<ITenantAisle> tenantAisleInfo = Sets.newHashSet();

        if (CollectionUtils.isEmpty(tenantAisleInfoData)) {
            tenantAisleInfo = Sets.newConcurrentHashSet(baseMapper.selectList(
                    new LambdaQueryWrapper<ITenantAisle>()
                            .eq(ITenantAisle::getTenantId, tenantId)
                            .eq(except, ITenantAisle::getStatus, true)
            ));
        } else {
            return tenantAisleInfoData;
        }
        if (CollectionUtils.isEmpty(tenantAisleInfo)) {
            if (except) {
                throw new BizException("you not channel");
            } else {
                return Sets.newHashSet();
            }
        }

        tenantAisleInfoData = tenantAisleInfo.stream().map(item -> {
            TenantAisleDto dto = new TenantAisleDto();
            dto.setITenantAisle(item);
            dto.setIsAisle(isAisleDao.findById(item.getAisleId()));
            return dto;
        }).collect(Collectors.toSet());
        redisCache.setCacheObject(String.format(Constants.TENANT_AISLE, tenantId), tenantAisleInfoData, 1, TimeUnit.MINUTES);
        return tenantAisleInfoData;
    }

    @Transient
    public boolean update(Integer tenantId, List<Integer> aisleIds) {
        baseMapper.delete(new LambdaQueryWrapper<ITenantAisle>().in(ITenantAisle::getAisleId, aisleIds));
        List<ITenantAisle> save = aisleIds.stream().map(item -> {
            ISAisle aisle = isAisleDao.findById(item);
            if (Objects.nonNull(aisle)) {
                ITenantAisle tenantAisle = new ITenantAisle();
                tenantAisle.setAisleId(item);
                tenantAisle.setName(aisle.getName());
                tenantAisle.setNotProvince(Lists.newArrayList());
                tenantAisle.setStatus(true);
                return tenantAisle;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(save)) {
            super.saveBatch(save);
        }
        redisCache.deleteObject(String.format(Constants.TENANT_AISLE, tenantId));
        return true;
    }
}
