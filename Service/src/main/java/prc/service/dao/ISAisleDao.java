package prc.service.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ISAisleMapper;

import prc.service.model.entity.ISAisle;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ISAisleDao extends ServiceImpl<ISAisleMapper, ISAisle> {
    @Autowired
    private RedisCache redisCache;

    public ISAisle findById(Integer aisleId) {
        ISAisle aisleInfo = redisCache.getCacheObject(String.format(Constants.AISLE, aisleId));
        if (Objects.isNull(aisleInfo)) {
            aisleInfo = baseMapper.selectById(aisleId);
            redisCache.setCacheObject(String.format(Constants.AISLE, aisleId), aisleInfo,2, TimeUnit.MINUTES);
        }
        if (Objects.isNull(aisleInfo) || aisleInfo.getStatus().equals(Boolean.FALSE)) {
            return null;
        }
        return aisleInfo;
    }
}
