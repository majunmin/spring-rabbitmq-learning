package com.mjm.queue.direct;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 15:30
 */
public class RoutingSendDirect {

    public static final String EXCHANGE_NAME = "exchange_name";

    public static final String[] routingKeys = new String[]{"info","error","warn"};

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = CommonUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        for (String routingKey : routingKeys) {
            String msg = "RoutingSendDirect send message level : " + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes(Charsets.UTF_8));
        }
        connection.close();
    }
}
