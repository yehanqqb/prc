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
@Table(name = "s_menu")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName("s_menu")
public class ISMenu extends BaseEntity {
    @Column(unique = true)
    private String title;

    private String icon;

    private String path;

    private Boolean hide;

    private Integer parentId;

    private Integer menuType;

    private Integer sortNumber;

    private String target;

    private String authority;

    private String component;
}
