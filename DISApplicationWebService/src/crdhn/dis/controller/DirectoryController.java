/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.FileInfo;
import crdhn.dis.model.SessionInfo;
import crdhn.dis.render.RenderMain;
import crdhn.dis.transport.DISUserDBConnector;
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
import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class DirectoryController extends Controller {

    private static final Logger _logger = LoggerFactory.getLogger(DirectoryController.class);
    private static final String _className = "=============DirectoryController";

    public DirectoryController() {
    }

    @RouteInfo(method = "post", path = "/folder/create")
    public Route createFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String parentId = ServletUtil.getStringParameter(req, "parentId");
            String folderName = ServletUtil.getStringParameter(req, "folderName");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (parentId.isEmpty() || folderName.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("parentId", parentId);
                        params.put("folderName", folderName);
                        params.put("accountName", sessionInfo.email);

                        String url_create = Configuration.url_agent_server + File.separator + "folder/create";
                        String result = HttpRequestUtils.sendHttpRequest(url_create, "POST", params);
                        Utils.printLogSystem(_className, "create folder parentId=" + parentId + "\t folderName=" + folderName + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/owner/get")
    public Route getRootOwnerInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            try {
                SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                if (sessionInfo != null) {
                    HashMap<String, String> params = new HashMap();
                    params.put("accountName", sessionInfo.email);
                    String url_submit = Configuration.url_agent_server + "/folder/owner/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getRootOwnerInfo resp=" + result);
                    return result;
                } else {
                    return DataResponse.SESSION_EXPIRED;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/shared/get")
    public Route getRootSharedInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            try {
                SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                if (sessionInfo != null) {
                    HashMap<String, String> params = new HashMap();
                    params.put("accountName", sessionInfo.email);
                    String url_submit = Configuration.url_agent_server + "/folder/shared/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getRootSharedInfo resp=" + result);
                    return result;
                } else {
                    return DataResponse.SESSION_EXPIRED;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/get")
    public Route getFolderInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (folderId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("folderId", Long.toString(folderId));
                        params.put("accountName", sessionInfo.email);
                        String url_submit = Configuration.url_agent_server + File.separator + "folder/get";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                        Utils.printLogSystem(_className, "getFolderInfo folderId=" + folderId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "post", path = "/folder/changename")
    public Route changeNameFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String folderId = ServletUtil.getStringParameter(req, "folderId");
            String folderName = ServletUtil.getStringParameter(req, "folderName");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (folderId.isEmpty() || folderName.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("folderId", folderId);
                        params.put("folderName", folderName);
                        params.put("accountName", sessionInfo.email);
                        String url_submit = Configuration.url_agent_server + File.separator + "folder/changename";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "ChangeNameFolder folderId=" + folderId + "\t folderName=" + folderName + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "post, get", path = "/folder/move")
    public Route MoveFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            long newFolderId = ServletUtil.getLongParameter(req, "newFolderId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (folderId <= 0 || newFolderId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("folderId", folderId + "");
                        params.put("newFolderId", Long.toString(newFolderId));
                        params.put("accountName", sessionInfo.email);
                        String url_submit = Configuration.url_agent_server + File.separator + "folder/move";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "MoveFolder folderId=" + folderId + "\t newFolderId=" + newFolderId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/delete")
    public Route deleteFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (folderId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("folderId", folderId + "");
                        params.put("accountName", sessionInfo.email);
                        String url_submit = Configuration.url_agent_server + File.separator + "folder/delete";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "deleteFolder folderId=" + folderId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share")
    public Route shareFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long folderId = ServletUtil.getLongParameter(req, "folderId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            String permissions = ServletUtil.getStringParameter(req, "permissions");
            String accounts = ServletUtil.getStringParameter(req, "emails");
//            String linkShare = ServletUtil.getStringParameter(req, "link");
//            String viewerId = ServletUtil.getStringParameter(req, "viewerId");
//            String itemName = ServletUtil.getStringParameter(req, "itemName");
            if (folderId <= 0 || accounts.isEmpty() || permissions.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("folderId", folderId + "");
                        params.put("permissions", permissions);

                        params.put("accountName", sessionInfo.email);
                        String[] listAccount = accounts.split(",");
                        JSONArray arrAccount = new JSONArray();
                        for (String acc : listAccount) {
                            JSONObject obj = new JSONObject();
                            obj.put("appKey", Configuration.appKey);
                            obj.put("accountName", acc);
                            arrAccount.put(obj);
                        }
                        params.put("accounts", arrAccount.toString());
                        String url_submit = Configuration.url_agent_server + File.separator + "folder/share";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "shareFolder folderId=" + folderId + "\t  resp=" + result);
//                        JSONObject objResp = new JSONObject(result);
//                        if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
//                            String subject = "[DIS] Share folder";
//                            FSPEmailSenderServiceClient.getInstance().sendEmailWithParams(accounts, subject, viewerId, itemName, linkShare);
//                        }
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/get")
    public Route getFileInfo() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                Utils.printLogSystem(_className, " getFileInfo");
                String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
                if (sessionKey.isEmpty()) {
                    sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
                }
                SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                if (sessionInfo != null) {
                    long fileId = ServletUtil.getFileIdFromParameter(request, "fileid");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("accountName", sessionInfo.email);
                    params.put("fileId", Long.toString(fileId));
                    String resp = HttpRequestUtils.sendHttpRequest(Configuration.url_agent_server + "/file/agent/get", "GET", params);
                    Utils.printLogSystem(_className, "getFileInfo \t resp=" + resp);
                    JSONObject dataResp = new JSONObject(resp);
                    if (dataResp.has("error_code") && dataResp.getInt("error_code") == 0) {
                        JSONObject objData = dataResp.getJSONObject("data");
                        return new DataResponse(objData, DataResponse.DataType.JSON_STR, true);
                    }
                    return resp;
                } else {
                    return DataResponse.SESSION_EXPIRED;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DataResponse(-1, ex.getMessage());
            }
        };
    }

    @RouteInfo(method = "post, get", path = "/file/move")
    public Route MoveFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            long parentId = ServletUtil.getLongParameter(req, "parentId");
            long newFolderId = ServletUtil.getLongParameter(req, "newFolderId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (fileId <= 0 || newFolderId <= 0 || parentId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("fileId", fileId + "");
                        params.put("parentId", parentId + "");
                        params.put("newFolderId", Long.toString(newFolderId));
                        params.put("accountName", sessionInfo.email);
                        String url_submit = Configuration.url_agent_server + File.separator + "file/move";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "MoveFile fileId=" + fileId + "\t newFileId=" + newFolderId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/delete")
    public Route deleteFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            long parentId = ServletUtil.getLongParameter(req, "parentId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            if (fileId <= 0 || parentId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("fileId", fileId + "");
                        params.put("parentId", parentId + "");
                        params.put("accountName", sessionInfo.email);
                        String url_submit = Configuration.url_agent_server + File.separator + "file/delete";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "deleteFile fileId=" + fileId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share")
    public Route shareFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long fileId = ServletUtil.getLongParameter(req, "fileId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            String permissions = ServletUtil.getStringParameter(req, "permissions");
            String accounts = ServletUtil.getStringParameter(req, "emails");
//            String linkShare = ServletUtil.getStringParameter(req, "link");
//            String viewerId = ServletUtil.getStringParameter(req, "viewerId");
//            String itemName = ServletUtil.getStringParameter(req, "itemName");
            if (fileId <= 0 || accounts.isEmpty() || permissions.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("fileId", fileId + "");
                        params.put("permissions", permissions);
                        params.put("accountName", sessionInfo.email);
                        String[] listAccount = accounts.split(",");
                        JSONArray arrAccount = new JSONArray();
                        for (String acc : listAccount) {
                            JSONObject obj = new JSONObject();
                            obj.put("appKey", Configuration.appKey);
                            obj.put("accountName", acc);
                            arrAccount.put(obj);
                        }
                        params.put("accounts", arrAccount.toString());
                        String url_submit = Configuration.url_agent_server + File.separator + "file/share";
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "shareFile fileId=" + fileId + "\t  resp=" + result);
//                        JSONObject objResp = new JSONObject(result);
//                        if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
//                            String subject = "[DIS] Share File";
//                            FSPEmailSenderServiceClient.getInstance().sendEmailWithParams(emails, subject, viewerId, itemName, linkShare);
//                        }
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/share/public/add")
    public Route createLinkSharedPublic() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long itemId = ServletUtil.getLongParameter(req, "itemId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            int type = ServletUtil.getIntParameter(req, "type");
            if (itemId <= 0 || type < 0 || type > 1) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        String url_submit = "";
                        if (type == 0) { //file
                            params.put("fileId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/file/share/public/add";
                        } else {
                            params.put("folderId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/folder/share/public/add";
                        }
                        params.put("permission", Configuration.PUBLIC_VIEW + "");
                        params.put("accountName", sessionInfo.email);
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "createLinkSharedPublic itemId=" + itemId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/share/public/change")
    public Route changePermissionLinkShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long itemId = ServletUtil.getLongParameter(req, "itemId");
            int permission = ServletUtil.getIntParameter(req, "permission");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            int type = ServletUtil.getIntParameter(req, "type");
            if (itemId <= 0 || type < 0 || type > 1 || !(permission == Configuration.PUBLIC_EDIT || permission == Configuration.PUBLIC_VIEW || permission == Configuration.ONLY_PEPOLE_SHARED)) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        String url_submit = "";
                        if (type == 0) { //file
                            params.put("fileId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/file/share/public/add";
                        } else {
                            params.put("folderId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/folder/share/public/add";
                        }
                        params.put("permission", permission + "");
                        params.put("accountName", sessionInfo.email);
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                        Utils.printLogSystem(_className, "changePermissionLinkShared itemId=" + itemId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/share/public/get")
    public Route getLinkShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            long itemId = ServletUtil.getLongParameter(req, "itemId");
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            int type = ServletUtil.getIntParameter(req, "type");
            if (itemId <= 0 || type < 0 || type > 1) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        String url_submit = "";
                        if (type == 0) { //file
                            params.put("fileId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/file/share/public/get";
                        } else {
                            params.put("folderId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/folder/share/public/get";
                        }
                        params.put("accountName", sessionInfo.email);
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                        Utils.printLogSystem(_className, "getLinkShared itemId=" + itemId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/share/public/get/:key")
    public Route accessLinkShared() {
        return (Request req, Response response) -> {
//            response.header("Content-Type", "application/json");
            String keyItem = String.valueOf(req.params(":key"));
            if (keyItem == null || keyItem.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        params.put("accountName", sessionInfo.email);
//                    params.put("itemKey", keyItem);
                        String url_submit = Configuration.url_agent_server + "/share/public/get/" + keyItem;
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                        Utils.printLogSystem(_className, "accessLinkShared keyItem=" + keyItem + "\t  resp=" + result);
                        JSONObject objResp = new JSONObject(result);
                        if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
                            JSONObject objData = objResp.getJSONObject("data");
                            String type = objData.getString("type");
                            if ("file".equals(type)) {
                                FileInfo finfo = new FileInfo();
                                finfo.assignFrom(objData);
                                return RenderMain.getInstance().renderDownloadFile(req, finfo, objData.getInt("fileStatus"));
                            } else {
                                long folderId = objData.getLong("folderId");
                                return RenderMain.getInstance().renderFolderPublic(folderId, req, response);
                            }

                        } else if (objResp.has("error_code") && objResp.getInt("error_code") == DataResponse.SESSION_EXPIRED.getError()) {
                            response.raw().sendRedirect("/login");
                        } else {
                            return RenderMain.getInstance().renderNotFoundItem(req, objResp.getString("error_message") + " (" + objResp.getInt("error_code") + ")");
                        }
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/share/public/folder/get/:folderId")
    public Route getFolderPublic() {
        return (Request req, Response response) -> {
//            response.header("Content-Type", "application/json");
            Long folderId = Long.valueOf(req.params(":folderId"));
            if (folderId == null || folderId <= 0) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        params.put("accountName", sessionInfo.email);
//                    params.put("itemKey", keyItem);
                        String url_submit = Configuration.url_agent_server + "/folder/share/public/get/" + folderId;
                        String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                        Utils.printLogSystem(_className, "getFolderPublic keyItem=" + folderId + "\t  resp=" + result);
                        return result;
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/item/user/get")
    public Route getUsersAccess() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(_className, " getUsersAccess");
                String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
                long itemId = ServletUtil.getLongParameter(request, "itemId");
                int type = ServletUtil.getIntParameter(request, "type");
                if (itemId <= 0 || type < 0 || type > 1) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        String url_submit = "";
                        if (type == 0) { //file
                            params.put("fileId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/file/share/user/get";
                        } else {
                            params.put("folderId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/folder/share/user/get";
                        }
                        params.put("accountName", sessionInfo.email);
                        return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/item/user/permission/update")
    public Route updatePermission() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(_className, " updatePermission");
                String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
                long itemId = ServletUtil.getLongParameter(request, "itemId");
                int type = ServletUtil.getIntParameter(request, "type");
                String accountShared = ServletUtil.getStringParameter(request, "accountShared");
                String appKeyShared = ServletUtil.getStringParameter(request, "appKeyShared");
                String permissions = ServletUtil.getStringParameter(request, "permissions");
                if (itemId <= 0 || accountShared.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        String url_submit;
                        if (type == 0) { //file
                            params.put("fileId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/file/user/permission/update";
                        } else {
                            params.put("folderId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/folder/user/permission/update";
                        }
                        params.put("accountName", sessionInfo.email);
                        params.put("accountShared", accountShared);
                        params.put("appKeyShared", appKeyShared);
                        params.put("permissions", permissions);
                        return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

    @RouteInfo(method = "get, post", path = "/item/user/permission/remove")
    public Route removePermission() {
        return (Request request, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                Utils.printLogSystem(_className, " removePermission");
                String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
                long itemId = ServletUtil.getLongParameter(request, "itemId");
                int type = ServletUtil.getIntParameter(request, "type");
                String accountShared = ServletUtil.getStringParameter(request, "accountShared");
                String appShared = ServletUtil.getStringParameter(request, "appKeyShared");
                if (itemId <= 0 || type < 0 || type > 1 || accountShared.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                } else {
                    SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
                    if (sessionInfo != null) {
                        HashMap<String, String> params = new HashMap();
                        String url_submit;
                        if (type == 0) { //file
                            params.put("fileId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/file/user/permission/remove";
                        } else {
                            params.put("folderId", Long.toString(itemId));
                            url_submit = Configuration.url_agent_server + "/folder/user/permission/remove";
                        }
                        params.put("accountName", sessionInfo.email);
                        params.put("accountShared", accountShared);
                        params.put("appKeyShared", appShared);
                        return HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    } else {
                        return DataResponse.SESSION_EXPIRED;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

}
