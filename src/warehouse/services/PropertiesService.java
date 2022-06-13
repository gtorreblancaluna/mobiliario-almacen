package warehouse.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class PropertiesService {
    
    private static PropertiesService INSTANCE = null;
    private Properties prop = null;
    private final static Logger LOG = Logger.getLogger(PropertiesService.class.getName());
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new PropertiesService();
        }
    }
    
    public static PropertiesService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    private PropertiesService() {
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("configuration.properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException e) {
           LOG.error(e.getMessage(),e);
        }

    }
    
    public String getProperty (String property) {
        return prop.get(property).toString();
    }
    
}
