/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import crdhn.utils.DataResponse;
import crdhn.utils.Utils;
import java.util.HashMap;
import firo.utils.config.Config;

/**
 *
 * @author namdv
 */
public class UsersCollection extends MongoDBConnector {

    private static UsersCollection instance = null;
    private MongoCollection<Document> collection = null;

    public static UsersCollection getInstance() {
        if (instance == null) {
            instance = new UsersCollection();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "users_collection", "Users"));
            try {
                instance.collection.createIndex(new BasicDBObject("appKey", 1).append("accountName", 1));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }

    public DataResponse createRootDirectory(String appKey, String accountName, long rootOwner, long rootShared) {
        System.out.println("UserCollection::createRootDirectory appKey=" + appKey + "\t accountName=" + accountName);
        try {

            HashMap<String, Long> directorys = new HashMap();
            directorys.put("root_owner", rootOwner);
            directorys.put("root_shared", rootShared);
            Document userDocument = new Document("appKey", appKey)
                    .append("accountName", accountName)
                    .append("directorys", directorys);
            collection.insertOne(userDocument);
            return new DataResponse(directorys);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            if (e.getCode() == 11000) {
                return DataResponse.MONGO_USER_EXISTED;
            } else {
                return DataResponse.MONGO_WRITE_EXCEPTION;
            }
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse getRootDirectory(String appKey, String accountName) {
        System.out.println("UserCollection::getRootDirectory accountName=" + accountName);
        try {
            Document userDocument = collection.find(Updates.combine(eq("appKey", appKey), eq("accountName", accountName))).first();
            if (userDocument == null) {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
            Document directory = (Document) userDocument.get("directorys");
            HashMap<String, Long> resp = new HashMap();
            resp.put("root_owner", directory.getLong("root_owner"));
            resp.put("root_shared", directory.getLong("root_shared"));
            return new DataResponse(resp);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

}
