package prc.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "sd_tao_account")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "sd_tao_account", autoResultMap = true)
public class SDTaoAccount extends BaseEntity {
    @Column(unique = true)
    private String account;

    private String smsUrl;

    private Boolean status;

    private Integer success;

    private Integer pay;

    private String taoCookie;

    private String aliCookie;

    private Boolean smsError;

    private Boolean accountError;

    private String remark;

    private Boolean init;
}
