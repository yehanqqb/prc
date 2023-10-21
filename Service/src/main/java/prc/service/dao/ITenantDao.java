package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ITenantMapper;
import prc.service.model.entity.ITenant;
import prc.service.model.entity.ITenantAisle;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ITenantDao extends ServiceImpl<ITenantMapper, ITenant> {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ITenantAisleDao iTenantAisleDao;


    public ITenant findByIdAndExist(Integer tenantId) {
        ITenant tenantInfo = redisCache.getCacheObject(String.format(Constants.TENANT, tenantId));
        if (Objects.isNull(tenantInfo)) {
            tenantInfo = baseMapper.selectById(tenantId);
            redisCache.setCacheObject(String.format(Constants.TENANT, tenantId), tenantInfo, 2, TimeUnit.MINUTES);
        }

        if (Objects.isNull(tenantInfo) || tenantInfo.getStatus().equals(Boolean.FALSE)) {
            throw new BizException("tenant is closed");
        }

        return tenantInfo;
    }
}
