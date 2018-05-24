/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.client.MongoCollection;
import crdhn.dis.configuration.Configuration;
import firo.utils.config.Config;
import java.util.Date;
import java.util.HashMap;
import org.bson.Document;

/**
 *
 * @author namdv
 */
public class HistoriesCollection {

    private static HistoriesCollection instance = null;
    private MongoCollection<Document> collection = null;

    public static final String ACTION_CREATE_FOLDER = "ACTION_CREATE_FOLDER";
    public static final String ACTION_CHANGENAME = "ACTION_CHANGENAME";
    public static final String ACTION_ADD_FILE = "ACTION_ADD_FILE";
    public static final String ACTION_MOVE_FILE = "ACTION_MOVE_FILE";
    public static final String ACTION_MOVE_FOLDER = "ACTION_MOVE_FOLDER";
    public static final String ACTION_DELETE_FILE = "ACTION_DELETE_FILE";
    public static final String ACTION_DELETE_FOLDER = "ACTION_DELETE_FOLDER";
    public static final String ACTION_SHARE_FILE = "ACTION_SHARE_FILE";
    public static final String ACTION_SHARE_FOLDER = "ACTION_SHARE_FOLDER";
    public static final String ACTION_GET_USER_ACCESS = "ACTION_GET_USER_ACCESS";
    public static final String ACTION_UPDATE_USER_ACCESS = "ACTION_UPDATE_USER_ACCESS";

    public static HistoriesCollection getInstance() {
        if (instance == null) {
            instance = new HistoriesCollection();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "histories_collection", "Histories"));
        }
        return instance;
    }

    public void addHistory(String appKey, String accountName, String actionType, HashMap<String, Object> params) {
        Document document = new Document()
                .append("time", new Date())
                .append("appKey", appKey)
                .append("accountName", accountName)
                .append("type", actionType);
        params.keySet().stream().forEach((key) -> {
            document.append(key, params.get(key));
        });
        try {
            collection.insertOne(document);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(
                    Configuration.SERVICE_NAME,
                    ex.getStackTrace()[0].toString(),
                    ex.toString());
            System.out.println(ex.toString());
            System.out.println(document.toJson());
        }
    }
}
