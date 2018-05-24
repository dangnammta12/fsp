package crdhn.dis.server;

import crdhn.dis.configuration.Configuration;
import firo.Firo;
import static firo.Firo.*;
import firo.utils.config.Config;
import java.io.File;

public class ServiceDaemon {

    public static void main(String[] args) throws NoSuchMethodException {
        Firo.getInstance().externalStaticFileLocation("html");
        Firo.getInstance().init(Config.getParamString("service", "host", "localhost"), Config.getParamInt("service", "port", 1301));
        Firo.getInstance().initializeControllerFromPackage(Config.getParamString("service", "controllerPackage", "crdhn.fsp.agent.controller"), ServiceDaemon.class);
        
        File dir = new File(Configuration.path_folder_store);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dirFile = new File(Configuration.path_file_store);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File dirUpload = new File(Configuration.path_folder_upload);
        if (!dirUpload.exists()) {
            dirUpload.mkdirs();
        }
        get("/hello", (req, res) -> {
            System.out.println("abc");
            return "";
        });
    }
}
