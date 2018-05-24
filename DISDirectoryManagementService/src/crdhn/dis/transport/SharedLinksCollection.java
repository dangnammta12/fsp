/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.result.UpdateResult;
import crdhn.dis.configuration.Configuration;
import crdhn.utils.DataResponse;
import crdhn.utils.Utils;
import java.util.HashMap;
import firo.utils.config.Config;
import static com.mongodb.client.model.Updates.combine;

/**
 *
 * @author namdv
 */
public class SharedLinksCollection {

    private static SharedLinksCollection instance = null;
    private MongoCollection<Document> collection = null;

    public static final int PUBLIC_VIEW = Config.getParamInt("permission", "PUBLIC_VIEW");
    public static final int PUBLIC_EDIT = Config.getParamInt("permission", "PUBLIC_EDIT");
    public static final int ONLY_PEPOLE_SHARED = Config.getParamInt("permission", "ONLY_PEPOLE_SHARED");

    public static SharedLinksCollection getInstance() {
        if (instance == null) {
            instance = new SharedLinksCollection();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "sharedlinks_collection", "SharedLinks"));
        }
        try {
            instance.collection.createIndex(new BasicDBObject("fileId", 1));
            instance.collection.createIndex(new BasicDBObject("folderId", 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return instance;
    }

    public DataResponse createSharedLinkFile(long fileId, int permission) {
        System.out.println("LinkSharedCollection::createSharedLinkFile itemId=" + fileId + "\t permission=" + permission);
        try {
            String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FILE);
            Bson update = Updates.combine(set("_id", keyItem), set("fileId", fileId), set("permission", permission),
                    set("modifierTime", System.currentTimeMillis()));
            collection.updateOne(eq("_id", keyItem), update, MongoDBConnector.updateUpsert);
            HashMap<String, Object> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("key", keyItem);
            result.put("permission", permission);
            return new DataResponse(result);
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (DuplicateKeyException e) {
            return DataResponse.SUCCESS;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse createSharedLinkFolder(long folderId, int permission) {
        System.out.println("LinkSharedCollection::createSharedLinkFolder itemId=" + folderId + "\t permission=" + permission);
        try {
            String keyItem = Utils.genKeyItem(folderId, Configuration.TYPE_FOLDER);
            Bson permissionData = combine(Updates.set("_id", keyItem), Updates.set("folderId", folderId), Updates.set("permission", permission), Updates.set("modifierTime", System.currentTimeMillis()));
            collection.updateOne(eq("_id", keyItem), permissionData, MongoDBConnector.updateUpsert);
            HashMap<String, Object> result = new HashMap<>();
            result.put("folderId", folderId);
            result.put("key", keyItem);
            result.put("permission", permission);
            return new DataResponse(result);
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (DuplicateKeyException e) {
            return DataResponse.SUCCESS;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse updatePermissionLinkFile(long fileId, int permission) {
        System.out.println("LinkSharedCollection::updatePermissionLinkFile itemId=" + fileId + "\t permission=" + permission);
        try {
            String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FILE);
            Bson filters = combine(eq("_id", keyItem));
            Bson update = combine(set("permission", permission), set("modifierTime", System.currentTimeMillis()));
            UpdateResult updateResult = collection.updateOne(filters, update);
            if (updateResult != null && updateResult.getMatchedCount() > 0) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("fileId", fileId);
                result.put("key", keyItem);
                result.put("permission", permission);
                return new DataResponse(result);
            }
            return DataResponse.MONGO_ITEM_NOT_FOUND;
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse updatePermissionLinkFolder(long fileId, int permission) {
        System.out.println("LinkSharedCollection::updatePermissionLinkFolder itemId=" + fileId + "\t permission=" + permission);
        try {
            String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FOLDER);
            Bson filters = combine(eq("_id", keyItem));
            Bson update = combine(set("permission", permission), set("modifierTime", System.currentTimeMillis()));
            UpdateResult updateResult = collection.updateOne(filters, update);
            if (updateResult != null && updateResult.getMatchedCount() > 0) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("folderId", fileId);
                result.put("key", keyItem);
                result.put("permission", permission);
                return new DataResponse(result);
            }
            return DataResponse.MONGO_ITEM_NOT_FOUND;
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse deleteLinkFile(long fileId) {
        System.out.println("LinkSharedCollection::deleteLinkFile itemId=" + fileId);
        try {
            String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FILE);
            collection.deleteOne(eq("_id", keyItem));
            return DataResponse.SUCCESS;
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse deleteLinkFolder(long folderId) {
        System.out.println("LinkSharedCollection::deleteLinkFolder itemId=" + folderId);
        try {
            String keyItem = Utils.genKeyItem(folderId, Configuration.TYPE_FOLDER);
            collection.deleteOne(eq("_id", keyItem));
            return DataResponse.SUCCESS;
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse getLinkSharedWithKey(String key) {
        System.out.println("LinkSharedCollection::getLinkSharedWithKey key=" + key);
        try {
            Document sharedLink = collection.find(eq("_id", key)).first();
            if (sharedLink != null) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("fileId", sharedLink.containsKey("fileId") ? sharedLink.getLong("fileId") : 0l);
                result.put("folderId", sharedLink.containsKey("folderId") ? sharedLink.getLong("folderId") : 0l);
                result.put("key", key);
                result.put("permission", sharedLink.getInteger("permission"));
                return new DataResponse(result);
            }
            return DataResponse.MONGO_ITEM_NOT_FOUND;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse getLinkFileShared(long fileId) {
        System.out.println("LinkSharedCollection::getLinkFileShared itemId=" + fileId);
        try {
            String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FILE);
            Document sharedLink = collection.find(eq("_id", keyItem)).first();
            if (sharedLink != null) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("fileId", sharedLink.getLong("fileId"));
                result.put("key", keyItem);
                result.put("permission", sharedLink.getInteger("permission"));
                return new DataResponse(result);
            } else {
                Document permisionData = new Document()
                        .append("_id", keyItem)
                        .append("fileId", fileId)
                        .append("permission", ONLY_PEPOLE_SHARED)
                        .append("modifierTime", System.currentTimeMillis());
                collection.insertOne(permisionData);
                HashMap<String, Object> result = new HashMap<>();
                result.put("fileId", fileId);
                result.put("key", keyItem);
                result.put("permission", ONLY_PEPOLE_SHARED);
                return new DataResponse(result);
            }
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse getLinkFolderShared(long folderId) {
        System.out.println("LinkSharedCollection::getLinkFolderShared itemId=" + folderId);
        try {
            String keyItem = Utils.genKeyItem(folderId, Configuration.TYPE_FOLDER);
            Document sharedLink = collection.find(eq("_id", keyItem)).first();
            if (sharedLink != null) {
                HashMap<String, Object> result = new HashMap<>();
                result.put("folderId", sharedLink.getLong("folderId"));
                result.put("key", keyItem);
                result.put("permission", sharedLink.getInteger("permission"));
                return new DataResponse(result);
            } else {
                Document permisionData = new Document()
                        .append("_id", keyItem)
                        .append("folderId", folderId)
                        .append("permission", ONLY_PEPOLE_SHARED)
                        .append("modifierTime", System.currentTimeMillis());
                collection.insertOne(permisionData);
                HashMap<String, Object> result = new HashMap<>();
                result.put("folderId", folderId);
                result.put("key", keyItem);
                result.put("permission", ONLY_PEPOLE_SHARED);
                return new DataResponse(result);
            }
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public boolean checkPublicFolder(long folderId) {
        System.out.println("LinkSharedCollection::checkPublicFolder itemId=" + folderId);
        try {
            String keyItem = Utils.genKeyItem(folderId, Configuration.TYPE_FOLDER);
            Document sharedLink = collection.find(eq("_id", keyItem)).first();
            if (sharedLink != null) {
                int permission = sharedLink.getInteger("permission");
                return permission == PUBLIC_EDIT || permission == PUBLIC_VIEW;
            }
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
        }
        return false;
    }
    
    public boolean checkPublicFile(long fileId) {
        System.out.println("LinkSharedCollection::checkPublicFile itemId=" + fileId);
        try {
            String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FILE);
            Document sharedLink = collection.find(eq("_id", keyItem)).first();
            if (sharedLink != null) {
                int permission = sharedLink.getInteger("permission");
                return permission == PUBLIC_EDIT || permission == PUBLIC_VIEW;
            }
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
        }
        return false;
    }

}
