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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "d_payment")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "d_payment", autoResultMap = true)
public class IUPayment extends BaseEntity {
    @Column(unique = true)
    private String paymentId;

    private String merchantOrderId;

    private String supplierOrderId;

    private String paymentNo;

    private String productNo;

    private BigDecimal money;

    private String payUrl;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject queryJson;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject extJson;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private PayStatus payStatus;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private FinishStatus finishStatus;

    @Column
    private String remark;

    private String userIp;

    private String userKey;

    // 拉单等待时间 接口的时间
    private Long wait;

    // 拉单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    // 到账时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    // 监控时间
    private Long monitor;

    private Integer supplierId;

    private Integer merchantId;
    // 供货商
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Operator operator;

    // 代理ip
    private String proxyIp;

    // 通道Id
    private Integer aisleId;

    // 租户
    private Integer tenantId;

    // 省份id
    private Integer provinceId;

    private String provinceName;

    private String payBean;
    // 监控的bean
    private String monitorBean;

    @Column
    private Boolean slow;

    private Boolean supplierNotify;

    private Boolean merchantNotify;

    @Column
    private String agent;

    private String payType;

    @Column(name = "`primary`")
    @TableField(value = "`primary`")
    private Boolean primary;
}
