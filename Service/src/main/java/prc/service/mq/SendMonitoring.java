package prc.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.config.RabbitConfig;
import prc.service.model.dto.TenantSupplierOrderBeforeDto;
import prc.service.model.entity.IUPayment;

import java.util.Date;

@Service
@Slf4j
public class SendMonitoring implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(IUPayment payment) {
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setReturnCallback(this);
        this.rabbitTemplate.setConfirmCallback(this);
        log.info("[预产队列发送] 发送时间: {} -- [{}]", new Date(), JSON.toJSONString(payment));
        rabbitTemplate.convertAndSend(RabbitConfig.MONITORING_EXCHANGE, RabbitConfig.MONITORING_ROUTING, JSON.toJSONString(payment));
    }

    @Override
    public void returnedMessage(@NotNull Message message, int i, @NotNull String s, @NotNull String s1, @NotNull String s2) {
        log.info("[预产队列重新发送] 发送时间: {} -- [{}]", new Date(), message);
        send(JSONObject.parseObject(JSON.toJSONString(message), IUPayment.class));
    }

    /**
     * 消息生产者发送消息至交换机时触发，用于判断交换机是否成功收到消息
     *
     * @param correlationData 相关配置信息
     * @param ack             exchange 交换机，判断交换机是否成功收到消息    true 表示交换机收到
     * @param cause           失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("---- confirm ----ack=" + ack + "  cause=" + String.valueOf(cause));
        log.info("correlationData -->" + JSON.toJSONString(correlationData));
        if (ack) {
            // 交换机接收到
            log.info("---- confirm ----ack==true  cause=" + cause);
        } else {
            // 没有接收到
            log.info("---- confirm ----ack==false  cause=" + cause);
        }
    }
}
