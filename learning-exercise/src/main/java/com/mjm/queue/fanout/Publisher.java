package com.mjm.queue.fanout;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 15:13
 *
 * 采用广播的方式 发送消息  fanout
 * 将 exchange 与所有的 queue绑定
 */
public class Publisher {

    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = CommonUtils.getConnection();
        Channel channel = conn.createChannel();

        //fanout : 分发 所有的消费者得到同样的消息
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        for (int i = 0; i < 5; i++) {
            String msg = "Hello World " + i;
            channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes(Charset.forName("utf-8")));
            System.out.println("producer produce : " + msg);
        }
        conn.close();
    }
}
