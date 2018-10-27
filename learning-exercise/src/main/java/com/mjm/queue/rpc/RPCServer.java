package com.mjm.queue.rpc;

import com.mjm.common.CommonUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    /**
     * 计算斐波拉契和
     * @param n
     * @return
     */
    private static int fib(int n) {
        if (n == 0){
            return 0;
        }
        if (n == 1){
            return 1;
        }
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = CommonUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            //清空队列
            channel.queuePurge(RPC_QUEUE_NAME);
            channel.basicQos(1);
            System.out.println("wait for request...");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String correlationId = properties.getCorrelationId();
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(correlationId)
                            .build();
                    StringBuffer response = new StringBuffer();
                    String message = new String(body, "utf-8");
                    int n = Integer.parseInt(message);
                    System.out.println("fib(" + message + ")...");
                    response.append(fib(n));
                    channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
                    //手动回复
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    synchronized (this) {
                        this.notify();
                    }

                }
            };
            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
            while (true) {
                synchronized (consumer) {
                    try {
                        consumer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }
    }
}