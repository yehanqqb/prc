package prc.service.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.IUOrderErrorNotify;
import prc.service.model.enumeration.FinishStatus;
import prc.service.model.enumeration.PayStatus;


@Mapper
public interface IUOrderErrorNotifyMapper extends BaseMapper<IUOrderErrorNotify> {

}
