package org.apache.tuscany.sca.binding.jsonrpc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: jacky
 */
public class PropertiesHelper {

    private final static PropertiesHelper INSTANCE = new PropertiesHelper();

    private static final String SCALLOP_PROPERTIES = "scallop.properties";
    private static final String SCALLOP_ZOOKEEPER_HOSTS = "scallop.zookeeper.hosts";
    private static final String SCALLOP_PERFORMACE_RMI_PROCESSTIME_THRESHOLD = "scallop.performace.rmi.processTimeThreshold";
    private static final String SCALLOP_PERFORMACE_RMI_SHOW_PARAMETERS = "scallop.performace.rmi.showParameters";
    private static final String SCALLOP_PERFORMACE_RMI_EXECUTE_TIMES_PRINT_INTERVAL = "scallop.performace.rmi.executeTimesPrintInterval";

    public static PropertiesHelper getInstance() {
        return INSTANCE;
    }

    private Properties properties = new Properties();

    private PropertiesHelper() {
        InputStream inputStream = getDefaultClassLoader().getResourceAsStream(
                SCALLOP_PROPERTIES);
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system
            // class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = PropertiesHelper.class.getClassLoader();
        }
        return cl;
    }


    public String getProperty(String name) {
        return properties.getProperty(name, null);
    }

    public long getRmiProcessTimeThreshold() {
        String str = getProperty(SCALLOP_PERFORMACE_RMI_PROCESSTIME_THRESHOLD);
        if (str == null) {
            return 50;
        } else {
            return Long.parseLong(str);
        }
    }

    public boolean getRmiShowParameters() {
        String str = getProperty(SCALLOP_PERFORMACE_RMI_SHOW_PARAMETERS);
        if ("true".equalsIgnoreCase(str)) {
            return true;
        } else {
            return false;
        }
    }

    public long getRmiExecuteTimesPrintInterval() {
        String str = getProperty(SCALLOP_PERFORMACE_RMI_EXECUTE_TIMES_PRINT_INTERVAL);
        if (str == null) {
            return 3600000;
        } else {
            return Long.parseLong(str);
        }
    }

    public String getZooKeeperHosts() {
        return getProperty(SCALLOP_ZOOKEEPER_HOSTS);
    }
}
