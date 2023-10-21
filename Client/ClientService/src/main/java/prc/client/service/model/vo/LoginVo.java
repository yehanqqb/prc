package prc.client.service.model.vo;

import lombok.Data;

@Data
public class LoginVo {
    private String username;

    private String password;

    private String code;

    private String clientId;

    private Integer googleCode;
}
