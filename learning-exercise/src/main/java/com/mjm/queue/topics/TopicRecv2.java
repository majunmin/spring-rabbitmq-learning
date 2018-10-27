package com.mjm.queue.topics;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 16:20
 */
public class TopicRecv2 {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static final String[] routingKeys = new String[]{
            "*.brown.*"
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
                String message = new String(body, Charsets.UTF_8);
                System.out.println("TopicRecv2 Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);

    }
}
