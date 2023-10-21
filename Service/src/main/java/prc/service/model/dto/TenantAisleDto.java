package prc.service.model.dto;

import lombok.Data;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ITenantAisle;
import prc.service.model.enumeration.Operator;

import java.math.BigDecimal;
import java.net.Proxy;
import java.util.List;

@Data
public class TenantAisleDto {
    private ITenantAisle iTenantAisle;

    private ISAisle isAisle;
}
