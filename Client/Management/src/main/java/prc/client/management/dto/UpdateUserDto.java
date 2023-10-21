package prc.client.management.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserDto {
    private String username;

    private String password;

    private boolean status;

    private List<Integer> authorityIds;
}
