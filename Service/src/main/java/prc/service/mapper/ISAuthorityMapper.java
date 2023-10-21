package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISAuthority;

@Mapper
public interface ISAuthorityMapper extends BaseMapper<ISAuthority> {
}
