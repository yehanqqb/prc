package prc.service.model.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import prc.service.model.enumeration.Operator;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentSupplierDto {
    private Integer supplierId;

    // 拉取比例
    private BigDecimal radio;

    private List<Operator> existOperator;

    private long bankLong;
}
