/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crdhn.dis.manager.FileManager;
import crdhn.dis.manager.LocalStorage;
import crdhn.dis.model.FileInfo;
import crdhn.dis.render.RenderMain;
import crdhn.dis.upload.UploadFileQueue;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import javax.servlet.MultipartConfigElement;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class UploadServlet extends Controller {

    private static final String _className = "=============UploadServlet";
    private static final Logger _logger = LoggerFactory.getLogger(UploadServlet.class);

    public UploadServlet() {
        rootPath = "/upload";
    }

    @RouteInfo(method = "get", path = "/test")
    public Route testUpload() {
        return (Request request, Response response) -> {
            return RenderMain.getInstance().renderUploadTest();
        };
    }

    @RouteInfo(method = "get,post", path = "/browser")
    public Route uploadBrowser() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            long maxFileSize = 100000000000l;  //10GB the maximum size allowed for uploaded files
            long maxRequestSize = 100000000000l;  // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;  // the size threshold after which files will be written to disk
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(Configuration.path_folder_store, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            Utils.printLogSystem(_className, " uploadBrowser");
            String appKey = ServletUtil.getStringParameter(request, "appKey");
            String accountName = ServletUtil.getStringParameter(request, "accountName");
            if (appKey.isEmpty() || accountName.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            }
            return uploadFileBrowser(appKey, accountName, request, response);
        };
    }

    @RouteInfo(method = "get,post", path = "/path")
    public Route uploadFromPath() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            if (!Configuration.agentStatus) {
                return DataResponse.AGENT_UNAVAILABLE;
            }
            Utils.printLogSystem(_className, " ==============uploadFromPath");
            String appKey = ServletUtil.getStringParameter(request, "appKey");
            String accountName = ServletUtil.getStringParameter(request, "accountName");
            if (appKey.isEmpty() || accountName.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            }
            return uploadFilePath(appKey, accountName, request, response);
        };
    }

    private DataResponse uploadFileBrowser(String appKey, String accountName, Request req, Response resp) {
        try {
            long startTime = System.currentTimeMillis();
            Part filePart = req.raw().getPart("file");
            long parentId = ServletUtil.getLongParameter(req, "parentId");
            String fileName = filePart.getSubmittedFileName();
            if (fileName.isEmpty()) {
                return new DataResponse(-1, "File not found");
            }
            FileInfo fInfo = new FileInfo(fileName);
            fInfo.startTime = System.nanoTime();
            fInfo.setParentId(parentId);
            LocalStorage.writeFileUploadBrowser(fInfo, filePart);
            long endTime = System.currentTimeMillis();
            Utils.printLogSystem(_className, "********.FileName(http)=" + fileName + "\t timeProcess=" + (endTime - startTime) + "\t fileSize=" + fInfo.fileSize);
            fInfo.ownerName = accountName;
            fInfo.appKey = appKey;
            UploadFileQueue.put(fInfo);
            JSONObject objResult = new JSONObject();
            while (true) {
                long fileId = FileManager.getFileId(fInfo.fileName + "_" + fInfo.startTime);
                if (fileId > 0) {
                    objResult.put("fileId", fileId);
                    break;
                } else if (fileId == -1) {
                    return new DataResponse(-1, "Cannot addfile to server, please check your connection");
                } else {
                    try {
                        Thread.sleep(100l);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return new DataResponse(objResult, DataResponse.DataType.JSON_STR, false);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
            return new DataResponse(-1, ex.getMessage());
        }
    }

    private DataResponse uploadFilePath(String appKey, String accountName, Request req, Response resp) throws IOException {
        long timeStart = System.currentTimeMillis();
        try {
            String filePath = ServletUtil.getParameter(req, "path");
            long parentId = ServletUtil.getLongParameter(req, "parentId");
            if (filePath.isEmpty() || !Utils.checkFileExisted(filePath) || parentId <= 0) {
                Utils.printLogSystem(_className, "uploadFilePath. paramError===filePath=" + filePath + "\t parentId=" + parentId);
                return DataResponse.PARAM_ERROR;
            } else {
                String[] arrPath = filePath.split("/");
                String fileName = arrPath[arrPath.length - 1];
                System.out.println("************filePath=" + filePath);
                System.out.println("************arrPath.length=" + arrPath.length);
                System.out.println("************fileName=" + fileName);
                if (fileName == null || fileName.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                FileInfo fInfo = new FileInfo(fileName);
                fInfo.setParentId(parentId);
//                long fileSize = ServletUtil.getLongParameter(req, "fileSize");
//                String hashSHA1 = ServletUtil.getParameter(req, "hashSHA1");
//                String hashMD5 = ServletUtil.getParameter(req, "hashMD5");
//                if (hashMD5.isEmpty() || hashMD5.isEmpty() || fileSize <= 0) {
                LocalStorage.writeFileUploadPath(fInfo, filePath);
//                } else {
//                    fInfo.setChecksumMD5(hashMD5);
//                    fInfo.setChecksumSHA2(hashSHA1);
//                    fInfo.setFileSize(fileSize);
//                    fInfo.filePath = filePath;
//                    fInfo.startTime = System.nanoTime();
//                }
//                String pathFileTmp = Configuration.path_folder_store + "/" + fileName;
//                fInfo.startTime = System.nanoTime();
//                long fileSize;
//                String hashSHA1;
//                String hashMD5;
//                try (BufferedInputStream f = new BufferedInputStream(
//                        new FileInputStream(filePath))) {
//                    fileSize = 0;
//                    MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
//                    MessageDigest digest_md5 = MessageDigest.getInstance("MD5");
//                    hashSHA1 = "";
//                    hashMD5 = "";
//                    try (FileOutputStream fout = new FileOutputStream(pathFileTmp)) {
//                        byte[] buffer = new byte[8192];
//                        int len;
//                        while ((len = f.read(buffer)) != -1) {
//                            fout.write(buffer, 0, len);
//                            fileSize += len;
//                            hashSHA1 = Utils.toHex(buffer, digest256);
//                            hashMD5 = Utils.toHex(buffer, digest_md5);
//                        }
//                    }
//                }
//                fInfo.filePath = pathFileTmp;
//                fInfo.fileSize = fileSize;
//                fInfo.checksumSHA2 = hashSHA1;
//                fInfo.checksumMD5 = hashMD5;
                fInfo.ownerName = accountName;
                fInfo.appKey = appKey;

                UploadFileQueue.put(fInfo);
                JSONObject objResult = new JSONObject();
                while (true) {
                    long fileId = FileManager.getFileId(fileName + "_" + fInfo.startTime);
                    if (fileId > 0) {
                        objResult.put("fileId", fileId);
                        break;
                    } else if (fileId == -1) {
                        return new DataResponse(-1, "Cannot addfile to server, please check your connection");
                    } else {
                        try {
                            Thread.sleep(100l);
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(DownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                Utils.printLogSystem(_className, "Time Upload===" + (endTime - timeStart));
//                if (content.isEmpty()) {
//                    content = "File Upload Success!";
//                }
//                resp.raw().sendRedirect(Configuration.path_home + "/" + parentId);
                return new DataResponse(objResult, DataResponse.DataType.JSON_STR, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new DataResponse(-1, "upload exception:" + ex.getMessage());
        }
    }

    private String getFileName(Part part) {
        String partHeader = part.getHeader("content-disposition");
        _logger.info("Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
