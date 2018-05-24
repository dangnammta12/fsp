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
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import java.util.ArrayList;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.FolderInfo;
import crdhn.utils.DataResponse;
import crdhn.utils.Utils;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import firo.utils.config.Config;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;
import java.util.Arrays;
import org.json.JSONArray;

/**
 *
 * @author namdv
 */
public class DirectoriesCollection extends MongoDBConnector {

    private static DirectoriesCollection instance = null;
    private MongoCollection<Document> collection = null;

    public static final String FOLDER_ID_COUNTER_KEY = Config.getParamString("counter", "folderid_counter_key", "");

    public static DirectoriesCollection getInstance() {
        if (instance == null) {
            instance = new DirectoriesCollection();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "directories_collection", "Directories"));
        }
        try {
            instance.collection.createIndex(new BasicDBObject("appKey", 1).append("accountName", 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return instance;
    }

    public DataResponse createFolder(FolderInfo folder, long parentFolder) {
        System.out.println("DirectoriesCollection::createFolder" + folder.folderName);
        try {
            Document docParent = collection.find(eq("_id", parentFolder)).projection(include("path", "subFolderIds")).first();
            if (docParent != null) {
                String path = docParent.getString("path");
                List<Long> listSubfolderId = (ArrayList) docParent.get("subFolderIds");
                if (listSubfolderId != null && listSubfolderId.size() > 0) {
                    if (checkExistedFolderName(listSubfolderId, folder.folderName)) {
                        return DataResponse.MONGO_FOLDERNAME_EXISTED;
                    }
                }
                Document folderDocument = new Document("_id", folder.folderId)
                        .append("folderName", folder.folderName)
                        .append("ownerName", folder.ownerName)
                        .append("appKey", folder.appKey)
                        .append("path", (path + "/" + folder.folderId))
                        .append("subFolderIds", folder.subFolderIds)
                        .append("fileIds", folder.fileIds)
                        .append("createTime", folder.createTime);
                collection.insertOne(folderDocument);
                collection.updateOne(eq("_id", parentFolder), combine(Updates.push("subFolderIds", folder.folderId)));
                return new DataResponse(folder);
            } else {
                return DataResponse.PARAM_ERROR;
            }

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

    public DataResponse createRootFolder(String appKey, String accountName, String rootName, long rootFolderId) {
        System.out.println("DirectoriesCollection::createRootFolder accountName=" + appKey + "=" + accountName);
        try {

            Document folderDocument = new Document("_id", rootFolderId)
                    .append("folderName", rootName)
                    .append("ownerName", accountName)
                    .append("appKey", appKey)
                    .append("path", rootFolderId + "")
                    .append("subFolderIds", new ArrayList<>())
                    .append("fileIds", new ArrayList<>())
                    .append("createTime", System.currentTimeMillis());
            collection.insertOne(folderDocument);

            return DataResponse.SUCCESS;

        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return new DataResponse(-1, ex.getMessage());
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public DataResponse ChangeNameFolder(long folderId, String folderName, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::ChangeNameFolder folderId=" + folderId + "\t newFolderName=" + folderName);
        try {
            Document docFolder = collection.find(eq("_id", folderId)).projection(include("path")).first();
            if (docFolder != null) {
                String pathFolder = docFolder.getString("path");
                long parentId = Utils.getParentFolderFromPath(pathFolder);
                if (parentId > 0) {
                    Document docParent = collection.find(eq("_id", parentId)).projection(include("subFolderIds")).first();
                    List<Long> listSubfolderId = (ArrayList) docParent.get("subFolderIds");
                    if (listSubfolderId != null && listSubfolderId.size() > 0) {
                        if (checkExistedFolderName(listSubfolderId, folderName)) {
                            return DataResponse.MONGO_FOLDERNAME_EXISTED;
                        }
                    }
                    UpdateResult result = collection.updateOne(eq("_id", folderId), set("folderName", folderName));
                    if (result != null && result.getMatchedCount() > 0) {
                        return DataResponse.SUCCESS;
                    } else {
                        return DataResponse.PARAM_ERROR;
                    }
                } else {
                    return DataResponse.MONGO_ITEM_NOT_FOUND;
                }
            } else {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }

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

    public DataResponse moveFile(long fileId, long parentFolderId, long destFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::moveFile fileId=" + fileId + "\t destFolderId=" + destFolderId);
        try {
            Document folderParent = collection.find(eq("_id", parentFolderId)).projection(include("_id", "fileIds")).first();
            if (folderParent != null) {
                ArrayList<Long> listFileId = (ArrayList<Long>) folderParent.get("fileIds");
                if (listFileId.contains(fileId)) {
                    UpdateResult updateResultOrig = collection.updateOne(eq("_id", parentFolderId), combine(Updates.pull("fileIds", fileId)));
                    if (updateResultOrig != null && updateResultOrig.getMatchedCount() > 0) {
                        collection.updateOne(eq("_id", destFolderId), Updates.push("fileIds", fileId));
                    }
                    return DataResponse.SUCCESS;
                } else {
                    return DataResponse.PARAM_ERROR;
                }
            } else {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }

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

    public DataResponse moveFolder(long folderId, long destFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::moveFolder folderId=" + folderId + "\t destFolderId=" + destFolderId);
        try {

            Document folder = collection.find(eq("_id", folderId)).projection(include("_id", "path", "folderName")).first();
            if (folder != null) {
                String pathFolder = folder.getString("path");
                long parentFolderId = Utils.getParentFolderFromPath(pathFolder);
                String folderName = folder.getString("folderName");
                if (parentFolderId > 0) {
                    Document docDest = collection.find(eq("_id", destFolderId)).projection(include("subFolderIds", "path")).first();
                    List<Long> listSubfolderId = (ArrayList) docDest.get("subFolderIds");
                    if (listSubfolderId != null && listSubfolderId.size() > 0) {
                        if (checkExistedFolderName(listSubfolderId, folderName)) {
                            return DataResponse.MONGO_FOLDERNAME_EXISTED;
                        }
                    }
                    UpdateResult updateOrig = collection.updateOne(eq("_id", parentFolderId), Updates.pull("subFolderIds", folderId));
                    if (updateOrig != null && updateOrig.getMatchedCount() > 0) {
                        collection.updateOne(eq("_id", destFolderId), Updates.push("subFolderIds", folderId));
                        String newPath = docDest.getString("path") + "/" + folderId;
                        collection.updateOne(eq("_id", folderId), Updates.set("path", newPath));
                        return DataResponse.SUCCESS;
                    } else {
                        return DataResponse.MONGO_ITEM_NOT_FOUND;
                    }
                } else {
                    return DataResponse.PARAM_ERROR;
                }
            } else {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }

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

    public DataResponse deleteFolder(long folderId, String pathFolder, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::deleteFolder folderId=" + folderId);
        try {
            long parentFolderId = Utils.getParentFolderFromPath(pathFolder);
            if (parentFolderId > 0) {
                collection.updateOne(eq("_id", parentFolderId), Updates.pull("subFolderIds", folderId));
                collection.deleteOne(eq("_id", folderId));
                return DataResponse.SUCCESS;
            } else {
                return DataResponse.MONGO_PERMISSION_DENY;
            }
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

    public DataResponse shareFolder(long folderId, long rootSharedId) {
        System.out.println("DirectoriesCollection::shareFolder folderId=" + folderId + "\t rootSharedId=" + rootSharedId);
        try {
            collection.updateOne(eq("_id", rootSharedId), Updates.addToSet("subFolderIds", folderId));
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

    public DataResponse addFile(long fileId, long parentFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::addFile addFile=" + fileId);
        try {
            UpdateResult updateResult = collection.updateOne(eq("_id", parentFolderId), Updates.addToSet("fileIds", fileId));
            if (updateResult != null && updateResult.getMatchedCount() > 0) {
                return DataResponse.SUCCESS;
            }
            return DataResponse.PARAM_ERROR;
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

    public DataResponse deleteFile(long fileId, long parentFolderId) {
        System.out.println("DirectoriesCollection::deleteFile folderId=" + fileId + "\t parentFolderId=" + parentFolderId);
        try {
            collection.updateOne(eq("_id", parentFolderId), Updates.pull("fileIds", fileId));
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

    public DataResponse shareFile(long fileId, long rootShared) {
        System.out.println("DirectoriesCollection::shareFolder fileId=" + fileId + "\t rootShared=" + rootShared);
        try {
            collection.updateOne(eq("_id", rootShared), Updates.addToSet("fileIds", fileId));
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

    public DataResponse getFolderInfos(List<Long> folderIds) {
        System.out.println("DirectoriesCollection::getListFolderInfo ");
        try {
            JSONArray arrFolder = new JSONArray();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document documentFolder) {
//                    FolderInfo folder = FolderInfo.assignFrom(documentFolder);
                    try {
                        JSONObject folder = new JSONObject();
                        folder.put("folderId", documentFolder.getLong("_id"));
                        folder.put("folderName", documentFolder.getString("folderName"));
                        folder.put("ownerName", documentFolder.getString("ownerName"));
                        folder.put("appKey", documentFolder.getString("appKey"));
                        folder.put("path", documentFolder.getString("path"));
                        folder.put("subFolderIds", documentFolder.get("subFolderIds"));
                        folder.put("fileIds", documentFolder.get("fileIds"));
                        folder.put("createTime", documentFolder.getLong("createTime"));
                        arrFolder.put(folder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
            collection.find(in("_id", folderIds))
                    .projection(projection).sort(Sorts.ascending(Arrays.asList("folderName", "createTime"))).forEach(printBlock);
            return new DataResponse(arrFolder, DataResponse.DataType.JSON_STR, false);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

    public List<Long> getFolderIdsFromPath(long folderId) {
        System.out.println("DirectoriesCollection::getFolderIdsFromPath folderId=" + folderId);
        try {
            Bson projection = include("_id", "path");
            Document documentFolder = collection.find(eq("_id", folderId)).projection(projection).first();
            if (documentFolder == null) {
                return null;
            }
            String path = documentFolder.getString("path");
            return Utils.arrayStringToListLong(path.split("/"));
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
        }
        return null;
    }

    public ArrayList getFullPathFolder(String path) {
        List<Long> folderIds = Utils.arrayStringToListLong(path.split("/"));
        ArrayList<Object> arrPath = new ArrayList();

        if (folderIds.size() > 1) {
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    HashMap<String, String> mapId2Name = new HashMap<>();
                    mapId2Name.put("id", doc.getLong("_id").toString());
                    mapId2Name.put("name", doc.getString("folderName"));
                    arrPath.add(mapId2Name);
                }
            };
            collection.find(in("_id", folderIds)).projection(include("_id", "folderName")).forEach(printBlock);

        }
        return arrPath;
    }

    public DataResponse getFolderInfo(long folderId) {
        System.out.println("DirectoriesCollection::getFolderInfo folderId=" + folderId);
        try {
            JSONObject objFolder = new JSONObject();
            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
            Document documentFolder = collection.find(eq("_id", folderId)).projection(projection).sort(Sorts.descending("createTime")).first();
            if (documentFolder == null) {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
            objFolder.put("folderId", documentFolder.getLong("_id"));
            objFolder.put("folderName", documentFolder.getString("folderName"));
            objFolder.put("ownerName", documentFolder.getString("ownerName"));
            objFolder.put("appKey", documentFolder.getString("appKey"));
            objFolder.put("path", documentFolder.getString("path"));
            objFolder.put("subFolderIds", documentFolder.get("subFolderIds"));
            objFolder.put("fileIds", documentFolder.get("fileIds"));
            objFolder.put("createTime", documentFolder.getLong("createTime"));
            return new DataResponse(objFolder, DataResponse.DataType.JSON_STR, false);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }

//    public DataResponse getFolderInfo(long folderId) {
//        System.out.println("DirectoriesCollection::getFolderInfoBase folderId=" + folderId);
//        try {
//            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
//            Document documentFolder = collection.find(eq("_id", folderId)).projection(projection).first();
//            if (documentFolder == null) {
//                return DataResponse.MONGO_ITEM_NOT_FOUND;
//            }
//            FolderInfo folder = FolderInfo.assignFrom(documentFolder);
//            return new DataResponse(folder);
//        } catch (Exception ex) {
//            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
//            StackTraceElement traceElement = ex.getStackTrace()[0];
//            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
//            return new DataResponse(-1, ex.getMessage());
//        }
//    }
    public boolean checkExistedFolderName(List<Long> folderIds, String folderName) {
        System.out.println("DirectoriesCollection::checkExistedFolderName ");
        try {
            long countExisted = collection.count(combine(in("_id", folderIds), eq("folderName", folderName)));
            return countExisted > 0;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return false;
        }
    }

    public boolean checkOwnerFolder(long folderId, String appKey, String accountName) {
        System.out.println("DirectoriesCollection::checkOwnerFolder ");
        try {
            long countExisted = collection.count(combine(eq("_id", folderId), eq("appKey", appKey), eq("ownerName", accountName)));
            return countExisted > 0;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return false;
        }
    }

}
