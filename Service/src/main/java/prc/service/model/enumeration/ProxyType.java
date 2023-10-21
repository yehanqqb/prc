package prc.service.model.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProxyType {
    WD(1, "WdProxyService", "豌豆","PROXY:WD"),
    QG(2, "QgProxyService", "青果分省","PROXY:QG");

    @JsonValue
    private final Integer id;

    private final String name;

    private final String display;

    private final String reKey;
}
