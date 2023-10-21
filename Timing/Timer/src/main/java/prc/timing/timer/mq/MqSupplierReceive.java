package prc.timing.timer.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import prc.service.config.RabbitConfig;
import prc.service.model.dto.TenantSupplierOrderBeforeDto;
import prc.timing.timer.service.PaymentCreateService;

@Component
@Slf4j
public class MqSupplierReceive {
    @Autowired
    private PaymentCreateService paymentCreateService;

    /**
     * payment create
     *
     * @param message
     */
    @RabbitListener(queues = RabbitConfig.RECHARGE_QUEUE)
    @Async("mqExecutor")
    public void onMessage(Message message) {
        try {

            TenantSupplierOrderBeforeDto rechargeDto = JSON.parseObject(new String(message.getBody()), TenantSupplierOrderBeforeDto.class);
            log.info("[payment create supplierId is {}]", rechargeDto.getIuSupplierOrder().getOrderId());
            paymentCreateService.createPayment(rechargeDto);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("create supplier order is error", e);
        }
    }
}
