package prc.service.channel.payment.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import prc.service.channel.payment.ChannelPaymentBefore;
import prc.service.model.entity.IUPayment;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.model.vo.ChannelPaymentVo;

@Service("UidSpPayment")
public class UidSpPayment extends ChannelPaymentBefore {
    @Override
    public ChannelPaymentVo getChannelPayUrl(IUPayment iuPayment) {
        return null;
    }

    @Override
    public ChannelMonitoringVo monitoringChannel(IUPayment iuPayment) {
        return null;
    }

    @Override
    public String refreshUrl(IUPayment iuPayment) {
        return null;
    }

    @Override
    public JSONObject getBefore(IUPayment iuPayment) {
        return null;
    }
}
