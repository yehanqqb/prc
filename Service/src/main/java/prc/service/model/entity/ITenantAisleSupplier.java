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
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_aisle_supplier")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "t_aisle_supplier", autoResultMap = true)
public class ITenantAisleSupplier extends BaseEntity {
    private Integer tenantId;

    private Integer aisleId;

    private Integer supplierId;

    // 拉取比例
    private BigDecimal radio;

    private Boolean slow;
}
