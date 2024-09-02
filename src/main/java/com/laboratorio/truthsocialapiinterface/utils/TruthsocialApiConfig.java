package com.laboratorio.truthsocialapiinterface.utils;

import java.io.FileReader;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 16/08/2024
 * @updated 01/09/2024
 */
public class TruthsocialApiConfig {
    private static final Logger log = LogManager.getLogger(TruthsocialApiConfig.class);
    private static TruthsocialApiConfig instance;
    private final Properties properties;

    private TruthsocialApiConfig() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try {
            this.properties.load(new FileReader("config//truthsocial_api.properties"));
        } catch (Exception e) {
            log.error("Ha ocurrido un error leyendo el fichero de configuración del API de Truth Social. Finaliza la aplicación!");
            log.error(String.format("Error: %s", e.getMessage()));
            if (e.getCause() != null) {
                log.error(String.format("Causa: %s", e.getCause().getMessage()));
            }
            System.exit(-1);
        }
    }

    public static TruthsocialApiConfig getInstance() {
        if (instance == null) {
            synchronized (TruthsocialApiConfig.class) {
                if (instance == null) {
                    instance = new TruthsocialApiConfig();
                }
            }
        }
        
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}