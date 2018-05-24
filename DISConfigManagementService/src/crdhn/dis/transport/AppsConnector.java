/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import crdhn.dis.model.AppInfo;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.Utils;
import firo.utils.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import crdhn.dis.model.AgentInfo;

/**
 *
 * @author nguyen
 */
public class AppsConnector extends MongoDBConnector {

    private static AppsConnector instance = null;

    private MongoCollection<Document> collection = null;

    public static AppsConnector getInstance() {
        if (null == instance) {
            instance = new AppsConnector();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParam("mongodb", "app_collection_name"));
        }

        return instance;
    }

    public DataResponse getAppInfo(String appId) {
        System.out.println("AgentDBConnector.getAppInfo() ");
        try {
            Bson projection = include("_id", "appName", "appDesc", "agentIds");
            Document doc = collection.find(eq("_id", appId)).projection(projection).first();
            HashMap<String, Object> mapApps = mapAppInfo(doc);
            List<String> agentIds = (ArrayList) mapApps.get("agentIds");
            ArrayList arrAgent = AgentsConnector.getInstance().getListAgents(agentIds);
            mapApps.put("agents", arrAgent);
            return new DataResponse(mapApps);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------//
    public DataResponse getListAppInfo() {
        System.out.println("AgentDBConnector.getListAppInfo() ");
        try {
            ArrayList<HashMap<String, Object>> arrApp = new ArrayList<HashMap<String, Object>>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(Document doc) {
                    HashMap<String, Object> mapApps = mapAppInfo(doc);
                    List<String> agentIds = (ArrayList) mapApps.get("agentIds");
                    ArrayList arrAgent = AgentsConnector.getInstance().getListAgents(agentIds);
                    mapApps.put("agents", arrAgent);
                    arrApp.add(mapApps);
                }
            };

            Bson projection = include("_id", "appName", "appDesc", "agentIds");
            collection.find().projection(projection).sort(Sorts.ascending("appName")).forEach(printBlock);

            return new DataResponse(arrApp);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------//
    public DataResponse addNewApp(AppInfo appInfo) {
        System.out.println("AppDBConnector.addNewApp()");

        try {
            String id = UUID.randomUUID().toString();
            Document app = new Document("_id", id)
                    .append("appName", appInfo.getAppName())
                    .append("appDesc", appInfo.getAppDesc())
                    //                    .append("appIp", appInfo.getAppIp())
                    //                    .append("appPort", appInfo.getAppPort())
                    .append("agentIds", appInfo.getAgentIds())
                    .append("statisticRequest", appInfo.getStatisticRequest());

            collection.insertOne(app);

            MongoCollection<Document> agentCollection = AgentsConnector.getInstance().getCollection();
            UpdateResult result = agentCollection.updateMany(in("_id", appInfo.getAgentIds()), Updates.push("appIds", id));
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

    public DataResponse updateAppInfo(AppInfo appInfo) {

        System.out.println("AppsConnector::updateAppInfo()");

        try {

            Bson update;
            if (appInfo.getAgentIds() != null && appInfo.getAgentIds().size() > 0) {
                update = combine(set("appName", appInfo.getAppName()), set("appDesc", appInfo.getAppDesc()), set("agentIds", appInfo.getAgentIds()));
            } else {
                update = combine(set("appName", appInfo.getAppName()), set("appDesc", appInfo.getAppDesc()));
            }
            UpdateResult result = collection.updateOne(eq("_id", appInfo.getAppId()), update);

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

    public DataResponse deleteAppInfo(String appId) {
        System.out.println("AgentDBConnector.deleteAppInfo() ");
        try {
            DeleteResult result = collection.deleteOne(eq("_id", appId));
            if (result.getDeletedCount() > 0) {
                return DataResponse.SUCCESS;
            } else {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------//
    private HashMap<String, Object> mapAppInfo(final Document doc) {
        HashMap<String, Object> mapApp = new HashMap<String, Object>();
        mapApp.put("appId", doc.getString("_id"));
        mapApp.put("appName", doc.getString("appName"));
        mapApp.put("appDesc", doc.getString("appDesc"));
        mapApp.put("agentIds", doc.get("agentIds"));
        if (doc.get("statisticRequest") != null) {
            mapApp.put("statisticRequest", doc.get("statisticRequest"));
        }

        return mapApp;
    }
}
