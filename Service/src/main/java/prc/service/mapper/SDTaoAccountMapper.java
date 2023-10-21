package prc.service.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Mapper;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prc.service.model.entity.BaseEntity;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.entity.SDTaoAccount;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Mapper
public interface SDTaoAccountMapper extends BaseMapper<SDTaoAccount> {
}
