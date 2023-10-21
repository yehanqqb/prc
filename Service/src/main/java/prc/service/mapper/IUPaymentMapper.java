package prc.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import prc.service.model.dto.PaymentGroupMoneyTenantDto;
import prc.service.model.entity.IUOrderSlowSlot;
import prc.service.model.entity.IUPayment;

import java.util.Date;
import java.util.List;

@Mapper
public interface IUPaymentMapper extends BaseMapper<IUPayment> {
    @Select("SELECT sum( CASE WHEN finish_status = 1 THEN money ELSE 0 END ) AS finishMoney, sum( CASE WHEN finish_status = 1 THEN 1 ELSE 0 END ) AS finishCount, sum( money ) AS payMoney, count( 1 ) AS payCount, sum( CASE WHEN finish_status = 3 THEN 1 ELSE 0 END ) AS bankCount, sum( CASE WHEN finish_status = 3 THEN money ELSE 0 END ) AS bankMoney, tenant_id as tenantId  FROM d_payment WHERE pay_status = 2 and pay_time BETWEEN #{start}  and #{end} GROUP BY tenant_id")
    List<PaymentGroupMoneyTenantDto> findMoneyGroupBy(@Param("start") String start, @Param("end") String end);

    @Select("SELECT sum( CASE WHEN finish_status = 1 THEN money ELSE 0 END ) AS finishMoney, sum( CASE WHEN finish_status = 1 THEN 1 ELSE 0 END ) AS finishCount, sum( money ) AS payMoney, count( 1 ) AS payCount, sum( CASE WHEN finish_status = 3 THEN 1 ELSE 0 END ) AS bankCount, sum( CASE WHEN finish_status = 3 THEN money ELSE 0 END ) AS bankMoney, tenant_id as tenantId  FROM d_payment WHERE pay_status = 2 and pay_time BETWEEN #{start}  and #{end} and tenant_id = #{tenantId} GROUP BY tenant_id")
    List<PaymentGroupMoneyTenantDto> findMoneyGroupByTenantId(@Param("start") String start, @Param("end") String end, @Param("tenantId") Integer tenantId);

    @Update("update d_payment set pay_status = 9 where supplier_order_id = #{supplierOrderId} and payment_id!= #{paymentId} and pay_status= 0")
    Integer updateOtherSlowTrue(@Param("supplierOrderId") String supplierOrderId, @Param("paymentId") String paymentId);
}
