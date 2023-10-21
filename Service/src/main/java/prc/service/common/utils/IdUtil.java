package prc.service.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Random;

public class IdUtil {
    // 生成单号
    public static String getId(String prefix, String suffix, int length) {
        if (StringUtils.isEmpty(prefix)) {
            prefix = "";
        }
        String time = DateUtil.dateTimeNow(DateUtil.YYYYMMDDHHMMSS);
        StringBuilder result = new StringBuilder(prefix + time);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(random.nextInt(10));
        }
        return result + suffix;
    }

    // 生成单号
    public static String getId558(String stringNo, BigInteger number) {
        return stringNo + (number.add(BigInteger.valueOf((long) 28)));
    }
}
