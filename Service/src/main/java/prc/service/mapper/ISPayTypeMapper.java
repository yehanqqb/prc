package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISPayType;

@Mapper
public interface ISPayTypeMapper extends BaseMapper<ISPayType> {
}
