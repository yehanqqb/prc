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
public class ProvinceProxyWechatImpl implements ProvinceProxy {
    private Proxy proxy;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProvinceNameCodeDto getProvinceByNo(String No) {
        ProvinceNameCodeDto provinceNameCodeDto = new ProvinceNameCodeDto();
        String operator = HttpRequestUtil.sendHttpsBody("https://chong.qq.com/tws/mobileqqprequery/GetMobileProductInfo?loginstate=1&vb2ctag=4_2038_1_1425_73&groupid=2038&productype=3&mobile=" + No + "&amount=3000%235000%2310000%2320000%2330000%2350000&appId=wx47031447c8352579&code=&_=1668528823536&virWx=1&lwxx=1&g_tk=5381", new HashMap<>(), proxy);
        log.info("Wechat:" + operator);

        JSONObject operatorJSON = JSON.parseObject(operator);
        provinceNameCodeDto.setProvinceName(operatorJSON.getString("province"));
        provinceNameCodeDto.setProvinceId(new ConstantsCodeCity().getNameProvince().get(provinceNameCodeDto.getProvinceName()));
        provinceNameCodeDto.setCity(operatorJSON.getJSONObject("data").getString("city"));
        String oper = operatorJSON.getString("isd");
        if (oper.contains("移动")) {
            provinceNameCodeDto.setOperator(Operator.MOBILE);
        } else if (oper.contains("联通")) {
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
