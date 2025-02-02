package com.remember.rabbitmq.demo1.provider;

import com.remember.rabbitmq.demo1.message.Demo02Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author wangjiahao
 * @date 2021/11/10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Demo02Provider {

    /**
     * 自动注入RabbitTemplate模板类
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 使用exchange + routingKey发送
     * 发送消息方法调用: 构建Message消息
     *
     * @param message    message
     * @param properties properties
     */
    public void send1(String msg) {
        Demo02Message message = new Demo02Message();
        message.setMessage(msg);
        // 根据定义到规则，解析不同到routing key
        rabbitTemplate.convertAndSend(Demo02Message.EXCHANGE, Demo02Message.KEY_1, message);
        log.info("[send1][发送消息：[{}] 发送成功]",message);

    }

    /**
     * 使用exchange + routingKey发送
     * 发送消息方法调用: 构建Message消息
     *
     * @param message    message
     * @param properties properties
     */
    public void send2(String msg) {
        Demo02Message message = new Demo02Message();
        message.setMessage(msg);
        // 根据定义到规则，解析不同到routing key
        rabbitTemplate.convertAndSend(Demo02Message.EXCHANGE, Demo02Message.KEY_2, message);
        log.info("[send2][发送消息：[{}] 发送成功]",message);
    }
}
