package prc.api.service.dto;

import lombok.Data;
import lombok.NonNull;
import prc.service.model.dto.TreadSupplierOrderExt;
import prc.service.model.enumeration.Operator;

import java.math.BigDecimal;

@Data
public class TreadSupplierDto {
    @NonNull
    private Integer supplierId;

    @NonNull
    private String orderId;

    @NonNull
    private String operator;

    @NonNull
    private Integer money;

    @NonNull
    private String productNo;

    @NonNull
    private String notifyUrl;

    @NonNull
    private boolean slow;

    @NonNull
    private String sign;

    @NonNull
    private Integer tenantId;

    @NonNull
    private TreadSupplierOrderExt ext;
}
