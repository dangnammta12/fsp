/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.utils.AsymmetricCryptography;
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
import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class DirectoryController extends Controller {

    private static final Logger _logger = LoggerFactory.getLogger(DirectoryController.class);
    private static final String _className = "DirectoryController";

    public DirectoryController() {
    }

    @RouteInfo(method = "get", path = "/ping")
    public Route Ping() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "Ping.data_req=" + encryptedData);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String agentKey = objParams.getString("agentKey");
            if (agentKey.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("agentKey", agentKey);
                    params.put("agentIp", req.raw().getRemoteHost());
                    String url_ping = Configuration.url_dis_config + File.separator + "agent/ping";
                    String result = HttpRequestUtils.sendHttpRequest(url_ping, "GET", params);
                    Utils.printLogSystem(_className, " agent ping agentKey=" + agentKey + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "post", path = "/folder/create")
    public Route createFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "createFolder.data_req=" + encryptedData);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String parentId = objParams.getString("parentId");
            String folderName = objParams.getString("folderName");
            String accountName = objParams.getString("accountName");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");

            if (parentId.isEmpty() || folderName.isEmpty() || accountName.isEmpty() || appKey.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("parentId", parentId);
                    params.put("folderName", folderName);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_create = Configuration.url_directory + File.separator + "folder/create";
                    String result = HttpRequestUtils.sendHttpRequest(url_create, "POST", params);
                    Utils.printLogSystem(_className, "create folder parentId=" + parentId + "\t folderName=" + folderName + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/get")
    public Route getFolderInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "getFolderInfo.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            long folderId = objParams.getLong("folderId");
            String accountName = objParams.getString("accountName");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (folderId <= 0 || accountName.isEmpty() || appKey.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getFolderInfo folderId=" + folderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/get/public")
    public Route getFolderInfoPublic() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "getFolderInfoPublic.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            long folderId = objParams.getLong("folderId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (folderId <= 0 || appKey.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/get/public";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getFolderInfoPublic folderId=" + folderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/gets")
    public Route getFolderInfos() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            Utils.printLogSystem(_className, "getFolderInfos.data_req=" + data_req);
            JSONObject objParams = new JSONObject(data_req);
            String folderIds = objParams.getString("folderIds");
            String accountName = objParams.getString("accountName");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || folderIds == null || folderIds.isEmpty() || accountName == null || accountName.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderIds", folderIds);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/gets";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getFolderInfos folderIds=" + folderIds + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/owner/get")
    public Route getRootOwnerInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "getRootOwnerInfo.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            }
            try {
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String url_submit = Configuration.url_directory + "/folder/owner/get";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                Utils.printLogSystem(_className, "getRootOwnerInfo resp=" + result);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/shared/get")
    public Route getRootSharedInfo() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "getRootSharedInfo.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            }
            try {
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String url_submit = Configuration.url_directory + "/folder/shared/get";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                Utils.printLogSystem(_className, "getRootSharedInfo resp=" + result);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "post,get", path = "/folder/changename")
    public Route changeNameFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "changeNameFolder.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String folderName = objParams.getString("folderName");
            String accountName = objParams.getString("accountName");
            long folderId = objParams.getLong("folderId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || folderName == null || folderName.isEmpty() || folderId <= 0 || accountName == null || accountName.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("folderName", folderName);
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/changename";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "ChangeNameFolder folderId=" + folderId + "\t folderName=" + folderName + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "post", path = "/folder/move")
    public Route MoveFolder() {
        return (Request req, Response response) -> {
//            String data_req;
//            if ("GET".equals(req.raw().getMethod().toUpperCase())) {
//                String query = ServletUtil.getStringParameter(req, "q");
//                data_req = AsymmetricCryptography.getInstance().decryptText(query, Configuration.privateKeyDIS);
//            } else {
//                data_req = AsymmetricCryptography.getInstance().decryptText(req.body(), Configuration.privateKeyDIS);
//            }
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "MoveFolder.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long folderId = objParams.getLong("folderId");
            long newFolderId = objParams.getLong("newFolderId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || folderId <= 0 || newFolderId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("newFolderId", Long.toString(newFolderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/move";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "MoveFolder folderId=" + folderId + "\t newFolderId=" + newFolderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/delete")
    public Route deleteFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "deleteFolder.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long folderId = objParams.getLong("folderId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || folderId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", folderId + "");
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/delete";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "deleteFolder folderId=" + folderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share")
    public Route shareFolder() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "shareFolder.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long folderId = objParams.getLong("folderId");
            String permissions = objParams.getString("permissions");
            Object accounts = objParams.get("accounts");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accounts == null || permissions == null || folderId <= 0 || permissions.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("permissions", permissions);
                    params.put("accounts", accounts.toString());
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "folder/share";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "shareFolder folderId=" + folderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "post", path = "/file/move")
    public Route MoveFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "MoveFile.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long fileId = objParams.getLong("fileId");
            long parentId = objParams.getLong("parentId");
            long newFolderId = objParams.getLong("newFolderId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || fileId <= 0 || newFolderId <= 0 || parentId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("fileId", Long.toString(fileId));
                    params.put("parentId", Long.toString(parentId));
                    params.put("newFolderId", Long.toString(newFolderId));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "file/move";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "MoveFile fileId=" + fileId + "\t newFileId=" + newFolderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/delete")
    public Route deleteFileFromDirectory() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "deleteFileFromDirectory.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long fileId = objParams.getLong("fileId");
            long parentId = objParams.getLong("parentId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || fileId <= 0 || parentId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("fileId", fileId + "");
                    params.put("parentId", parentId + "");
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "file/delete";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "deleteFileFromDirectory fileId=" + fileId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share")
    public Route shareFile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "shareFile.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long fileId = objParams.getLong("fileId");
            String permissions = objParams.getString("permissions");
            Object accounts = objParams.get("accounts");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || permissions == null || accounts == null
                    || fileId <= 0 || accountName.isEmpty() || permissions.isEmpty()) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("fileId", Long.toString(fileId));
                    params.put("permissions", permissions);
                    params.put("accounts", accounts.toString());
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + File.separator + "file/share";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "shareFile fileId=" + fileId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/add")
    public Route shareFilePublic() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "shareFilePublic.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long fileId = objParams.getLong("fileId");
            int permission = objParams.getInt("permission");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || permission <= 0 || fileId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("fileId", Long.toString(fileId));
                    params.put("permission", Integer.toString(permission));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + "/file/share/public/add";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "shareFilePublic itemid=" + fileId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/add")
    public Route shareFolderPublic() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "shareFolderPublic.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            String accountName = objParams.getString("accountName");
            long folderId = objParams.getLong("folderId");
            int permission = objParams.getInt("permission");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || permission <= 0 || folderId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("permission", Integer.toString(permission));
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + "/folder/share/public/add";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "POST", params);
                    Utils.printLogSystem(_className, "shareFolderPublic itemid=" + folderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/share/public/get")
    public Route getLinkFileShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "getLinkFileShared.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            long fileId = objParams.getLong("fileId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            if (appKey.isEmpty() || agentKey.isEmpty() || fileId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("fileId", Long.toString(fileId));
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    String url_submit = Configuration.url_directory + "/file/share/public/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getLinkFileShared itemId=" + fileId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/folder/share/public/get")
    public Route getLinkFolderShared() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String keySecret = ServletUtil.getStringParameter(req, "k");
            String encryptedData = ServletUtil.getStringParameter(req, "data");
            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
            Utils.printLogSystem(_className, "getLinkFolderShared.data_req=" + data_req);
            if (data_req == null || data_req.isEmpty()) {
                return DataResponse.DECRYPT_FAILED;
            }
            JSONObject objParams = new JSONObject(data_req);
            long folderId = objParams.getLong("folderId");
            String appKey = objParams.getString("appKey");
            String agentKey = objParams.getString("agentKey");
            String accountName = objParams.getString("accountName");
            if (appKey.isEmpty() || agentKey.isEmpty() || folderId <= 0) {
                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
            } else {
                try {
                    HashMap<String, String> params = new HashMap();
                    params.put("folderId", Long.toString(folderId));
                    params.put("appKey", appKey);
                    params.put("agentKey", agentKey);
                    params.put("accountName", accountName);
                    String url_submit = Configuration.url_directory + "/folder/share/public/get";
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                    Utils.printLogSystem(_className, "getLinkFolderShared itemId=" + folderId + "\t  resp=" + result);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/share/public/get/:key")
    public Route getItemWithLinkPublic() {
        return (Request req, Response response) -> {
            try {
                String keyItem = String.valueOf(req.params(":key"));
                if (keyItem == null || keyItem.isEmpty()) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                } else {
                    HashMap<String, String> paramCheckPermission = new HashMap();
                    String url_submit = Configuration.url_directory + "/share/public/get/" + keyItem;
                    String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", paramCheckPermission);
                    Utils.printLogSystem(_className, "getItemWithLinkPublic keyItem=" + keyItem + "\t  resp=" + result);
                    JSONObject objRespCheckPermission = new JSONObject(result);
                    if (objRespCheckPermission.has("error_code") && objRespCheckPermission.getInt("error_code") == 0) {
                        JSONObject objData = objRespCheckPermission.getJSONObject("data");
                        int permission = objData.getInt("permission");
                        long fileId = objData.getLong("fileId");
                        long folderId = objData.getLong("folderId");
                        String respData = "";
                        if (permission == Configuration.PUBLIC_VIEW || permission == Configuration.PUBLIC_EDIT) {

                            if (fileId > 0) {
                                HashMap<String, String> paramGetFile = new HashMap<>();
                                paramGetFile.put("fileId", Long.toString(fileId));
                                respData = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/getFile", "GET", paramGetFile);
                            } else {
                                HashMap<String, String> paramFolder = new HashMap();
                                paramFolder.put("folderId", Long.toString(folderId));
                                String url_get_folder = Configuration.url_directory + "/folder/get/public";
                                respData = HttpRequestUtils.sendHttpRequest(url_get_folder, "GET", paramFolder);
                            }

                        } else {
                            response.header("Content-Type", "application/json");
                            String keySecret = ServletUtil.getStringParameter(req, "k");
                            String encryptedData = ServletUtil.getStringParameter(req, "data");
                            String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                            Utils.printLogSystem(_className, "getItemWithLinkPublic.data_req=" + data_req);
                            if (data_req == null || data_req.isEmpty()) {
                                return DataResponse.DECRYPT_FAILED;
                            }
                            JSONObject objParams = new JSONObject(data_req);
                            String accountName = objParams.getString("accountName");
                            String appKey = objParams.getString("appKey");
                            String agentKey = objParams.getString("agentKey");
                            if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || (folderId <= 0 && fileId <= 0)) {
                                return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.ACCESS_DENY.toString(), Configuration.publicKeyAgent);
                            } else if (folderId > 0) {
                                HashMap<String, String> params = new HashMap();
                                params.put("accountName", accountName);
                                params.put("appKey", appKey);
                                params.put("agentKey", agentKey);
                                params.put("folderId", Long.toString(folderId));
                                String url_submit_getfolder = Configuration.url_directory + File.separator + "folder/get";
                                respData = HttpRequestUtils.sendHttpRequest(url_submit_getfolder, "GET", params);
                            } else {
                                HashMap paramPermission = new HashMap();
                                paramPermission.put("accountName", accountName);
                                paramPermission.put("appKey", appKey);
                                paramPermission.put("agentKey", agentKey);
                                paramPermission.put("fileId", Long.toString(fileId));
                                String checkPermission = HttpRequestUtils.sendHttpRequest(Configuration.url_directory + "/file/permission", "GET", paramPermission);
                                JSONObject objSession = new JSONObject(checkPermission);
                                if (objSession.has("error_code") && objSession.getInt("error_code") == 0) {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("accountName", accountName);
                                    params.put("fileId", Long.toString(fileId));
                                    params.put("appKey", appKey);
                                    params.put("agentKey", agentKey);
                                    respData = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/getFile", "GET", params);
                                } else {
                                    return AsymmetricCryptography.getInstance().encryptTextData(checkPermission, Configuration.publicKeyAgent);
                                }

                            }
                        }
                        JSONObject objResp = new JSONObject(respData);
                        if (objResp.has("error_code") && objResp.getInt("error_code") == 0) {
                            JSONObject objDataResp = objResp.getJSONObject("data");
                            String type = "error";
                            if (fileId > 0) {
                                type = "file";
                            } else if (folderId > 0) {
                                type = "folder";
                            }
                            objDataResp.put("type", type);
                            return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(objDataResp, DataResponse.DataType.JSON_STR, true).toString(), Configuration.publicKeyAgent);
                        }
                        return AsymmetricCryptography.getInstance().encryptTextData(respData, Configuration.publicKeyAgent);
                    } else {
                        return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, e.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get", path = "/file/share/user/get")
    public Route getUsersAccessFile() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "getUsersAccessFile.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || fileId <= 0) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("fileId", Long.toString(fileId));
                String url_submit = Configuration.url_directory + "/file/share/user/get";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get", path = "/folder/share/user/get")
    public Route getUsersAccessFolder() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "getUsersAccessFolder.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long folderId = objParams.getLong("folderId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || folderId <= 0) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("folderId", Long.toString(folderId));
                String url_submit = Configuration.url_directory + "/folder/share/user/get";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/user/permission/update")
    public Route updatePermissionFile() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "updatePermissionFile.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                long fileId = objParams.getLong("fileId");
                String accountShared = objParams.getString("accountShared");
                String appKeyShared = objParams.getString("appKeyShared");
                String permissions = objParams.getString("permissions");

                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || accountShared == null
                        || accountShared.isEmpty() || appKeyShared.isEmpty() || permissions == null) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("accountShared", accountShared);
                params.put("appKeyShared", appKeyShared);
                params.put("fileId", Long.toString(fileId));
                params.put("permissions", permissions);
                String url_submit = Configuration.url_directory + "/file/user/permission/update";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/folder/user/permission/update")
    public Route updatePermissionFolder() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "updatePermissionFolder.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                long folderId = objParams.getLong("folderId");
                String accountShared = objParams.getString("accountShared");
                String appKeyShared = objParams.getString("appKeyShared");
                String permissions = objParams.getString("permissions");

                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || accountShared == null
                        || accountShared.isEmpty() || appKeyShared.isEmpty() || permissions == null) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("accountShared", accountShared);
                params.put("appKeyShared", appKeyShared);
                params.put("folderId", Long.toString(folderId));
                params.put("permissions", permissions);
                String url_submit = Configuration.url_directory + "/folder/user/permission/update";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get, post", path = "/file/user/permission/remove")
    public Route removePermissionFile() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "removePermissionFile.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                String accountShared = objParams.getString("accountShared");
                String appKeyShared = objParams.getString("appKeyShared");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || accountShared == null
                        || accountShared.isEmpty()) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("accountShared", accountShared);
                params.put("appKeyShared", appKeyShared);
                params.put("fileId", Long.toString(fileId));
                String url_submit = Configuration.url_directory + "/file/user/permission/remove";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }
    
    @RouteInfo(method = "get, post", path = "/folder/user/permission/remove")
    public Route removePermissionFolder() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "removePermissionFolder.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long folderId = objParams.getLong("folderId");
                String accountShared = objParams.getString("accountShared");
                String appKeyShared = objParams.getString("appKeyShared");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || accountShared == null
                        || accountShared.isEmpty()) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("accountShared", accountShared);
                params.put("appKeyShared", appKeyShared);
                params.put("folderId", Long.toString(folderId));
                String url_submit = Configuration.url_directory + "/folder/user/permission/remove";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/permission")
    public Route checkPermissionFile() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "checkPermissionFile.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");

                HashMap<String, String> params = new HashMap();
                params.put("fileId", Long.toString(fileId));
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String url_submit = Configuration.url_directory + "/file/permission";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }
    
     @RouteInfo(method = "get,post", path = "/folder/permission")
    public Route checkPermissionFolder() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "checkPermissionFolder.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long folderId = objParams.getLong("folderId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                
                HashMap<String, String> params = new HashMap();
                params.put("folderId", Long.toString(folderId));
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String url_submit = Configuration.url_directory + "/folder/permission";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }
}
