/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import crdhn.utils.DataResponse;
import crdhn.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import firo.utils.config.Config;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Projections.include;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Updates.combine;
import com.mongodb.client.result.DeleteResult;
import crdhn.dis.configuration.Configuration;

/**
 *
 * @author namdv
 */
public class PermissionsCollection {

    private static PermissionsCollection instance = null;
    private MongoCollection<Document> collection = null;
//    public static final int FULL = 1;
    public static final int CREATE_DELETE = Config.getParamInt("permission", "CREATE_DELETE");
    public static final int VIEW = Config.getParamInt("permission", "VIEW");
//    public static final int SHARED = 4;
//    public static final int DELETE = 5;
//    public static final int NONE = 6;

    public static PermissionsCollection getInstance() {
        if (instance == null) {
            instance = new PermissionsCollection();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "permissions_collection", "Permissions"));
            try {
                instance.collection.createIndex(new BasicDBObject("appKey", 1).append("accountName", 1));
                instance.collection.createIndex(new BasicDBObject("fileId", 1));
                instance.collection.createIndex(new BasicDBObject("folderId", 1));
                instance.collection.createIndex(new BasicDBObject("modifierTime", 1));
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
            }
        }
        return instance;
    }

    public DataResponse getFileIds(String accountName, String appKey) {
        System.out.println("PermissionCollection::getFileIds accountName=" + accountName + "\t appKey=" + appKey);
        try {
            ArrayList<Long> arrFileId = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    long fileId = doc.getLong("fileId");
                    arrFileId.add(fileId);
                }
            };
            long permissionFull = getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
            Bson filters = combine(eq("appKey", appKey), eq("accountName", accountName), exists("fileId", true), eq("permission", permissionFull));
            collection.find(filters).projection(include("fileId")).sort(Sorts.descending("modifierTime")).forEach(printBlock);
            return new DataResponse(arrFileId);
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

    public DataResponse setPermissionFile(String accountName, String appKey, long fileId, long permission) {
        System.out.println("PermissionCollection::setPermissionFile accountName=" + appKey + "#" + accountName + "\t itemId=" + fileId + "\t permission=" + permission);
        try {
            Bson filters = and(eq("fileId", fileId), eq("appKey", appKey), eq("accountName", accountName));
             Bson updateData = combine(
                    Updates.set("fileId", fileId)
                    ,Updates.set("appKey", appKey)
                    ,Updates.set("accountName", accountName)
                    ,Updates.set("permission", permission)
                    ,Updates.set("modifierTime", System.currentTimeMillis()));
            collection.updateOne(filters, updateData, MongoDBConnector.updateUpsert);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse setPermissionFolder(String accountName, String appKey, long folderId, long permission) {
        System.out.println("PermissionCollection::setPermissionFolder accountName=" + appKey + "#" + accountName + "\t itemId=" + folderId + "\t permission=" + permission);
        try {
            Bson filters = and(eq("folderId", folderId), eq("appKey", appKey), eq("accountName", accountName));
            Bson updateData = combine(Updates.set("folderId", folderId), Updates.set("appKey", appKey), Updates.set("accountName", accountName), Updates.set("permission", permission), Updates.set("modifierTime", System.currentTimeMillis()));
            collection.updateOne(filters, updateData, MongoDBConnector.updateUpsert);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse deletePermissionFile(String accountName, String appKey, long fileId) {
        System.out.println("PermissionCollection::deletePermissionFile accountName=" + appKey + "#" + accountName + "\t itemId=" + fileId);
        try {
            Bson filters = and(eq("fileId", fileId), eq("appKey", appKey), eq("accountName", accountName));
            collection.deleteOne(filters);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse deletePermissionFolder(String accountName, String appKey, long folderId) {
        System.out.println("PermissionCollection::deletePermissionFolder accountName=" + appKey + "#" + accountName + "\t itemId=" + folderId);
        try {
            Bson filters = and(eq("folderId", folderId), eq("appKey", appKey), eq("accountName", accountName));
            collection.deleteOne(filters);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse removeAllPermissionFile(long fileId) {
        System.out.println("PermissionCollection::removeAllPermissionFile  fileId=" + fileId);
        try {
            collection.deleteMany(eq("fileId", fileId));
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse removeAllPermissionFolder(long folderId) {
        System.out.println("PermissionCollection::removeAllPermissionFolder  folderId=" + folderId);
        try {
            collection.deleteMany(eq("folderId", folderId));
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public long getPermissionFile(String accountName, String appKey, long fileId) {
        System.out.println("PermissionCollection::getPermissionFile accountName=" + accountName + "\t itemId=" + fileId);
        try {
            Bson filters = combine(eq("fileId", fileId), eq("appKey", appKey), eq("accountName", accountName));
            Document permission = collection.find(filters).first();
            return permission != null ? permission.getLong("permission") : -1l;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return -2;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return -3;
        }
    }

    public long getPermissionFolder(String accountName, String appKey, long folderId) {
        System.out.println("PermissionCollection::getPermissionFolder accountName=" + accountName + "\t itemId=" + folderId);
        try {
            Bson filters = combine(eq("folderId", folderId), eq("appKey", appKey), eq("accountName", accountName));
            Document permission = collection.find(filters).first();
            return permission != null ? permission.getLong("permission") : -1l;
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return -2;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return -3;
        }
    }

    public DataResponse getUsersAccessFile(String appKey, String accountName, long fileId) {
        System.out.println("PermissionCollection::getUsersAccessFile accountName=" + accountName + "\t itemId=" + fileId + "\t appKey=" + appKey);
        try {
            ArrayList<Map> arrData = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    Map<String, Object> mapData = new HashMap<>();
                    long docPermission = doc.getLong("permission");
                    if (checkExistedPermission(docPermission, Arrays.asList(PermissionsCollection.CREATE_DELETE, PermissionsCollection.VIEW))) {
                        mapData.put("accountName", doc.getString("accountName"));
                        mapData.put("appKey", doc.getString("appKey"));
                        mapData.put("fileId", doc.getLong("fileId"));
                        int p = PermissionsCollection.VIEW;
                        if (checkExistedPermission(docPermission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                            p = PermissionsCollection.CREATE_DELETE;
                        }
                        mapData.put("permission", p);
                        arrData.add(mapData);
                    }

                }
            };
            collection.find(combine(eq("fileId", fileId))).forEach(printBlock);
            return new DataResponse(arrData);
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse getUsersAccessFolder(String appKey, String accountName, long folderId) {
        System.out.println("PermissionCollection::getUsersAccessFolder accountName=" + accountName + "\t itemId=" + folderId + "\t appKey=" + appKey);
        try {
            ArrayList<Map> arrData = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    Map<String, Object> mapData = new HashMap<>();
                    long docPermission = doc.getLong("permission");
                    if (checkExistedPermission(docPermission, Arrays.asList(PermissionsCollection.CREATE_DELETE, PermissionsCollection.VIEW))) {
                        mapData.put("accountName", doc.getString("accountName"));
                        mapData.put("folderId", doc.getLong("folderId"));
                        mapData.put("appKey", doc.getString("appKey"));
                        int p = PermissionsCollection.VIEW;
                        if (PermissionsCollection.checkExistedPermission(docPermission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                            p = PermissionsCollection.CREATE_DELETE;
                        }
                        mapData.put("permission", p);
                        arrData.add(mapData);
                    }

                }
            };
            collection.find(combine(eq("folderId", folderId))).forEach(printBlock);
            return new DataResponse(arrData);
        } catch (MongoWriteException e) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public static boolean checkExistedPermission(long permission, List<Integer> arrayIndexPermission) {
        if (permission < 0) {
            return false;
        }
        long[] arrLong = {permission};
        BitSet bitSet = BitSet.valueOf(arrLong);
        for (int index : arrayIndexPermission) {
            boolean check = bitSet.get(index);
            if (check) {
                return true;
            }
        }
        return false;

    }

    public static boolean checkPermissionForPathParent(String accountName, String appKey, List<Long> folderIds, List<Integer> arrayIndexPermission) {
        if(folderIds == null){
            return false;
        }
        for (int i = folderIds.size() - 1; i >= 0; i--) {
            long folderId = folderIds.get(i);
            long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
            long[] arrLong = {permissionFolder};
            BitSet bitSet = BitSet.valueOf(arrLong);
            for (int index : arrayIndexPermission) {
                boolean check = bitSet.get(index);
                if (check) {
                    return true;
                }
            }
        }
        return false;

    }

    public static long getPermissionBitSet(List<Integer> indexPermissions) {
        long[] arrLong = {0l};
        BitSet bitSet = BitSet.valueOf(arrLong);
        indexPermissions.stream().forEach((index) -> {
            bitSet.set(index);
        });
        return bitSet.toLongArray()[0];
    }

//    public DataResponse checkPermissionFile(String appKey, String accountName, long fileId) {
//        System.out.println("FSPLinkSharedConnector::checkPermissionFile itemId=" + fileId + "\t appKey=" + appKey + "\t accountName=" + accountName);
//        try {
//            DataResponse linkShare = SharedLinksCollection.getInstance().getLinkFileShared(fileId);
//            if (linkShare != null && linkShare.getError() == 0) {
//                return linkShare;
//            } else {
//                HashMap<String, Object> result = new HashMap<>();
//                String keyItem = Utils.genKeyItem(fileId, Configuration.TYPE_FILE);
//                result.put("fileId", fileId);
//                result.put("key", keyItem);
//                long permissionItem = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
//                if (!PermissionsCollection.checkExistedPermission(permissionItem, Arrays.asList(PermissionsCollection.FULL, PermissionsCollection.CREATE_DELETE, PermissionsCollection.SHARED, PermissionsCollection.VIEW))) {
//                    result.put("permission", Configuration.PERMISSION_NONE);
//                } else {
//                    result.put("permission", Configuration.ONLY_PEPOLE_SHARED);
//                }
//
//                return new DataResponse(result);
//            }
//        } catch (Exception ex) {
//            StackTraceElement traceElement = ex.getStackTrace()[0];
//            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
//            return new DataResponse(-1, ex.getMessage());
//        }
//    }
//
//    public DataResponse checkPermissionFolder(String appKey, String accountName, long folderId) {
//        System.out.println("FSPLinkSharedConnector::checkPermissionFolder itemId=" + folderId + "\t appKey=" + appKey + "\t accountName=" + accountName);
//        try {
//            DataResponse linkShare = SharedLinksCollection.getInstance().getLinkFolderShared(folderId);
//            if (linkShare != null && linkShare.getError() == 0) {
//                return linkShare;
//            } else {
//                HashMap<String, Object> result = new HashMap<>();
//                String keyItem = Utils.genKeyItem(folderId, Configuration.TYPE_FOLDER);
//                result.put("folderId", folderId);
//                result.put("key", keyItem);
//                long permissionItem = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, folderId);
//                if (!PermissionsCollection.checkExistedPermission(permissionItem, Arrays.asList(PermissionsCollection.FULL, PermissionsCollection.CREATE_DELETE, PermissionsCollection.SHARED, PermissionsCollection.VIEW))) {
//                    return DataResponse.MONGO_PERMISSION_DENY;
//                } else {
//                    result.put("permission", ONLY_PEPOLE_SHARED);
//                }
//
//                return new DataResponse(result);
//            }
//        } catch (Exception ex) {
//            StackTraceElement traceElement = ex.getStackTrace()[0];
//            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
//            return new DataResponse(-1, ex.getMessage());
//        }
//    }
}
