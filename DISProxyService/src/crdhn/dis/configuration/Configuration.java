package crdhn.dis.configuration;

import crdhn.dis.controller.DirectoryController;
import crdhn.dis.utils.AsymmetricCryptography;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;
import firo.utils.config.Config;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class Configuration {

    public static final int PUBLIC_VIEW = Config.getParamInt("permission", "PUBLIC_VIEW");
    public static final int PUBLIC_EDIT = Config.getParamInt("permission", "PUBLIC_EDIT");
    public static final int ONLY_PEPOLE_SHARED = Config.getParamInt("permission", "ONLY_PEPOLE_SHARED");
    public static final int TYPE_FOLDER = Config.getParamInt("type", "FOLDER");
    public static final int TYPE_FILE = Config.getParamInt("type", "FILE");

    public static final String SERVICE_NAME;
    public static final String SERVICE_HOST;
    public static final int SERVICE_PORT;

    public static String url_file = "";
    public static String url_chunk = "";

    public static String url_directory;
    public static String url_dis_config;
    public static PrivateKey privateKeyDIS;
    public static PublicKey publicKeyDIS;
    public static PublicKey publicKeyAgent;
    public static PrivateKey privateKeyAgent;

    static {

        SERVICE_NAME = Config.getParamString("service", "name", "");
        SERVICE_PORT = Config.getParamInt("service", "port");
        SERVICE_HOST = Config.getParamString("service", "host", "");

        url_file = Config.getParam("path", "url_file");
        url_chunk = Config.getParam("path", "url_chunk");
        url_directory = Config.getParamString("path", "url_directory", "");
        url_dis_config = Config.getParamString("path", "url_dis_config", "");

    }

    
}
