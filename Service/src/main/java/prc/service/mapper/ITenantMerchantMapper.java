package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.ITenant;
import prc.service.model.entity.ITenantMerchant;

@Mapper
public interface ITenantMerchantMapper extends BaseMapper<ITenantMerchant> {
}
