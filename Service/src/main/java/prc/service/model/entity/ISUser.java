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
@Table(name = "s_user")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "s_user", autoResultMap = true)
public class ISUser extends BaseEntity {
    @Column(unique = true)
    private String username;

    private String avatar;

    private String password;

    private Integer roleId;

    private String googleKey;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<Integer> authority;

    private Boolean status;
}
