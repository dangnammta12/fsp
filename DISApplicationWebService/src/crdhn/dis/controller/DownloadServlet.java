/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crdhn.dis.model.SessionInfo;
import crdhn.dis.transport.DISUserDBConnector;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.servlet.ServletOutputStream;

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

    @RouteInfo(method = "get,post", path = "/file")
    public Route downloadFile() {
        return (Request request, Response response) -> {
//            response.header("Content-Type", "application/json");
            Utils.printLogSystem(_className, " downloadFile");
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            if (sessionKey.isEmpty()) {
                sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            }
            long fileId = ServletUtil.getLongParameter(request, "itemid");
            String pathDest = ServletUtil.getStringParameter(request, "path");
            String itemName = ServletUtil.getStringParameter(request, "itemname");
            if (fileId <= 0) {
                return DataResponse.PARAM_ERROR;
            }
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null && sessionInfo.email != null) {
                DataResponse result;
                if (pathDest.isEmpty()) {
                    result = downloadFileHTTP(fileId, itemName, sessionInfo.email, Configuration.TYPE_FILE);
                } else {
                    result = downloadFileWithPath(sessionInfo.email, fileId, itemName, pathDest, Configuration.TYPE_FILE);
                }
                return result;
//                if (result != null && result.getError() == 0) {
//                    returnFileToBrowser(itemName, result.getData().toString(), request, response);
//                    return response;
//                } else {
//                    return result;
//                }
            } else {
                return DataResponse.SESSION_EXPIRED;
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/folder")
    public Route downloadFolder() {
        return (Request request, Response response) -> {
//            response.header("Content-Type", "application/json");
            Utils.printLogSystem(_className, " downloadFolder");
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            if (sessionKey.isEmpty()) {
                sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            }
            long folderId = ServletUtil.getLongParameter(request, "itemid");
            String pathDest = ServletUtil.getStringParameter(request, "path");
            String itemName = ServletUtil.getStringParameter(request, "itemname");
            if (folderId <= 0) {
                return DataResponse.PARAM_ERROR;
            }
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null && sessionInfo.email != null) {
                DataResponse result;
                if (pathDest.isEmpty()) {
                    result = downloadFileHTTP(folderId, itemName, sessionInfo.email, Configuration.TYPE_FOLDER);
                } else {
                    result = downloadFileWithPath(sessionInfo.email, folderId, itemName, pathDest, Configuration.TYPE_FOLDER);
                }
//                return response;
                return result;
//                if (result != null && result.getError() == 0) {
//                    returnFileToBrowser(itemName, result.getData().toString(), request, response);
//                    return response;
//                } else {
//                    return result;
//                }
            } else {
                return DataResponse.SESSION_EXPIRED;
            }

        };
    }

    @RouteInfo(method = "get,post", path = "/folder/:folderId")
    public Route downloadFolderFromApp() {
        return (Request request, Response response) -> {
            Utils.printLogSystem(_className, " downloadFolderFromApp");
            long folderId = Long.valueOf(request.params(":folderId"));
            String itemName = ServletUtil.getStringParameter(request, "itemname");
            String pathFolder = Configuration.path_folder_store + "/" + folderId;
            boolean isDownload = returnFileToBrowser(itemName, pathFolder, request, response);
            if (isDownload) {
                return response;
            } else {
                response.redirect("/folder/" + folderId);
                return isDownload;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/file/:fileId")
    public Route downloadFileFromApp() {
        return (Request request, Response response) -> {
            Utils.printLogSystem(_className, " downloadFileFromApp");
            long fileId = Long.valueOf(request.params(":fileId"));
            String itemName = ServletUtil.getStringParameter(request, "itemname");
            String pathFolder = Configuration.path_file_store + "/" + fileId;
            boolean isDownload = returnFileToBrowser(itemName, pathFolder, request, response);
            if (isDownload) {
                return response;
            } else {
                response.redirect("/file/" + fileId);
                return isDownload;
            }
        };
    }

    public boolean returnFileToBrowser(String itemName, String pathFile, Request request, Response response) throws IOException {
        File file = new File(pathFile);
        boolean checkExistPath = Utils.checkFileExisted(pathFile);
        if (checkExistPath) {
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
            System.out.println("returnFileToBrowser success!");
            return true;
        } else {
            System.out.println("returnFileToBrowser item not found");
            return false;
        }
    }

    protected DataResponse downloadFileWithPath(String accountName, long itemId, String itemName, String pathDest, int itemType) {
        String url_download;
        HashMap<String, String> params = new HashMap();
        String saveDir;
        if (itemType == Configuration.TYPE_FILE) {
            url_download = "/download/file";
            params.put("fileId", Long.toString(itemId));
            saveDir = Configuration.path_file_store;
        } else {
            url_download = "/download/folder";
            params.put("folderId", Long.toString(itemId));
            saveDir = Configuration.path_folder_store;
        }
        params.put("path", pathDest);
        params.put("accountName", accountName);
        return HttpRequestUtils.downloadFile(Configuration.url_agent_server + url_download, itemName, itemId, "GET", saveDir, params);

    }

    public DataResponse downloadFileHTTP(long itemId, String itemName, String viewerName, int itemType) throws IOException {
        String url_download;
        HashMap<String, String> params = new HashMap();
        String saveDir;
        if (itemType == Configuration.TYPE_FILE) {
            url_download = "/download/file";
            params.put("fileId", Long.toString(itemId));
            saveDir = Configuration.path_file_store;
        } else {
            url_download = "/download/folder";
            params.put("folderId", Long.toString(itemId));
            saveDir = Configuration.path_folder_store;
        }
        params.put("accountName", viewerName);
        return HttpRequestUtils.downloadFile(Configuration.url_agent_server + url_download, itemName, itemId, "GET", saveDir, params);
    }
}
