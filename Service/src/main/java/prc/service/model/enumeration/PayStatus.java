package prc.service.model.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayStatus {
    WAIT(0, "等待中"),
    ING(1, "支付中"),
    SUCCESS(2, "支付成功"),
    ERROR(3, "支付失败"),
    CREATE_ERROR(4, "产码失败"),
    BANK(5, "批量失败"),
    NOTIFY_ERROR(6, "通知失败"),
    CREATE_ING(7, "产码中"),
    TIME_OUT(8, "超时"),
    NOT_PAY(9, "不可付");
    @JsonValue
    private final Integer id;
    @EnumValue
    private final String display;
}
