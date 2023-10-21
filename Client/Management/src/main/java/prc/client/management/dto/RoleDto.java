package prc.client.management.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.hibernate.annotations.Type;
import prc.service.model.entity.ISAuthority;

import javax.persistence.Column;
import java.util.List;

@Data
public class RoleDto {
    private String name;

    private String roleKey;

    private Integer sort;

    private Boolean status;

    private List<ISAuthority> authority;

    private Integer id;
}
