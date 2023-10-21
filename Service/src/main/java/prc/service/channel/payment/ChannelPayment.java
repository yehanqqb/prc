package prc.service.channel.payment;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import prc.service.model.dto.PaymentBeforeDto;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.dto.TenantAisleRatio;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.IUPayment;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.model.vo.ChannelPaymentVo;

import java.math.BigDecimal;
import java.util.List;

public interface ChannelPayment {
    IUPayment getPayment(PaymentBeforeDto paymentBeforeDto,TenantAisleDto tenantAisleDto);

    ChannelPaymentVo getChannelPayUrl(IUPayment iuPayment);

    ChannelMonitoringVo monitoringChannel(IUPayment iuPayment);

    void monitoringSuccess(IUPayment iuPayment);

    void monitoringError(IUPayment iuPayment);

    String refreshUrl(IUPayment iuPayment);

    JSONObject getBefore(IUPayment iuPayment);

    JSONObject getAfter(IUPayment iuPayment);

    ChannelMonitoringVo monitoringChannelParent(ChannelPayment channelPayment, IUPayment iuPayment, ISAisle aisle);

}
