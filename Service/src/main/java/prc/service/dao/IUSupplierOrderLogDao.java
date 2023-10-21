package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import prc.service.mapper.IUSupplierOrderLogMapper;
import prc.service.model.entity.IUSupplierOrderLog;

@Service
public class IUSupplierOrderLogDao extends ServiceImpl<IUSupplierOrderLogMapper, IUSupplierOrderLog> {
}
