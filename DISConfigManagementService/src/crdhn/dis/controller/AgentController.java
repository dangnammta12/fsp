/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.model.AgentInfo;
import crdhn.dis.transport.AgentsConnector;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nguyen
 */
public class AgentController extends Controller {

    private static final String className = "[AgentController]";

    public AgentController() {
        rootPath = "/agent";
    }

    @RouteInfo(method = "get", path = "/get")
    public Route getAgentInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " getAgentInfo");
                String agentId = ServletUtil.getStringParameter(request, "agentId");
                if (agentId.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                return AgentsConnector.getInstance().getAgentInfo(agentId);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get", path = "/ping")
    public Route Ping() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " ping ");
                String agentId = ServletUtil.getStringParameter(request, "agentKey");
                String agentIP = ServletUtil.getStringParameter(request, "agentIp");
                if (agentId.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }

                return AgentsConnector.getInstance().getAgentInfoPing(agentId, agentIP);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get", path = "/gets")
    public Route getListAgents() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " getListAgents");
                return AgentsConnector.getInstance().getListAgents();
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/update")
    public Route updateAgentInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " updateAgentInfo");
                String agentId = ServletUtil.getStringParameter(request, "agentId");
                String agentName = ServletUtil.getStringParameter(request, "agentName");
                String agentDesc = ServletUtil.getStringParameter(request, "agentDesc");
//                String agentIp = ServletUtil.getStringParameter(request, "agentIp");
                String agentUrl = ServletUtil.getStringParameter(request, "agentUrl");
                if (agentId.isEmpty() || agentName.isEmpty() || agentUrl.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                AgentInfo agentInfo = new AgentInfo();
                agentInfo.setAgentName(agentName);
                agentInfo.setAgentDesc(agentDesc);
//                agentInfo.setAgentIp(agentIp);
                agentInfo.setAgentUrl(agentUrl);

                return AgentsConnector.getInstance().updateAgent(agentId, agentInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/changestatus")
    public Route setAgentStatus() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " setAgentStatus");
                String agentId = ServletUtil.getStringParameter(request, "agentId");
                Boolean status = ServletUtil.getBooleanParameter(request, "status");
                if (agentId.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                return AgentsConnector.getInstance().setAgentStatus(agentId, status);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/add")
    public Route addAgent() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " addAgent");
                String agentName = ServletUtil.getStringParameter(request, "agentName");
                String agentDesc = ServletUtil.getStringParameter(request, "agentDesc");
//                String agentIp = ServletUtil.getStringParameter(request, "agentIp");
                String agentUrl = ServletUtil.getStringParameter(request, "agentUrl");
                List<String> appIds = ServletUtil.getListStringParameter(request, "appIds", ",");
                if (agentName.isEmpty() || agentUrl.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                if (appIds == null) {
                    appIds = new ArrayList<String>();
                }
                AgentInfo agentInfo = new AgentInfo();
                agentInfo.setAgentName(agentName);
                agentInfo.setAgentDesc(agentDesc);
                agentInfo.setAppIds(appIds);
//                agentInfo.setAgentIp(agentIp);
                agentInfo.setAgentUrl(agentUrl);
                return AgentsConnector.getInstance().addAgent(agentInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/delete")
    public Route deleteAgent() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " deleteAgent");
                String agentId = ServletUtil.getStringParameter(request, "agentId");
                if (agentId.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                return AgentsConnector.getInstance().deleteAgent(agentId);
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

//    @RouteInfo(method = "get,post", path = "/removeApp")
//    public Route removeAppFromAgent() {
//        return (Request request, Response response) -> {
//            response.header("Content-Type", "application/json");
//            try {
//                Utils.printLogSystem(className, " removeAppFromAgent");
//                String agentId = ServletUtil.getStringParameter(request, "agentId");
//                String apptId = ServletUtil.getStringParameter(request, "apptId");
//
//                return AgentsConnector.getInstance().removeAppFromAgent(agentId, apptId);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return DataResponse.UNKNOWN_EXCEPTION;
//            } //To change body of generated lambdas, choose Tools | Templates.
//        };
//    }
//    @RouteInfo(method = "get", path = "/getdis")
//    public Route getDisInfo() {
//        return (Request request, Response response) -> {
//            response.header("Content-Type", "application/json");
//            try {
//                Utils.printLogSystem(className, " displayDisInfo");
//                return DisDBConnector.getInstance().getDisInfo();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return DataResponse.UNKNOWN_EXCEPTION;
//            } //To change body of generated lambdas, choose Tools | Templates.
//        };
//    }
//    @RouteInfo(method = "get", path = "/getapps")
//    public Route getListAppInfo() {
//        return (Request request, Response response) -> {
//            response.header("Content-Type", "application/json");
//            try {
//                String agentId = ServletUtil.getStringParameter(request, "agentId");
//                Utils.printLogSystem(className, " getListAppInfo");
//                return AppDBConnector.getInstance().getListAppInfo(agentId);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return DataResponse.UNKNOWN_EXCEPTION;
//            } //To change body of generated lambdas, choose Tools | Templates.
//        };
//    }
}
