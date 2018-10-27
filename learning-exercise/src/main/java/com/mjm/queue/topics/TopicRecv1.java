package com.mjm.queue.topics;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 16:16
 */
public class TopicRecv1 {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static final String[] routingKeys = new String[]{
            "*.orange.*"
    };


    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = CommonUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        for (String routingKey : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("TopicRecv1 Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);

    }
}
