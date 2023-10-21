package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import prc.service.model.entity.IMountLog;
import prc.service.model.entity.ISVoucher;


@Mapper
public interface ISVoucherMapper extends BaseMapper<ISVoucher> {
}
