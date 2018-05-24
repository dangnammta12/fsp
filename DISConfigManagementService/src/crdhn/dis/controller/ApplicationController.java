/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.model.AppInfo;
import crdhn.dis.transport.AppsConnector;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.util.List;

/**
 *
 * @author nguyen
 */
public class ApplicationController extends Controller {

    private static final String className = "[ApplicationController]";

    public ApplicationController() {
        rootPath = "/app";
    }

    @RouteInfo(method = "get", path = "/gets")
    public Route getListAppInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
//                String agentId = ServletUtil.getStringParameter(request, "agentId");
                Utils.printLogSystem(className, " getListAppInfo");
                return AppsConnector.getInstance().getListAppInfo();
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/add")
    public Route addNewApp() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String appName = ServletUtil.getStringParameter(request, "appName");
                String appDesc = ServletUtil.getStringParameter(request, "appDesc");
//                String appIp = ServletUtil.getStringParameter(request, "appIp");
//                String appPort = ServletUtil.getStringParameter(request, "appPort");
//                String agentIds = ServletUtil.getStringParameter(request, "agentIds");
                List<String> agentIdLst = ServletUtil.getListStringParameter(request, "agentIds", ",");
                Utils.printLogSystem(className, " addNewApp");
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName(appName);
                appInfo.setAppDesc(appDesc);
//                appInfo.setAppIp(appIp);
//                appInfo.setAppPort(appPort);
                appInfo.setAgentIds(agentIdLst);
                Utils.printLogSystem(className, " addNewApp");
                return AppsConnector.getInstance().addNewApp(appInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/update")
    public Route updateAppInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " updateAppInfo");
                String appId = ServletUtil.getStringParameter(request, "appId");
                String appName = ServletUtil.getStringParameter(request, "appName");
                String appDesc = ServletUtil.getStringParameter(request, "appDesc");
                List<String> agentIds = ServletUtil.getListStringParameter(request, "agentIds", ",");
                if (appId.isEmpty() || appName.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                AppInfo appInfo = new AppInfo();
                appInfo.setAppId(appId);
                appInfo.setAppName(appName);
                appInfo.setAppDesc(appDesc);
                appInfo.setAgentIds(agentIds);

                return AppsConnector.getInstance().updateAppInfo(appInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/delete")
    public Route deleteAppInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String appId = ServletUtil.getStringParameter(request, "appId");
                Utils.printLogSystem(className, " deleteAppInfo");

                return AppsConnector.getInstance().deleteAppInfo(appId);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }
}
