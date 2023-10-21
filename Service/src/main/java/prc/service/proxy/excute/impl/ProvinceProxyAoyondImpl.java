package prc.service.proxy.excute.impl;

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
public class ProvinceProxyAoyondImpl implements ProvinceProxy {
    private Proxy proxy;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProvinceNameCodeDto getProvinceByNo(String No) {
        ProvinceNameCodeDto provinceNameCodeDto = new ProvinceNameCodeDto();
        String operator = HttpRequestUtil.sendHttpsBody("https://h5.aoyond.cn/api/credit/rule?phone=" + No, new HashMap<>(), proxy);
        log.info("Aoyond:"+operator);
        JSONObject operatorJSON = JSON.parseObject(operator);
        provinceNameCodeDto.setProvinceName(operatorJSON.getJSONObject("data").getString("province"));
        provinceNameCodeDto.setProvinceId(new ConstantsCodeCity().getNameProvince().get(provinceNameCodeDto.getProvinceName()));
        provinceNameCodeDto.setCity(operatorJSON.getJSONObject("data").getString("city"));
        int oper = operatorJSON.getJSONObject("data").getInteger("operator");
        if (oper == 1) {
            provinceNameCodeDto.setOperator(Operator.MOBILE);
        } else if (oper == 2) {
            provinceNameCodeDto.setOperator(Operator.UNI);
        } else {
            provinceNameCodeDto.setOperator(Operator.TELECOM);
        }
        return provinceNameCodeDto;
    }

    public static void main(String[] args) {
        System.out.println(new ProvinceProxyAoyondImpl().getProvinceByNo("15698963369"));
    }
}
