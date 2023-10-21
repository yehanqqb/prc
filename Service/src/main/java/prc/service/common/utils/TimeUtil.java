package prc.service.common.utils;

import cn.hutool.core.util.RandomUtil;

import java.util.Date;

public class TimeUtil {
    public static String getTimeFormat(Date currentTime) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    public static String getTimeFormatNumber(Date currentTime) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime);
    }

    public static void main(String[] args) {
        System.out.println("731" + TimeUtil.getTimeFormatNumber(new Date())+ RandomUtil.randomNumbers(6));
    }
}
