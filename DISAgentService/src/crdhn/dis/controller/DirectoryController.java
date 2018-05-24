/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.download.DownloadFileWorker;
import crdhn.dis.manager.FolderManager;
import crdhn.dis.manager.FileManager;
import crdhn.dis.model.FileInfo;
import crdhn.dis.model.FolderDownloadInfo;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class DirectoryController extends Controller {

    private static final Logger _logger = LoggerFactory.getLogger(DirectoryController.class);
    private static final String className = "=============DirectoryController";

    public DirectoryController() {
    }

    @RouteInfo(method = "post,get", path = "/folder/create")
    public Route createFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            String parentId = ServletUtil.getStringParameter(req, "parentId");
            String folderName = ServletUtil.getStringParameter(req, "folderName");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (appKey.isEmpty() || parentId.isEmpty() || folderName.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("parentId", parentId);
                    params.put("folderName", folderName);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_create = Configuration.url_proxy + File.separator + "folder/create";
                    String result = HttpRequestUtils.sendHttpRequest(url_create, "POST", params);
                    Utils.printLogSystem(className, "create folder parentId=" + parentId + "\t folderName=" + folderName + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/owner/get")
    public Route getRootOwnerInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            try {
                JSONObject params = new JSONObject();
                params.put("accountName", accountName);
                params.put("appKey", appKey);

                String url_submit = Configuration.url_proxy + "/folder/owner/get";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                Utils.printLogSystem(className, "getRootOwnerInfo resp=" + result);
                return result;
            } catch (IOException ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/shared/get")
    public Route getRootSharedInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            try {
                JSONObject params = new JSONObject();
                params.put("accountName", accountName);
                params.put("appKey", appKey);

                String url_submit = Configuration.url_proxy + "/folder/shared/get";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                Utils.printLogSystem(className, "getRootSharedInfo resp=" + result);
                return result;
            } catch (IOException ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/get")
    public Route getFolderInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (folderId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "folder/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(className, "getFolderInfo folderId=" + folderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "post,get", path = "/folder/changename")
    public Route changeNameFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            String folderId = ServletUtil.getStringParameter(req, "folderId");
            String folderName = ServletUtil.getStringParameter(req, "folderName");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (folderId.isEmpty() || folderName.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", folderId);
                    params.put("folderName", folderName);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "folder/changename";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "ChangeNameFolder folderId=" + folderId + "\t folderName=" + folderName + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "post, get", path = "/folder/move")
    public Route MoveFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            long newFolderId = ServletUtil.getLongParameter(req, "newFolderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (folderId <= 0 || newFolderId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("newFolderId", Long.toString(newFolderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "folder/move";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "MoveFolder folderId=" + folderId + "\t newFolderId=" + newFolderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/delete")
    public Route deleteFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (folderId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "folder/delete";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "deleteFolder folderId=" + folderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share")
    public Route shareFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            String permissions = ServletUtil.getStringParameter(req, "permissions");
            String accounts = ServletUtil.getParameter(req, "accounts");
            if (folderId <= 0 || accounts.isEmpty() || permissions.isEmpty() || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("permissions", permissions);
                    params.put("accounts", accounts);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "folder/share";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "shareFolder folderId=" + folderId + "\t  resp=" + result);
//                    JSONObject objResp = new JSONObject(result);
//                    if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
//                        String subject = "[DIS] Share folder";
//                        FSPEmailSenderServiceClient.getInstance().sendEmailWithParams(accounts, subject, viewerId, itemName, linkShare);
//                    }
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/get")
    public Route getFileInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            try {
                Utils.printLogSystem(className, " getFileInfo");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long fileId = ServletUtil.getLongParameter(req, "fileId");
                if (fileId <= 0 || accountName.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                JSONObject params = new JSONObject();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("fileId", Long.toString(fileId));
                String resp = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/file/get", "GET", params);
                Utils.printLogSystem(className, "getFileInfo \t resp=" + resp);
                JSONObject dataResp = new JSONObject(resp);
                if (dataResp.has("error_code") && dataResp.getInt("error_code") == 0) {
                    JSONObject objData = dataResp.getJSONObject("data");
                    FileInfo fInfo = FileManager.getFileUploading(fileId);
                    if (fInfo == null) {
                        objData.put("status_upload", "false");
                    } else {
                        objData.put("status_upload", "true");
                    }
                    return new DataResponse(objData, DataResponse.DataType.JSON_STR, true);
                }
                return resp;
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/file/agent/get")
    public Route getFileInfoAgent() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            try {
                Utils.printLogSystem(className, " getFileInfoAgent");
                long fileId = ServletUtil.getLongParameter(req, "fileId");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                FileInfo fInfo = FileManager.getFileUploading(fileId);
                if (fInfo != null) {
//                    if(fInfo.fileStatus == 2){
//                        FileManager.removeUploadFile(fInfo);
//                    }
//                    JSONObject obj = new JSONObject();
//                    obj.put("fileId", fInfo.fileId);
//                    obj.put("fileName", fInfo.fileName);
//                    obj.put("numberChunkSucess", fInfo.numberChunkSucess);
//                    obj.put("numberOfChunks", fInfo.numberOfChunks);
//                    obj.put("sha", fInfo.checksumSHA2);

                    return new DataResponse(fInfo.toJsonString(), DataResponse.DataType.JSON_STR, false);
                } else {
                    response.raw().sendRedirect("/file/get?fileId="+fileId+"&accountName="+accountName+"&appKey="+appKey);
                    return response;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/agent/get")
    public Route getFolderInfoAgent() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            try {
                Utils.printLogSystem(className, " getFolderInfoAgent");
                long folderId = ServletUtil.getLongParameter(req, "folderId");
                FolderDownloadInfo fdInfo = FolderManager.checkProcessFiles(folderId);
                if (fdInfo != null) {
                    JSONObject obj = new JSONObject();
                    obj.put("folderId", fdInfo.folderId);
                    obj.put("folderName", fdInfo.folderName);
                    obj.put("numberChunkSucess", (fdInfo.numberFiles - fdInfo.fileIds.size()));
                    obj.put("numberOfChunks", fdInfo.numberFiles);
                    MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                    obj.put("sha", Utils.toHex((fdInfo.folderId + "_" + Configuration.TYPE_FOLDER).getBytes(), digest256));
                    return new DataResponse(obj, DataResponse.DataType.JSON_STR, false);
                } else {
                    return DataResponse.ITEM_NOT_FOUND;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

//    @RouteInfo(method = "get", path = "/file/retry")
//    public Route retryUploadFile() {
//        return (Request req, Response response) -> {
//            response.header("Content-Type", "application/json");
//            if (!Configuration.agentStatus) {
//                return DataResponse.AGENT_UNAVAILABLE;
//            }
//            try {
//                Utils.printLogSystem(className, " retryUploadFile");
//                String appKey = ServletUtil.getStringParameter(req, "appKey");
//                String accountName = ServletUtil.getStringParameter(req, "accountName");
//                if (appKey.isEmpty() || accountName.isEmpty()) {
//                    return DataResponse.PARAM_ERROR;
//                }
//                FileManager.retryUploadForUser(appKey, accountName);
//                return DataResponse.SUCCESS;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return new DataResponse(-1, ex.getMessage());
//            }
//        };
//    }
    @RouteInfo(method = "post, get", path = "/file/move")
    public Route MoveFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            long parentId = ServletUtil.getLongParameter(req, "parentId");
            long newFolderId = ServletUtil.getLongParameter(req, "newFolderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (fileId <= 0 || newFolderId <= 0 || parentId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("fileId", Long.toString(fileId));
                    params.put("parentId", Long.toString(parentId));
                    params.put("newFolderId", Long.toString(newFolderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "file/move";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "MoveFile fileId=" + fileId + "\t newFileId=" + newFolderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/delete")
    public Route deleteFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            long parentId = ServletUtil.getLongParameter(req, "parentId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (fileId <= 0 || parentId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("fileId", Long.toString(fileId));
                    params.put("parentId", Long.toString(parentId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "file/delete";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "deleteFile fileId=" + fileId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share")
    public Route shareFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            String permissions = ServletUtil.getStringParameter(req, "permissions");
            String accounts = ServletUtil.getParameter(req, "accounts");
            if (fileId <= 0 || accounts.isEmpty() || permissions.isEmpty() || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("fileId", Long.toString(fileId));
                    params.put("permissions", permissions);
                    params.put("accounts", accounts);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + File.separator + "file/share";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "shareFile fileId=" + fileId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/add")
    public Route createLinkFileSharedPublic() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (fileId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("fileId", Long.toString(fileId));
                    params.put("permission", Integer.toString(Configuration.PUBLIC_VIEW));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/file/share/public/add";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "createLinkFileSharedPublic itemId=" + fileId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/add")
    public Route createLinkFolderSharedPublic() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (folderId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("permission", Integer.toString(Configuration.PUBLIC_VIEW));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/folder/share/public/add";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "createLinkFolderSharedPublic fileId=" + folderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/change")
    public Route changePermissionLinkFileShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            int permission = ServletUtil.getIntParameter(req, "permission");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (appKey.isEmpty() || fileId <= 0 || !(permission == Configuration.PUBLIC_EDIT || permission == Configuration.PUBLIC_VIEW || permission == Configuration.ONLY_PEPOLE_SHARED)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("fileId", Long.toString(fileId));
                    params.put("permission", permission + "");
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/file/share/public/add";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "changePermissionLinkFileShared file=" + fileId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/change")
    public Route changePermissionLinkFolderShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            int permission = ServletUtil.getIntParameter(req, "permission");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (appKey.isEmpty() || folderId <= 0 || !(permission == Configuration.PUBLIC_EDIT || permission == Configuration.PUBLIC_VIEW || permission == Configuration.ONLY_PEPOLE_SHARED)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("permission", permission + "");
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/folder/share/public/add";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(className, "changePermissionLinkFolderShared file=" + folderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/get")
    public Route getLinkFileShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (appKey.isEmpty() || fileId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("fileId", Long.toString(fileId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/file/share/public/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(className, "getLinkFileShared fileId=" + fileId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/get")
    public Route getLinkFolderShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (appKey.isEmpty() || folderId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("folderId", Long.toString(folderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/folder/share/public/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(className, "getLinkFolderShared folderId=" + folderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/share/public/get/:key")
    public Route accessLinkShared() {
        return (Request req, Response response) -> {
//            response.header("Content-Type", "application/json");            if(!Configuration.agentStatus){                return DataResponse.AGENT_UNAVAILABLE;            }
            String keyItem = String.valueOf(req.params(":key"));
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (keyItem == null || keyItem.isEmpty() || accountName.isEmpty() || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    String url_submit = Configuration.url_proxy + "/share/public/get/" + keyItem;
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(className, "accessLinkShared keyItem=" + keyItem + "\t  resp=" + result);
                    JSONObject objResp = new JSONObject(result);
                    if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
                        JSONObject objData = objResp.getJSONObject("data");
                        String type = objData.getString("type");
                        if ("file".equals(type)) {
                            FileInfo f = new FileInfo();
                            f.assignFrom(objData);
                            if (objData.getInt("fileStatus") == 2) {
//                                long fileId = objData.getLong("fileId");
//
//                                String shaFile = objData.getString("checksumSHA2");
//                                String fileName = objData.getString("fileName");
                                DownloadFileWorker.processDownloadFile(f);
//                                while (true) {
//                                    FileInfo fInfo = FileManager.getFileDownloading(fileId);
//                                    if (fInfo != null) {
//                                        if (fInfo.numberChunkSucess == fInfo.numberOfChunks) {
//                                            shaFile = fInfo.checksumSHA2;
//                                            fileName = fInfo.fileName;
//                                            break;
//                                        }
//                                    }
//                                    try {
//                                        Thread.sleep(100l);
//                                    } catch (InterruptedException ex) {
//                                        java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                }
                                String pathFile = Configuration.path_folder_store + File.separator + f.checksumSHA2 + ".content";
                                DownloadServlet.returnFileToBrowser(f.fileName, pathFile, req, response);
                                return response;
                            } else {
                                return result;
                            }

                        }

                    }
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/share/public/get/:folderId")
    public Route getFolderPublic() {
        return (Request req, Response response) -> {
//            response.header("Content-Type", "application/json");            if(!Configuration.agentStatus){                return DataResponse.AGENT_UNAVAILABLE;            }
            Long folderId = Long.valueOf(req.params(":folderId"));
            String appKey = ServletUtil.getStringParameter(req, "appKey");
            String accountName = ServletUtil.getStringParameter(req, "accountName");
            if (folderId == null || folderId <= 0 || appKey.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    JSONObject params = new JSONObject();

                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    params.put("folderId", Long.toString(folderId));
                    String url_submit = Configuration.url_proxy + "/folder/share/public/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(className, "getFolderPublic keyItem=" + folderId + "\t  resp=" + result);
                    return result;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return new DataResponse(-1, ex.getMessage());
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/file/share/user/get")
    public Route getUsersAccessFile() {
        return (Request req, Response response) -> {
            try {
                Utils.printLogSystem(className, " getUsersAccessFile");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long itemId = ServletUtil.getLongParameter(req, "fileId");
                if (itemId <= 0 || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    params.put("fileId", Long.toString(itemId));
                    String url_submit = Configuration.url_proxy + "/file/share/user/get";
                    return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/share/user/get")
    public Route getUsersAccessFolder() {
        return (Request req, Response response) -> {
            try {
                Utils.printLogSystem(className, " getUsersAccessFolder");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long itemId = ServletUtil.getLongParameter(req, "folderId");
                if (itemId <= 0 || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    params.put("folderId", Long.toString(itemId));
                    String url_submit = Configuration.url_proxy + "/folder/share/user/get";
                    return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/user/permission/update")
    public Route updatePermissionFile() {
        return (Request req, Response response) -> {
            try {
                Utils.printLogSystem(className, " updatePermissionFile");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long fileId = ServletUtil.getLongParameter(req, "fileId");
                String accountShared = ServletUtil.getStringParameter(req, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(req, "appKeyShared");
                String permissions = ServletUtil.getStringParameter(req, "permissions");
                if (fileId <= 0 || accountName.isEmpty() || accountShared.isEmpty()
                        || appKey.isEmpty() || appKeyShared.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    params.put("accountShared", accountShared);
                    params.put("appKeyShared", appKeyShared);
                    params.put("fileId", Long.toString(fileId));
                    params.put("permissions", permissions);
                    String url_submit = Configuration.url_proxy + "/file/user/permission/update";
                    return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/user/permission/update")
    public Route updatePermissionFolder() {
        return (Request req, Response response) -> {
            try {
                Utils.printLogSystem(className, " updatePermissionFolder");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long folderId = ServletUtil.getLongParameter(req, "folderId");
                String accountShared = ServletUtil.getStringParameter(req, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(req, "appKeyShared");
                String permissions = ServletUtil.getStringParameter(req, "permissions");
                if (folderId <= 0 || accountName.isEmpty() || accountShared.isEmpty()
                        || appKey.isEmpty() || appKeyShared.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);

                    params.put("accountShared", accountShared);
                    params.put("appKeyShared", appKeyShared);
                    params.put("folderId", Long.toString(folderId));
                    params.put("permissions", permissions);
                    String url_submit = Configuration.url_proxy + "/folder/user/permission/update";
                    return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get, post", path = "/file/user/permission/remove")
    public Route removePermissionFile() {
        return (Request req, Response response) -> {
            try {
                Utils.printLogSystem(className, " removePermissionFile");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long fileId = ServletUtil.getLongParameter(req, "fileId");
                String accountShared = ServletUtil.getStringParameter(req, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(req, "appKeyShared");
                if (fileId <= 0 || accountShared.isEmpty() || accountName.isEmpty()
                        || appKeyShared.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("appKeyShared", appKeyShared);
                    params.put("accountShared", accountShared);
                    params.put("fileId", Long.toString(fileId));
                    String url_submit = Configuration.url_proxy + "/file/user/permission/remove";
                    return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "get, post", path = "/folder/user/permission/remove")
    public Route removePermissionFolder() {
        return (Request req, Response response) -> {
            try {
                Utils.printLogSystem(className, " removePermissionFolder");
                String appKey = ServletUtil.getStringParameter(req, "appKey");
                String accountName = ServletUtil.getStringParameter(req, "accountName");
                long folderId = ServletUtil.getLongParameter(req, "folderId");
                String accountShared = ServletUtil.getStringParameter(req, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(req, "appKeyShared");
                if (folderId <= 0 || accountShared.isEmpty() || accountName.isEmpty()
                        || appKeyShared.isEmpty() || appKey.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    JSONObject params = new JSONObject();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("appKeyShared", appKeyShared);
                    params.put("accountShared", accountShared);
                    params.put("folderId", Long.toString(folderId));
                    String url_submit = Configuration.url_proxy + "/folder/user/permission/remove";
                    return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

}
