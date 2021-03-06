package org.tommy.stationery.ink.api.config;

import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by kun7788 on 14. 12. 19..
 */
public class ParametersConfig {

    private Map<String, Map<String, Object>> parameters;

    public ParametersConfig(String config) {
        ymlLoadAsResourceByArgFileName(config);
    }

    public Map<String, Map<String, Object>> getConfig() {
        return parameters;
    }
    
    private void ymlLoadAsResourceByArgFileName(String config) {

        String configFileName = System.getProperty(config);
        if (configFileName == null) {
            configFileName = "config-dev.yml";
        }

        Yaml yaml = new Yaml();
        try {
            InputStream in = new ClassPathResource(configFileName).getInputStream();
            parameters = (Map<String, Map<String, Object>>) yaml.load(in);
        } catch (Exception e) {
            InputStream in = null;
            try {
                in = new FileInputStream(configFileName);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            parameters = (Map<String, Map<String, Object>>) yaml.load(in);
        }
    }

    public static Integer IntegerValue(Object value) {
        return Integer.valueOf(value.toString());
    }

    public static String StringValue(Object value) {
        return value.toString();
    }
}
