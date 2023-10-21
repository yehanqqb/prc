package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.IUSupplierOrder;
import prc.service.model.entity.IUSupplierOrderLog;
@Mapper
public interface IUSupplierOrderLogMapper extends BaseMapper<IUSupplierOrderLog> {
}
