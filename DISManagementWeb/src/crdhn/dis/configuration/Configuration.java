package crdhn.dis.configuration;

import firo.utils.config.Config;

public class Configuration {


    public static final String SERVICE_NAME;
    public static final String SERVICE_HOST;
    public static final int SERVICE_PORT;    
   
     

    public static String static_url = "";
    
    public static String url_agent_gets;
    public static String url_agent_get;
    public static String url_agent_update;
    public static String url_agent_status;
    public static String url_agent_delete;
    public static String url_disInfo;
    public static String url_disURL;
    public static String url_agent_add;
    public static String url_app_gets;
    public static String url_app_get;
    public static String url_app_delete;
    public static String url_app_add;
    public static String url_disAddIps;
    public static String url_disMoveIps;
    
    static void init() {
        
    }
    
    static {
        static_url = Config.getParamString("url", "static_url", "");
        SERVICE_NAME = Config.getParamString("service", "name", "");
        SERVICE_PORT = Config.getParamInt("service", "port");
        SERVICE_HOST = Config.getParamString("service", "host", "");
        
        url_agent_gets = Config.getParamString("url", "url_agent_gets", "");
        url_agent_get = Config.getParamString("url", "url_agent_get", "");
        url_agent_update = Config.getParamString("url", "url_agent_update", "");
        url_agent_status = Config.getParamString("url", "url_agent_status", "");
        url_agent_delete = Config.getParamString("url", "url_agent_delete", "");
        url_disInfo = Config.getParamString("url", "url_disInfo", "");
        url_agent_add = Config.getParamString("url", "url_agent_add", "");
        url_app_gets = Config.getParamString("url", "url_app_gets", "");
        url_app_get = Config.getParamString("url", "url_app_get", "");
        url_app_delete = Config.getParamString("url", "url_app_delete", "");
        url_app_add = Config.getParamString("url", "url_app_add", "");
        url_disURL = Config.getParamString("url", "url_disURL", "");
        url_disAddIps = Config.getParamString("url", "url_disAddIps", "");
        url_disMoveIps = Config.getParamString("url", "url_disMoveIps", "");
    }
}
