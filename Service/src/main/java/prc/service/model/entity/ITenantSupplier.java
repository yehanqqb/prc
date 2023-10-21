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
import java.math.BigDecimal;
import java.util.Set;

/**
 * 租户供货商
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_supplier")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "t_supplier", autoResultMap = true)

public class ITenantSupplier extends BaseEntity {
    private Integer tenantId;

    private String secret;

    private Boolean status;

    private String name;

    private Integer userId;

    private String username;

    private String produceIps;

    private Long maxCount;

    Boolean repetition;

    Integer repetitionCount;

    Boolean repetitionNo;
}
