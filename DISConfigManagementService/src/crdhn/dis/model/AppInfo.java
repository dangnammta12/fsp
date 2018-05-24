/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nguyen
 */
public class AppInfo {

    private String appId;
    private String appName;
    private String appDesc;
    private List<String> agentIds = new ArrayList<String>();
//    private String appIp;
//    private String appPort;
    private StatisticRequest statisticRequest;

    public AppInfo() {
    }

    public AppInfo(String appId, String appName, String appDesc, StatisticRequest statisticRequest) {
        this.appId = appId;
        this.appName = appName;
        this.appDesc = appDesc;
//        this.appIp = appIp;
//        this.appPort = appPort;
        this.statisticRequest = statisticRequest;
    }

    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public List<String> getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(List<String> agentIds) {
        this.agentIds = agentIds;
    }

    public StatisticRequest getStatisticRequest() {
        return statisticRequest;
    }

    public void setStatisticRequest(StatisticRequest statisticRequest) {
        this.statisticRequest = statisticRequest;
    }

}
