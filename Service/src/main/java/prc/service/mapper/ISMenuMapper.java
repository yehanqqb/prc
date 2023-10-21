package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.ISDict;
import prc.service.model.entity.ISMenu;
@Mapper
public interface ISMenuMapper extends BaseMapper<ISMenu> {
}
