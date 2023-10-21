package prc.service.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentBeforeDto {
    // 用户标识
    private String userKey;

    private Integer tenantId;

    private BigDecimal money;

    private Integer provinceId;

    private String payType;
}
