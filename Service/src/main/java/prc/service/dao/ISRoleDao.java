package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import prc.service.mapper.ISRoleMapper;
import prc.service.model.entity.ISRole;

@Service
public class ISRoleDao extends ServiceImpl<ISRoleMapper,ISRole> {
}
