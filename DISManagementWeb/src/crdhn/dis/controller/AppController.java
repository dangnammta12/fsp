/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import firo.utils.config.Config;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Admin
 */
public class AppController extends Controller {
     private static final String className = "[AppController]";

    public AppController() {
    }
    
    @RouteInfo(method = "get", path = "/app/get")
    public Route getAppInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String appId = ServletUtil.getStringParameter(request, "appId");

            try {
                HashMap<String, String> params = new HashMap();
                params.put("appId", appId);
                String url_getapps = Configuration.url_app_get;
                String result = HttpRequestUtils.sendHttpRequest(url_getapps, "GET", params);
                Utils.printLogSystem(className, " getListAppInfo" + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            } //To change body of generated lambdas, choose Tools | Templates.

        };
    }
    
    @RouteInfo(method = "get", path = "/app/gets")
    public Route getListAppInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
//            String agentId = ServletUtil.getStringParameter(request, "agentId");

            try {
                HashMap<String, String> params = new HashMap();
//                params.put("agentId", agentId);
                String url_getapps = Configuration.url_app_gets;
                String result = HttpRequestUtils.sendHttpRequest(url_getapps, "GET", params);
                Utils.printLogSystem(className, " getListAppInfo" + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            } //To change body of generated lambdas, choose Tools | Templates.

        };
    }
    
    @RouteInfo(method = "get,post", path = "/app/add")
    public Route addNewApp() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
           // String agentId = ServletUtil.getStringParameter(request, "agentId");
            String appName = ServletUtil.getStringParameter(request, "appName");
            String appDesc = ServletUtil.getStringParameter(request, "appDesc");
//            String appIp = ServletUtil.getStringParameter(request, "appIp");
//            String appPort = ServletUtil.getStringParameter(request, "appPort");
            String agentIds = ServletUtil.getStringParameter(request, "agentIds");
            try {
                HashMap<String, String> params = new HashMap();
                params.put("appName", appName);
                params.put("appDesc", appDesc);
//                params.put("appIp", appIp);
//                params.put("appPort", appPort);
                params.put("agentIds", agentIds);
                String url_newapps = Configuration.url_app_add;
                String result = HttpRequestUtils.sendHttpRequest(url_newapps, "POST", params);
                Utils.printLogSystem(className, " addNewApp" + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            } //To change body of generated lambdas, choose Tools | Templates.

        };
    }
    
    @RouteInfo(method = "get,post", path = "/app/update")
    public Route updateApp() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String appId = ServletUtil.getStringParameter(request, "appId");
            String appName = ServletUtil.getStringParameter(request, "appName");
            String appDesc = ServletUtil.getStringParameter(request, "appDesc");
            String agentIds = ServletUtil.getStringParameter(request, "agentIds");
            try {
                HashMap<String, String> params = new HashMap();
                params.put("appName", appName);
                params.put("appDesc", appDesc);
                params.put("appId", appId);
                params.put("agentIds", agentIds);
                String result = HttpRequestUtils.sendHttpRequest(Config.getParam("url", "url_app_update"), "POST", params);
                Utils.printLogSystem(className, " updateApp" + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            } 

        };
    }
    
    @RouteInfo(method = "get,post", path = "/app/delete")
    public Route deleteApp() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String appId = ServletUtil.getStringParameter(request, "appId");

            try {
                HashMap<String, String> params = new HashMap();
                params.put("appId", appId);
                String url_getapps = Configuration.url_app_delete;
                String result = HttpRequestUtils.sendHttpRequest(url_getapps, "POST", params);
                Utils.printLogSystem(className, " deleteApp" + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            } 

        };
    }
}
