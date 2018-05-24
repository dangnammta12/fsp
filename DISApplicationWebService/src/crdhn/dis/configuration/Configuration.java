package crdhn.dis.configuration;

import firo.utils.config.Config;

public class Configuration {

    public static final int PUBLIC_VIEW = 10001;
    public static final int PUBLIC_EDIT = 10002;
    public static final int ONLY_PEPOLE_SHARED = 10003;
    public static final int TYPE_FOLDER = 1000;
    public static final int TYPE_FILE = 1001;
    public static final int TYPE_USER = 100;
    public static final int TYPE_ADMIN = 111;
    public static int port_listen = 1107;
    public static String domain = "";
    public static String cookie_domain;
    public static String path_file_store;
    public static String path_folder_store;
    public static String path_folder_upload;

    public static String static_url = "";
    public static String url_agent_server;

    public static String MONGODB_USER_COLLECTION_NAME;
    public static String MONGODB_SESSION_COLLECTION_NAME;
    public static String FOLDER_ID_COUNTER_KEY;
    public static String MONGODB_COUNTER_COLLECTION_NAME;

    public static int LOGIN_TIMEOUT;
    public static String appKey;

    static void init() {
        port_listen = Config.getParamInt("service", "port", 1107);
        domain = Config.getParamString("url", "domain", "http://127.0.0.1");
        cookie_domain = Config.getParamString("cookie", "domain", "127.0.0.1");
        static_url = Config.getParamString("url", "static_url", "");
        LOGIN_TIMEOUT = Config.getParamInt("setting", "login_timeout", 3600);
        appKey = Config.getParamString("setting", "app_key", "");

        path_folder_store = Config.getParamString("setting", "path_folder_store", "");
        path_file_store = Config.getParamString("setting", "path_file_store", "");
        path_folder_upload = Config.getParamString("setting", "path_folder_upload", "");
        url_agent_server = Config.getParamString("url", "url_agent_server", "");

        MONGODB_USER_COLLECTION_NAME = Config.getParamString("mongodb", "user_collection_name", "");
        MONGODB_SESSION_COLLECTION_NAME = Config.getParamString("mongodb", "session_collection_name", "");
        MONGODB_COUNTER_COLLECTION_NAME = Config.getParamString("mongodb", "counter_collection_name", "");
        FOLDER_ID_COUNTER_KEY = Config.getParamString("mongodb", "folderid_counter_key", "");

    }

    static {
        init();
    }
}
