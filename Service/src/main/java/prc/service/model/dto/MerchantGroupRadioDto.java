package prc.service.model.dto;

import lombok.Data;

@Data
public class MerchantGroupRadioDto {
    private Integer tenantId;

    private Integer count;

    private Integer successCount;

    private Integer merchantId;
}
