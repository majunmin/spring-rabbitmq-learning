package com.mjm.queue.direct;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 15:38
 */
public class RoutingDirect2 {

    public static final String EXCHANGE_NAME = "exchange_name";

    public static final String[] routingKeys = new String[]{"info", "warn"};

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = CommonUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //获取匿名队列名称
        String queueName = channel.queueDeclare().getQueue();

        for (String routingKey : routingKeys) {
            //将exchagne 与 queue 通过 bindingKey(routingKey) 绑定
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               String msg = new String(body, Charsets.UTF_8);
                System.out.println("RoutingDirect2 recv: " + envelope.getRoutingKey() + " - " + msg);
            }
        };
        channel.basicConsume(queueName, false, consumer);
    }
}
