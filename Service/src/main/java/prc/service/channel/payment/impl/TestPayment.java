package prc.service.channel.payment.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import prc.service.channel.payment.ChannelPaymentBefore;
import prc.service.model.entity.IUPayment;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.model.vo.ChannelPaymentVo;

import java.util.Date;

@Service("TestPayment")
public class TestPayment extends ChannelPaymentBefore {
    @Override
    public ChannelPaymentVo getChannelPayUrl(IUPayment iuPayment) {
        ChannelPaymentVo channelPaymentVo = new ChannelPaymentVo();
        channelPaymentVo.setCreateTime(new Date().getTime());
        channelPaymentVo.setPaymentNo(IdUtil.fastSimpleUUID());
        channelPaymentVo.setPayUrl("https://www.alipay.com/");
        channelPaymentVo.setProductNo(iuPayment.getProductNo());
        channelPaymentVo.setQuery(new JSONObject());
        channelPaymentVo.setProxyIp("127.0.0.1");
        channelPaymentVo.setStatus(true);
        channelPaymentVo.setRemark("测试");
        return channelPaymentVo;
    }

    @Override
    public ChannelMonitoringVo monitoringChannel(IUPayment iuPayment) {
        ChannelMonitoringVo channelMonitoringVo = new ChannelMonitoringVo();
        channelMonitoringVo.setFinishStatus(true);
        channelMonitoringVo.setPayStatus(true);
        channelMonitoringVo.setOrderStatus(false);
        channelMonitoringVo.setPaymentNo(IdUtil.fastSimpleUUID());
        try {
            Thread.sleep(3 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelMonitoringVo;
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
