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
import crdhn.utils.HttpRequestUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import firo.utils.config.Config;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;

/**
 *
 * @author namdv
 */
public class DirectoriesCollection_backup extends MongoDBConnector {

    private static DirectoriesCollection_backup instance = null;
    private MongoCollection<Document> collection = null;

    public static final String FOLDER_ID_COUNTER_KEY = Config.getParamString("counter", "folderid_counter_key", "");

    public static DirectoriesCollection_backup getInstance() {
        if (instance == null) {
            instance = new DirectoriesCollection_backup();
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
            long permission = PermissionsCollection.getInstance().getPermissionFolder(folder.ownerName, folder.appKey, parentFolder);
            if (!PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }

            Document docParent = collection.find(eq("_id", parentFolder)).projection(include("path", "subFolderIds")).first();
            if (docParent != null) {
                String path = docParent.getString("path");
                List<Long> listSubfolderId = (ArrayList) docParent.get("subFolderIds");
                if (listSubfolderId != null && listSubfolderId.size() > 0) {
                    if (checkExistedFolderName(listSubfolderId, folder.folderName)) {
                        return DataResponse.MONGO_FOLDERNAME_EXISTED;
                    }
                }
                long folderId = CountersCollection.getInstance().getNextValue(FOLDER_ID_COUNTER_KEY);
                Document folderDocument = new Document("_id", folderId)
                        .append("folderName", folder.folderName)
                        .append("ownerName", folder.ownerName)
                        .append("appKey", folder.appKey)
                        .append("path", (path + "/" + folderId))
                        .append("subFolderIds", folder.subFolderIds)
                        .append("fileIds", folder.fileIds)
                        .append("createTime", folder.createTime);
                collection.insertOne(folderDocument);
                HashMap<String, Object> paramsAction = new HashMap();
                paramsAction.put("parentId", parentFolder);
                paramsAction.put("folderId", folderId);
                paramsAction.put("folderName", folder.folderName);
                HistoriesCollection.getInstance().addHistory(folder.appKey, folder.ownerName, HistoriesCollection.ACTION_CREATE_FOLDER, paramsAction);
                collection.updateOne(eq("_id", parentFolder), combine(Updates.push("subFolderIds", folderId)));

                long permissionFolder = PermissionsCollection.getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
                PermissionsCollection.getInstance().setPermissionFolder(folder.ownerName, folder.appKey, folderId, permissionFolder);

                HashMap<String, Object> resultData = new HashMap<>();
                resultData.put("folderId", folderId);
                resultData.put("folderName", folder.folderName);
//                String ownerName = folder.ownerName;
//                ownerName = ownerName != null && ownerName.contains("#") ? ownerName.split("#")[1] : ownerName;
                resultData.put("ownerName", folder.ownerName);
                resultData.put("appKey", folder.appKey);
                resultData.put("createTime", folder.createTime);
                return new DataResponse(resultData);
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public long createRootFolder(String appKey, String accountName, String rootName) {
        System.out.println("DirectoriesCollection::createRootFolder accountName=" + appKey + "=" + accountName);
        try {
            long folderId = CountersCollection.getInstance().getNextValue(FOLDER_ID_COUNTER_KEY);
            Document folderDocument = new Document("_id", folderId)
                    .append("folderName", rootName)
                    .append("ownerName", accountName)
                    .append("appKey", appKey)
                    .append("path", folderId + "")
                    .append("subFolderIds", new ArrayList<>())
                    .append("fileIds", new ArrayList<>())
                    .append("createTime", System.currentTimeMillis());
            collection.insertOne(folderDocument);
            long permissionFolder = PermissionsCollection.getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
            PermissionsCollection.getInstance().setPermissionFolder(accountName, appKey, folderId, permissionFolder);
            return folderId;

        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return -1l;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return -2l;
        }
    }

    public DataResponse ChangeNameFolder(long folderId, String folderName, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::ChangeNameFolder folderId=" + folderId + "\t newFolderName=" + folderName);
        try {
            long permission = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
            if (!PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }

            Document docFolder = collection.find(eq("_id", folderId)).projection(include("path")).first();
            if (docFolder != null) {
                String pathFolder = docFolder.getString("path");
                long parentId = getParentFolderFromPath(pathFolder);
                if (parentId > 0) {
                    Document docParent = collection.find(eq("_id", parentId)).projection(include("subFolderIds")).first();
                    List<Long> listSubfolderId = (ArrayList) docParent.get("subFolderIds");
                    if (listSubfolderId != null && listSubfolderId.size() > 0) {
                        if (checkExistedFolderName(listSubfolderId, folderName)) {
                            return DataResponse.MONGO_FOLDERNAME_EXISTED;
                        }
                    }
                    UpdateResult result = collection.updateOne(eq("_id", folderId), set("folderName", folderName));
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("folderId", folderId);
                    paramsAction.put("folderName", folderName);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_CHANGENAME, paramsAction);
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse moveFile(long fileId, long parentFolderId, long destFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::moveFile fileId=" + fileId + "\t destFolderId=" + destFolderId);
        try {
            long permissionFile = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
            long permissionDestFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, destFolderId);
            if (!PermissionsCollection.checkExistedPermission(permissionFile, Arrays.asList(PermissionsCollection.CREATE_DELETE))
                    || !PermissionsCollection.checkExistedPermission(permissionDestFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }

            Document folderParent = collection.find(eq("_id", parentFolderId)).projection(include("_id", "fileIds")).first();
            if (folderParent != null) {
                ArrayList<Long> listFileId = (ArrayList<Long>) folderParent.get("fileIds");
                if (listFileId.contains(fileId)) {
                    UpdateResult updateResultOrig = collection.updateOne(eq("_id", parentFolderId), combine(Updates.pull("fileIds", fileId)));
                    if (updateResultOrig != null && updateResultOrig.getMatchedCount() > 0) {
                        collection.updateOne(eq("_id", destFolderId), Updates.push("fileIds", fileId));
                        HashMap<String, Object> paramsAction = new HashMap();
                        paramsAction.put("parentId", parentFolderId);
                        paramsAction.put("fileId", fileId);
                        paramsAction.put("destFolderId", destFolderId);
                        HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_MOVE_FILE, paramsAction);
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse moveFolder(long folderId, long destFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::moveFolder folderId=" + folderId + "\t destFolderId=" + destFolderId);
        try {
            long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
            long permissionDestFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, destFolderId);
            if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))
                    || !PermissionsCollection.checkExistedPermission(permissionDestFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }

            Document folder = collection.find(eq("_id", folderId)).projection(include("_id", "path", "folderName")).first();
            if (folder != null) {
                String pathFolder = folder.getString("path");
                long parentFolderId = getParentFolderFromPath(pathFolder);
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
                        HashMap<String, Object> paramsAction = new HashMap();
                        paramsAction.put("parentId", parentFolderId);
                        paramsAction.put("folderId", folderId);
                        paramsAction.put("destFolderId", destFolderId);
                        HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_MOVE_FOLDER, paramsAction);
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse deleteFolder(long folderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::deleteFolder folderId=" + folderId);
        try {
            long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
            if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }
            Document folder = collection.find(eq("_id", folderId)).projection(include("_id", "path", "ownerName", "subFolderIds", "fileIds")).first();
            if (folder != null) {
                String pathFolder = folder.getString("path");
                long parentFolderId = getParentFolderFromPath(pathFolder);
                if (parentFolderId > 0) {
                    if (accountName.equals(folder.getString("ownerName")) && appKey.equals(folder.getString("appKey"))) {
                        UpdateResult updateResult = collection.updateOne(eq("_id", parentFolderId), Updates.pull("subFolderIds", folderId));
                        if (updateResult != null && updateResult.getMatchedCount() > 0) {
                            removeFolderPrivate(appKey, accountName, folder);
                            collection.deleteOne(eq("_id", folderId));
                            PermissionsCollection.getInstance().removeAllPermissionFolder(folderId);
                        }
                    } else {
                        PermissionsCollection.getInstance().deletePermissionFolder(accountName, appKey, folderId);
                    }
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("parentId", parentFolderId);
                    paramsAction.put("folderId", folderId);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_DELETE_FOLDER, paramsAction);
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse shareFolder(long folderId, String appKey, String accountName, JSONArray accounts, long permission) {
        System.out.println("DirectoriesCollection::shareFolder folderId=" + folderId + "\taccountName=" + accountName + "\t permission=" + permission);
        try {
            long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
            if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }
            Document doc = collection.find(eq("_id", folderId)).projection(include("_id", "appKey", "ownerName")).first();
            if (doc != null) {
                for (int i = 0; i < accounts.length(); i++) {
                    JSONObject obj = accounts.getJSONObject(i);
                    String appKeyShared = obj.getString("appKey");
                    String accountShared = obj.getString("accountName");
                    if (appKeyShared.equals(doc.getString("appKey")) && accountShared.equals(doc.getString("ownerName"))) {

                    } else {
                        DataResponse respRootShared = UsersCollection.getInstance().getRootDirectory(appKeyShared, accountShared);
                        if (respRootShared != null && respRootShared.getError() == 0) {
                            HashMap<String, Long> dataRoot = (HashMap<String, Long>) respRootShared.getData();
                            long rootShared = dataRoot.get("root_shared");
                            if (rootShared > 0) {
                                collection.updateOne(eq("_id", rootShared), Updates.addToSet("subFolderIds", folderId));
                            }
                        }
                        PermissionsCollection.getInstance().setPermissionFolder(accountShared, appKeyShared, folderId, permission);
                    }
                }
                HashMap<String, Object> paramsAction = new HashMap();
                paramsAction.put("folderId", folderId);
                HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_SHARE_FOLDER, paramsAction);
                return DataResponse.SUCCESS;
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse addFile(long fileId, long parentFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::addFile addFile=" + fileId);
        try {
            long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, parentFolderId);
            if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }
            UpdateResult updateResult = collection.updateOne(eq("_id", parentFolderId), Updates.addToSet("fileIds", fileId));
            long permissionFile = PermissionsCollection.getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
            PermissionsCollection.getInstance().setPermissionFile(accountName, appKey, fileId, permissionFile);
            if (updateResult != null && updateResult.getMatchedCount() > 0) {
                HashMap<String, Object> paramsAction = new HashMap();
                paramsAction.put("parentId", parentFolderId);
                paramsAction.put("fileId", fileId);
                HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_ADD_FILE, paramsAction);
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
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse deleteFile(long fileId, long parentFolderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::deleteFile folderId=" + fileId + "\t accountName=" + appKey + "#" + accountName);
        try {
            long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, parentFolderId);
            if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("fileId", Long.toString(fileId));
            String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/getFile", "GET", params);
            JSONObject objFileInfo = new JSONObject(respFileInfo);
            if (objFileInfo.has("error_code") && objFileInfo.getInt("error_code") == 0) {
                JSONObject fInfo = objFileInfo.getJSONObject("data");
                if (fInfo != null) {
                    String ownerNameFile = fInfo.getString("ownerName");
                    String appKeyFile = fInfo.getString("appKey");
                    if (appKey.equals(appKeyFile) && ownerNameFile.equals(ownerNameFile)) {
                        UpdateResult updateResult = collection.updateOne(eq("_id", parentFolderId), Updates.pull("fileIds", fileId));
                        if (updateResult != null && updateResult.getMatchedCount() > 0) {
                            String respDel = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/deleteFile", "POST", params);
                            Utils.printLogSystem("DirectoriesCollection.deleteFile.responseDeleteFileFromeEnginne:", respDel);
                            JSONObject objRespDel = new JSONObject(respDel);
                            if (objRespDel.has("error_code")) {
                                if (objRespDel.getInt("error_code") == 0) {
                                    PermissionsCollection.getInstance().removeAllPermissionFile(fileId);
                                } else {
                                    return new DataResponse(objRespDel.getInt("error_code"), objRespDel.getString("error_message"));
                                }
                            } else {
                                return DataResponse.SERVER_RESPONSE_ERROR;
                            }
                        } else {
                            return DataResponse.MONGO_ITEM_NOT_FOUND;
                        }
                    } else {
                        PermissionsCollection.getInstance().deletePermissionFile(accountName, appKey, fileId);

                    }

                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("parentId", parentFolderId);
                    paramsAction.put("fileId", fileId);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_DELETE_FILE, paramsAction);
                    return DataResponse.SUCCESS;
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
        } catch (IOException | JSONException ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse shareFile(long fileId, String accountName, String appKey, JSONArray accounts, long permission) {
        System.out.println("DirectoriesCollection::shareFolder fileId=" + fileId + "\taccountName=" + accountName + "\t permission=" + permission);
        try {
            long permissionFile = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
            if (!PermissionsCollection.checkExistedPermission(permissionFile, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("fileId", Long.toString(fileId));
            String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/getFile", "GET", params);
            JSONObject objFileInfo = new JSONObject(respFileInfo);
            if (objFileInfo.has("error_code") && objFileInfo.getInt("error_code") == 0) {
                JSONObject fInfo = objFileInfo.getJSONObject("data");
                if (fInfo != null) {
                    String ownerNameFile = fInfo.getString("ownerName");
                    String appKeyFile = fInfo.getString("appKey");
                    for (int i = 0; i < accounts.length(); i++) {
                        JSONObject objShared = accounts.getJSONObject(i);
                        String appKeyShared = objShared.getString("appKey");
                        String accountNameShared = objShared.getString("accountName");
                        if (appKeyFile.equals(appKeyShared) && ownerNameFile.equals(accountNameShared)) {

                        } else {
                            DataResponse respRootShared = UsersCollection.getInstance().getRootDirectory(appKeyShared, accountNameShared);
                            if (respRootShared != null && respRootShared.getError() == 0) {
                                HashMap<String, Long> dataRoot = (HashMap<String, Long>) respRootShared.getData();
                                long rootShared = dataRoot.get("root_shared");
                                if (rootShared > 0) {
                                    collection.updateOne(eq("_id", rootShared), Updates.addToSet("fileIds", fileId));
                                }
                            } else {
                                Utils.printLogSystem(this.getClass().getSimpleName(), "can not get rootShare appKey=" + appKeyShared + "\t accountName=" + accountNameShared);
                            }
                            PermissionsCollection.getInstance().setPermissionFile(accountNameShared, appKeyShared, fileId, permission);
                        }
                    }
                } else {
                    return DataResponse.SERVER_RESPONSE_ERROR;
                }
            } else {
                return DataResponse.SERVER_RESPONSE_ERROR;
            }
            HashMap<String, Object> paramsAction = new HashMap();
            paramsAction.put("fileId", fileId);
            HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_SHARE_FILE, paramsAction);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    private DataResponse getListFolderInfo(List<Long> folderIds) {
        System.out.println("DirectoriesCollection::getListFolderInfo ");
        try {
            ArrayList<FolderInfo> arrFolder = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    FolderInfo folder = new FolderInfo();
                    folder.setFolderId(doc.getLong("_id"));
                    folder.setFolderName(doc.getString("folderName"));
                    folder.setOwnerName(doc.getString("ownerName"));
                    folder.setAppKey(doc.getString("appKey"));
                    folder.setPath(doc.getString("path"));
                    folder.setCreateTime(doc.getLong("createTime"));
                    folder.setSubFolderIds((ArrayList) doc.get("subFolderIds"));
                    folder.setFileIds((ArrayList) doc.get("fileIds"));
                    arrFolder.add(folder);
                }
            };
            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
            collection.find(in("_id", folderIds))
                    .projection(projection).sort(Sorts.ascending("folderName")).forEach(printBlock);
            return new DataResponse(arrFolder);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getListFolderInfoPublic(List<Long> folderIds) {
        System.out.println("DirectoriesCollection::getListFolderInfoPublic ");
        try {
            ArrayList<FolderInfo> arrFolder = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    FolderInfo folder = new FolderInfo();
                    folder.setFolderId(doc.getLong("_id"));
                    folder.setFolderName(doc.getString("folderName"));
                    folder.setOwnerName(doc.getString("ownerName"));
                    folder.setAppKey(doc.getString("appKey"));
                    folder.setPath(doc.getString("path"));
                    folder.setCreateTime(doc.getLong("createTime"));
                    folder.setSubFolderIds((ArrayList) doc.get("subFolderIds"));
                    folder.setFileIds((ArrayList) doc.get("fileIds"));
                    arrFolder.add(folder);
                }
            };

            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
            collection.find(in("_id", folderIds))
                    .projection(projection).sort(Sorts.ascending("folderName")).forEach(printBlock);
            return new DataResponse(arrFolder);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getFolderInfo(long folderId, String accountName, String appKey) {
        System.out.println("DirectoriesCollection::getFolderInfo folderId=" + folderId + "\t accountName=" + accountName);
        try {
            HashMap<String, Object> mapFolderInfo = new HashMap();
            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
            Document documentFolder = collection.find(eq("_id", folderId)).projection(projection).first();
            if (documentFolder == null) {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
            String path = documentFolder.getString("path");
            List<Long> folderIds = Utils.arrayStringToListLong(path.split("/"));

//            long permissionFolder = PermissionCollection.getInstance().getPermission(accountName, folderId, Configuration.TYPE_FOLDER);
            if (!PermissionsCollection.checkPermissionForPathParent(accountName, appKey, folderIds, Arrays.asList(PermissionsCollection.CREATE_DELETE, PermissionsCollection.VIEW))) {
                return DataResponse.MONGO_PERMISSION_DENY;
            }

            ArrayList<Object> arrPath = new ArrayList<>();
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
            mapFolderInfo.put("folderId", documentFolder.getLong("_id"));
            mapFolderInfo.put("folderName", documentFolder.getString("folderName"));
            mapFolderInfo.put("ownerName", documentFolder.getString("ownerName"));
            mapFolderInfo.put("appKey", documentFolder.getString("appKey"));
            mapFolderInfo.put("path", documentFolder.getString("path"));
            mapFolderInfo.put("pathFull", arrPath);
            mapFolderInfo.put("createTime", documentFolder.getLong("createTime"));
            List<Long> subFolderIds = (ArrayList<Long>) documentFolder.get("subFolderIds");
            DataResponse dataSubFolders = getListFolderInfo(subFolderIds);
            mapFolderInfo.put("subFolders", dataSubFolders.getData());
            List<Long> fileIds = (ArrayList<Long>) documentFolder.get("fileIds");
            DataResponse dataFileInfos = getFileInfos(fileIds);
            mapFolderInfo.put("files", dataFileInfos.getData());
            return new DataResponse(mapFolderInfo);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getFolderInfoPublic(long folderId) {
        System.out.println("DirectoriesCollection::getFolderInfoPublic folderId=" + folderId);
        try {
            HashMap<String, Object> mapFolderInfo = new HashMap();
            Bson projection = include("_id", "folderName", "ownerName", "appKey", "path", "subFolderIds", "fileIds", "createTime");
            Document documentFolder = collection.find(eq("_id", folderId)).projection(projection).first();
            if (documentFolder == null) {
                return DataResponse.MONGO_ITEM_NOT_FOUND;
            }
            String path = documentFolder.getString("path");
            List<Long> folderIds = Utils.arrayStringToListLong(path.split("/"));
            ArrayList<Object> arrPath = new ArrayList<>();
            if (folderIds.size() > 1) {
                Block<Document> printBlock;
                printBlock = new Block<Document>() {
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
            mapFolderInfo.put("folderId", documentFolder.getLong("_id"));
            mapFolderInfo.put("folderName", documentFolder.getString("folderName"));
            mapFolderInfo.put("ownerName", documentFolder.getString("ownerName"));
            mapFolderInfo.put("appKey", documentFolder.getString("appKey"));
            mapFolderInfo.put("path", documentFolder.getString("path"));
            mapFolderInfo.put("pathFull", arrPath);
            mapFolderInfo.put("createTime", documentFolder.getLong("createTime"));
            List<Long> subFolderIds = (ArrayList<Long>) documentFolder.get("subFolderIds");
            DataResponse dataSubFolders = getListFolderInfo(subFolderIds);
            mapFolderInfo.put("subFolders", dataSubFolders.getData());
            List<Long> fileIds = (ArrayList<Long>) documentFolder.get("fileIds");
            DataResponse dataFileInfos = getFileInfos(fileIds);
            if (dataFileInfos != null && dataFileInfos.getData() != null) {
                mapFolderInfo.put("files", dataFileInfos.getData());
            } else {
                mapFolderInfo.put("files", new ArrayList());
            }

            return new DataResponse(mapFolderInfo);
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getFileInfos(List<Long> fileIds) {
        System.out.println("DirectoriesCollection::getFileInfos");
        try {
//            List<Long> listFileID = new ArrayList<>();
//            for (Long fileId : fileIds) {
//                long permissionFolder = PermissionCollection.getInstance().getPermission(accountName, fileId, Configuration.TYPE_FILE);
//                if (PermissionCollection.checkPermissionForPathParent(permissionFolder, Arrays.asList(PermissionCollection.FULL,
//                        PermissionCollection.CREATE_DELETE, PermissionCollection.VIEW, PermissionCollection.SHARED))) {
//                    listFileID.add(fileId);
//                }
//            }
            if (fileIds.isEmpty()) {
                return new DataResponse(new ArrayList<>());
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("fileIds", Utils.listLongToString(fileIds));
            String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/getFiles", "GET", params);
            Utils.printLogSystem("DirectoriesCollection.getFileInfos. response", respFileInfo);
            JSONObject objresp = new JSONObject(respFileInfo);
            if (objresp.has("error_code")) {
                if (objresp.getInt("error_code") == 0) {
                    JSONArray arrFile = objresp.getJSONArray("data");
                    List<Object> listFile = new ArrayList<>();
                    for (int i = 0; i < arrFile.length(); i++) {
                        JSONObject objFileInfo = arrFile.getJSONObject(i);
                        if (objFileInfo != null) {
                            HashMap<String, Object> dataObj = new HashMap<>();
                            dataObj.put("fileId", objFileInfo.getLong("fileId"));
                            dataObj.put("fileName", objFileInfo.getString("fileName"));
                            String ownerName = objFileInfo.getString("ownerName");
                            ownerName = ownerName != null && ownerName.contains("#") ? ownerName.split("#")[1] : ownerName;
                            dataObj.put("ownerName", ownerName);
                            dataObj.put("appKey", objFileInfo.getString("appKey"));
                            dataObj.put("checksumSHA2", objFileInfo.getString("checksumSHA2"));
                            dataObj.put("checksumMD5", objFileInfo.getString("checksumMD5"));
                            dataObj.put("fileSize", objFileInfo.getLong("fileSize"));
                            dataObj.put("chunkSize", objFileInfo.getLong("chunkSize"));
                            dataObj.put("numberOfChunks", objFileInfo.getInt("numberOfChunks"));
                            dataObj.put("downloadCount", objFileInfo.getLong("downloadCount"));
                            dataObj.put("fileStatus", objFileInfo.getInt("fileStatus"));
                            dataObj.put("startUploadingTime", objFileInfo.getLong("startUploadingTime"));
                            dataObj.put("endUploadingTime", objFileInfo.getLong("endUploadingTime"));
                            listFile.add(dataObj);
                        }
                    }
                    return new DataResponse(listFile);
                } else {
                    return new DataResponse(objresp.getInt("error_code"), objresp.getString("error_message"));
                }
            }

            return DataResponse.SERVER_RESPONSE_ERROR;
        } catch (MongoWriteException ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            ex.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

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

    private long getParentFolderFromPath(String path) {
        System.out.println("getParentFolderFromPath path=" + path);
        if (path == null || path.trim().length() == 0 || !path.contains("/")) {
            return 0l;
        }
        String[] ids = path.split("/");
        try {
            return Long.valueOf(ids[ids.length - 2]);
        } catch (Exception e) {
            return -1l;
        }
    }

    private void removeFolderPrivate(String appKey, String accountName, Document document) {

        ArrayList<Long> fileIds = (ArrayList<Long>) document.get("fileIds");
        ArrayList<Long> subFolderIds = (ArrayList<Long>) document.get("subFolderIds");
        if (fileIds != null && !fileIds.isEmpty()) {
            for (Long fileId : fileIds) {
                try {
                    long permission = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                    if (PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("fileId", Long.toString(fileId));
                        String respDel = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/deleteFile", "POST", params);
                        Utils.printLogSystem("DirectoriesCollection.RemoveFolder.deletefile.responseDeleteFileFromeEnginne:", respDel);
                    } else {
                        Utils.printLogSystem("DirectoriesCollection.RemoveFolder.deletefile.", " Don't permission deletefile accountName=" + accountName + "\t fileId=" + fileId);
                    }

                } catch (IOException ex) {
                    ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                    Logger.getLogger(DirectoriesCollection_backup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        if (subFolderIds != null && !subFolderIds.isEmpty()) {
            for (Long folderId : subFolderIds) {
                long permission = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                if (PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    Utils.printLogSystem("DirectoriesCollection.RemoveFolder.remove folderId=", folderId + "");
                    Document doc = collection.find(eq("_id", folderId)).projection(include("_id", "subFolderIds", "fileIds")).first();
                    removeFolderPrivate(appKey, accountName, doc);
                } else {
                    Utils.printLogSystem("DirectoriesCollection.RemoveFolder.remove ", "folderId=" + folderId + "\t Don't permission");
                }
            }
        }

    }

}
