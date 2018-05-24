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
public class DisInfo {
    
    private String disId;
    private List<String> agentAcceptedIp = new ArrayList<String>();    
    private String disPublicKey;
    private String disPrivateKey;
    private String agentPublicKey;
    private String agentPrivateKey;
    private String disConnectionInfo;
    private StatisticRequest statisticRequest;

    public DisInfo() {
    }

    public DisInfo(String disId, String disPublicKey, String disPrivateKey, String agentPublicKey, String agentPrivateKey, String disConnectionInfo, StatisticRequest statisticRequest) {
        this.disId = disId;
        this.disPublicKey = disPublicKey;
        this.disPrivateKey = disPrivateKey;
        this.agentPublicKey = agentPublicKey;
        this.agentPrivateKey = agentPrivateKey;
        this.disConnectionInfo = disConnectionInfo;
        this.statisticRequest = statisticRequest;
    }

    public String getDisId() {
        return disId;
    }

    public List<String> getAgentAcceptedIp() {
        return agentAcceptedIp;
    }

    public void setAgentAcceptedIp(List<String> agentAcceptedIp) {
        this.agentAcceptedIp = agentAcceptedIp;
    }

    public String getDisPublicKey() {
        return disPublicKey;
    }

    public void setDisPublicKey(String disPublicKey) {
        this.disPublicKey = disPublicKey;
    }

    public String getDisPrivateKey() {
        return disPrivateKey;
    }

    public void setDisPrivateKey(String disPrivateKey) {
        this.disPrivateKey = disPrivateKey;
    }

    public String getAgentPublicKey() {
        return agentPublicKey;
    }

    public void setAgentPublicKey(String agentPublicKey) {
        this.agentPublicKey = agentPublicKey;
    }

    public String getAgentPrivateKey() {
        return agentPrivateKey;
    }

    public void setAgentPrivateKey(String agentPrivateKey) {
        this.agentPrivateKey = agentPrivateKey;
    }

    public String getDisConnectionInfo() {
        return disConnectionInfo;
    }

    public void setDisConnectionInfo(String disConnectionInfo) {
        this.disConnectionInfo = disConnectionInfo;
    }

    public StatisticRequest getStatisticRequest() {
        return statisticRequest;
    }

    public void setStatisticRequest(StatisticRequest statisticRequest) {
        this.statisticRequest = statisticRequest;
    }
 
    
}
