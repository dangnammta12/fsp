/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Updates.set;
import com.mongodb.client.result.UpdateResult;

import crdhn.dis.model.AgentInfo;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.Utils;
import firo.utils.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;

/**
 *
 * @author nguyen
 */
public class AgentsConnector extends MongoDBConnector {

    private static AgentsConnector instance = null;
    private MongoCollection<Document> collection = null;

    public static AgentsConnector getInstance() {
        if (instance == null) {
            instance = new AgentsConnector();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParam("mongodb", "agent_collection_name"));
        }
        return instance;
    }

    public MongoCollection<Document> getCollection() {
        return instance.collection;
    }
//-----------------------------------------------------------------------------------//

    public DataResponse getAgentInfo(String agentId) {

        System.out.println("AgentsConnector::getAgentInfo() ");

        try {
            Bson projection = include("_id", "agentName", "agentDesc", "appIds", "status", "agentIp", "agentUrl", "lastPingTime");
            Document agentDoc = collection.find(eq("_id", agentId)).projection(projection).first();
            if (null == agentDoc) {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
            HashMap<String, Object> mapAgentInfo = mapAgentInfo(agentDoc);
            return new DataResponse(mapAgentInfo);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse getAgentInfoPing(String agentId, String agentIp) {

        System.out.println("AgentsConnector::getAgentInfoPing() ");

        try {
            Bson projection = include("_id", "agentName", "status", "agentIp");
            Document agentDoc = collection.find(eq("_id", agentId)).projection(projection).first();
            if (null == agentDoc) {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
            HashMap<String, Object> mapAgentInfo = mapAgentInfo(agentDoc);
            String agentIpOld = agentDoc.getString("agentIp");
            Bson update = set("lastPingTime", System.currentTimeMillis());
            if (!agentIp.isEmpty() && !agentIp.equals(agentIpOld)) {
                update = combine(set("lastPingTime", System.currentTimeMillis()), set("agentIp", agentIp));
            }
            collection.updateOne(eq("_id", agentId), update);
            String disConnectionInfo = ConfigurationConnector.getInstance().getDISConnectionInfo();
            mapAgentInfo.put("disConnectionInfo", disConnectionInfo);
            return new DataResponse(mapAgentInfo);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

//-----------------------------------------------------------------------------------//
    public DataResponse getListAgents() {

        System.out.println("AgentsConnector::getListAgentInfo()");
        try {
            ArrayList<HashMap<String, Object>> arrAgent = new ArrayList();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(Document doc) {
                    //           throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    HashMap<String, Object> mapAgent = mapAgentInfo(doc);
                    arrAgent.add(mapAgent);
                }
            };
            Bson projection = include("_id", "agentName", "agentDesc", "appIds", "status", "agentIp", "agentUrl", "lastPingTime");
            collection.find().projection(projection).sort(Sorts.ascending("agentName")).forEach(printBlock);

            return new DataResponse(arrAgent);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public ArrayList getListAgents(List<String> agentIds) {

        System.out.println("AgentsConnector::getListAgentInfo()");
        try {
            ArrayList<HashMap<String, Object>> arrAgent = new ArrayList();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(Document doc) {
                    //           throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    HashMap<String, Object> mapAgent = mapAgentInfo(doc);
                    arrAgent.add(mapAgent);
                }
            };
            Bson projection = include("_id", "agentName", "agentDesc", "appIds", "status", "agentIp", "agentUrl", "lastPingTime");
            collection.find(in("_id", agentIds)).projection(projection).sort(Sorts.ascending("agentName")).forEach(printBlock);

            return arrAgent;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return null;
        }
    }
//-----------------------------------------------------------------------------------//

    public DataResponse setAgentStatus(String agentId, boolean status) {
        System.out.println("AgentsConnector.setAgentStatus() agentId=" + agentId + "\t status=" + status);

        try {
            UpdateResult result = collection.updateOne(eq("_id", agentId), set("status", status));
            if (result != null && result.getMatchedCount() > 0) {
                return DataResponse.SUCCESS;
            } else {
                return DataResponse.PARAM_ERROR;
            }
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }
//-----------------------------------------------------------------------------------//

    public DataResponse updateAgent(String agentId, AgentInfo agentInfo) {

        System.out.println("AgentsConnector::updateAgent()");

        try {
            Bson update = combine(set("agentName", agentInfo.getAgentName()), set("agentDesc", agentInfo.getAgentDesc()), set("agentUrl", agentInfo.getAgentUrl()));
            UpdateResult result = collection.updateOne(eq("_id", agentId), update);

            if (result != null && result.getMatchedCount() > 0) {
                return DataResponse.SUCCESS;
            } else {
                return DataResponse.PARAM_ERROR;
            }
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }
//-----------------------------------------------------------------------------------//

    public DataResponse addAgent(AgentInfo agentInfo) {

        System.out.println("AgentsConnector::addAgent()");

        try {
//            Document disDoc = ConfigurationConnector.getInstance().disCollection.find().first();
//            if (!Utils.checkIps(agentInfo.getAgentIp(), disDoc, "agentAcceptedIp")) {
//                return DataResponse.AGENT_IP_NOT_ACCEPTED;
//            }

            //  long agentId = MongoDBConnector.getInstance().getNextValue(Configuration.AGENT_ID_COUNTER_KEY, database);
            String agentId = UUID.randomUUID().toString();
            Document agent = new Document("_id", agentId)
                    .append("agentName", agentInfo.getAgentName())
                    .append("agentDesc", agentInfo.getAgentDesc())
                    .append("status", true)
                    .append("agentIp", "")
                    .append("agentUrl", agentInfo.getAgentUrl())
                    .append("appIds", agentInfo.getAppIds())
                    .append("lastPingTime", -1l)
                    .append("statisticRequest", agentInfo.getStatisticRequest());

            collection.insertOne(agent);
            HashMap<String, String> result = new HashMap();
            result.put("agentId", agentId);
            return new DataResponse(result);

        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

//-----------------------------------------------------------------------------------//
    public DataResponse deleteAgent(String agentId) {
        System.out.println("AgentsConnector::deleteAgent() = " + agentId);
        try {
            Document agentDoc = collection.find(eq("_id", agentId)).first();
            List<String> appIds = (List<String>) agentDoc.get("appIds");
            Boolean status = agentDoc.getBoolean("status");
            if (!appIds.isEmpty() || true == status) {
                return DataResponse.CANNOT_DELETE_ITEM;
            }
            collection.deleteOne(agentDoc);

            return DataResponse.SUCCESS;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }
//-----------------------------------------------------------------------------------//

    public DataResponse addAppIDToAgent(String appId, List<String> agentIds) {

        System.out.println("AgentsConnector::addAppIDToAgent");

        try {
            UpdateResult result = collection.updateMany(in("_id", agentIds), Updates.push("appIds", appId));
            if (result != null && result.getMatchedCount() > 0) {
                return DataResponse.SUCCESS;
            } else {
                return DataResponse.PARAM_ERROR;
            }

        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

//-----------------------------------------------------------------------------------//
//    public DataResponse removeAppFromAgent(String agentId, String appId) {
//        System.out.println("AgentsConnector::removeApp()");
//
//        try {
//            UpdateResult result = collection.updateOne(eq("_id", agentId), Updates.pull("appIds", appId));
//            if (result != null && result.getMatchedCount() > 0) {
//                return DataResponse.SUCCESS;
//            } else {
//                return DataResponse.PARAM_ERROR;
//            }
//
//        } catch (Exception ex) {
//            StackTraceElement traceElement = ex.getStackTrace()[0];
//            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
//            return new DataResponse(-1, ex.getMessage());
//        }
//    }
//-----------------------------------------------------------------------------------//
    private HashMap<String, Object> mapAgentInfo(final Document doc) {
        HashMap<String, Object> mapAgent = new HashMap();
        mapAgent.put("agentId", doc.getString("_id"));
        if (doc.containsKey("agentName")) {
            mapAgent.put("agentName", doc.getString("agentName"));
        }
        if (doc.containsKey("agentDesc")) {
            mapAgent.put("agentDesc", doc.getString("agentDesc"));
        }
        if (doc.containsKey("appIds")) {
            mapAgent.put("appIds", doc.get("appIds"));
        }
        if (doc.containsKey("agentIp")) {
            mapAgent.put("agentIp", doc.getString("agentIp"));
        }
        if (doc.containsKey("status")) {
            mapAgent.put("status", doc.getBoolean("status"));
        }
        if (doc.containsKey("agentUrl")) {
            mapAgent.put("agentUrl", doc.getString("agentUrl"));
        }
        Long lastTime = doc.getLong("lastPingTime");
        long currentTime = System.currentTimeMillis();
        long remain = 0l;
        if (lastTime != null && lastTime > 0l) {
            remain = currentTime - lastTime;
        }
        String timeText = Utils.convertMiliTimeToText(remain);
        mapAgent.put("lastPingTimeNumber", remain);
        mapAgent.put("lastPingTime", timeText);
        if (doc.containsKey("statisticRequest")) {
            mapAgent.put("statisticRequest", doc.get("statisticRequest"));
        }

        return mapAgent;
    }
}
