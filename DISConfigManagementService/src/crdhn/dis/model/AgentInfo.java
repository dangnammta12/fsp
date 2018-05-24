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
public class AgentInfo {

    private String agentId;
    private String agentName;
    private String agentDesc;
    private List<String> appIds = new ArrayList<String>();
    private boolean status;
    private String agentIp;
    private String agentUrl;
    private StatisticRequest statisticRequest;

    public AgentInfo() {
    }

    public AgentInfo(String agentId, String agentName, String agentDesc, boolean status, String agentIp, String agentUrl, StatisticRequest statisticRequest) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentDesc = agentDesc;
        this.status = status;
        this.agentIp = agentIp;
        this.agentUrl = agentUrl;
        this.statisticRequest = statisticRequest;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentDesc() {
        return agentDesc;
    }

    public void setAgentDesc(String agentDesc) {
        this.agentDesc = agentDesc;
    }

    public List<String> getAppIds() {
        return appIds;
    }

    public void setAppIds(List<String> appIds) {
        this.appIds = appIds;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAgentIp() {
        return agentIp;
    }

    public void setAgentIp(String agentIp) {
        this.agentIp = agentIp;
    }

    public String getAgentUrl() {
        return agentUrl;
    }

    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
    }

    public StatisticRequest getStatisticRequest() {
        return statisticRequest;
    }

    public void setStatisticRequest(StatisticRequest statisticRequest) {
        this.statisticRequest = statisticRequest;
    }

}
