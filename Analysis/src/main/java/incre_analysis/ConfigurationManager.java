package incre_analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigurationManager provides centralized access to configuration properties.
 * This allows the system to be easily configured for different environments
 * (local, cloud, etc.) by modifying a single properties file.
 */
public class ConfigurationManager {
    private static final String PROPERTIES_FILE = "analysis.properties";
    private static ConfigurationManager instance;
    private Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
        loadProperties();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Unable to find " + PROPERTIES_FILE + ", using defaults");
                setDefaults();
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading " + PROPERTIES_FILE + ", using defaults: " + e.getMessage());
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("hdfs.namenode.uri", "hdfs://localhost:8000");
        properties.setProperty("hdfs.client.analysis.conf.path", "/client/analysis_conf");
        properties.setProperty("redis.host", "localhost");
        properties.setProperty("redis.port", "6379");
    }

    public String getHdfsNamenodeUri() {
        return properties.getProperty("hdfs.namenode.uri", "hdfs://localhost:8000");
    }

    public String getHdfsClientAnalysisConfPath() {
        return properties.getProperty("hdfs.client.analysis.conf.path", "/client/analysis_conf");
    }

    public String getFullHdfsAnalysisConfPath() {
        return getHdfsNamenodeUri() + getHdfsClientAnalysisConfPath();
    }

    public String getRedisHost() {
        return properties.getProperty("redis.host", "localhost");
    }

    public int getRedisPort() {
        return Integer.parseInt(properties.getProperty("redis.port", "6379"));
    }
}
