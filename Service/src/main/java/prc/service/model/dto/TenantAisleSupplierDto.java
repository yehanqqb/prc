package prc.service.model.dto;

import lombok.Data;
import prc.service.model.entity.ITenantAisleSupplier;
import prc.service.model.entity.ITenantSupplier;

import java.util.List;

@Data
public class TenantAisleSupplierDto {
    private ITenantAisleSupplier iTenantAisleSupplier;

    private ITenantSupplier iTenantSupplier;
}
