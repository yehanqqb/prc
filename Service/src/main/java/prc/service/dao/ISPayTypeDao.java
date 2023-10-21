package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ISAisleMapper;
import prc.service.mapper.ISPayTypeMapper;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISPayType;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ISPayTypeDao extends ServiceImpl<ISPayTypeMapper, ISPayType> {
    @Autowired
    private RedisCache redisCache;

    public ISPayType findByKey(String payKey) {
        ISPayType isPayType = redisCache.getCacheObject(String.format(Constants.PAY_TYPE, payKey));
        if (Objects.isNull(isPayType)) {
            isPayType = baseMapper.selectOne(new LambdaQueryWrapper<ISPayType>().eq(ISPayType::getPayKey, payKey));
            redisCache.setCacheObject(String.format(Constants.PAY_TYPE, payKey), isPayType, 1, TimeUnit.MINUTES);
        }
        return isPayType;
    }
}
