package prc.client.service.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Type;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentOrderDto {
    @Column(unique = true)
    private String paymentId;

    private String merchantOrderId;

    private String supplierOrderId;

    private String paymentNo;

    private String productNo;

    private BigDecimal money;

    private PayStatus payStatus;

    private FinishStatus finishStatus;

    private Integer supplierId;

    private Integer merchantId;

    private Operator operator;

    // 代理ip
    private String proxyIp;

    // 通道Id
    private Integer aisleId;

    // 租户
    private Integer tenantId;

    private Boolean slow;

    private Boolean supplierNotify;

    private Boolean merchantNotify;

    private String payType;

    private Boolean primary;

    private Date startFinishTime;

    private Date endFinishTime;

    private Date startPayTime;

    private Date endPayTime;
}
