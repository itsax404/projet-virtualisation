package fr.davindruhet.consumer.managers;

import com.rabbitmq.client.*;
import fr.davindruhet.consumer.classes.Calcul;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class MessageQueue {

    private Channel channel;
    private Connection connection;
    private String queueName;
    private UnifiedJedis jedis;

    private static Logger logger = LoggerFactory.getLogger(MessageQueue.class);

    public MessageQueue(String host, String queueName, UnifiedJedis jedis){
        this.jedis = jedis;
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(host);
        cf.setAutomaticRecoveryEnabled(true);
        cf.setNetworkRecoveryInterval(5000);
        this.queueName = queueName;
        logger.info("Lancement des tentatives de connexion");
        int i = 1;
        while(this.connection == null) {
            try {
                this.connection = cf.newConnection();
            } catch (Exception e) {
                logger.warn("Connexion ratée n°" + i+ " pour la première connexion");
                i++;
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        logger.info("Déclaration de la queue");
        try{
            this.channel = this.connection.createChannel();
            this.channel.queueDeclare(queueName, false, false, false, null);
        } catch (Exception e) {
            System.out.println("probleme declaration channel");
        }
    }

    public void listenToQueue(){
        try{;
            UnifiedJedis jedisTmp = this.jedis;
            Channel channelTmp = this.channel;
            channelTmp.basicConsume(this.queueName, true, "consumer", new DefaultConsumer(channelTmp) {
                @Override
                public void handleDelivery(
                        String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body)
                    throws IOException{
                    logger.info("Réception d'un nouveau message");
                    String message = new String(body, StandardCharsets.UTF_8);
                    Calcul.calculate(jedisTmp, message);
                }
            });
        }catch(Exception e){
            logger.error("Erreur lors de la lecture du channel : " + e.getMessage());
        }
    }
}
