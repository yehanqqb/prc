package prc.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ITenantAisle;
import prc.service.model.entity.ITenantSupplier;

import java.util.Set;

@Data
public class TenantSupplierAisleDto {
    private Set<TenantAisleDto> tenantAisleDto;

    private ITenantSupplier iTenantSupplier;
}
