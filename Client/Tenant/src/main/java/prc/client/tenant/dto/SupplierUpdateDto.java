package prc.client.tenant.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SupplierUpdateDto {
    private Integer id;

    private Boolean status;

    private String name;

    private Integer userId;

    private String username;

    private String produceIps;

    private Set<Integer> tenantAisleIds;

    private long maxCount;

    boolean repetition;

    Integer repetitionCount;

    boolean repetitionNo;

    private Long bankLong;

    private String password;
}
