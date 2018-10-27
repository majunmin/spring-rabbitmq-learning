package com.mjm.queue.topics;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author majun
 * @date 2018/10/24 16:11
 */
public class TopicSend {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static final String[] routingKeys = new String[]{
            "quick.orange.rabbit",
            "lazy.orange.elephant",
            "quick.orange.fox",
            "lazy.brown.fox",
            "quick.brown.fox",
            "quick.orange.male.rabbit",
            "lazy.orange.male.rabbit"
    };

    public static void main(String[] args) throws IOException, TimeoutException {


        Connection connection = CommonUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        for (String routingKey : routingKeys) {
            String msg = "send : " + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null,msg.getBytes(Charsets.UTF_8));
        }

        connection.close();

    }
}
