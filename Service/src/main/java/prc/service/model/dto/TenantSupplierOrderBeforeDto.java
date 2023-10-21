package prc.service.model.dto;

import lombok.Data;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;

@Data
public class TenantSupplierOrderBeforeDto {
    private IUSupplierOrder iuSupplierOrder;

    private TenantSupplierAisleDto supplierInfo;

    private IUPayment iuPayment;

    private boolean retry;
}
