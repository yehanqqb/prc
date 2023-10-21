package prc.service.model.entity;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prc.service.model.enumeration.ProxyType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.net.Proxy;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "s_proxy")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@TableName(value = "s_proxy", autoResultMap = true)
public class ISProxy extends BaseEntity {
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ProxyType type;

    private Boolean status;

    private String account;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private JSONObject config;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Proxy.Type proxyType;
}
