package prc.client.management.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.dto.UserDto;
import prc.service.model.entity.ITenant;
import prc.service.model.entity.ITenantAisle;

import java.util.List;
import java.util.Set;

@Data
public class TenantDto {
    ITenant iTenant;

    UserDto isUser;

    Set<TenantAisleDto> iTenantAisleLists;
}
