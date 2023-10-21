package prc.service.model.entity;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prc.service.model.enumeration.PayStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "d_merchant_order")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "d_merchant_order", autoResultMap = true)
public class IUMerchantOrder extends BaseEntity {
    private Integer tenantId;

    private Integer merchantId;

    @Column(unique = true)
    private String orderId;

    private BigDecimal money;

    // 类型
    private String payType;

    // 支付状态
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private PayStatus payStatus;

    private String payUrl;

    private Boolean notify;

    private String notifyUrl;

    private String paymentId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject notifyJson;

    @Column
    private String remark;
}
