package prc.client.service.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.hibernate.annotations.Type;
import prc.service.model.enumeration.PayStatus;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MerchantOrderDto {
    private Integer tenantId;

    private Integer merchantId;

    private String orderId;

    private BigDecimal money;

    private String payType;

    // 支付状态
    private PayStatus payStatus;

    private Boolean notify;

    private String paymentId;

    private Date startTime;

    private Date endTime;
}
