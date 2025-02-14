package fr.davindruhet.consumer.classes;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;

public class Calcul {

    private static Logger logger = LoggerFactory.getLogger(Calcul.class);

    public static void calculate(UnifiedJedis jedis, String message){
        String[] parts = message.split("@");
        String id = parts[0];
        String calcul = parts[1];


        logger.info("Réalisation du calcul : " + id + " | Calcul : " + calcul);
        double result = 0;
        Expression e = new ExpressionBuilder(calcul).build();
        try{
            result = e.evaluate();
        } catch(ArithmeticException error){
            if(jedis != null){
                if(error.getMessage().equals("Division by zero!")){
                    jedis.set(id, "Erreur : Division par zéro");
                }else{
                    jedis.set(id, error.getMessage());
                }
                return;
            }else{
                logger.error("Erreur arithmétique : " + error.getMessage());
                return;
            }
        }

        if (jedis != null) {
            logger.info("Ajout du calcul dans le Redis");
            jedis.set(id, String.valueOf(result));
        } else {
            logger.error("Erreur avec le client Redis");
        }
    };
}
