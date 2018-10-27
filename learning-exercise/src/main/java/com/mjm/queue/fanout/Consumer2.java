package com.mjm.queue.fanout;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 15:23
 */
public class Consumer2 {

    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = CommonUtils.getConnection();
        Channel channel = conn.createChannel();
        // 声明一个队列
        String queueName = channel.queueDeclare().getQueue();
        //将队列 与 exchange绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumer2 recv msg: " + new String(body, Charset.forName("utf-8")));
            }
        };

        //队列会自动删除
        channel.basicConsume(queueName, false, consumer);
    }
}
