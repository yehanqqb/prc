package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.ITenantAisle;
import prc.service.model.entity.ITenantAisleSupplier;

@Mapper
public interface ITenantAisleSupplierMapper extends BaseMapper<ITenantAisleSupplier> {
}
