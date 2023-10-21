package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import prc.service.mapper.ISAuthorityMapper;
import prc.service.model.entity.ISAuthority;

@Service
public class ISAuthorityDao extends ServiceImpl<ISAuthorityMapper, ISAuthority> {
}
