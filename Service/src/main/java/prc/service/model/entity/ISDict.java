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
@Table(name = "s_dict")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName("s_dict")
public class ISDict extends BaseEntity {
    @Column(unique = true)
    private String majorKey;

    private String remark;

    private String val;
}
