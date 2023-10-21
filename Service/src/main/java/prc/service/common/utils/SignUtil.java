package prc.service.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;

import java.util.*;
@Slf4j
public class SignUtil<T> {
    public static String getSign(Object signObject, String secret, List<String> noSignKeys) {

        Map<String,String> signBody = JSON.parseObject(JSON.toJSONString(signObject), TreeMap.class);
        Map<String, String> sortMap = new TreeMap<>(String::compareTo);

        sortMap.putAll(signBody);
        StringBuilder buffer = new StringBuilder();
        for (String key : sortMap.keySet()) {
            if (!noSignKeys.contains(key)) {
                buffer.append(String.valueOf(signBody.get(key)));
            }
        }

        buffer.append(secret);
        log.info(buffer.toString());
        return SecureUtil.md5(buffer.toString());
    }
}
