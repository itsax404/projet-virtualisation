package fr.davindruhet.consumer.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class ConfigImporter {

    public static Properties loadYaml(String filePath){
        Yaml yaml = new Yaml();
        Properties properties = new Properties();
        try(InputStream input = ConfigImporter.class.getClassLoader().getResourceAsStream(filePath)) {
            if(input == null){
                System.err.println("Impossible to find the file : " + filePath);
                return null;
            }

            Map<String, Object> map = yaml.load(input);
            if(map !=  null){
                flattenMap(map, "", properties);
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return properties;
    }

    private static void flattenMap(Map<String, Object> map, String prefix, Properties properties) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flattenMap((Map<String, Object>) entry.getValue(), key, properties);
            } else {
                properties.setProperty(key, entry.getValue().toString());
            }
        }
    }

}
