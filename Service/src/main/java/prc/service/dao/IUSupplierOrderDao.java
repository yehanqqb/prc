package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import prc.service.mapper.IUSupplierOrderMapper;
import prc.service.model.entity.IUSupplierOrder;

@Service
public class IUSupplierOrderDao extends ServiceImpl<IUSupplierOrderMapper, IUSupplierOrder> {
}
