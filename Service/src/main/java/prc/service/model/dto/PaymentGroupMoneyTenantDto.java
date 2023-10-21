package prc.service.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentGroupMoneyTenantDto {
    private Integer tenantId;

    private String name;

    private BigDecimal finishMoney;

    private Integer finishCount;

    private BigDecimal payMoney;

    private Integer payCount;

    private BigDecimal bankMoney;

    private Integer bankCount;
}
