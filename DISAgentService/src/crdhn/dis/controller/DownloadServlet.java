/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crdhn.dis.download.DownloadFileQueue;
import crdhn.dis.download.DownloadFileWorker;
import crdhn.dis.manager.FolderManager;
import crdhn.dis.manager.FileManager;
import crdhn.dis.model.FileInfo;
import crdhn.dis.model.FolderDownloadInfo;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class DownloadServlet extends Controller {

    private static final String _className = "=============DownloadServlet";
    private static final Logger _logger = LoggerFactory.getLogger(DownloadServlet.class);

    public DownloadServlet() {
        rootPath = "/download";
    }

    /**
     * *
     *
     * @return @RouteInfo(method = "get,post", path = "/file") public Route
     * downloadFile() { return (Request request, Response response) -> {
     * response.header("Content-Type", "application/json"); if
     * (!Configuration.agentStatus) { return DataResponse.AGENT_UNAVAILABLE; }
     * Utils.printLogSystem(_className, " downloadFile"); String accountName =
     * ServletUtil.getStringParameter(request, "accountName"); long fileId =
     * ServletUtil.getLongParameter(request, "fileId"); String pathDest =
     * ServletUtil.getStringParameter(request, "path"); if (fileId <= 0) {
     * return DataResponse.PARAM_ERROR; } if
     * (!Utils.checkPermissionAccess(accountName, fileId,
     * Configuration.TYPE_FILE)) { return DataResponse.ACCESS_DENY; } if
     * (pathDest.isEmpty()) { return downloadFileHTTP(fileId, accountName,
     * Configuration.TYPE_FILE, request, response); } else { return
     * downloadFileWithPath(fileId, pathDest, Configuration.TYPE_FILE, request,
     * response); }
     *
     * }; }
     *
     * @RouteInfo(method = "get,post", path = "/folder") public Route
     * downloadFolder() { return (Request request, Response response) -> {
     * response.header("Content-Type", "application/json"); if
     * (!Configuration.agentStatus) { return DataResponse.AGENT_UNAVAILABLE; }
     * Utils.printLogSystem(_className, " downloadFolder"); String accountName =
     * ServletUtil.getStringParameter(request, "accountName"); long folderId =
     * ServletUtil.getLongParameter(request, "folderId"); String pathDest =
     * ServletUtil.getStringParameter(request, "path"); if (folderId <= 0) {
     * return DataResponse.PARAM_ERROR; } if
     * (!Utils.checkPermissionAccess(accountName, folderId,
     * Configuration.TYPE_FOLDER)) { return DataResponse.ACCESS_DENY; } if
     * (pathDest.isEmpty()) { return downloadFileHTTP(folderId, accountName,
     * Configuration.TYPE_FOLDER, request, response); } else { return
     * downloadFileWithPath(folderId, pathDest, Configuration.TYPE_FOLDER,
     * request, response); }
     *
     * }; }
     */
    @RouteInfo(method = "get,post", path = "/file")
    public Route downloadFileAPI() {
        return (Request request, Response response) -> {
            Utils.printLogSystem(_className, " downloadFileAPI");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(request, "appKey");
            String accountName = ServletUtil.getStringParameter(request, "accountName");
            long fileId = ServletUtil.getLongParameter(request, "fileId");
            String pathDest = ServletUtil.getStringParameter(request, "path");
            if (fileId <= 0) {
                return DataResponse.PARAM_ERROR;
            }
//            if (!FileManager.checkPermissionFile(appKey, accountName, fileId)) {
//                return DataResponse.ACCESS_DENY;
//            }
            DataResponse result;
            if (pathDest.isEmpty()) {
                result = processDownloadFileHTTP(appKey, fileId, accountName, request, response);
            } else {
                result = processFileWithPath(appKey, fileId, accountName, pathDest, request, response);
            }
            if (result.getError() == 0) {
                String shaFile = "";
                String fileName = "";
                while (true) {
                    FileInfo fInfo = FileManager.getFileDownloading(fileId);
                    if (fInfo != null) {
                        if (fInfo.numberChunkSucess == fInfo.numberOfChunks && fInfo.numberChunkSucess > 0) {
                            shaFile = fInfo.checksumSHA2;
                            fileName = fInfo.fileName;
                            break;
                        }
                    }
                    try {
                        Thread.sleep(100l);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                String pathFile = Configuration.path_folder_store + File.separator + shaFile + ".content";
                returnFileToBrowser(fileName, pathFile, request, response);
                return response;
            }
            return result;
        };
    }

    @RouteInfo(method = "get,post", path = "/folder")
    public Route downloadFolderAPI() {
        return (Request request, Response response) -> {
            Utils.printLogSystem(_className, " downloadFolderAPI");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(request, "appKey");
            String accountName = ServletUtil.getStringParameter(request, "accountName");
            long folderId = ServletUtil.getLongParameter(request, "folderId");
            String pathDest = ServletUtil.getStringParameter(request, "path");
            if (folderId <= 0) {
                return DataResponse.PARAM_ERROR;
            }
//            if (!FolderManager.checkPermissionFolder(appKey, accountName, folderId)) {
//                return DataResponse.ACCESS_DENY;
//            }
            DataResponse result;
            if (pathDest.isEmpty()) {
                result = processDownloadFolderHTTP(appKey, folderId, accountName, request, response);
            } else {
                result = processDownloadFolderWithPath(appKey, folderId, accountName, pathDest, request, response);
            }
            Utils.printLogSystem(_className, " downloadFolderAPI.result============="+result);
            if (result.getError() == 0) {
                String shaFolder = "";
                String folderName = "";
                while (true) {
                    FolderDownloadInfo fInfo = FolderManager.checkProcessFiles(folderId);
                    if (fInfo!=null && fInfo.isReadyDownload) {
                        folderName = fInfo.folderName;
                        shaFolder = fInfo.sha;
                        break;
                    }
                    try {
                        Thread.sleep(100l);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                String pathFolder = Configuration.path_folder_store + "/" + shaFolder + ".zip";
                returnFileToBrowser(folderName, pathFolder, request, response);
                System.out.println("DownloadFolder success!");
                return response;
            }
            return result;
        };
    }

    @RouteInfo(method = "get,post", path = "/file/:key")
    public Route downloadFileFromAgent() {
        return (Request request, Response response) -> {

            Utils.printLogSystem(_className, " downloadFileFromAgent");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(request, "appKey");
            long fileId = ServletUtil.getIntParameter(request, "fileId");
            String accountName = ServletUtil.getStringParameter(request, "accountName");
            if (!FileManager.checkPermissionFile(appKey, accountName, fileId)) {
                return DataResponse.ACCESS_DENY;
            }
            String shaFile = String.valueOf(request.params(":key"));
            String pathFile = Configuration.path_folder_store + File.separator + shaFile + ".content";
            returnFileToBrowser("", pathFile, request, response);
            return response;

        };
    }

    @RouteInfo(method = "get,post", path = "/folder/:key")
    public Route downloadFolderFromAgent() {
        return (Request request, Response response) -> {

            Utils.printLogSystem(_className, " downloadFileFromAgent");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            String appKey = ServletUtil.getStringParameter(request, "appKey");
            long folderId = ServletUtil.getIntParameter(request, "folderId");
            String accountName = ServletUtil.getStringParameter(request, "accountName");
            if (!FolderManager.checkPermissionFolder(appKey, accountName, folderId)) {
                return DataResponse.ACCESS_DENY;
            }
            String shaFolder = String.valueOf(request.params(":key"));
            String pathFolder = Configuration.path_folder_store + "/" + shaFolder + ".zip";
            returnFileToBrowser("", pathFolder, request, response);
            return response;

        };
    }

    public static void returnFileToBrowser(String fileName, String path, Request request, Response response) throws IOException {
//        String pathFile = Configuration.path_folder_store + File.separator + shaFile + ".content";
//        if ("folder".equals(itemType)) {
//            pathFile = Configuration.path_folder_store + "/" + shaFile + ".zip";
//        }
        File file = new File(path);
        boolean checkExistPath = Utils.checkFileExisted(path);
        if (checkExistPath) {
            String itemName = ServletUtil.getStringParameter(request, "fileName");
            if (itemName.isEmpty()) {
                itemName = fileName;
            }
            int bufferSize = Math.max(102400, Long.valueOf(file.length()).intValue());

            response.raw().setBufferSize(bufferSize);
            response.header("Content-Type", "application/octet-stream");
            response.header("Content-Length", String.valueOf(file.length()));
            response.header("Content-Disposition", String.format("attachment; filename=\"%s\"", itemName));
            ServletOutputStream outStream;
            try (InputStream inStream = new FileInputStream(file)) {
                outStream = response.raw().getOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead = -1;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();

        } else {
            System.out.println("returnFileToBrowser item not found");
        }
    }

    protected DataResponse processDownloadFolderWithPath(String appKey, long folderId, String accountName, String pathDest, Request req, Response resp) {
//        String folderName = ServletUtil.getStringParameter(req, "folderName");
        boolean checkExistPath = Utils.checkFileExisted(pathDest);
        if (!checkExistPath) {
            return new DataResponse(-1, "Can not access path destination!");
        }
        if (folderId > 0) {
            try {
                //is folder
                JSONObject params = new JSONObject();
                params.put("appKey", appKey);
                
                params.put("accountName", accountName);
                params.put("folderId", Long.toString(folderId));
//                    params.put("sessionKey", sessionKey);
                String url_submit = Configuration.url_proxy + "/folder/get/public";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                Utils.printLogSystem(_className, "downloadFolder.getFolderInfo folderId=" + folderId + "\t  resp=" + result);
                JSONObject objRespFolder = new JSONObject(result);
                if (objRespFolder.has("error_code") && objRespFolder.getInt("error_code") == 0) {
                    JSONObject objData = objRespFolder.getJSONObject("data");
                    String parentFolderName = objData.getString("folderName");
                    String pathFolder = Utils.createFolderOverwrite(Configuration.path_folder_store + "/" + objData.getLong("folderId") + "/" + parentFolderName);
                    List<Long> fileIds = new ArrayList<>();
                    JSONArray fileInfos = objData.getJSONArray("files");
                    for (int i = 0; i < fileInfos.length(); i++) {

                        JSONObject objFile = fileInfos.getJSONObject(i);
                        FileInfo fInfo = new FileInfo();
                        fInfo.assignFrom(objFile);
                        fileIds.add(fInfo.fileId);
                        fInfo.setFilePath(pathFolder);
                        int fileStatus = objFile.getInt("fileStatus");
                        if (fileStatus == 2) {
                            DownloadFileWorker.processDownloadFile(fInfo);
                        }
                    }
                    JSONArray arrSubfolder = objData.getJSONArray("subFolders");
                    this.preDownloadFolder(appKey,accountName,  arrSubfolder, pathFolder, fileIds);

                    FolderDownloadInfo fdInfo = new FolderDownloadInfo(folderId, parentFolderName);
                    fdInfo.setFileIds(fileIds);
                    fdInfo.setPath(pathDest);
                    fdInfo.setNumberFiles(fileIds.size());
                    FolderManager.putFolderDownload(fdInfo);
                    return DataResponse.SUCCESS;
                } else {
                    return new DataResponse(objRespFolder.getInt("error_code"), objRespFolder.getString("error_message"));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
                return DataResponse.UNKNOWN_EXCEPTION;
            }

        } else {
            return DataResponse.PARAM_ERROR;
        }

    }

    protected DataResponse processFileWithPath(String appKey, long fileId, String accountName, String pathDest, Request req, Response resp) {
//        String fileName = ServletUtil.getStringParameter(req, "fileName");
        boolean checkExistPath = Utils.checkFileExisted(pathDest);
        if (!checkExistPath) {
            return new DataResponse(-1, "Can not access path destination!");
        }
        if (fileId > 0) {
            String path = pathDest;
            FileInfo fInfo = new FileInfo("file_"+fileId);
            fInfo.setFileId(fileId);
            fInfo.setFilePath(path);
            fInfo.setAppKey(appKey);
            fInfo.setOwnerName(accountName);
            fInfo.setFileStatus(2);
            DownloadFileQueue.put(fInfo);
            FileManager.putDownloadFileInfo(fInfo);
            return DataResponse.SUCCESS;

        } else {
            return DataResponse.PARAM_ERROR;
        }

    }

    private void preDownloadFolder(String appKey,String accountName,  JSONArray listFolder, String parentPath, List<Long> listFileId) {
        try {
            System.out.println("downloadFolder data=" + listFolder);
            for (int i = 0; i < listFolder.length(); i++) {
                JSONObject objFolder = listFolder.getJSONObject(i);
                if (objFolder.has("folderId")) {
                    String folderName = objFolder.getString("folderName");
                    String pathFolder = Utils.createFolderOverwrite(parentPath + "/" + folderName);

                    JSONArray fileIds = objFolder.getJSONArray("fileIds");
                    if (fileIds.length() > 0) {
                        for (int j = 0; j < fileIds.length(); j++) {
                            long fileId = fileIds.getLong(j);
                            listFileId.add(fileId);
                            FileInfo fInfo = new FileInfo("fileId_" + fileId);
                            fInfo.setFileId(fileId);
                            fInfo.setFilePath(pathFolder);
                            fInfo.setAppKey(appKey);
                            fInfo.setOwnerName(accountName);
                            DownloadFileQueue.put(fInfo);
                        }
                    }

                    JSONArray subFolderIds = objFolder.getJSONArray("subFolderIds");
                    if (subFolderIds.length() > 0) {
                        JSONObject paramsSubFolder = new JSONObject();
                        paramsSubFolder.put("folderIds", Utils.convertJSONArrayToString(subFolderIds));
                        paramsSubFolder.put("appKey", appKey);
                        paramsSubFolder.put("accountName",accountName);
                        paramsSubFolder.put("agentKey", Configuration.agentKey);
                        String url_getfolders = Configuration.url_proxy + File.separator + "folder/gets";
                        String result_getfolders = HttpRequestUtils.sendHttpRequest(url_getfolders, "GET", paramsSubFolder);
                        Utils.printLogSystem(_className, "downloadFolder.getFolderInfos resp=" + result_getfolders);
                        JSONObject objRespListFolder = new JSONObject(result_getfolders);
                        if (objRespListFolder.has("error_code") && objRespListFolder.getInt("error_code") == 0) {
                            JSONArray arrFolders = objRespListFolder.getJSONArray("data");
                            if (arrFolders.length() > 0) {
                                this.preDownloadFolder(appKey, accountName, arrFolders, pathFolder, listFileId);
                            }
                        }
                    }

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DataResponse processDownloadFileHTTP(String appKey, long fileId, String accountName, Request req, Response resp) throws IOException {
        String fileName = ServletUtil.getStringParameter(req, "fileName");
        if (fileId > 0) {
            FileInfo fInfo = new FileInfo(fileName);
            fInfo.setFileId(fileId);
            fInfo.ownerName = accountName;
            fInfo.appKey = appKey;
            fInfo.setFileStatus(2);
            DownloadFileQueue.put(fInfo);
            FileManager.putDownloadFileInfo(fInfo);
            return DataResponse.SUCCESS;
        } else {
            return DataResponse.PARAM_ERROR;
        }
    }

    private DataResponse processDownloadFolderHTTP(String appKey, long folderId, String accountName, Request req, Response resp) throws IOException {
        String folderName = ServletUtil.getStringParameter(req, "folderName");
        if (folderId > 0) {
            try {
                //is folder
                JSONObject params = new JSONObject();
                params.put("appKey", appKey);
                
                params.put("accountName", accountName);
                params.put("folderId", Long.toString(folderId));
                String url_submit = Configuration.url_proxy + "/folder/get/public";
                String result = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
                Utils.printLogSystem(_className, "downloadFolder.getFolderInfo folderId=" + folderId + "\t  resp=" + result);
                JSONObject objRespFolder = new JSONObject(result);
                if (objRespFolder.has("error_code") && objRespFolder.getInt("error_code") == 0) {
                    JSONObject objData = objRespFolder.getJSONObject("data");
                    String parentFolderName = objData.getString("folderName");
                    String pathFolder = Utils.createFolderOverwrite(Configuration.path_folder_store + "/" + folderId + "/" + parentFolderName);
                    List<Long> fileIds = new ArrayList<>();
                    JSONArray fileInfos = objData.getJSONArray("files");
                    for (int i = 0; i < fileInfos.length(); i++) {

                        JSONObject objFile = fileInfos.getJSONObject(i);
                        FileInfo fInfo = new FileInfo();
                        fInfo.assignFrom(objFile);
                        fileIds.add(fInfo.fileId);
                        fInfo.setFilePath(pathFolder);
                        int fileStatus = objFile.getInt("fileStatus");
                        if (fileStatus == 2) {
                            DownloadFileWorker.processDownloadFile(fInfo);
                        }
                    }
                    JSONArray arrSubfolder = objData.getJSONArray("subFolders");
                    this.preDownloadFolder(appKey, accountName,  arrSubfolder, pathFolder, fileIds);
                    if(folderName.isEmpty()){
                        folderName = parentFolderName;
                    }
                    FolderDownloadInfo fdInfo = new FolderDownloadInfo(folderId, folderName);
                    fdInfo.setFileIds(fileIds);
                    fdInfo.setNumberFiles(fileIds.size());
                    FolderManager.putFolderDownload(fdInfo);
                    return DataResponse.SUCCESS;
                } else {
                    return new DataResponse(objRespFolder.getInt("error_code"), objRespFolder.getString("error_message"));
                }

            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
                java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        } else {
            return DataResponse.PARAM_ERROR;
        }
    }

}
