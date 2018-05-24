/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.render;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.FileInfo;
import crdhn.dis.model.SessionInfo;
import crdhn.dis.model.UserInfo;
import crdhn.dis.transport.DISUserDBConnector;
import java.io.IOException;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.json.JSONObject;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Request;
import firo.Response;
import java.util.Map;

/**
 *
 * @author namdv
 */
public class RenderMain extends RenderEngine {

    private static final Logger log = Log.getLogger(RenderMain.class);
    private static RenderMain _instance = new RenderMain();

    public static RenderMain getInstance() {

        return _instance;
    }

    private String renderMenuTop(boolean isShowUser, String fullname, String username) throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            if (!isShowUser) {
                attributes.put("show_user", "hidden");
            } else {
                attributes.put("viewerFullName", fullname);
                attributes.put("viewerName", username);
            }
            content = RenderEngine.getInstance().render(attributes, "menu_top.ftl");
        } catch (Exception ex) {
            log.warn("render renderMenuTop", ex);
        }
        return content;
    }

    private String renderMenuLeft() {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            content = RenderEngine.getInstance().render(attributes, "menu_left.ftl");
        } catch (Exception ex) {
            log.warn("render renderMenuLeft", ex);
        }
        return content;
    }

    public String renderHome(long folderId, Request request, Response response) throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                attributes.put("MENU_TOP", renderMenuTop(true, sessionInfo.email, sessionInfo.email));
                attributes.put("MENU_LEFT", renderMenuLeft());
                attributes.put("static_url", Configuration.static_url);
                attributes.put("folderId", folderId + "");
                attributes.put("viewerId", sessionInfo.email);
                content = RenderEngine.getInstance().render(attributes, "home.ftl");
            } else {
                response.raw().sendRedirect("/login");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.warn("render renderHome", ex);
        }
        return content;
    }

    public String renderLogin(String message) throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            attributes.put("static_url", Configuration.static_url);
            if (!message.isEmpty()) {
                attributes.put("message", message);
            }
            content = RenderEngine.getInstance().render(attributes, "login.ftl");
        } catch (Exception ex) {
            log.warn("Exception renderLogin", ex);
        }
        return content;
    }

    public String renderRegister() throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            attributes.put("static_url", Configuration.static_url);
            attributes.put("MENU_TOP", renderMenuTop(false, "", ""));
            content = RenderEngine.getInstance().render(attributes, "register.ftl");
        } catch (Exception ex) {
            log.warn("Exception renderRegister", ex);
        }
        return content;
    }

    public String renderProfile(Request req, Response response) {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            String sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                UserInfo uInfo = DISUserDBConnector.getInstance().getUser(sessionInfo.email);
                attributes.put("MENU_TOP", renderMenuTop(true, uInfo.fullName, uInfo.email));
                    attributes.put("MENU_LEFT", renderMenuLeft());
                    attributes.put("static_url", Configuration.static_url);
                    attributes.put("fullname", uInfo.fullName);
                    attributes.put("email", uInfo.email);
                    if (uInfo.gender > 0) {
                        attributes.put("selected_female", "selected");
                    } else {
                        attributes.put("selected_male", "selected");
                    }
                    attributes.put("birthday", Utils.convertLongTimeToString(uInfo.birthday).split(" ")[0]);
                    attributes.put("numberPhone", uInfo.phoneNumber);
                    attributes.put("address", uInfo.address);
                    attributes.put("lastUpdateTime", Utils.convertLongTimeToString(uInfo.lastUpdateTime));
                    content = RenderEngine.getInstance().render(attributes, "profile.ftl");
            } else {
                response.raw().sendRedirect("/login");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            log.warn("renderProfile", ex);
        }
        return content;
    }


    public String renderFolderPublic(long folderId, Request request, Response response) throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                attributes.put("MENU_TOP", renderMenuTop(true, sessionInfo.email, sessionInfo.email));
                attributes.put("MENU_LEFT", renderMenuLeft());
                attributes.put("viewerId", sessionInfo.email);
            } else {
                attributes.put("MENU_TOP", renderMenuTop(false, "", ""));
                attributes.put("viewerId", "");
            }

            attributes.put("static_url", Configuration.static_url);
            attributes.put("folderId", folderId);

            content = RenderEngine.getInstance().render(attributes, "public_folder.ftl");

        } catch (Exception ex) {
            ex.printStackTrace();
            log.warn("render renderFolderPublic", ex);
        }
        return content;
    }

    public String renderDownloadFile(Request request, FileInfo fInfo, int fileStatus) {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                attributes.put("MENU_TOP", renderMenuTop(true, sessionInfo.email, sessionInfo.email));
                attributes.put("MENU_LEFT", renderMenuLeft());
                attributes.put("viewerId", sessionInfo.email);
            } else {
                attributes.put("MENU_TOP", renderMenuTop(false, "", ""));
                attributes.put("COLSE_MENU_LEFT", "margin-left:0px;");
                attributes.put("viewerId", "");
            }
            attributes.put("static_url", Configuration.static_url);
            attributes.put("FILENAME", fInfo.fileName);
            attributes.put("FILEID", fInfo.fileId);
            attributes.put("FILESIZE", Utils.formatFileSize(fInfo.fileSize, true));
            String status_message = "";
            String isShowDownload = "";
            if (fileStatus == 2) {
                status_message = "File available for you download, uploaded by <b>" + fInfo.owner + "</b>";
            } else {
                isShowDownload = "disabled";
                status_message = "File not uploaded successfully, uploading by <b>" + fInfo.owner + "</b>";
            }
            attributes.put("FileStatus", status_message);
            attributes.put("show_download", isShowDownload);
            content = RenderEngine.getInstance().render(attributes, "downloadFile.ftl");
        } catch (Exception ex) {
            log.warn("renderDownloadFile", ex);
        }
        return content;
    }

    public String renderNotFoundItem(Request request, String message) {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                attributes.put("MENU_TOP", renderMenuTop(true, sessionInfo.email, sessionInfo.email));
                attributes.put("MENU_LEFT", renderMenuLeft());
                attributes.put("viewerId", sessionInfo.email);
            } else {
                attributes.put("MENU_TOP", renderMenuTop(false, "", ""));
                attributes.put("viewerId", "");
            }
            attributes.put("static_url", Configuration.static_url);
            attributes.put("Error_message", message);
            content = RenderEngine.getInstance().render(attributes, "notfound_page.ftl");
        } catch (Exception ex) {
            log.warn("renderNotFoundItem", ex);
        }
        return content;
    }

    public String renderDownload(String username, Request req) throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            String message = "";
            attributes.put("MENU_TOP", renderMenuTop(true, username, username));
            attributes.put("static_url", Configuration.static_url);
            attributes.put("title", "Download");
            String fileName = ServletUtil.getStringParameter(req, "filename");
            attributes.put("filename", fileName);
            int fileId = ServletUtil.getIntParameter(req, "fileid", 0);
            if (fileId <= 0) {
                attributes.put("hidden", "hidden");
                message = "Enter parameters following";
            } else {
                attributes.put("fileid", Integer.toString(fileId));
                String destination = ServletUtil.getStringParameter(req, "destination");
                String destPath = ServletUtil.getStringParameter(req, "dest");
                if (destPath != null && !destPath.isEmpty()) {
                    destination = destPath;
                }
                if (destination.isEmpty()) {
                    destination = System.getProperty("user.home");
                }
                attributes.put("pathdownload", Configuration.domain
                        + "/download?fileid=" + fileId + "&filename=" + fileName + "&destination=" + destination);
                attributes.put("destination", destination);
                HashMap<String, String> params = new HashMap<>();
                params.put("method", "getFile");
                params.put("sessionKey", HttpRequestUtils.getCookieFiro(req, "sessionKey"));
                params.put("fileid", Integer.toString(fileId));
                params.put("filename", fileName);
                String resp_getfile = "";
                try {
                    resp_getfile = HttpRequestUtils.sendHttpRequest(Configuration.url_agent_server+"/download", "GET", params);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.printLogSystem(this.getClass().getName(), "renderDownload resp_getfile=" + resp_getfile);
                    JSONObject objResp = new JSONObject(resp_getfile);
                    if (objResp.getInt("error_code") == 0) {
                        JSONObject objData = objResp.getJSONObject("data");
                        int numberOfChunks = objData.getInt("numberOfChunks");
                        int numberUploadedChunks = objData.getInt("numberOfUploadedChunks");
                        int fileStatus = objData.getInt("fileStatus");
                        if (fileStatus == 2 || numberOfChunks == numberUploadedChunks) {
                            //file upload success
                            message = "File uploaded and ready to download!";
                        } else if (fileStatus == 1) {
                            message = "File uploading, please wait for complete!";
                        } else {
                            message = "file upload failed, please reupload!";
                        }
                    } else {
                        message = resp_getfile;
                    }
                }

            }
            String paramMessage = ServletUtil.getStringParameter(req, "message");
            if (!paramMessage.isEmpty()) {
                message += "<br><p style=\"color:red;\">" + paramMessage + "</p>";
            }
            attributes.put("message", message);
            content = RenderEngine.getInstance().render(attributes, "download.ftl");
        } catch (Exception ex) {
            log.warn("render renderDownload", ex);
            ex.printStackTrace();
        }
        return content;
    }

    public String renderDownloadResult(String username, Request req) throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
            attributes.put("MENU_TOP", renderMenuTop(true, username, username));
            attributes.put("static_url", Configuration.static_url);
            attributes.put("message_download", ServletUtil.getParameter(req, "message"));
            attributes.put("fileid", ServletUtil.getParameter(req, "fileid"));
            attributes.put("filename", ServletUtil.getParameter(req, "filename"));
            attributes.put("destination", ServletUtil.getParameter(req, "destination"));
            attributes.put("filesize", ServletUtil.getParameter(req, "filesize"));
            attributes.put("timedownload", ServletUtil.getParameter(req, "timedownload"));
            content = RenderEngine.getInstance().render(attributes, "download_result.ftl");
        } catch (Exception ex) {
            log.warn("render renderDownload", ex);
        }
        return content;
    }

}
