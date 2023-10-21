package prc.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 租户商户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_merchant")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "t_merchant", autoResultMap = true)

public class ITenantMerchant extends BaseEntity {
    private Integer tenantId;

    private String secret;

    private Boolean status;

    private String name;

    private Integer userId;

    private String whiteIp;
}
