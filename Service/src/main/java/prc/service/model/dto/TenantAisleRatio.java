package prc.service.model.dto;

import lombok.Data;
import prc.service.model.enumeration.Operator;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TenantAisleRatio {
    private Operator operator;

    private Integer ratio;

    List<BigDecimal> rechargeMoney;
}
