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
@Table(name = "d_order_slow_slot")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "d_order_slow_slot", autoResultMap = true)

public class IUOrderSlowSlot extends BaseEntity {
    private Integer tenantId;

    private String paymentId;

    @Column(unique = true)
    private String supplierOrderId;

    private String paymentNo;


    private String phone;

    private Integer supplierId;

    private Boolean end;
}
