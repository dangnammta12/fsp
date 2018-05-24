/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import javax.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crdhn.dis.model.FileInfo;
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

    @RouteInfo(method = "get,post", path = "/browser")
    public Route uploadBrowser() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            long maxFileSize = 100000000000l;  //10GB the maximum size allowed for uploaded files
            long maxRequestSize = 100000000000l;  // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;  // the size threshold after which files will be written to disk
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(Configuration.path_folder_store, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            Utils.printLogSystem(_className, " uploadBrowser");
//            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
//            if (sessionKey.isEmpty()) {
//                sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
//            }
            String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                return uploadFileBrowser(sessionInfo.email, request, response);
            } else {
                response.raw().sendRedirect("/login");
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/path")
    public Route uploadFromPath() {
        return (Request request, Response response) -> {
            response.header("Content-Type", "application/json");
            Utils.printLogSystem(_className, " uploadFromPath");
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            if (sessionKey.isEmpty()) {
                sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            }
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                return uploadFilePath(sessionInfo.email, request, response);
            } else {
                response.raw().sendRedirect("/login");
                response.header("Content-Type", "application/json");
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    private String uploadFileBrowser(String accountName, Request req, Response resp) {
        try {
            long startTime = System.currentTimeMillis();
            Part filePart = req.raw().getPart("file");
            long parentId = ServletUtil.getLongParameter(req, "parentId_browser");
            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.isEmpty() || parentId <= 0) {
                System.out.println("uploadFileBrowser.accountName=" + accountName + "\t fileName=" + fileName + "\t parentId=" + parentId);
                return DataResponse.PARAM_ERROR.toString();
            }
            String pathFileTmp = Configuration.path_folder_upload + "/" + fileName;
            FileOutputStream fout;
            long fileSize;
            String hashSHA1;
            String hashMD5;
            try (InputStream filecontent = filePart.getInputStream()) {
                fout = new FileOutputStream(pathFileTmp);
                int len = -1;
                byte[] buffer = new byte[8192];
                filePart.delete();
                fileSize = 0;
                MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                MessageDigest digest_md5 = MessageDigest.getInstance("MD5");
                hashSHA1 = "";
                hashMD5 = "";
                while ((len = filecontent.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                    fileSize += len;
                    hashSHA1 = Utils.toHex(buffer, digest256);
                    hashMD5 = Utils.toHex(buffer, digest_md5);
                }
                buffer = null;
            }
            filePart.delete();
            fout.close();
            long endTime = System.currentTimeMillis();
            Utils.printLogSystem(_className, "********.FileName(http)=" + fileName + "\t timeProcess=" + (endTime - startTime) + "\t fileSize=" + fileSize);
//            DataResponse result = HttpRequestUtils.upload(accountName, pathFileTmp, fileName, parentId);
            String result = HttpRequestUtils.uploadWithPath(hashSHA1, hashMD5, fileSize, accountName, pathFileTmp, fileName, parentId);
            System.out.println("uploadFileBrowser.result=" + result);
//            JSONObject objResult = new JSONObject(result);
//            if (objResult.getInt("error_code") == DataResponse.SUCCESS.getError()) {
//                System.out.println("uploadFileBrowser.send redirect="+ "/folder/" + parentId);
//                resp.raw().sendRedirect("/folder/" + parentId);
//            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new DataResponse(-1, ex.getMessage()).toString();
        }
    }

    private String uploadFilePath(String accountName, Request req, Response resp) throws IOException {
        long timeStart = System.currentTimeMillis();
        try {
            String filePath = ServletUtil.getParameter(req, "path");
            long parentId = ServletUtil.getLongParameter(req, "parentId_path");
            if (filePath.isEmpty() || parentId <= 0) {
                System.out.println("uploadFilePath.accountName=" + accountName + "\t filePath=" + filePath + "\t parentId=" + parentId);
                return DataResponse.PARAM_ERROR.toString();
            } else {
                String[] arrPath = filePath.split("/");
                String fileName = arrPath[arrPath.length - 1];
                FileInfo fInfo = new FileInfo(fileName);
                fInfo.setParentId(parentId);
                String pathFileTmp = Configuration.path_folder_store + "/" + fileName;
//                fInfo.filePath = filePath;
                fInfo.startTime = System.nanoTime();
                long fileSize;
                String hashSHA1;
                String hashMD5;
                try (BufferedInputStream f = new BufferedInputStream(
                        new FileInputStream(filePath))) {
                    fileSize = 0;
                    MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                    MessageDigest digest_md5 = MessageDigest.getInstance("MD5");
                    hashSHA1 = "";
                    hashMD5 = "";
                    try (FileOutputStream fout = new FileOutputStream(pathFileTmp)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = f.read(buffer)) != -1) {
                            fout.write(buffer, 0, len);
                            fileSize += len;
                            hashSHA1 = Utils.toHex(buffer, digest256);
                            hashMD5 = Utils.toHex(buffer, digest_md5);
                        }
                    }
                }
//                DataResponse result = HttpRequestUtils.upload(accountName, pathFileTmp, fileName, parentId);
                String result = HttpRequestUtils.uploadWithPath(hashSHA1, hashMD5, fileSize, accountName, pathFileTmp, fileName, parentId);
                System.out.println("uploadFilePath.result=" + result);
                long endTime = System.currentTimeMillis();
                Utils.printLogSystem(_className, "File Upload Success \t Time Upload===" + (endTime - timeStart));
//                JSONObject objResult = new JSONObject(result);
//                if (objResult.has("error_code") && objResult.getInt("error_code") == DataResponse.SUCCESS.getError()) {
//                    resp.raw().sendRedirect("/folder/" + parentId);
//                }
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new DataResponse(-1, ex.getMessage()).toString();
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
