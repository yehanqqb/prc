package prc.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;


@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "s_authority")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "s_authority", autoResultMap = true)
public class ISAuthority extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String authority;

    private Integer sort;

    private Boolean hide;

    private Boolean hasRole;
}
