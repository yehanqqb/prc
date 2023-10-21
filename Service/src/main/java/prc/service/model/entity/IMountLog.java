package prc.service.model.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "m_mount_log")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "m_mount_log", autoResultMap = true)
public class IMountLog extends BaseEntity {
    private String orderId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private TYPE type;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject req;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject res;

    private String productNo;

    @AllArgsConstructor
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum TYPE {
        ONE(0, ""),
        TOW(1, ""),
        THREE(3, ""),
        FOUR(4, ""),
        FIVE(5, ""),
        SEX(6, ""),
        SEVEN(7, "");
        private final Integer id;
        private final String display;
    }
}
