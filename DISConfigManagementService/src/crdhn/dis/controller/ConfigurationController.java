/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.transport.ConfigurationConnector;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;

/**
 *
 * @author nguyen
 */
public class ConfigurationController extends Controller {
    private static final String className = "[DisController]";

    public ConfigurationController() {
        rootPath = "/dis";
    }
    
    @RouteInfo(method = "get", path = "/get")
    public Route getDisInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " displayDisInfo");
                return ConfigurationConnector.getInstance().getDisInfo();
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/updateURL")
    public Route updateDisConnectionInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String url = ServletUtil.getStringParameter(request, "disConnectionInfo");
                Utils.printLogSystem(className, " updateDisConnectionInfo");
                return ConfigurationConnector.getInstance().updateDisConnectionInfo(url);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/addIp")
    public Route addAgentAcceptIps() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String ipStr = ServletUtil.getStringParameter(request, "agentAcceptedIp");
//                List<String> ipLst = new ArrayList<String>(Arrays.asList(ipStr.split(";")));
                Utils.printLogSystem(className, " addAgentAcceptIps");
                return ConfigurationConnector.getInstance().addAgentAcceptIps(ipStr);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/removeIp")
    public Route removeAgentAcceptIps() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String ipStr = ServletUtil.getStringParameter(request, "agentAcceptedIp");
//                List<String> ipLst = new ArrayList<String>(Arrays.asList(ipStr.split(";")));
                Utils.printLogSystem(className, " removeAgentAcceptIps");
                return ConfigurationConnector.getInstance().removeAgentAcceptIps(ipStr);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }
}
