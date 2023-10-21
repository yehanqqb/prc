package prc.client.management.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TenantCreateDto {
    @NotNull(message = "username为空")
    private String username;
    @NotNull(message = "password为空")
    private String password;
}
