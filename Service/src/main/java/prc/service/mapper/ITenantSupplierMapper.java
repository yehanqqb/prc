package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.ITenantSupplier;

@Mapper
public interface ITenantSupplierMapper extends BaseMapper<ITenantSupplier> {
}
