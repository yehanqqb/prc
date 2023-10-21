package prc.service.model.entity;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prc.service.model.dto.TreadSupplierOrderExt;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 租户供货商订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "d_supplier_order")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "d_supplier_order", autoResultMap = true)
public class IUSupplierOrder extends BaseEntity {
    private Integer tenantId;

    @Column(unique = true)
    private String orderId;

    private String productNo;

    private Integer supplierId;

    private BigDecimal money;

    private Boolean notify;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject notifyJson;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Operator operator;

    // 省份id
    private Integer provinceId;

    private String provinceName;

    private String notifyUrl;

    // 慢充
    private Boolean slow;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private PayStatus payStatus;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private FinishStatus finishStatus;

    // 动态变化
    private String paymentId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private TreadSupplierOrderExt ext;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    private Date finishDate;

    private String remark;
}
