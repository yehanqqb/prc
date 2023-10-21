package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.IMountLogMapper;
import prc.service.mapper.SDTaoAccountMapper;
import prc.service.model.entity.IMountLog;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.SDTaoAccount;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SDTaoAccountDao extends ServiceImpl<SDTaoAccountMapper, SDTaoAccount> {
    @Autowired
    private RedisCache redisCache;

    public SDTaoAccount getInit() {
        SDTaoAccount sdTaoAccount = (SDTaoAccount) redisCache.rPop(Constants.TAO_INIT);
        if (Objects.isNull(sdTaoAccount)) {
            List<SDTaoAccount> list = baseMapper.selectList(new LambdaQueryWrapper<SDTaoAccount>().eq(SDTaoAccount::getInit, true).eq(SDTaoAccount::getStatus, true));
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            redisCache.setCacheList(Constants.TAO_INIT, list);
            sdTaoAccount = (SDTaoAccount) redisCache.rPop(Constants.TAO_INIT);
        }
        return sdTaoAccount;
    }

    public SDTaoAccount getUse() {
        SDTaoAccount sdTaoAccount = (SDTaoAccount) redisCache.rPop(Constants.TAO_USE);
        if (Objects.isNull(sdTaoAccount)) {
            List<SDTaoAccount> list = baseMapper.selectList(new LambdaQueryWrapper<SDTaoAccount>().eq(SDTaoAccount::getInit, false).eq(SDTaoAccount::getStatus, true));
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            redisCache.setCacheList(Constants.TAO_USE, list);
            sdTaoAccount = (SDTaoAccount) redisCache.rPop(Constants.TAO_USE);
        }
        return sdTaoAccount;
    }
}
