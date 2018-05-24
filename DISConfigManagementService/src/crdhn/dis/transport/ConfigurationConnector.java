/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.GenerateKeys;
import crdhn.dis.utils.Utils;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.types.Binary;
import firo.utils.config.Config;
import static com.mongodb.client.model.Filters.eq;

/**
 *
 * @author namdv
 */
public class ConfigurationConnector extends MongoDBConnector {

    private static ConfigurationConnector instance = null;
    public MongoCollection<Document> collection = null;

    public static ConfigurationConnector getInstance() {
        if (instance == null) {
            instance = new ConfigurationConnector();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParam("mongodb", "config_collection_name"));
        }
        return instance;
    }

//-----------------------------------------------------------------------------------//
    public static void checkKeyPairIsExist() {
        try {
            Document doc = ConfigurationConnector.getInstance().collection.find().first();
            if (null != doc) {
                System.out.println("PPKey existed!");
                return;
            }

            GenerateKeys gk;
            gk = new GenerateKeys(2048);//16384
            gk.createKeys();
            byte[] disPublicKey = gk.getPublicKey().getEncoded();
            byte[] disPrivateKey = gk.getPrivateKey().getEncoded();

            gk = new GenerateKeys(2048);
            gk.createKeys();
            byte[] agentPublicKey = gk.getPublicKey().getEncoded();
            byte[] agentPrivateKey = gk.getPrivateKey().getEncoded();
            String keyDis = UUID.randomUUID().toString();
            List<String> listIP = new ArrayList();
            listIP.add("127.0.0.1");
//            listIP.add("10.86.222.16");
            Document newDis = new Document("_id", keyDis)
                    .append("disPublicKey", disPublicKey)
                    .append("disPrivateKey", disPrivateKey)
                    .append("agentPublicKey", agentPublicKey)
                    .append("agentPrivateKey", agentPrivateKey)
                    .append("agentAcceptedIp", listIP)
                    .append("disConnectionInfo", "http://127.0.0.1:1111");
            ConfigurationConnector.getInstance().collection.insertOne(newDis);
        } catch (Exception ex) {
            Logger.getLogger(AgentsConnector.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    public DataResponse getDisInfo() {
        System.out.println("ConfigurationConnector.getDisInfo()");

        try {
            HashMap<String, Object> mapDisInfo = new HashMap<String, Object>();
            Document disDoc = collection.find().first();

            if (null == disDoc) {

                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
//            mapDisInfo.put("disId", disDoc.getLong("_id"));
            mapDisInfo.put("agentAcceptedIp", (List<String>) disDoc.get("agentAcceptedIp"));
            mapDisInfo.put("disPublicKey", Base64.getUrlEncoder().encodeToString(((Binary) disDoc.get("disPublicKey")).getData()));
            mapDisInfo.put("disPrivateKey", Base64.getUrlEncoder().encodeToString(((Binary) disDoc.get("disPrivateKey")).getData()));
            mapDisInfo.put("agentPublicKey", Base64.getUrlEncoder().encodeToString(((Binary) disDoc.get("agentPublicKey")).getData()));
            mapDisInfo.put("agentPrivateKey", Base64.getUrlEncoder().encodeToString(((Binary) disDoc.get("agentPrivateKey")).getData()));
//            mapDisInfo.put("statisticRequest", disDoc.getString("statisticRequest"));
            mapDisInfo.put("disConnectionInfo", disDoc.getString("disConnectionInfo"));
            return new DataResponse(mapDisInfo);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public String getDISConnectionInfo() {
        System.out.println("ConfigurationConnector.getDISConnectionInfo()");
        try {
            Document disDoc = collection.find().first();
            if (disDoc == null) {
                return null;
            } else {
                return disDoc.getString("disConnectionInfo");
            }
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return null;
        }
    }
//-----------------------------------------------------------------------------------//

    public DataResponse updateDisConnectionInfo(String disConnectionInfo) {

        System.out.println("ConfigurationConnector.updateDisConnectionInfo()");
        try {
            Document doc = collection.find().first();
            UpdateResult result = collection.updateOne(eq("_id", doc.getString("_id")), Updates.set("disConnectionInfo", disConnectionInfo));
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
    public DataResponse addAgentAcceptIps(String ip) {
        System.out.println("ConfigurationConnector::addAgentAcceptIps()");

        try {
            Document doc = collection.find().first();
            UpdateResult result = collection.updateOne(eq("_id", doc.getString("_id")), Updates.push("agentAcceptedIp", ip));
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
    public DataResponse removeAgentAcceptIps(String ip) {
        System.out.println("ConfigurationConnector:::removeAcceptedIpinDis()");

        try {
            Document doc = collection.find().first();
            UpdateResult result = collection.updateOne(eq("_id", doc.getString("_id")), Updates.pull("agentAcceptedIp", ip));
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
}
