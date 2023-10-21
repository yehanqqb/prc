package prc.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.common.constant.ConstantsDb;
import prc.service.common.exception.BizException;
import prc.service.config.RedisCache;
import prc.service.mapper.ISDictMapper;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISDict;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ISDictDao extends ServiceImpl<ISDictMapper, ISDict> {
    @Autowired
    private RedisCache redisCache;

    public String getCashierUrl(String tradeId) {
        String cashierUrl = redisCache.getCacheObject(Constants.CASHIER_URL);
        if (StringUtils.isBlank(cashierUrl)) {
            cashierUrl = baseMapper.selectOne(new LambdaQueryWrapper<ISDict>().eq(ISDict::getMajorKey, ConstantsDb.CASHIER_KEY)).getVal();
            redisCache.setCacheObject(Constants.CASHIER_URL, cashierUrl, 2, TimeUnit.MINUTES);
        }
        return String.format(cashierUrl, tradeId);
    }

    public String getByKey(String key) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ISDict>().eq(ISDict::getMajorKey, key)).getVal();

    }

    public List<Integer> getMoneyList() {
        String[] dicky = baseMapper.selectOne(new LambdaQueryWrapper<ISDict>().eq(ISDict::getMajorKey, ConstantsDb.MONEY_KEY)).getVal().split(",");
        return Arrays.stream(dicky).map(Integer::parseInt).collect(Collectors.toList());
    }
}
