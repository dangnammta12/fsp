/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.render.RenderMain;
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
import java.util.logging.Level;

/**
 *
 * @author nguyen
 */
public class AgentController extends Controller {

    private static final String className = "[AgentController]";

    public AgentController() {
    }
    
    @RouteInfo(method = "get", path = "/")
    public Route renderHome() {
        return (Request request, Response response) -> {
            return RenderMain.getInstance().renderHome(request, response, "home.ftl");
        };
    }

    @RouteInfo(method = "get", path = "/agent/get")
    public Route getAgentInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String agentId = ServletUtil.getStringParameter(request, "agentId");
            if ("".equals(agentId)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentId", agentId);
                    String url_get = Configuration.url_agent_get;
                    String result = HttpRequestUtils.sendHttpRequest(url_get, "GET", params);
                    Utils.printLogSystem(className, " getAgentInfo" + "\t  resp=" + result);

                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }

    @RouteInfo(method = "get", path = "/agent/gets")
    public Route getListAgents() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String url_gets = Configuration.url_agent_gets;
                String result = HttpRequestUtils.sendHttpRequest(url_gets, "GET", new HashMap());
                Utils.printLogSystem(className, " getListAgents" + "\t  resp=" + result);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            } //To change body of generated lambdas, choose Tools | Templates.
        };
    }

    @RouteInfo(method = "get,post", path = "/agent/update")
    public Route updateAgentInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String agentId = ServletUtil.getStringParameter(request, "agentId");
            String agentName = ServletUtil.getStringParameter(request, "agentName");
            String agentDesc = ServletUtil.getStringParameter(request, "agentDesc");
//            String agentIp = ServletUtil.getStringParameter(request, "agentIp");
            String agentUrl = ServletUtil.getStringParameter(request, "agentUrl");
            if (agentId.isEmpty() || agentName.isEmpty() || agentUrl.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentId", agentId);
                    params.put("agentName", agentName);
                    params.put("agentDesc", agentDesc);
//                    params.put("agentIp", agentIp);
                    params.put("agentUrl", agentUrl);

                    String url_update = Configuration.url_agent_update;
                    String result = HttpRequestUtils.sendHttpRequest(url_update, "POST", params);
                    Utils.printLogSystem(className, " updateAgentInfo" + "\t  resp=" + result);

                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/agent/changestatus")
    public Route setAgentStatus() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String agentId = ServletUtil.getStringParameter(request, "agentId");
            String status = ServletUtil.getStringParameter(request, "status");
            if (agentId.isEmpty() || status.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentId", agentId);
                    params.put("status", status);
                    String url_status = Configuration.url_agent_status;
                    String result = HttpRequestUtils.sendHttpRequest(url_status, "POST", params);
                    Utils.printLogSystem(className, " setAgentStatus" + "\t  resp=" + result);

                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/agent/add")
    public Route addAgent() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String agentName = ServletUtil.getStringParameter(request, "agentName");
            String agentDesc = ServletUtil.getStringParameter(request, "agentDesc");
//            String agentIp = ServletUtil.getStringParameter(request, "agentIp");
            String agentUrl = ServletUtil.getStringParameter(request, "agentUrl");
            //   List<String> appIds = ServletUtil.getListStringParameter(request, "appIds", ",");
            if (agentName.isEmpty() || agentUrl.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {

                    HashMap<String, String> params = new HashMap();
//                    params.put("agentIp", agentIp);
                    params.put("agentUrl", agentUrl);
                    params.put("agentName", agentName);
                    params.put("agentDesc", agentDesc);

                    String url_create = Configuration.url_agent_add;
                    String result = HttpRequestUtils.sendHttpRequest(url_create, "POST", params);
                    Utils.printLogSystem(className, " addAgent" + "\t  resp=" + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/agent/delete")
    public Route deleteAgent() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            String agentId = ServletUtil.getStringParameter(request, "agentId");
            if (agentId.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentId", agentId);

                    String url_delete = Configuration.url_agent_delete;
                    String result = HttpRequestUtils.sendHttpRequest(url_delete, "POST", params);
                    Utils.printLogSystem(className, " deleteAgent" + "\t  resp=" + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                } //To change body of generated lambdas, choose Tools | Templates.
            }

        };
    }

}
