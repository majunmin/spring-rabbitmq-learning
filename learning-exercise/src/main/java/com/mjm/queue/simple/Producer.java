package com.mjm.queue.simple;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 14:18
 *
 * 耦合性较高：生产者和消费者一一对应（如果想要实现一个生产者对应多个消费者，不满足），
 * 队列名修改的时候，需要生产者和消费者同时修改。
 */
public class Producer {

    public static final String QUEUE_NAME = "simpleQueue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = CommonUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        /**
         * exchange: 交换机名字 默认情况下，服务端有一个名为“”的exchange
         * routingKey:
         */
        channel.basicPublish("", QUEUE_NAME, null, "Hello!".getBytes(Charset.forName("utf-8")));
        channel.close();
        connection.close();
    }
}
