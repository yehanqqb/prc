package prc.service.common.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class ReqTitle {
    private String name;

    private Object value;

    private TYPE type;

    private String other;

    private String asName;

    @AllArgsConstructor
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum TYPE {
        COUNT(0, "数量"),
        SUM(1, "相加");
        private final Integer id;
        private final String display;
    }

}
