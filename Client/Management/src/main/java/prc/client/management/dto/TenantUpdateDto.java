package prc.client.management.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TenantUpdateDto {
    private Integer userId;

    private Integer tenantId;

    private String password;

    private String username;

    private List<Integer> aisleIds;

    private List<Integer> mountIds;

    private List<Integer> authorityIds;

    private BigDecimal balance;

    private String secret;

    private boolean status;

    private Integer repertory;
}
