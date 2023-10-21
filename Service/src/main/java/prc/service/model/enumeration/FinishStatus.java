package prc.service.model.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinishStatus {
    WAIT(0, "等待中"),
    SUCCESS(1, "已到账"),
    ERROR(2, "返销"),
    BANK(3, "退单");
    @JsonValue
    private final Integer id;
    @EnumValue
    private final String display;
}
