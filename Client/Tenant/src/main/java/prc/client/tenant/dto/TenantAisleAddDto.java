package prc.client.tenant.dto;

import lombok.Data;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TenantAisleAddDto {
    private Integer aisleId;

    private Integer supplierId;

    private BigDecimal radio;

    private Integer id;

    private Boolean slow;
}
