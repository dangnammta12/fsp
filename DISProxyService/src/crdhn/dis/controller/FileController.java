package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.utils.AsymmetricCryptography;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.util.HashMap;
import org.json.JSONObject;

public class FileController extends Controller {

    private static final String _className = "FileController";

    public FileController() {
    }

    @RouteInfo(method = "get,post", path = "/file/add")
    public Route addFileToDirectory() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "addFileToDirectory.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                long parentId = objParams.getLong("parentId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty() || fileId <= 0 || parentId <= 0) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("fileId", Long.toString(fileId));
                params.put("parentId", Long.toString(fileId));
                String result = HttpRequestUtils.sendHttpRequest(Configuration.url_directory + "/file/add", "POST", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

//    @RouteInfo(method = "get,post", path = "/file/getids")
//    public Route getFileIds() {
//        return (Request req, Response response) -> {
//            response.header("Content-Type", "application/json");
//            try {
//                String keySecret = ServletUtil.getStringParameter(req, "k");
//                String encryptedData = ServletUtil.getStringParameter(req, "data");
//                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
//                Utils.printLogSystem(_className, "getFileIds.data_req=" + data_req);
//                if (data_req == null || data_req.isEmpty()) {
//                    return DataResponse.DECRYPT_FAILED;
//                }
//                JSONObject objParams = new JSONObject(data_req);
//                String accountName = objParams.getString("accountName");
//                String appKey = objParams.getString("appKey");
//                String agentKey = objParams.getString("agentKey");
//                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty()) {
//                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
//                }
//                HashMap<String, String> params = new HashMap<>();
//                params.put("accountName", accountName);
//                params.put("appKey", appKey);
//                params.put("agentKey", agentKey);
//                String result = HttpRequestUtils.sendHttpRequest(Configuration.url_directory + "/file/getids", "GET", params);
//                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
//            }
//        };
//    }
    @RouteInfo(method = "get,post", path = "/file/get")
    public Route getFileInfo() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "getFileInfo.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                //check permission file
                HashMap<String, String> paramsCheckPermission = new HashMap<>();
                paramsCheckPermission.put("accountName", accountName);
                paramsCheckPermission.put("appKey", appKey);
                paramsCheckPermission.put("agentKey", agentKey);
                paramsCheckPermission.put("fileId", Long.toString(fileId));
                String result_permission = HttpRequestUtils.sendHttpRequest(Configuration.url_directory + "/file/permission", "GET", paramsCheckPermission);
                Utils.printLogSystem("FileController.getFileInfo.checkpermission =", result_permission);
                JSONObject objRespPermission = new JSONObject(result_permission);
                if (objRespPermission.has("error_code") && objRespPermission.getInt("error_code") == 0) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("accountName", accountName);
                    params.put("appKey", appKey);
                    params.put("fileId", Long.toString(fileId));
                    String result = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/getFile", "GET", params);
                    return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
                } else {
                    return AsymmetricCryptography.getInstance().encryptTextData(result_permission, Configuration.publicKeyAgent);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }

        };
    }

