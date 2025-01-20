package fr.davindruhet.consumer;

import fr.davindruhet.consumer.classes.Calcul;
import redis.clients.jedis.UnifiedJedis;

public class Main {

    public static void main(String[] args){

        String redisHost = System.getenv("REDIS_HOST");
        int redisPort = Integer.parseInt(System.getenv("REDIS_PORT"));
        String rabbitHost = System.getenv("RABBIT_HOST");
        String queueName = System.getenv("QUEUE_NAME");

        UnifiedJedis jedis = new UnifiedJedis("redis://"+redisHost+":"+redisPort);
        Calcul calcul = new Calcul(queueName, jedis, rabbitHost);
        calcul.listenToMessage();
    }
}