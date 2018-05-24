package crdhn.dis.server;


import firo.Firo;
import static firo.Firo.*;
import firo.utils.config.Config;

public class ServiceDaemon {

    public static void main(String[] args) throws NoSuchMethodException {

        //Firo.getInstance().staticFileLocation("/resources");
        Firo.getInstance().externalStaticFileLocation("html");
        Firo.getInstance().init(Config.getParamString("service", "host", "localhost"), Config.getParamInt("service", "port", 11005));
        Firo.getInstance().initializeControllerFromPackage(Config.getParamString("service", "controllerPackage", "crdhn.controller"), ServiceDaemon.class);        
        get("/hello", (req, res) -> {
            System.out.println("abc");
            return "";
        });

    }
}
