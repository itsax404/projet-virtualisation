package fr.davindruhet.consumer.classes;

import com.rabbitmq.client.DeliverCallback;
import fr.davindruhet.consumer.managers.MessageQueue;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Calcul {

    private static Logger logger = LoggerFactory.getLogger(Calcul.class);

    public static void calculate(UnifiedJedis jedis, String message){
        String[] parts = message.split("@");
        String id = parts[0];
        String calcul = parts[1];


        logger.info("Réalisation du calcul : " + id);
        double result = 0;
        Expression e = new ExpressionBuilder(calcul).build();
        result = e.evaluate();


        if (jedis != null) {
            logger.info("Ajout du calcul dans le Redis");
            jedis.set(id, String.valueOf(result));
        } else {
            logger.error("Erreur avec le client Redis");
        }
    };
}
