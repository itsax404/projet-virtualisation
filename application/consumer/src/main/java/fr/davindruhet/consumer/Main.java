package fr.davindruhet.consumer;

import fr.davindruhet.consumer.config.ConfigImporter;
import fr.davindruhet.consumer.managers.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;

import java.util.Properties;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){

        logger.info("Importation de la configuration");
        Properties prop = ConfigImporter.loadYaml("config.yaml");
        //Properties prop = ConfigImporter.loadYaml("config-test.yaml");
        if(prop == null){
            return;
        }
        logger.info("=======[Informations]========");
        logger.info("Redis host : " + prop.getProperty("redis.host"));
        logger.info("Rabbit host : " + prop.getProperty("rabbitmq.host"));
        logger.info("Rabbit queue name :" + prop.getProperty("rabbitmq.queue_name"));
        logger.info("=============================");

        logger.info("Récupération des informations privées");
        String redisHost = prop.getProperty("redis.host");
        int redisPort = Integer.parseInt(prop.getProperty("redis.port"));
        String rabbitHost = prop.getProperty("rabbitmq.host");
        String queueName = prop.getProperty("rabbitmq.queue_name");

        logger.info("Connexion au serveur Redis et au RabbitMQ");
        UnifiedJedis jedis = new UnifiedJedis("redis://"+redisHost+":"+redisPort);
        MessageQueue queue = new MessageQueue(rabbitHost, queueName, jedis);

        logger.info("Lancement du consumer");
        queue.listenToQueue();
    }
}