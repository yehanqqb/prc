package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;
import prc.service.mapper.IUMerchantOrderMapper;
import prc.service.model.entity.IUMerchantOrder;
@Service
public class IUMerchantOrderDao extends ServiceImpl<IUMerchantOrderMapper,IUMerchantOrder> {

}
