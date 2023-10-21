package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.config.RedisCache;
import prc.service.mapper.ISProxyMapper;
import prc.service.model.entity.ISProxy;
import prc.service.model.enumeration.ProxyType;

import java.net.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
public class ISProxyDao extends ServiceImpl<ISProxyMapper, ISProxy> {
    @Autowired
    private RedisCache redisCache;

    public ISProxy findOneByType(ProxyType proxyType) {
        List<ISProxy> sysProxies = redisCache.getCacheObject(Constants.PROXY_KEY);
        Random random = new Random();
        if (Objects.isNull(sysProxies)) {
            sysProxies = baseMapper.selectList(
                    new LambdaQueryWrapper<ISProxy>()
                            .eq(ISProxy::getStatus, true)
                            .eq(ISProxy::getType, proxyType)
            );
            redisCache.setCacheObject(Constants.PROXY_KEY, sysProxies, 2, TimeUnit.MINUTES);
        }
        return sysProxies.get(random.nextInt(sysProxies.size()));
    }
}
