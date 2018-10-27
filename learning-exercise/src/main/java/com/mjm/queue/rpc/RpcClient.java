package com.mjm.queue.rpc;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RpcClient {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RpcClient() throws IOException, TimeoutException {
        connection = CommonUtils.getConnection();
        channel = connection.createChannel();
    }

    public String call(String message) throws IOException, InterruptedException {
        String correlateId = UUID.randomUUID().toString();
        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlateId)
                .replyTo(replyQueueName)
                .build();
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));
        //阻塞队列，大小为1
        BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //如果唯一标识对应，则以非阻塞的形式向阻塞队列中加入服务器返回的结果
                if (properties.getCorrelationId().equals(correlateId)) {
                    response.offer(new String(body, "UTF-8"));
                }
            }
        };
        String consumerTag = channel.basicConsume(replyQueueName, true, consumer);
        //以阻塞的形式在阻塞队列中取出结果
        String result = response.take();
        //清除消费者，下次不会再往该消费者发送消息
        channel.basicCancel(consumerTag);
        return result;
    }

    public void close() throws IOException, TimeoutException {
        if(channel!=null){
            channel.close();
        }
        if(connection!=null){
            connection.close();
        }
    }

    public static void main(String[] args) {
        RpcClient client = null;
        String response = null;
        try {
            client = new RpcClient();
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println("请求计算：fib(" + i + ")");
                response = client.call(i_str);
                System.out.println("得到计算结果：" + response);
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                }
            }
        }
    }
}