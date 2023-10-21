package prc.service.model.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prc.service.model.enumeration.Operator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 通道配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "s_aisle")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "s_aisle", autoResultMap = true)
public class ISAisle extends BaseEntity {
    String name;

    @Column(name = "`before`")
    @TableField(value = "`before`")
    Boolean before;

    String beforeBeanName;

    String payBeanName;

    private String monitorBean;

    Boolean status;

    // 支持的运营商
    @TableField(typeHandler = JacksonTypeHandler.class)
/*
    @Enumerated(EnumType.ORDINAL)
*/
    @Type(type = "json")
    @Column(columnDefinition = "json")
    @EnumValue
    List<Integer> operators;

    // 支持的金额
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    List<BigDecimal> rechargeMoney;

    private Boolean slow;

    // 支持的付款方式
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    List<String> payType;

    private Long monitoringLong;

    // 监控休眠时长
    Long mountSleep;

    private Boolean fix;

    @Column(name = "`primary`")
    @TableField(value = "`primary`")
    private Boolean primary;

    // 未被拉起退回时间 只有快充才有
    private Long bankLong;

    public ISAisle() {


    }
}
