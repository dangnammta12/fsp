package crdhn.dis.configuration;

import firo.utils.config.Config;

public class Configuration {


    public static final String SERVICE_NAME;
    public static final String SERVICE_HOST;
    public static final int SERVICE_PORT;    
    

    public static String static_url = "";
    
    static void init() {
        
    }
    
    static {
        static_url = Config.getParamString("url", "static_url", "");
        SERVICE_NAME = Config.getParamString("service", "name", "");
        SERVICE_PORT = Config.getParamInt("service", "port");
        SERVICE_HOST = Config.getParamString("service", "host", "");
        
    }
}
