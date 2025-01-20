package fr.davindruhet.consumer.managers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class MessageQueue {

    private Channel channel;
    private String queueName;

    public MessageQueue(String host, String queueName){
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(host);
        this.queueName = queueName;
        try{
            Connection connection = cf.newConnection();
            this.channel = connection.createChannel();
            this.channel.queueDeclare(queueName, false, false, false, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void listenToQueue(DeliverCallback dlr){
        try{
            channel.basicConsume(this.queueName, true, dlr, consumerTag -> { });
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}
