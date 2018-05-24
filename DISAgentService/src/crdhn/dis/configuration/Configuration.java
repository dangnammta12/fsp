package crdhn.dis.configuration;

import crdhn.dis.utils.AsymmetricCryptography;
import firo.utils.config.Config;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

    public static final int PUBLIC_VIEW = Config.getParamInt("permission", "PUBLIC_VIEW");
    public static final int PUBLIC_EDIT = Config.getParamInt("permission", "PUBLIC_EDIT");
    public static final int ONLY_PEPOLE_SHARED = Config.getParamInt("permission", "ONLY_PEPOLE_SHARED");
    public static final int TYPE_FOLDER = Config.getParamInt("type", "FOLDER");
    public static final int TYPE_FILE = Config.getParamInt("type", "FILE");
    
    public static int CHUNK_LENGTH;
    public static int size_worker_chunk_upload;
    public static int size_worker_chunk_download;
    public static long limit_local_store_max;
    public static long limit_local_store_min;
    public static int number_chunk_failed;

    public static String path_folder_store;
    public static int port_listen = 1107;
    public static String domain = "";
    public static String cookie_domain;

    public static String path_upload_browser = "";
    public static String path_upload_path = "";
    public static String path_upload_result = "";
    public static String path_download = "";
    public static String path_submit_download = "";
    public static String path_download_result = "";
    public static String path_home;
    public static String static_url = "";

    public static String url_user_deletefile;
    public static String url_proxy;
    public static int pingTime;
    public static String agentKey;
    public static boolean agentStatus;
    

    public static PrivateKey privateKeyAgent;
    public static PublicKey publicKeyDIS;

    static void init() {

        try {
            privateKeyAgent = AsymmetricCryptography.getInstance().getPrivate("conf/prAgentKey.pem");
            publicKeyDIS = AsymmetricCryptography.getInstance().getPublic("conf/puDISKey.pem");
        } catch (Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        agentStatus = false;
        port_listen = Config.getParamInt("service", "port", 1107);
        domain = Config.getParamString("url", "domain", "http://127.0.0.1");
        cookie_domain = Config.getParamString("cookie", "domain", "127.0.0.1");
        static_url = Config.getParamString("url", "static_url", "");
        CHUNK_LENGTH = Config.getParamInt("setting", "size_chunk");
        size_worker_chunk_upload = Config.getParamInt("setting", "size_worker_chunk_upload");
        size_worker_chunk_download = Config.getParamInt("setting", "size_worker_chunk_download");
        number_chunk_failed = Config.getParamInt("setting", "number_chunk_failed");
        limit_local_store_max = (Config.getParamInt("setting", "limit_local_storage_max")) * 1073741824l; //bytes
        limit_local_store_min = (Config.getParamInt("setting", "limit_local_storage_min")) * 1073741824l; //bytes
        pingTime = Config.getParamInt("setting", "pingtime");
        agentKey = Config.getParamString("setting", "agentKey", "");
        
        path_folder_store = Config.getParamString("setting", "folder_path_store", "");
        path_home = Config.getParamString("path", "home_path", "");
        path_download = Config.getParamString("path", "download", "/download");
        path_submit_download = Config.getParamString("path", "download_submit", "/download/submit");
        path_download_result = Config.getParamString("path", "download_result", "/download/result");

        path_upload_browser = Config.getParamString("path", "upload_browser", "/upload/browser");
        path_upload_path = Config.getParamString("path", "upload_path", "/upload/path");
        path_upload_result = Config.getParamString("path", "upload_result", "");

//        url_user_login = Config.getParamString("url", "url_user_login", "");
//        url_user_logout = Config.getParamString("url", "url_user_logout", "");
//        url_user_register = Config.getParamString("url", "url_user_register", "");
//        url_user_profile = Config.getParamString("url", "url_user_profile", "");
//        url_user_changpass = Config.getParamString("url", "url_user_changepass", "");
//        url_user_checkSession = Config.getParamString("url", "url_user_checkSession", "");
        url_user_deletefile = Config.getParamString("url", "url_user_deletefile", "");
        url_proxy = Config.getParamString("url", "url_dis", "");
    }

    static {
        init();
    }
}
