package prc.service.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitConfig {

    @Bean
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setMessageConverter(new SerializerMessageConverter());
        return template;
    }

    public static final String RECHARGE_QUEUE = "prc.recharge.queue";
    public static final String RECHARGE_EXCHANGE = "prc.recharge.exchange";
    public static final String RECHARGE_ROUTING = "prc.recharge.routing";

    @Bean
    public Queue recharge() {
        return new Queue(RECHARGE_QUEUE);
    }


    @Bean
    DirectExchange rechargeExchange() {
        return new DirectExchange(RECHARGE_EXCHANGE);
    }

    @Bean
    Binding bindingRecharge() {
        return BindingBuilder.bind(recharge()).to(rechargeExchange()).with(RECHARGE_ROUTING);
    }


    public static final String MONITORING_QUEUE = "prc.monitoring.queue";
    public static final String MONITORING_EXCHANGE = "prc.monitoring.exchange";
    public static final String MONITORING_ROUTING = "prc.monitoring.routing";

    @Bean
    public Queue monitoring() {
        return new Queue(MONITORING_QUEUE);
    }


    @Bean
    DirectExchange monitoringExchange() {
        return new DirectExchange(MONITORING_EXCHANGE);
    }

    @Bean
    Binding bindingMonitoring() {
        return BindingBuilder.bind(monitoring()).to(monitoringExchange()).with(MONITORING_ROUTING);
    }


    public static final String COPY_MONITORING_QUEUE = "prc.copy.monitoring.queue";
    public static final String COPY_MONITORING_EXCHANGE = "prc.copy.monitoring.exchange";
    public static final String COPY_MONITORING_ROUTING = "prc.copy.monitoring.routing";

    @Bean
    public Queue monitoringCopy() {
        return new Queue(COPY_MONITORING_QUEUE);
    }


    @Bean
    DirectExchange monitoringExchangeCopy() {
        return new DirectExchange(COPY_MONITORING_EXCHANGE);
    }

    @Bean
    Binding bindingMonitoringCopy() {
        return BindingBuilder.bind(monitoringCopy()).to(monitoringExchangeCopy()).with(COPY_MONITORING_ROUTING);
    }
}
