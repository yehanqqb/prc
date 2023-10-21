package prc.service.model.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayType {
    WECHAT(0, "微信"),
    ALIPAY(1, "支付宝"),
    YUN(2, "云闪付"),
    QQ(3, "qq");
    @JsonValue
    private final Integer id;

    private final String display;
}
