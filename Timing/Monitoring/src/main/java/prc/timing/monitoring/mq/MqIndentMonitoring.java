package prc.timing.monitoring.mq;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import prc.service.channel.payment.ConcurrentPaymentService;
import prc.service.config.RabbitConfig;
import prc.service.model.entity.IUPayment;

@Component
@Slf4j
public class MqIndentMonitoring {
    @Autowired
    private ConcurrentPaymentService concurrentPaymentService;

    /**
     * 监控  多线程
     *
     * @param message
     */
    @Async("mqExecutor")
    @RabbitListener(queues = RabbitConfig.MONITORING_QUEUE)
    public void onMessage(Message message) {
        try {
            IUPayment payment = JSON.parseObject(new String(message.getBody()), IUPayment.class);
            log.info("[支付单监控paymentId-{}]", payment.getPaymentId());
            concurrentPaymentService.monitoring(payment);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("监控报错");
        }
    }
}
