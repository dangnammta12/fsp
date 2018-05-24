package crdhn.dis.controller;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.SessionInfo;
import crdhn.dis.model.UserInfo;
import crdhn.dis.render.RenderMain;
import crdhn.dis.transport.DISUserDBConnector;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.util.Calendar;
import java.util.logging.Level;
import org.json.JSONObject;

public class UserController extends Controller {

    public static final String _className = "=============UserController";

    public UserController() {
    }

    @RouteInfo(method = "get", path = "/login")
    public Route renderLogin() {
        return (Request request, Response response) -> {
            return RenderMain.getInstance().renderLogin("");
        };
    }

    @RouteInfo(method = "post", path = "/login")
    public Route Login() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String email = ServletUtil.getStringParameter(req, "email");
            String password = ServletUtil.getStringParameter(req, "password");
            if (email.isEmpty() || password.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            } else {
                try {
                    DataResponse result = DISUserDBConnector.getInstance().login(email, password);
                    if (result.getError() == 0) {
                        JSONObject objData = new JSONObject(result.getData().toString());
                        String sessionKey = objData.getString("sessionKey");
                        int expireTime = Long.valueOf(objData.getLong("expireTime")).intValue();
                        HttpRequestUtils.setCookieFiro("sessionKey", sessionKey, Configuration.cookie_domain, expireTime, response);
                    }
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(DirectoryController.class.getName()).log(Level.SEVERE, null, ex);
                    return DataResponse.UNKNOWN_EXCEPTION;
                }
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/logout")
    public Route Logout() {
        return (Request request, Response response) -> {
            String sessionKey = HttpRequestUtils.getCookieFiro(request, "sessionKey");
            DataResponse respLogout = DISUserDBConnector.getInstance().logout(sessionKey);
            HttpRequestUtils.deleteCookie(request.raw(), response.raw(), "sessionKey");
            response.raw().sendRedirect("/login");
            return respLogout;
        };
    }

    @RouteInfo(method = "get", path = "/register")
    public Route renderRegister() {
        return (Request request, Response response) -> {
            return RenderMain.getInstance().renderRegister();
        };
    }

    @RouteInfo(method = "post", path = "/register")
    public Route register() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            try {
                String password = ServletUtil.getStringParameter(req, "password");
                String fullname = ServletUtil.getStringParameter(req, "fullname");
                String email = ServletUtil.getStringParameter(req, "email");
                String address = ServletUtil.getStringParameter(req, "address");
                int gender = ServletUtil.getIntParameter(req, "gender");
                String phone = ServletUtil.getStringParameter(req, "phone");
                String birthday = ServletUtil.getStringParameter(req, "birthday");
                String department = ServletUtil.getStringParameter(req, "department");
                if (email.isEmpty() || password.isEmpty()) {
                    return DataResponse.MISSING_PARAM;
                } else {
                    UserInfo uInfo = new UserInfo();
                    uInfo.address = address;
                    uInfo.email = email;
                    uInfo.fullName = fullname;
                    uInfo.gender = gender;
                    uInfo.lastUpdateTime = System.currentTimeMillis();
                    uInfo.password = password;
                    uInfo.phoneNumber = phone;
                    uInfo.type = Configuration.TYPE_USER;
                    Calendar cal = Calendar.getInstance();
                    String[] date_birthday = birthday.split("/");
                    if (date_birthday != null && date_birthday.length == 3) {
                        int day = Integer.valueOf(date_birthday[0]);
                        int month = Integer.valueOf(date_birthday[1]);
                        int year = Integer.valueOf(date_birthday[2]);
                        cal.set(year, (month - 1), day);
                    }
                    uInfo.birthday = cal.getTimeInMillis();
                    return DISUserDBConnector.getInstance().addUser(uInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DataResponse.UNKNOWN_EXCEPTION;
            }
        };
    }

    @RouteInfo(method = "post", path = "/profile/update")
    public Route updateProfile() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String sessionKey = ServletUtil.getStringParameter(req, "sessionKey");
            if (sessionKey.isEmpty()) {
                sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            }
            String fullname = ServletUtil.getStringParameter(req, "fullname");
            String address = ServletUtil.getStringParameter(req, "address");
            int gender = ServletUtil.getIntParameter(req, "gender");
            String phone = ServletUtil.getStringParameter(req, "phone");
            String birthday = ServletUtil.getStringParameter(req, "birthday");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null && sessionInfo.email != null) {
                response.raw().sendRedirect("/login");
                return DataResponse.SESSION_EXPIRED;
            } else {
                UserInfo uInfo = new UserInfo();
                uInfo.address = address;
                uInfo.email = sessionInfo.email;
                uInfo.fullName = fullname;
                uInfo.gender = gender;
                uInfo.lastUpdateTime = System.currentTimeMillis();
                uInfo.phoneNumber = phone;
                Calendar cal = Calendar.getInstance();
                String[] date_birthday = birthday.split("/");
                if (date_birthday != null && date_birthday.length == 3) {
                    int day = Integer.valueOf(date_birthday[0]);
                    int month = Integer.valueOf(date_birthday[1]);
                    int year = Integer.valueOf(date_birthday[2]);
                    cal.set(year, (month - 1), day);
                }
                uInfo.birthday = cal.getTimeInMillis();
                return DISUserDBConnector.getInstance().updateUser(uInfo);
            }
        };
    }

    @RouteInfo(method = "get", path = "/profile")
    public Route renderProfile() {
        return (Request request, Response response) -> {
            return RenderMain.getInstance().renderProfile(request, response);
        };
    }

    @RouteInfo(method = "get", path = "/")
    public Route renderHome() {
        return (Request request, Response response) -> {
            return RenderMain.getInstance().renderHome(-1, request, response);
        };
    }

    @RouteInfo(method = "get", path = "/folder/:folderId")
    public Route renderHomeWithFolder() {
        return (Request request, Response response) -> {
            
            Long folderId = Long.valueOf(request.params(":folderId"));
            System.out.println("renderHomeWithFolder folderId="+folderId);
            return RenderMain.getInstance().renderHome(folderId, request, response);
        };
    }

    @RouteInfo(method = "post", path = "/changePassword")
    public Route changePassword() {
        return (Request req, Response response) -> {
            response.header("Content-Type", "application/json");
            String sessionKey = ServletUtil.getStringParameter(req, "sessionKey");
            if (sessionKey.isEmpty()) {
                sessionKey = HttpRequestUtils.getCookieFiro(req, "sessionKey");
            }
            String password = ServletUtil.getStringParameter(req, "password");
            String newPassword = ServletUtil.getStringParameter(req, "newPassword");
            SessionInfo sessionInfo = DISUserDBConnector.getInstance().checkSession(sessionKey);
            if (sessionInfo != null && sessionInfo.email != null) {
                response.raw().sendRedirect("/login");
                return DataResponse.SESSION_EXPIRED;
            } else {
                return DISUserDBConnector.getInstance().changePassword(sessionInfo.email, password, newPassword);
            }
        };
    }
}