//    @RouteInfo(method = "get,post", path = "/file/get/public")
//    public Route getFileInfoPublic() {
//        return (Request req, Response response) -> {
//            try {
//                response.header("Content-Type", "application/json");
//                String keySecret = ServletUtil.getStringParameter(req, "k");
//                String encryptedData = ServletUtil.getStringParameter(req, "data");
//                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
//                Utils.printLogSystem(_className, "getFileInfoPublic.data_req=" + data_req);
//                if (data_req == null || data_req.isEmpty()) {
//                    return DataResponse.DECRYPT_FAILED;
//                }
//                JSONObject objParams = new JSONObject(data_req);
//                long fileId = objParams.getLong("fileId");
//                String appKey = objParams.getString("appKey");
//                String agentKey = objParams.getString("agentKey");
//                if (appKey.isEmpty() || agentKey.isEmpty() || fileId <= 0) {
//                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
//                }
//                HashMap<String, String> params = new HashMap<>();
//                params.put("fileId", Long.toString(fileId));
//                params.put("appKey", appKey);
//                params.put("agentKey", agentKey);
//                String result = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/getFile", "GET", params);
//                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
//            }
//
//        };
//    }
    @RouteInfo(method = "get,post", path = "/file/getFiles")
    public Route getFileInfos() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "getFileInfos.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String fileIds = objParams.getString("fileIds");
                String agentKey = objParams.getString("agentKey");
                if (!fileIds.isEmpty() && !agentKey.isEmpty()) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("fileIds", fileIds);
                    String resp_fileInfos = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/getFiles", "GET", params);
                    Utils.printLogSystem("FileController.getFileInfos resp_fileInfos=", resp_fileInfos);
                    return AsymmetricCryptography.getInstance().encryptTextData(resp_fileInfos, Configuration.publicKeyAgent);
                } else {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/file/delete")
    public Route deleteFile() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "deleteFile.data_req=" + data_req);
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
                HashMap<String, String> params = new HashMap<>();
                params.put("accountName", accountName);
                params.put("fileId", Long.toString(fileId));
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String respDeleteFile = HttpRequestUtils.sendHttpRequest(Configuration.url_directory + "/file/delete", "POST", params);
                return AsymmetricCryptography.getInstance().encryptTextData(respDeleteFile, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/file/addFile")
    public Route addFile() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "addFile.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                String accountName = objParams.getString("accountName");
                long fileSize = objParams.getLong("filesize");
                String fileName = objParams.getString("filename");
                String sha2 = objParams.getString("sha2");
                String md5 = objParams.getString("md5");
                int nbChunks = objParams.getInt("nbchunks");
                int chunkSize = objParams.getInt("chunkSize");
                long parentId = objParams.getLong("parentId");
                if (appKey == null || appKey.isEmpty() || agentKey.isEmpty() || accountName == null || accountName.isEmpty()
                        || fileSize <= 0 || sha2 == null || sha2.isEmpty()
                        || md5 == null || nbChunks <= 0 || parentId <= 0) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("fileSize", Long.toString(fileSize));
                params.put("fileName", fileName);
                params.put("sha2", sha2);
                params.put("md5", md5);
                params.put("nbChunks", Integer.toString(nbChunks));
                params.put("chunkSize", Integer.toString(chunkSize));
                params.put("ownerName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String respUploadFile = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/addFile", "POST", params);
                JSONObject objUploadfile = new JSONObject(respUploadFile);
                if (objUploadfile.has("error_code") && objUploadfile.getInt("error_code") == 0) {
                    HashMap paramAdd2User = new HashMap();
                    long fileId = objUploadfile.getJSONObject("data").getLong("fileId");
                    paramAdd2User.put("fileId", Long.toString(fileId));
                    paramAdd2User.put("parentId", Long.toString(parentId));
                    paramAdd2User.put("accountName", accountName);
                    paramAdd2User.put("appKey", appKey);
                    String respAddFile2Directory = HttpRequestUtils.sendHttpRequest(Configuration.url_directory + "/file/add", "POST", paramAdd2User);
                    System.out.println("respAddFile2Directory.accountName=" + accountName + "\t fileid=" + fileId + "\t resp=" + respAddFile2Directory);
                    JSONObject objAddFile = new JSONObject(respAddFile2Directory);
                    if(objAddFile.has("error_code") && objAddFile.getInt("error_code")!= 0){
                        return AsymmetricCryptography.getInstance().encryptTextData(respAddFile2Directory, Configuration.publicKeyAgent);
                    }
                }
                return AsymmetricCryptography.getInstance().encryptTextData(respUploadFile, Configuration.publicKeyAgent);

            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/file/finishUpload")
    public Route finishupload() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "finishupload.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || fileId <= 0 || accountName.isEmpty()) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("ownerName", accountName);
                params.put("fileId", Long.toString(fileId));
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                String result = HttpRequestUtils.sendHttpRequest(Configuration.url_file + "/finishUpload", "POST", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }

        };
    }

    @RouteInfo(method = "post", path = "/chunk/addChunk")
    public Route addChunk() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
//                Utils.printLogSystem(_className, "addChunk.data_req=" + data_req);
                if (keySecret == null || keySecret.isEmpty() || data_req == null) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                if (data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                int numberChunk = objParams.getInt("chunkNumber");
                String chunkData = objParams.getString("chunkData");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || chunkData == null
                        || fileId <= 0 || numberChunk <= 0) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("fileId", Long.toString(fileId));
                params.put("chunkNumber", Integer.toString(numberChunk));
                params.put("chunkData", chunkData);
                String result = HttpRequestUtils.sendHttpRequest(Configuration.url_chunk + "/addChunk", "POST", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/chunk/getChunk")
    public Route getChunk() {
        return (Request req, Response response) -> {
            try {
                response.header("Content-Type", "application/json");
                String keySecret = ServletUtil.getStringParameter(req, "k");
                String encryptedData = ServletUtil.getStringParameter(req, "data");
                String data_req = AsymmetricCryptography.getInstance().DecryptTextData(keySecret, encryptedData, Configuration.privateKeyDIS);
                Utils.printLogSystem(_className, "getChunk.data_req=" + data_req);
                if (data_req == null || data_req.isEmpty()) {
                    return DataResponse.DECRYPT_FAILED;
                }
                JSONObject objParams = new JSONObject(data_req);
                String appKey = objParams.getString("appKey");
                String agentKey = objParams.getString("agentKey");
                String accountName = objParams.getString("accountName");
                long fileId = objParams.getLong("fileId");
                int numberChunk = objParams.getInt("chunkNumber");
                if (appKey.isEmpty() || agentKey.isEmpty() || accountName == null || fileId <= 0 || numberChunk <= 0) {
                    return AsymmetricCryptography.getInstance().encryptTextData(DataResponse.PARAM_ERROR.toString(), Configuration.publicKeyAgent);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("accountName", accountName);
                params.put("appKey", appKey);
                params.put("agentKey", agentKey);
                params.put("fileId", Long.toString(fileId));
                params.put("chunkNumber", Integer.toString(numberChunk));
                String result = HttpRequestUtils.sendHttpRequest(Configuration.url_chunk + "/getChunk", "GET", params);
                return AsymmetricCryptography.getInstance().encryptTextData(result, Configuration.publicKeyAgent);
            } catch (Exception ex) {
                ex.printStackTrace();
                return AsymmetricCryptography.getInstance().encryptTextData(new DataResponse(401, ex.getMessage()).toString(), Configuration.publicKeyAgent);
            }
        };
    }
}
