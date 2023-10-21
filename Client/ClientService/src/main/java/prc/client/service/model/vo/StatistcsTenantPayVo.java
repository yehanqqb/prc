package prc.client.service.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatistcsTenantPayVo {
    private String nickname;

    private BigDecimal ratio;
}
