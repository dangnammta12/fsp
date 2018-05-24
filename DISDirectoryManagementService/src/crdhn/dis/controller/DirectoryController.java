package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.FolderInfo;
import crdhn.dis.transport.CountersCollection;
import crdhn.dis.transport.DirectoriesCollection;
import crdhn.dis.transport.ExceptionsCollection;
import crdhn.dis.transport.HistoriesCollection;
import crdhn.dis.transport.PermissionsCollection;
import crdhn.utils.DataResponse;
import crdhn.utils.ServletUtil;
import crdhn.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.util.ArrayList;
import java.util.List;
import crdhn.dis.transport.SharedLinksCollection;
import crdhn.dis.transport.UsersCollection;
import java.util.Arrays;
import java.util.HashMap;
import crdhn.utils.HttpRequestUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class DirectoryController extends Controller {

    private static final String className = "[DirectoriesController]";

    public DirectoryController() {
        rootPath = "/";
    }

    @RouteInfo(method = "get", path = "/folder/get")
    public Route getFolderInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " getFolderInfo");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                String agentKey = ServletUtil.getStringParameter(request, "agentKey");
                long folderId = ServletUtil.getLongParameter(request, "folderId");

                boolean isPublic = SharedLinksCollection.getInstance().checkPublicFolder(folderId);
                List<Long> folderIdsFromPath = DirectoriesCollection.getInstance().getFolderIdsFromPath(folderId);
                if (isPublic || PermissionsCollection.checkPermissionForPathParent(accountName, appKey, folderIdsFromPath, Arrays.asList(PermissionsCollection.CREATE_DELETE, PermissionsCollection.VIEW))) {
                    DataResponse result = DirectoriesCollection.getInstance().getFolderInfo(folderId);
                    if (result.getError() == 0) {
                        JSONObject objFolder = (JSONObject) result.getData();
                        ArrayList result_path = DirectoriesCollection.getInstance().getFullPathFolder(objFolder.getString("path"));
                        objFolder.put("pathFull", result_path);
                        List<Long> subFolderIds = (ArrayList<Long>) objFolder.get("subFolderIds");
                        DataResponse dataSubFolders = DirectoriesCollection.getInstance().getFolderInfos(subFolderIds);
                        objFolder.put("subFolders", dataSubFolders.getData());
                        List<Long> fileIds = (ArrayList<Long>) objFolder.get("fileIds");
                        DataResponse dataFileInfos = this.getFileInfos(fileIds);
                        objFolder.put("files", dataFileInfos.getData());
                        return new DataResponse(objFolder, DataResponse.DataType.JSON_STR, false);
                    } else {
                        return result;
                    }
                }
                return DataResponse.MONGO_PERMISSION_DENY;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/gets")
    public Route getFolderInfos() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " getFolderInfos");
                List<Long> folderIds = ServletUtil.getListLongParameter(request, "folderIds", ",");
                return DirectoriesCollection.getInstance().getFolderInfos(folderIds);
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/get/public")
    public Route getFolderInfoPublic() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(className, " getFolderInfoPublic");
                long folderId = ServletUtil.getLongParameter(request, "folderId");
                DataResponse result = DirectoriesCollection.getInstance().getFolderInfo(folderId);
                if (result.getError() == 0) {
                    JSONObject objFolder = (JSONObject) result.getData();
                    ArrayList result_path = DirectoriesCollection.getInstance().getFullPathFolder(objFolder.getString("path"));
                    objFolder.put("pathFull", result_path);
                    List<Long> subFolderIds = (ArrayList<Long>) objFolder.get("subFolderIds");
                    DataResponse dataSubFolders = DirectoriesCollection.getInstance().getFolderInfos(subFolderIds);
                    objFolder.put("subFolders", dataSubFolders.getData());
                    List<Long> fileIds = (ArrayList<Long>) objFolder.get("fileIds");
                    DataResponse dataFileInfos = this.getFileInfos(fileIds);
                    objFolder.put("files", dataFileInfos.getData());
                    return new DataResponse(objFolder, DataResponse.DataType.JSON_STR, false);
                } else {
                    return result;
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/owner/get")
    public Route getRootOwnerInfo() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getRootOwnerInfo");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                if (accountName.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.MISSING_PARAM;
                }
                DataResponse resp_root = UsersCollection.getInstance().getRootDirectory(appKey, accountName);
                if (resp_root.getError() == DataResponse.MONGO_ITEM_NOT_FOUND.getError()) {
                    long rootFolderIdOwner = CountersCollection.getInstance().getNextValue(DirectoriesCollection.FOLDER_ID_COUNTER_KEY);
                    long rootFolderIdShared = CountersCollection.getInstance().getNextValue(DirectoriesCollection.FOLDER_ID_COUNTER_KEY);
                    DataResponse result_owner = DirectoriesCollection.getInstance().createRootFolder(appKey, accountName, "Root", rootFolderIdOwner);
                    DataResponse result_shared = DirectoriesCollection.getInstance().createRootFolder(appKey, accountName, "Shared", rootFolderIdShared);

                    if (result_owner.getError() == 0 && result_shared.getError() == 0) {
                        long permissionFolder = PermissionsCollection.getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
                        PermissionsCollection.getInstance().setPermissionFolder(accountName, appKey, rootFolderIdOwner, permissionFolder);
                        PermissionsCollection.getInstance().setPermissionFolder(accountName, appKey, rootFolderIdShared, permissionFolder);

                        resp_root = UsersCollection.getInstance().createRootDirectory(appKey, accountName, rootFolderIdOwner, rootFolderIdShared);
                    } else {
                        return result_owner.getError() != 0 ? result_owner : result_shared;
                    }

                }

                long root_owner = -1l;
                if (resp_root != null && resp_root.getError() == 0) {
                    HashMap<String, Long> dataResp = (HashMap) resp_root.getData();
                    root_owner = dataResp.getOrDefault("root_owner", -1L);
                    DataResponse result = DirectoriesCollection.getInstance().getFolderInfo(root_owner);
                    if (result.getError() == 0) {
                        JSONObject objFolder = (JSONObject) result.getData();
                        ArrayList result_path = DirectoriesCollection.getInstance().getFullPathFolder(objFolder.getString("path"));
                        objFolder.put("pathFull", result_path);
                        List<Long> subFolderIds = (ArrayList<Long>) objFolder.get("subFolderIds");
                        DataResponse dataSubFolders = DirectoriesCollection.getInstance().getFolderInfos(subFolderIds);
                        objFolder.put("subFolders", (JSONArray) dataSubFolders.getData());
                        List<Long> fileIds = (ArrayList<Long>) objFolder.get("fileIds");
                        DataResponse dataFileInfos = this.getFileInfos(fileIds);
                        objFolder.put("files", dataFileInfos.getData());
                        return new DataResponse(objFolder, DataResponse.DataType.JSON_STR, false);
                    } else {
                        return result;
                    }
                } else {
                    return resp_root;
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/shared/get")
    public Route getRootSharedInfo() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getRootSharedInfo");

                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                if (accountName.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.MISSING_PARAM;
                }
                DataResponse resp_root = UsersCollection.getInstance().getRootDirectory(appKey, accountName);
                long root_shared = -1l;
                if (resp_root != null && resp_root.getError() == 0) {
                    HashMap<String, Long> dataResp = (HashMap) resp_root.getData();
                    root_shared = dataResp.getOrDefault("root_shared", -1L);
                }
                DataResponse result = DirectoriesCollection.getInstance().getFolderInfo(root_shared);
                if (result.getError() == 0) {
                    JSONObject objFolder = (JSONObject) result.getData();
                    ArrayList result_path = DirectoriesCollection.getInstance().getFullPathFolder(objFolder.getString("path"));
                    objFolder.put("pathFull", result_path);
                    List<Long> subFolderIds = (ArrayList<Long>) objFolder.get("subFolderIds");
                    DataResponse dataSubFolders = DirectoriesCollection.getInstance().getFolderInfos(subFolderIds);
                    objFolder.put("subFolders", dataSubFolders.getData());
                    List<Long> fileIds = (ArrayList<Long>) objFolder.get("fileIds");
                    DataResponse dataFileInfos = this.getFileInfos(fileIds);
                    objFolder.put("files", dataFileInfos.getData());
                    return new DataResponse(objFolder, DataResponse.DataType.JSON_STR, false);
                } else {
                    return result;
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/create")
    public Route createFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " createFolder");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String folderName = ServletUtil.getStringParameter(request, "folderName");
                long parentId = ServletUtil.getLongParameter(request, "parentId");
                FolderInfo folder = new FolderInfo();
                folder.setFolderName(folderName);
                folder.setOwnerName(accountName);
                folder.setAppKey(appKey);
                folder.setFileIds(new ArrayList());
                folder.setSubFolderIds(new ArrayList());
                folder.setCreateTime(System.currentTimeMillis());

                long permission = PermissionsCollection.getInstance().getPermissionFolder(folder.ownerName, folder.appKey, parentId);
                if (!PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                long folderId = CountersCollection.getInstance().getNextValue(DirectoriesCollection.FOLDER_ID_COUNTER_KEY);
                folder.setFolderId(folderId);
                DataResponse result = DirectoriesCollection.getInstance().createFolder(folder, parentId);
                if (result.getError() == 0) {
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("parentId", parentId);
                    paramsAction.put("folderId", folderId);
                    paramsAction.put("folderName", folder.folderName);
                    HistoriesCollection.getInstance().addHistory(folder.appKey, folder.ownerName, HistoriesCollection.ACTION_CREATE_FOLDER, paramsAction);

                    long permissionFolder = PermissionsCollection.getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
                    PermissionsCollection.getInstance().setPermissionFolder(folder.ownerName, folder.appKey, folderId, permissionFolder);
                }
                return result;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/changename")
    public Route changeNameFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " changeNameFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long folderId = ServletUtil.getLongParameter(request, "folderId");
                String newFolderName = ServletUtil.getStringParameter(request, "folderName");

                long permission = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                if (!PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                DataResponse result = DirectoriesCollection.getInstance().ChangeNameFolder(folderId, newFolderName, accountName, appKey);
                if (result.getError() == 0) {
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("folderId", folderId);
                    paramsAction.put("folderName", newFolderName);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_CHANGENAME, paramsAction);
                }
                return result;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/move")
    public Route moveFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " moveFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long folderId = ServletUtil.getLongParameter(request, "folderId");
                long newFolderId = ServletUtil.getLongParameter(request, "newFolderId");

                long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                long permissionDestFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, newFolderId);
                if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))
                        || !PermissionsCollection.checkExistedPermission(permissionDestFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }

                DataResponse result = DirectoriesCollection.getInstance().moveFolder(folderId, newFolderId, accountName, appKey);
                if (result.getError() == 0) {
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("folderId", folderId);
                    paramsAction.put("destFolderId", newFolderId);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_MOVE_FOLDER, paramsAction);
                }
                return result;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/delete")
    public Route deleteFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " deleteFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long folderId = ServletUtil.getLongParameter(request, "folderId");

                long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                DataResponse folderData = DirectoriesCollection.getInstance().getFolderInfo(folderId);
                if (folderData.getError() == 0) {
                    JSONObject folder = (JSONObject) folderData.getData();
                    if (accountName.equals(folder.getString("ownerName")) && appKey.equals(folder.getString("appKey"))) {
                        DataResponse respDeleteFolder = DirectoriesCollection.getInstance().deleteFolder(folderId, folder.getString("path"), accountName, appKey);
                        if (respDeleteFolder.getError() == DataResponse.SUCCESS.getError()) {
                            this.processDeleteFolder((List<Long>) folder.get("fileIds"), (List<Long>) folder.get("subFolderIds"));
                            PermissionsCollection.getInstance().removeAllPermissionFolder(folderId);
                        } else if (respDeleteFolder.getError() == DataResponse.MONGO_PERMISSION_DENY.getError()) {
                            this.processDeleteFolder((List<Long>) folder.get("fileIds"), (List<Long>) folder.get("subFolderIds"));
                        } else {
                            return respDeleteFolder;
                        }

                    } else {
                        DataResponse respDeletePermissionFolder = PermissionsCollection.getInstance().deletePermissionFolder(accountName, appKey, folderId);
                        if (respDeletePermissionFolder.getError() != respDeletePermissionFolder.getError()) {
                            return respDeletePermissionFolder;
                        }
                    }
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("folderId", folderId);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_DELETE_FOLDER, paramsAction);
                    return DataResponse.SUCCESS;
                } else {
                    return folderData;
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    private void processDeleteFolder(List<Long> fileIds, List<Long> subFolderIds) {

        String str_fileIds = Utils.listLongToString(fileIds);
        if (str_fileIds != null && !str_fileIds.isEmpty()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("fileIds", str_fileIds);
                String respDel = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/deleteFiles", "POST", params);
                Utils.printLogSystem(className, " removeFolderPrivate.resp=" + respDel);

            } catch (IOException ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                Logger.getLogger(DirectoriesCollection.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        //mai lam tiep
        if (subFolderIds != null && !subFolderIds.isEmpty()) {
            try {
                DataResponse dataFolders = DirectoriesCollection.getInstance().getFolderInfos(subFolderIds);
                if (dataFolders != null && dataFolders.getError() == 0) {
                    JSONArray arrFolder = (JSONArray) dataFolders.getData();
                    if (arrFolder != null && arrFolder.length() > 0) {
                        for (int i = 0; i < arrFolder.length(); i++) {
                            JSONObject obj = arrFolder.getJSONObject(i);
                            List<Long> listFileId = (List<Long>) obj.get("fileIds");
                            List<Long> listFolderId = (List<Long>) obj.get("subFolderIds");
                            this.processDeleteFolder(listFileId, listFolderId);
                        }
                    }
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                Logger.getLogger(DirectoriesCollection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @RouteInfo(method = "get,post", path = "/folder/share")
    public Route shareFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " shareFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long folderId = ServletUtil.getLongParameter(request, "folderId");
                List<Integer> permissions = ServletUtil.getListIntParameter(request, "permissions");
                for (Integer permission : permissions) {
                    if (permission != PermissionsCollection.CREATE_DELETE
                            && permission != PermissionsCollection.VIEW) {
                        return DataResponse.PARAM_ERROR;
                    }
                }
                long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }

                long permissionShared = PermissionsCollection.getPermissionBitSet(permissions);
                String accountShareds = ServletUtil.getParameter(request, "accounts");
                JSONArray arrShared = new JSONArray(accountShareds);
                for (int i = 0; i < arrShared.length(); i++) {
                    JSONObject obj = arrShared.getJSONObject(i);
                    String appKeyShared = obj.getString("appKey");
                    String accountShared = obj.getString("accountName");
                    DataResponse respRootShared = UsersCollection.getInstance().getRootDirectory(appKeyShared, accountShared);
                    if (respRootShared != null && respRootShared.getError() == 0) {
                        HashMap<String, Long> dataRoot = (HashMap<String, Long>) respRootShared.getData();
                        long rootShared = dataRoot.get("root_shared");
                        if (rootShared > 0) {
                            DirectoriesCollection.getInstance().shareFolder(folderId, rootShared);
                            PermissionsCollection.getInstance().setPermissionFolder(accountShared, appKeyShared, folderId, permissionShared);
                        }
                    }

                }
                HashMap<String, Object> paramsAction = new HashMap();
                paramsAction.put("folderId", folderId);
                HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_SHARE_FOLDER, paramsAction);
                return DataResponse.SUCCESS;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/permission")
    public Route checkPermissionFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " checkPermissionFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long fileId = ServletUtil.getIntParameter(request, "fileId");
                boolean isPublic = SharedLinksCollection.getInstance().checkPublicFile(fileId);
                if (isPublic) {
                    return DataResponse.SUCCESS;
                }
                long permissionFile = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                if (!PermissionsCollection.checkExistedPermission(permissionFile, Arrays.asList(PermissionsCollection.CREATE_DELETE, PermissionsCollection.VIEW))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                return DataResponse.SUCCESS;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/permission")
    public Route checkPermissionFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " checkPermissionFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long folderId = ServletUtil.getIntParameter(request, "folderId");
                boolean isPublic = SharedLinksCollection.getInstance().checkPublicFolder(folderId);
                if (isPublic) {
                    return DataResponse.SUCCESS;
                }
                long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE, PermissionsCollection.VIEW))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                return DataResponse.SUCCESS;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

//    @RouteInfo(method = "get,post", path = "/file/permission/check")
//    public Route checkPermissionFilePublic() {
//        return (Request request, Response response) -> {
//            try {
//                response.header("Content-Type", "application/json");
//                Utils.printLogSystem(className, " checkPermissionFilePublic");
//                long fileId = ServletUtil.getLongParameter(request, "fileId");
//                String accountName = ServletUtil.getStringParameter(request, "accountName");
//                String appKey = ServletUtil.getStringParameter(request, "appKey");
//                boolean isPublic = SharedLinksCollection.getInstance().checkPublicFile(fileId);
//                return isPublic ? DataResponse.SUCCESS : DataResponse.MONGO_PERMISSION_DENY;
//            } catch (Exception ex) {
//                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
//                ex.printStackTrace();
//                return new DataResponse(-1, ex.getMessage());
//            }
//        };
//    }
//
//    @RouteInfo(method = "get,post", path = "/folder/permission/check")
//    public Route checkPermissionFolderPublic() {
//        return (Request request, Response response) -> {
//            try {
//                response.header("Content-Type", "application/json");
//                Utils.printLogSystem(className, " checkPermissionFolderPublic");
//                long folderId = ServletUtil.getLongParameter(request, "folderId");
//                String accountName = ServletUtil.getStringParameter(request, "accountName");
//                String appKey = ServletUtil.getStringParameter(request, "appKey");
//                boolean isPublic = SharedLinksCollection.getInstance().checkPublicFolder(folderId);
//                return isPublic ? DataResponse.SUCCESS : DataResponse.MONGO_PERMISSION_DENY;
//            } catch (Exception ex) {
//                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
//                ex.printStackTrace();
//                return new DataResponse(-1, ex.getMessage());
//            }
//        };
//    }
    @RouteInfo(method = "get,post", path = "/file/add")
    public Route addFileToFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " addFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                long parentFolder = ServletUtil.getLongParameter(request, "parentId");
                if (fileId <= 0 || parentFolder <= 0) {
                    return DataResponse.PARAM_ERROR;
                }
                long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, parentFolder);
                if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                DataResponse result = DirectoriesCollection.getInstance().addFile(fileId, parentFolder, accountName, appKey);
                if (result.getError() == 0) {
                    long permissionFile = PermissionsCollection.getPermissionBitSet(Arrays.asList(PermissionsCollection.CREATE_DELETE));
                    PermissionsCollection.getInstance().setPermissionFile(accountName, appKey, fileId, permissionFile);
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("parentId", parentFolder);
                    paramsAction.put("fileId", fileId);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_ADD_FILE, paramsAction);
                }
                return result;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/getids")
    public Route getFileIds() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getFileIds");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                return PermissionsCollection.getInstance().getFileIds(accountName, appKey);
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/move")
    public Route moveFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " moveFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long fileId = ServletUtil.getLongParameter(request, "fileId");
                long parentFolder = ServletUtil.getLongParameter(request, "parentId");
                long destFolder = ServletUtil.getLongParameter(request, "newFolderId");
                if (fileId <= 0 || parentFolder <= 0 || destFolder <= 0 || accountName.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                long permissionFile = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                long permissionDestFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, destFolder);
                if (!PermissionsCollection.checkExistedPermission(permissionFile, Arrays.asList(PermissionsCollection.CREATE_DELETE))
                        || !PermissionsCollection.checkExistedPermission(permissionDestFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }

                DataResponse result = DirectoriesCollection.getInstance().moveFile(fileId, parentFolder, destFolder, accountName, appKey);
                if (result.getError() == 0) {
                    HashMap<String, Object> paramsAction = new HashMap();
                    paramsAction.put("parentId", parentFolder);
                    paramsAction.put("fileId", fileId);
                    paramsAction.put("destFolderId", destFolder);
                    HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_MOVE_FILE, paramsAction);
                }
                return result;
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/delete")
    public Route deleteFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " deleteFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                long parentFolder = ServletUtil.getLongParameter(request, "parentId");
                if (fileId <= 0 || parentFolder <= 0 || accountName.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                long permissionFolder = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, parentFolder);
                if (!PermissionsCollection.checkExistedPermission(permissionFolder, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("fileId", Long.toString(fileId));
                String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/getFile", "GET", params);
                Utils.printLogSystem(className, " deleteFile.getFileInfo.response=" + respFileInfo);
                JSONObject objFileInfo = new JSONObject(respFileInfo);
                if (objFileInfo.has("error_code") && objFileInfo.getInt("error_code") == 0) {
                    JSONObject fInfo = objFileInfo.getJSONObject("data");
                    if (fInfo != null) {
                        String ownerNameFile = fInfo.getString("ownerName");
                        String appKeyFile = fInfo.getString("appKey");
                        if (appKey.equals(appKeyFile) && accountName.equals(ownerNameFile)) {
                            DataResponse result = DirectoriesCollection.getInstance().deleteFile(fileId, parentFolder);
                            Utils.printLogSystem(className, " deleteFile.DirectoriesCollection.getInstance().deleteFile.response=" + result);
                            if (result != null && result.getError() == 0) {
                                String respDel = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/deleteFile", "POST", params);
                                Utils.printLogSystem(className + ".deleteFile.responseDeleteFileFromEnginne:", respDel);
                                JSONObject objRespDel = new JSONObject(respDel);
                                if (objRespDel.has("error_code")) {
                                    if (objRespDel.getInt("error_code") == 0) {
                                        PermissionsCollection.getInstance().removeAllPermissionFile(fileId);
                                    } else {
                                        return respDel;
                                    }
                                } else {
                                    return DataResponse.SERVER_RESPONSE_ERROR;
                                }
                            } else {
                                return result;
                            }
                        } else {
                            PermissionsCollection.getInstance().deletePermissionFile(accountName, appKey, fileId);
                        }

                        HashMap<String, Object> paramsAction = new HashMap();
                        paramsAction.put("parentId", parentFolder);
                        paramsAction.put("fileId", fileId);
                        HistoriesCollection.getInstance().addHistory(appKey, accountName, HistoriesCollection.ACTION_DELETE_FILE, paramsAction);
                        return DataResponse.SUCCESS;
                    } else {
                        return DataResponse.MONGO_ITEM_NOT_FOUND;
                    }
                } else {
                    return DataResponse.MONGO_ITEM_NOT_FOUND;
                }

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share")
    public Route shareFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " shareFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                List<Integer> permissions = ServletUtil.getListIntParameter(request, "permissions");
                String accountShareds = ServletUtil.getParameter(request, "accounts");
                JSONArray accounts = new JSONArray(accountShareds);
                if (accountName.isEmpty() || appKey.isEmpty() || fileId <= 0 || permissions.isEmpty() || accounts.length() == 0) {
                    return DataResponse.PARAM_ERROR;
                }
                for (Integer permission : permissions) {
                    if (permission != PermissionsCollection.CREATE_DELETE
                            && permission != PermissionsCollection.VIEW) {
                        return DataResponse.PARAM_ERROR;
                    }
                }
                long permissionFile = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                if (!PermissionsCollection.checkExistedPermission(permissionFile, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                long permissionShared = PermissionsCollection.getPermissionBitSet(permissions);
                HashMap<String, String> params = new HashMap();
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
                                        DirectoriesCollection.getInstance().shareFile(fileId, rootShared);
                                    }
                                } else {
                                    Utils.printLogSystem(this.getClass().getSimpleName(), "can not get rootShare appKey=" + appKeyShared + "\t accountName=" + accountNameShared);
                                }
                                PermissionsCollection.getInstance().setPermissionFile(accountNameShared, appKeyShared, fileId, permissionShared);
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

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/add")
    public Route shareLinkFilePublic() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " shareLinkFilePublic");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                int permission = ServletUtil.getIntParameter(request, "permission");
                if (fileId <= 0 || !(permission == SharedLinksCollection.PUBLIC_EDIT || permission == SharedLinksCollection.PUBLIC_VIEW || permission == SharedLinksCollection.ONLY_PEPOLE_SHARED)) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    long permissionItem = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                    if (!PermissionsCollection.checkExistedPermission(permissionItem, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    return SharedLinksCollection.getInstance().createSharedLinkFile(fileId, permission);
                }

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/add")
    public Route shareLinkFolderPublic() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " shareLinkFolderPublic");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long folderId = ServletUtil.getLongParameter(request, "folderId");
                int permission = ServletUtil.getIntParameter(request, "permission");
                if (folderId <= 0 || !(permission == SharedLinksCollection.PUBLIC_EDIT || permission == SharedLinksCollection.PUBLIC_VIEW || permission == SharedLinksCollection.ONLY_PEPOLE_SHARED)) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    long permissionItem = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                    if (!PermissionsCollection.checkExistedPermission(permissionItem, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    return SharedLinksCollection.getInstance().createSharedLinkFolder(folderId, permission);
                }

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/share/public/get/:keyItem")
    public Route getPermissionLinkPublic() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getPermissionLinkPublic");
                String keyItem = String.valueOf(request.params(":keyItem"));
                if (keyItem == null || keyItem.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    return SharedLinksCollection.getInstance().getLinkSharedWithKey(keyItem);
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/get")
    public Route getLinkFilePublic() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getLinkFilePublic");
                long fileId = ServletUtil.getLongParameter(request, "fileId");
                if (fileId <= 0) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    return SharedLinksCollection.getInstance().getLinkFileShared(fileId);
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/get")
    public Route getLinkFolderPublic() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getLinkFolderPublic");
                long folderId = ServletUtil.getLongParameter(request, "folderId");
                if (folderId <= 0) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    return SharedLinksCollection.getInstance().getLinkFolderShared(folderId);
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/file/share/user/get")
    public Route getUsersAccessFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getUsersAccessFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                if (fileId <= 0) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    long permission = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                    if (!PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    return PermissionsCollection.getInstance().getUsersAccessFile(appKey, accountName, fileId);
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/share/user/get")
    public Route getUsersAccessFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " getUsersAccessFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");
                long folderId = ServletUtil.getLongParameter(request, "folderId");
                if (folderId <= 0) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    long permission = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                    if (!PermissionsCollection.checkExistedPermission(permission, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    return PermissionsCollection.getInstance().getUsersAccessFolder(appKey, accountName, folderId);
                }
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/user/permission/update")
    public Route updatePermissionFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " updatePermissionFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                String accountShared = ServletUtil.getStringParameter(request, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(request, "appKeyShared");
                List<Integer> permissions = ServletUtil.getListIntParameter(request, "permissions");
                if (permissions == null || permissions.isEmpty() || accountName.isEmpty() || appKey.isEmpty() || fileId <= 0) {
                    return DataResponse.PARAM_ERROR;
                }
                for (Integer permission : permissions) {
                    if (permission != PermissionsCollection.CREATE_DELETE
                            && permission != PermissionsCollection.VIEW) {
                        return DataResponse.PARAM_ERROR;
                    }
                }
                HashMap<String, String> params = new HashMap();
                params.put("fileId", Long.toString(fileId));
                String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/getFile", "GET", params);
                JSONObject objFileInfo = new JSONObject(respFileInfo);
                if (objFileInfo.has("error_code") && objFileInfo.getInt("error_code") == 0) {
                    JSONObject fInfo = objFileInfo.getJSONObject("data");
                    if (fInfo != null) {
                        String ownerNameFile = fInfo.getString("ownerName");
                        String appKeyFile = fInfo.getString("appKey");
                        if (accountShared.equals(ownerNameFile) && appKeyShared.equals(appKeyFile)) {
                            return DataResponse.MONGO_PERMISSION_DENY;
                        }
                    } else {
                        return new DataResponse(-1, "Parse data file error: " + fInfo);
                    }
                } else {
                    return respFileInfo;
                }
                long permissionCurrentUser = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                if (!PermissionsCollection.checkExistedPermission(permissionCurrentUser, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                long permissionUpdate = PermissionsCollection.getPermissionBitSet(permissions);
                return PermissionsCollection.getInstance().setPermissionFile(accountShared, appKeyShared, fileId, permissionUpdate);
            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/user/permission/update")
    public Route updatePermissionFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " updatePermissionFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long folderId = ServletUtil.getLongParameter(request, "folderId");
                String accountShared = ServletUtil.getStringParameter(request, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(request, "appKeyShared");
                List<Integer> permissions = ServletUtil.getListIntParameter(request, "permissions");
                if (permissions == null || permissions.isEmpty() || accountName.isEmpty() || appKey.isEmpty() || folderId <= 0) {
                    return DataResponse.PARAM_ERROR;
                }
                for (Integer permission : permissions) {
                    if (permission != PermissionsCollection.CREATE_DELETE
                            && permission != PermissionsCollection.VIEW) {
                        return DataResponse.PARAM_ERROR;
                    }
                }

                boolean checkOwnerFolder = DirectoriesCollection.getInstance().checkOwnerFolder(folderId, appKeyShared, accountShared);
                if (checkOwnerFolder) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                long permissionCurrentUser = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                if (!PermissionsCollection.checkExistedPermission(permissionCurrentUser, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                    return DataResponse.MONGO_PERMISSION_DENY;
                }
                long permissionUpdate = PermissionsCollection.getPermissionBitSet(permissions);
                return PermissionsCollection.getInstance().setPermissionFolder(accountShared, appKeyShared, folderId, permissionUpdate);

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/user/permission/remove")
    public Route removePermissionFile() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " removePermissionFile");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long fileId = ServletUtil.getLongParameter(request, "fileId");
                String accountShared = ServletUtil.getStringParameter(request, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(request, "appKeyShared");
                if (accountName.isEmpty() || appKey.isEmpty() || fileId <= 0 || accountShared.isEmpty() || appKeyShared.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("fileId", Long.toString(fileId));
                    String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_download_file + "/getFile", "GET", params);
                    JSONObject objFileInfo = new JSONObject(respFileInfo);
                    if (objFileInfo.has("error_code") && objFileInfo.getInt("error_code") == 0) {
                        JSONObject fInfo = objFileInfo.getJSONObject("data");
                        if (fInfo != null) {
                            String ownerNameFile = fInfo.getString("ownerName");
                            String appKeyFile = fInfo.getString("appKey");
                            if (accountShared.equals(ownerNameFile) && appKeyShared.equals(appKeyFile)) {
                                return DataResponse.MONGO_PERMISSION_DENY;
                            }
                        }
                    } else {
                        return respFileInfo;
                    }
                    long permissionCurrentUser = PermissionsCollection.getInstance().getPermissionFile(accountName, appKey, fileId);
                    if (!PermissionsCollection.checkExistedPermission(permissionCurrentUser, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    return PermissionsCollection.getInstance().deletePermissionFile(accountShared, appKeyShared, fileId);
                }

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/user/permission/remove")
    public Route removePermissionFolder() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(className, " removePermissionFolder");
                String accountName = ServletUtil.getStringParameter(request, "accountName");
                String appKey = ServletUtil.getStringParameter(request, "appKey");

                long folderId = ServletUtil.getLongParameter(request, "folderId");
                String accountShared = ServletUtil.getStringParameter(request, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(request, "appKeyShared");
                if (accountName.isEmpty() || appKey.isEmpty() || folderId <= 0 || accountShared.isEmpty() || appKeyShared.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    boolean checkOwnerFolder = DirectoriesCollection.getInstance().checkOwnerFolder(folderId, appKeyShared, accountShared);
                    if (checkOwnerFolder) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    long permissionItem = PermissionsCollection.getInstance().getPermissionFolder(accountName, appKey, folderId);
                    if (!PermissionsCollection.checkExistedPermission(permissionItem, Arrays.asList(PermissionsCollection.CREATE_DELETE))) {
                        return DataResponse.MONGO_PERMISSION_DENY;
                    }
                    return PermissionsCollection.getInstance().deletePermissionFolder(accountShared, appKeyShared, folderId);
                }

            } catch (Exception ex) {
                ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    private DataResponse getFileInfos(List<Long> fileIds) {
        System.out.println("DirectoriesCollection::getFileInfos");
        try {
            if (fileIds.isEmpty()) {
                return new DataResponse(new ArrayList<>());
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("fileIds", Utils.listLongToStringReverse(fileIds));
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
//                            ownerName = ownerName != null && ownerName.contains("#") ? ownerName.split("#")[1] : ownerName;
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
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return new DataResponse(-1, ex.getMessage());
        }
    }
}
