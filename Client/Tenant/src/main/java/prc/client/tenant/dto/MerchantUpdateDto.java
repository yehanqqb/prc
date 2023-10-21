package prc.client.tenant.dto;

import lombok.Data;

@Data
public class MerchantUpdateDto {
    private Integer id;

    private Boolean status;

    private String name;

    private String username;

    private String whiteIp;

    private String password;

    private Integer userId;
}
