package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import prc.service.model.dto.MerchantGroupRadioDto;
import prc.service.model.entity.IMountLog;
import prc.service.model.entity.IUMerchantOrder;

import java.util.Date;
import java.util.List;

@Mapper
public interface IUMerchantOrderMapper extends BaseMapper<IUMerchantOrder> {

    @Select("SELECT merchant_id as merchantId,tenant_id as tenantId,count(1) as count,count(CASE WHEN pay_status = 2 THEN 1 ELSE 0 END ) as successCount from d_merchant_order where tenant_id = #{tenantId} and create_time BETWEEN #{start}  and #{end} group by tenant_id,merchant_id")
    List<MerchantGroupRadioDto> findByTenantGroup(@Param("tenantId") Integer tenantId, @Param("start") String start, @Param("end") String end);


    @Update("update d_merchant_order set pay_status = #{payStatus} , update_time = now() where order_id = #{orderId}")
    Integer updateStatus(@Param("orderId") String orderId, @Param("payStatus") Integer payStatus);
}
