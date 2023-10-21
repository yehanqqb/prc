package prc.service.proxy.excute.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import prc.service.common.constant.ConstantsCodeCity;
import prc.service.common.utils.HttpRequestUtil;
import prc.service.model.dto.ProvinceNameCodeDto;
import prc.service.model.enumeration.Operator;
import prc.service.proxy.excute.ProvinceProxy;

import java.net.Proxy;
import java.util.HashMap;
@Slf4j
public class ProvinceProxyJdImpl implements ProvinceProxy {
    private Proxy proxy;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProvinceNameCodeDto getProvinceByNo(String No) {
        ProvinceNameCodeDto provinceNameCodeDto = new ProvinceNameCodeDto();
        String zw = HttpRequest.post("https://newcz.m.jd.com/newcz/product.json")
                .body("mobile=" + No).timeout(200000).execute().body();
        log.info("zw:"+zw);
        JSONObject zwJSON = JSON.parseObject(zw);

        provinceNameCodeDto.setProvinceName(zwJSON.getJSONObject("skuPrice").getString("areaName"));
        provinceNameCodeDto.setProvinceId(new ConstantsCodeCity().getNameProvince().get(provinceNameCodeDto.getProvinceName()));
        provinceNameCodeDto.setCity(zwJSON.getJSONObject("skuPrice").getString("areaName"));

        if (zwJSON.getJSONObject("skuPrice").getString("providerName").equals(Operator.MOBILE.getDisplay())) {
            provinceNameCodeDto.setOperator(Operator.MOBILE);
        } else if (zwJSON.getJSONObject("skuPrice").getString("providerName").equals(Operator.UNI.getDisplay())) {
            provinceNameCodeDto.setOperator(Operator.UNI);
        } else {
            provinceNameCodeDto.setOperator(Operator.TELECOM);
        }
        return provinceNameCodeDto;
    }

    public static void main(String[] args) {
        System.out.println(new ProvinceProxyJdImpl().getProvinceByNo("15698963369"));
    }
}
