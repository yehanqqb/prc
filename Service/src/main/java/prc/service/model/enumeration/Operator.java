package prc.service.model.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 运营商
 */
@AllArgsConstructor
@Getter
public enum Operator {
    TELECOM(0, "DX", "电信"),
    UNI(1, "LT", "联通"),
    MOBILE(2, "YD", "移动"),
    OTHER(3, "OTHER", "自由金额");
    @JsonValue
    private final Integer id;

    private final String key;

    @EnumValue
    private final String display;

    public static Operator getByCode(String code) {
        for (Operator enums : Operator.values()) {
            if (enums.getKey().equals(code)) {
                return enums;
            }
        }
        return null;
    }
}
