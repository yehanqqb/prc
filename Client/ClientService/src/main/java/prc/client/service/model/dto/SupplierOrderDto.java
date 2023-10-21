package prc.client.service.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Type;
import prc.service.model.dto.TreadSupplierOrderExt;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.Operator;
import prc.service.model.enumeration.PayStatus;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SupplierOrderDto {
    private Integer tenantId;

    private String orderId;

    private String productNo;

    private Integer supplierId;

    private BigDecimal money;

    private Boolean notify;

    private Operator operator;
    // 慢充
    private Boolean slow;

    private PayStatus payStatus;

    private FinishStatus finishStatus;

    // 动态变化
    private String paymentId;

    private Date startFinishTime;

    private Date endFinishTime;
}
