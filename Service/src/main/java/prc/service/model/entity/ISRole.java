package prc.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "s_role")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "s_role", autoResultMap = true)
public class ISRole extends BaseEntity {
    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String roleKey;

    private Integer sort;

    private Boolean status;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<Integer> authority;
}
