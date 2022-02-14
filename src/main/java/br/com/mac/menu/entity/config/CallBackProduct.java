package br.com.mac.menu.entity.config;

import br.com.mac.menu.entity.ProductStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class CallBackProduct {
    private static Log log = LogFactory.getLog(CallBackProduct.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.exchange}")
    private String exchange;

    @Value("${queue.routing}")
    private String routing;

    private CallBackProduct(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostPersist
    @PostUpdate
    private void beforeAnyUpdate(ProductStatus productStatus) {
        rabbitTemplate.convertAndSend(exchange, routing, productStatus);
    }
}

