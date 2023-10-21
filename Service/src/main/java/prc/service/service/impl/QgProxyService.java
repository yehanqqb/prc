package prc.service.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.utils.DateUtil;
import prc.service.config.RedisCache;
import prc.service.dao.ISProxyDao;
import prc.service.model.dto.ProxyDto;
import prc.service.model.enumeration.ProxyType;
import prc.service.service.ProxyService;

import java.util.Date;
import java.util.Objects;

@Service("QgProxyService")
@Slf4j
public class QgProxyService implements ProxyService {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISProxyDao isProxyDao;

    @Override
    public ProxyDto getRandomHttp() {
        return getProxyHttp(0, 3);
    }

    @Override
    public ProxyDto getRandomSocket() {
        return null;
    }


    public ProxyDto getProxyHttp(Integer tryCount, Integer maxCount) {
        try {
            if (tryCount >= maxCount) {
                return null;
            }
            JSONObject proxyData = (JSONObject) redisCache.rPop(ProxyType.QG.getReKey());

            if (Objects.nonNull(proxyData) && new Date().getTime() >= DateUtil.dateTime(DateUtil.YYYY_MM_DD_HH_MM_SS, proxyData.getString("deadline")).getTime()) {
                return getProxyHttp(tryCount, maxCount);
            }


            if (Objects.nonNull(proxyData)) {
                log.info("取到缓存代理");
                ProxyDto proxyDto = new ProxyDto();
                proxyDto.setPort(proxyData.getString("port"));
                proxyDto.setIp(proxyData.getString("IP"));
                return proxyDto;
            }

            String url = isProxyDao.findOneByType(ProxyType.QG).getConfig().getString("uri");

            JSONObject jop = JSON.parseObject(HttpRequest.get(url)
                    .execute().body());

            if (jop.getInteger("Code") == 0) {

                jop.getJSONArray("Data").forEach(item -> {
                    JSONObject itData = (JSONObject) item;
                    redisCache.lPush(ProxyType.QG.getReKey(), itData);
                });

                JSONObject data = (JSONObject) redisCache.rPop(ProxyType.QG.getReKey());
                ProxyDto proxyDto = new ProxyDto();
                proxyDto.setPort(data.getString("port"));
                proxyDto.setIp(data.getString("IP"));
                return proxyDto;

            } else {

                return getProxyHttp(tryCount + 1, maxCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("qg proxy error", e);
            return null;
        }
    }
}
