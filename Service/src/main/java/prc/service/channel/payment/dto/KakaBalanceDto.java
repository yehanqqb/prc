package prc.service.channel.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class KakaBalanceDto {
    private BigDecimal totalBalance;

    public Integer code;
}
