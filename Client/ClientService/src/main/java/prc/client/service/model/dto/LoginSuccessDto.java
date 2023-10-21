package prc.client.service.model.dto;

import lombok.Data;

@Data
public class LoginSuccessDto {
    private String token;

    private Boolean isGoogle;

    private String googleKey;

    private String googleQr;
}
