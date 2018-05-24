package crdhn.dis.utils;

import crdhn.dis.configuration.Configuration;
import firo.Request;
import firo.Response;
import firo.utils.config.Config;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author namdv
 */
public class HttpRequestUtils {

    public static String buildCurrentUri(HttpServletRequest req) {
        return "http://" + req.getServerName() + req.getRequestURI() + "?" + req.getQueryString();
    }

    public static void redirect(HttpServletResponse resp, String urlRedirect) {
        try {
            resp.sendRedirect(urlRedirect);
        } catch (Exception ex) {
        }
    }

    public static void setCookieValue(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setValue(cookieValue);
    }

    public static String getCookieValue(Cookie[] cookies, String cookieName, String defaultValue) {
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName())) {
                    return (cookie.getValue());
                }
            }
        }
        return (defaultValue);
    }

    public static boolean deleteCookie(HttpServletRequest req, HttpServletResponse resp, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    resp.addCookie(cookie);
                    return true;
                }
            }
        }
        return false;
    }

    public static void setCookieFiro(String cookieName, String value, String domain, int expire, Response resp) {
// Create cookies for first and last names.      
        Cookie language = new Cookie(cookieName, value);

        // Set expiry date after 24 Hrs for both the cookies.
        language.setMaxAge(expire);
        language.setPath("/");
        language.setDomain(domain);
        language.setValue(value);
        // Add both the cookies in the response header.
        resp.raw().addCookie(language);
    }

    public static void setCookie(String cookieName, String value, String domain, int expire, HttpServletResponse resp) {
// Create cookies for first and last names.      
        Cookie language = new Cookie(cookieName, value);

        // Set expiry date after 24 Hrs for both the cookies.
        language.setMaxAge(expire);
        language.setPath("/");
        language.setDomain(domain);
        language.setValue(value);
        // Add both the cookies in the response header.
        resp.addCookie(language);
    }

    public static void setCookie(String cookieName, String value, int expire, boolean httponly, String path, String domain, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String strExpire = "";
            if (expire == 0) { // expire now
                Calendar cal = Calendar.getInstance();
                cal.add(cal.YEAR, -1);
                String ex = formatDate("EEE, dd-MMM-yyyy HH:mm:ss zzz", cal.getTime());
                strExpire = ";Expires=" + ex;
            } else if (expire > 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(cal.MILLISECOND, expire * 1000);
                String ex = formatDate("EEE, dd-MMM-yyyy HH:mm:ss zzz", cal.getTime());
                strExpire = ";Expires=" + ex;
            }
            // else expire < -1: expires after browser is closed.

            String strHttponly = "";
            if (httponly == true) {
                strHttponly = ";HttpOnly";
            }

            String headerValue = cookieName + "=" + value + ";Path=" + path + ";Domain=" + domain + strExpire + strHttponly;
            resp.setHeader("P3P", "CP=\"NOI ADM DEV PSAi COM NAV OUR OTRo STP IND DEM\"");
            resp.addHeader("Set-Cookie", headerValue);
        } catch (Exception e) {
        }
    }

    public static String getCookieFiro(Request req, String name) {
        Map<String, String> ret = getCookieMap(req);
        if (ret == null) {
            return null;
        }
        return ret.get(name);
    }

    public static String getCookie(HttpServletRequest req, String name) {
        Map<String, String> ret = getCookieMap(req);
        if (ret == null) {
            return null;
        }
        return ret.get(name);
    }

    public static Map<String, String> getCookieMap(HttpServletRequest req) {
        Map<String, String> ret = (Map<String, String>) req.getAttribute("zme.cookies");
        if (ret != null) {
            return ret;
        }
        ret = new HashMap<String, String>();

        try {
            Cookie[] cookies = req.getCookies();

            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    try {
                        //					String name = cookies[i].getName();
                        String name = URLDecoder.decode(cookies[i].getName(), "UTF-8");
                        String value = cookies[i].getValue();
                        if (value.toLowerCase().equals("deleted")) {
                            continue;
                        }

                        ret.put(name, value);
                        if (i > 50) {
                            break;
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception ex) {
        }
        req.setAttribute("zme.cookies", ret);
        return ret;
    }

    public static Map<String, String> getCookieMap(Request req) {
        Map<String, String> ret = (Map<String, String>) req.raw().getAttribute("zme.cookies");
        if (ret != null) {
            return ret;
        }
        ret = new HashMap<>();

        try {
            Cookie[] cookies = req.raw().getCookies();

            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    try {
                        String name = URLDecoder.decode(cookies[i].getName(), "UTF-8");
                        String value = cookies[i].getValue();
                        if (value.toLowerCase().equals("deleted")) {
                            continue;
                        }
                        ret.put(name, value);
                        if (i > 50) {
                            break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static String formatDate(String format, Date d) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(d);
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildParamRequestString(HashMap<String, String> mapParam) throws UnsupportedEncodingException {
        String str = "";
        for (Map.Entry<String, String> entry : mapParam.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() != null) {
                String value = URLEncoder.encode(entry.getValue(), "UTF-8");
                if (str.equals("")) {
                    str += key + "=" + value;
                } else {
                    str += "&" + key + "=" + value;
                }
            }

        }
        return str;
    }

    public static String sendHttpRequest(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        List<String> response = new ArrayList<>();
        try {
            params.put("appKey", Configuration.appKey);
            String requestParam = buildParamRequestString(params);
            String newRequestUrl = requestUrl;
            if ("POST".equals(method)) {
            } else if (requestParam.length() == 0) {
                newRequestUrl = requestUrl;
            } else {
                newRequestUrl = requestUrl + "?" + requestParam;
            }
            System.setProperty("sun.net.http.retryPost", "false");
            URL url = new URL(newRequestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(100000);
            urlConn.setReadTimeout(100000);
            urlConn.setUseCaches(false);
//        urlConn.setRequestProperty("Content-Type", "multipart/form-data;");
            if (params.containsKey("size")) {
                urlConn.setRequestProperty("Content-Length", params.get("size"));
            }
            // the request will return a response
            urlConn.setDoInput(true);
            if ("POST".equals(method)) {
                // set request method to POST
                urlConn.setDoOutput(true);
            } else {
                // set request method to GET
                urlConn.setDoOutput(false);
            }
            if ("POST".equals(method) && params.size() > 0) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
                writer.write(requestParam);
                writer.flush();
                writer.close();
            }
            BufferedReader br;
            if (200 <= urlConn.getResponseCode() && urlConn.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader((urlConn.getInputStream())));
            } else {
                urlConn.disconnect();
                return "{\"error_code\":" + urlConn.getResponseCode() + ",\"error_message\":" + urlConn.getResponseMessage() + "}";
            }
            String line = "";
            while ((line = br.readLine()) != null) {
                response.add(line);
            }                       
            br.close();
            urlConn.disconnect();
            return response.get(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "{\"error_code\":-1,\"error_message\":" + ex.getMessage() + "}";
        }
    }

    public static String sendPOSTRequest(String requestUrl, List<NameValuePair> urlParameters) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(requestUrl);

//            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//            urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
//            urlParameters.add(new BasicNameValuePair("cn", ""));
//            urlParameters.add(new BasicNameValuePair("locale", ""));
//            urlParameters.add(new BasicNameValuePair("caller", ""));
//            urlParameters.add(new BasicNameValuePair("num", "12345"));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
//            StringBuilder result = new StringBuilder();
            String line = rd.readLine();
//            while ((line = rd.readLine()) != null) {
//                break;
//            }
            rd.close();
            client.getConnectionManager().shutdown();
            return line;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpRequestUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpRequestUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static String getClientIP(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getHeader("X-Forwarded-For");
        }
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getHeader("x-forwarded-for");
        }
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    public static String buildCookieFromMap(HashMap<String, String> mapParam) {
        String str = "";
        for (Map.Entry<String, String> entry : mapParam.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (str.equals("")) {
                str += key + "=" + value;
            } else {
                str += "; " + key + "=" + value;
            }
        }
        return str;
    }

    public static DataResponse downloadFile(String requestUrl, String fileName, long itemId, String method, String saveDir, HashMap<String, String> params) {
        try {
            params.put("appKey", Configuration.appKey);
            String requestParam = buildParamRequestString(params);

            String newRequestUrl = requestUrl;
            if ("POST".equals(method)) {
            } else if (requestParam.length() == 0) {
                newRequestUrl = requestUrl;
            } else {
                newRequestUrl = requestUrl + "?" + requestParam;
            }
            URL url = new URL(newRequestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(300000);
            urlConn.setReadTimeout(300000);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            if ("POST".equals(method)) {
                urlConn.setDoOutput(true);
                if (params.size() > 0) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
                    writer.write(requestParam);
                    writer.flush();
                }
            } else {
                urlConn.setDoOutput(false);
            }
            int responseCode = urlConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String disposition = urlConn.getHeaderField("Content-Disposition");
//                if (fileName.isEmpty()) {
//                    if (disposition != null) {
//                        int index = disposition.indexOf("filename=");
//                        if (index > 0) {
//                            fileName = disposition.substring(index + 10, disposition.length() - 1);
//                        }
//                    } else {
//                        fileName = "item_" + itemId;
//                    }
//                }

                // opens input stream from the HTTP connection
                InputStream inputStream = urlConn.getInputStream();
                String saveFilePath = saveDir + File.separator + itemId;

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                urlConn.disconnect();
                System.out.println("File downloaded");
                return DataResponse.SUCCESS;
            } else {
                urlConn.disconnect();
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
                return new DataResponse(10, "responseCode=" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new DataResponse(11, "response Exception=" + e.getMessage());
        }

    }

    public static DataResponse upload(String accountName, String fileUrl, String fileName, long parentId) {
        try {
            File inFile = new File(fileUrl);
            MultipartUtility multipart = new MultipartUtility(Configuration.url_agent_server + "/upload/browser", "UTF-8");

            multipart.addHeaderField("User-Agent", "DIS");

            multipart.addFormField("appKey", Configuration.appKey);
            multipart.addFormField("parentId", Long.toString(parentId));
            multipart.addFormField("accountName", accountName);

            multipart.addFilePart("file", inFile);

            List<String> response = multipart.finish();

            System.out.println("response=" + response);
            return DataResponse.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return new DataResponse(10, "upload failed:" + e.getMessage());
        }

    }

    public static String uploadWithPath(String hashSHA1, String hashMD5, long fileSize, String accountName, String fileUrl, String fileName, long parentId) {
        try {
            HashMap<String, String> params = new HashMap();
            params.put("parentId", Long.toString(parentId));
            params.put("path", Config.getParam("setting", "path_service") + fileUrl);
            params.put("accountName", accountName);
            params.put("hashSHA1", hashSHA1);
            params.put("hashMD5", hashMD5);
            params.put("fileSize", Long.toString(fileSize));

            String url_create = Configuration.url_agent_server + "/upload/path";
            String result = HttpRequestUtils.sendHttpRequest(url_create, "POST", params);
            Utils.printLogSystem("HttpRequestUtils", "uploadFileWithPath parentId=" + parentId + "\t fileUrl=" + fileUrl + "\t  resp=" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new DataResponse(10, "upload failed:" + e.getMessage()).toString();
        }

    }
}
