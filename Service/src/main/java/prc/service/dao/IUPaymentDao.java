package prc.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;
import prc.service.mapper.IUPaymentMapper;
import prc.service.model.dto.PaymentGroupMoneyTenantDto;
import prc.service.model.entity.IUPayment;

import java.util.List;

@Service
public class IUPaymentDao extends ServiceImpl<IUPaymentMapper, IUPayment> {

}
