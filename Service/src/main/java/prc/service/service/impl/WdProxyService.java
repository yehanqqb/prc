package prc.service.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.exception.BizException;
import prc.service.dao.ISProxyDao;
import prc.service.model.dto.ProxyDto;
import prc.service.model.enumeration.ProxyType;
import prc.service.service.ProxyService;

@Service("WdProxyService")
public class WdProxyService implements ProxyService {
    @Autowired
    private ISProxyDao isProxyDao;


    @Override
    public ProxyDto getRandomHttp() {
        return null;
    }

    @Override
    public ProxyDto getRandomSocket() {
        return null;
    }


    public ProxyDto getWdProxyHttp(Integer tryCount) {
        if (tryCount >= 3) {
            throw new BizException("未获取豌豆代理");
        }

        String url = isProxyDao.findOneByType(ProxyType.WD).getConfig().getString("uri");
        JSONObject jop = JSON.parseObject(HttpRequest.get(url)
                .execute().body());

        if (jop.getInteger("code") == 200) {
            JSONObject data = jop.getJSONArray("data").getJSONObject(0);
            ProxyDto proxyDto = new ProxyDto();
            proxyDto.setPort(data.getString("port"));
            proxyDto.setIp(data.getString("ip"));
            return proxyDto;
        } else {
            return getWdProxyHttp(tryCount + 1);
        }
    }
}
