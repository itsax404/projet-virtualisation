package fr.davindruhet.consumer.classes;

import com.rabbitmq.client.DeliverCallback;
import fr.davindruhet.consumer.managers.MessageQueue;
import redis.clients.jedis.UnifiedJedis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Calcul {

    private String queueName;
    private UnifiedJedis jedis;
    private String host;

    public Calcul(String queueName, UnifiedJedis jedis, String host){
        this.queueName = queueName;
        this.jedis = jedis;
        this.host = host;
    }

    public void listenToMessage(){
        MessageQueue queue = new MessageQueue(this.host, this.queueName);
        queue.listenToQueue(getCalcul);
    }

    DeliverCallback getCalcul = (consumerTag, delivery) ->{
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

        String[] parts = message.split("@");
        String id = parts[0];
        String calcul = parts[1];

        // TODO faire en sorte de

        double result = -1.0;
        for(String element : new String[]{"\\+", "-", "/", "\\*"}){
            List<String> partsCalcul = List.of(calcul.split(element));
            if(partsCalcul.size() == 2){
                double value1 = Float.parseFloat(partsCalcul.get(0));
                double value2 = Float.parseFloat(partsCalcul.get(1));
                result = calculate(value1, value2, element);
            }
        }

        Logger.getLogger("Calcul").log(Level.INFO, message);
        if (this.jedis != null) {
            this.jedis.set(id, String.valueOf(result));
        } else {
            Logger.getLogger("Calcul").log(Level.SEVERE, "Redis connection is not initialized.");
        }
    };

    private String calculateString(String calcul){

        return "";
    }

    private double calculate(double value1, double value2, String operation){
        return switch (operation) {
            case "+" -> value1 + value2;
            case "-" -> value1 - value2;
            case "/" -> value1 / value2;
            case "*" -> value1 * value2;
            default -> -1;
        };
    }

}
