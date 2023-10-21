package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUOrderSlowSlot;
@Mapper
public interface IUOrderSlowSlotMapper extends BaseMapper<IUOrderSlowSlot> {
}
