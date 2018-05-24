package crdhn.dis.server;

import crdhn.dis.configuration.Configuration;
import static crdhn.dis.configuration.Configuration.privateKeyAgent;
import static crdhn.dis.configuration.Configuration.privateKeyDIS;
import static crdhn.dis.configuration.Configuration.publicKeyAgent;
import static crdhn.dis.configuration.Configuration.publicKeyDIS;
import crdhn.dis.controller.DirectoryController;
import crdhn.dis.utils.AsymmetricCryptography;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;
import firo.Firo;
import static firo.Firo.*;
import firo.utils.config.Config;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class ServiceDaemon {

    public static void main(String[] args) throws NoSuchMethodException {
        //Firo.getInstance().staticFileLocation("/resources");
        Firo.getInstance().init(Config.getParamString("service", "host", "localhost"), Config.getParamInt("service", "port", 11005));
        Firo.getInstance().initializeControllerFromPackage(Config.getParamString("service", "controllerPackage", "crdhn.controller"), ServiceDaemon.class);
        getDisConfiguration();
        get("/hello", (req, res) -> {
            System.out.println("abc");
            return "";
        });

    }

    private static void getDisConfiguration() {

        while (true) {
            try {
                HashMap<String, String> params = new HashMap();
                String url_get = Configuration.url_dis_config + File.separator + "dis/get";
                String result = HttpRequestUtils.sendHttpRequest(url_get, "GET", params);
                Utils.printLogSystem("Configuration", "Get DIS Configuration resp=" + result);
                JSONObject objResp = new JSONObject(result);
                if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
                    JSONObject objData = objResp.getJSONObject("data");
                    if (objData != null) {
                        String puKeyDIS = objData.getString("disPublicKey");
                        String priKeyDIS = objData.getString("disPrivateKey");
                        String priKeyAgent = objData.getString("agentPrivateKey");
                        String puKeyAgent = objData.getString("agentPublicKey");
                        if (puKeyDIS != null && priKeyAgent != null) {
                            try {
                                privateKeyDIS = AsymmetricCryptography.getInstance().getPrivateFromString(priKeyDIS);
                                publicKeyDIS = AsymmetricCryptography.getInstance().getPublicFromString(puKeyDIS);
                                privateKeyAgent = AsymmetricCryptography.getInstance().getPrivateFromString(priKeyAgent);
                                publicKeyAgent = AsymmetricCryptography.getInstance().getPublicFromString(puKeyAgent);
                                break;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
