package br.com.mac.menu.messages;

import br.com.mac.menu.entity.ProductStatus;
import br.com.mac.menu.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class QueueConsumer {

    private final ProductService productService;

    private QueueConsumer(ProductService productService){
        this.productService = productService;
    }

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload ProductStatus product) throws BusinessException, br.com.mac.menu.exception.BusinessException {
        productService.processUpdateEvent(product);
    }
}