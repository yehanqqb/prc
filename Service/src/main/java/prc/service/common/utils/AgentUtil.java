package prc.service.common.utils;

public class AgentUtil {
    public static String getAgent(String userAgent) {
        if (userAgent.contains("iPad")) {
            // 是iPad
            return "iPad";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone")) {
            return "iPhone";
        } else if (userAgent.contains("window")) {
            return "window";
        } else {
            return "未知";
        }
    }
}
