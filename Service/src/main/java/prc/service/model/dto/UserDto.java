package prc.service.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.Type;
import prc.service.model.entity.ISAuthority;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Integer id;

    private Date createTime;

    private Date updateTime;

    private String username;

    private String avatar;

    private String password;

    private Integer roleId;

    private String googleKey;

    private List<ISAuthority> authority;

    private Boolean status;
}
