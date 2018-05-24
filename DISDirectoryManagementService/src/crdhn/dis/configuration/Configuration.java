package crdhn.dis.configuration;

import firo.utils.config.Config;

public class Configuration {

    public static final int TYPE_FOLDER = Config.getParamInt("type", "FOLDER");
    public static final int TYPE_FILE = Config.getParamInt("type", "FILE");
    
    public static final String SERVICE_NAME;
    public static final String SERVICE_HOST;
    public static final int SERVICE_PORT;
    
    public static String url_user_getfiles;
    public static String url_download_file;

    static {
        SERVICE_NAME = Config.getParamString("service", "name", "");
        SERVICE_PORT = Config.getParamInt("service", "port");
        SERVICE_HOST = Config.getParamString("service", "host", "");

        url_user_getfiles = Config.getParamString("url", "url_user_getfile", "");
        url_download_file = Config.getParamString("url", "url_download_file", "");
    }
}
