package com.mjm.queue.simple;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 14:38
 */
public class Consumer {

    public static final String QUEUE_NAME = "simpleQueue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection conn = CommonUtils.getConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //每次从队列获取的数量
        channel.basicQos(1);
        System.out.println("Customer Waiting Received messages...");


        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("received msg:");
                System.out.println(new String(body, Charset.forName("utf-8")));
                channel.abort();
                System.out.println("worker Done");
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };

        /**
         * rabbitmq 消息回复确认机制
         * 监听队列
         */
        channel.basicConsume(QUEUE_NAME, false, consumer);


    }
}
