package prc.service.common.utils;

public class RegexUtil {
    public static String regexExist(String content, String key, String endingStr) {
        if (content == null || content.equals("")) {
            return "";
        }
        int weiIndex = content.indexOf(key);
        return content.substring(weiIndex,
                content.indexOf(endingStr, weiIndex));
    }
}
