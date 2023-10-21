package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ITenantMerchantMapper;
import prc.service.model.entity.ITenantMerchant;

import java.sql.Time;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ITenantMerchantDao extends ServiceImpl<ITenantMerchantMapper, ITenantMerchant> {
    @Autowired
    private RedisCache redisCache;


    public ITenantMerchant getMerchantByTenantIdAndMerchant(Integer tenantId, Integer merchantId) {
        ITenantMerchant merchantInfo = redisCache.getCacheObject(String.format(Constants.MERCHANT, merchantId));
        if (Objects.isNull(merchantInfo)) {
            merchantInfo = baseMapper.selectById(merchantId);
        }
        if (Objects.isNull(merchantInfo) || !merchantInfo.getTenantId().equals(tenantId)) {
            throw new BizException("merchant not exist");
        }
        redisCache.setCacheObject(String.format(Constants.MERCHANT, merchantId), merchantInfo,5, TimeUnit.MINUTES);
        return merchantInfo;
    }
}
