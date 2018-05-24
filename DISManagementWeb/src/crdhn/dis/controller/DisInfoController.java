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
import java.util.HashMap;

/**
 *
 * @author nguyen
 */
public class DisInfoController extends Controller {
    
    private static final String className = "[DisInfoController]";

    public DisInfoController() {
    }
    
    @RouteInfo(method = "get", path = "/getdis")
    public Route getDisInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String url_disInfo = Configuration.url_disInfo;
                String result = HttpRequestUtils.sendHttpRequest(url_disInfo, "GET", new HashMap());
                Utils.printLogSystem(className, " displayDisInfo: " + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }
    
    @RouteInfo(method = "get,post", path = "/updateURL")
    public Route updateDisConnectionInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String disConnectionInfo = ServletUtil.getStringParameter(request, "disConnectionInfo");
            if ("".equals(disConnectionInfo)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("disConnectionInfo", disConnectionInfo);
                    String url_disURL = Configuration.url_disURL;
                    String result = HttpRequestUtils.sendHttpRequest(url_disURL, "POST", params);
                    Utils.printLogSystem(className, " updateDisConnectionInfo: " + "\t  resp=" + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }
    
    @RouteInfo(method = "get,post", path = "/addIp")
    public Route addAgentAcceptIps() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String ipStr = ServletUtil.getStringParameter(request, "agentAcceptedIp");
            if ("".equals(ipStr)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentAcceptedIp", ipStr);
                    String url_disAddIps = Configuration.url_disAddIps;
                    String result = HttpRequestUtils.sendHttpRequest(url_disAddIps, "POST", params);
                    Utils.printLogSystem(className, " addAgentAcceptIps: " + "\t  resp=" + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }
    
    @RouteInfo(method = "get,post", path = "/removeIp")
    public Route removeAgentAcceptIps() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String ipStr = ServletUtil.getStringParameter(request, "agentAcceptedIp");
            if ("".equals(ipStr)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentAcceptedIp", ipStr);
                    String url_disRmvIps = Configuration.url_disMoveIps;
                    String result = HttpRequestUtils.sendHttpRequest(url_disRmvIps, "POST", params);
                    Utils.printLogSystem(className, " removeAgentAcceptIps: " + "\t  resp=" + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }
    
//    @RouteInfo(method = "get", path = "/config")
//    public Route renderHome() {
//        return (Request request, Response response) -> {
//            return RenderMain.getInstance().renderHome(request, response,"disinfo.ftl");
//        };
//    }
}
