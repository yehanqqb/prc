package prc.api.service.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PayMerchantDto {
    @NotNull(message = "order is not null")
    private String orderId;

    @NotNull(message = "money is not null")
    private Integer money;

    @NotNull(message = "sign is not null")
    private String sign;

    @NotNull(message = "notifyUrl is not null")
    private String notifyUrl;

    @NotNull(message = "merchantId is not null")
    private Integer merchantId;

    @NotNull(message = "tenantId is not null")
    private Integer tenantId;

    @NotNull(message = "payType is not null")
    private String payType;

    @NotNull(message = "fix is not null")
    private boolean fix;

    private String userIp;
}
