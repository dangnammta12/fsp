/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.utils;

import crdhn.dis.configuration.Configuration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 * @author namdv
 */
public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class);

    public static int toSecondTimeAndCheck(long time) {
        if (time > 100000000000L) {
            return (int) time / 1000;
        }
        return (int) time;
    }

    public static int toSecondTime(long time) {
        int ret = 0;
        if (time > 10000000000l) {
            ret = (int) (time / 1000);
        } else {
            ret = (int) time;
        }
        return ret;
    }

    public static long toLongTime(int time) {
        long ret = 0;
        if (time < 10000000000l) {
            ret = ((long) time) * 1000;
        } else {
            ret = (long) time;
        }
        return ret;
    }

    public static String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] checksum(byte[] data, MessageDigest digest) {
        try {
            digest.update(data);
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatFileSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void printLogSystem(String className, String message) {
        System.out.println(className + "." + message);
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < arrayBytes.length; i++) {
            String hex = Long.toHexString(0xff & arrayBytes[i]);
            if (hex.length() == 1) {
                stringBuffer.append('0');
            }
            stringBuffer.append(hex);
//            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
//                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static String toHex(byte[] input, MessageDigest digest) {
        return convertByteArrayToHexString(checksum(input, digest));
    }

    public static String getStatus(int status) {
        String result = "";
        switch (status) {
            case 0:
                result = "FS_EMPTY";
                break;
            case 1:
                result = "FS_UPLOADING";
                break;
            case 2:
                result = "FS_UPLOADED";
                break;
            case 3:
                result = "FS_UPLOAD_FAIL";
                break;
            case 4:
                result = "FS_DELETED";
                break;
            default:
                break;
        }
        return result;
    }

    public static Map<String, Object> getTimeService() {
        long miliTime = System.currentTimeMillis();

        long timesecond = Utils.toSecondTimeAndCheck(miliTime);

        Date datetime = new Date(miliTime);
        int s = datetime.getSeconds();
        int mi = datetime.getMinutes();
        int h = datetime.getHours();
        int d = datetime.getDate();
        int m = datetime.getMonth() + 1;
        int y = datetime.getYear() + 1900;

        String time = h + ":" + mi + ":" + s;
        String date = m + "-" + d + "-" + y;

        Map<String, Object> ret = new TreeMap<String, Object>();

        ret.put("second", timesecond);
        ret.put("time", time);
        ret.put("date", date);

        return ret;
    }

    public static String convertLongTimeToString(long miliTime) {

        long timesecond = Utils.toSecondTimeAndCheck(miliTime);

        Date datetime = new Date(miliTime);
        int s = datetime.getSeconds();
        int mi = datetime.getMinutes();
        int h = datetime.getHours();
        int d = datetime.getDate();
        int m = datetime.getMonth() + 1;
        int y = datetime.getYear() + 1900;

        String time = h + ":" + mi + ":" + s;
        String date = d + "/" + m + "/" + y;
        return (date + "  " + time);
//        Map<String, Object> ret = new TreeMap<String, Object>();
//
//        ret.put("second", timesecond);
//        ret.put("time", time);
//        ret.put("date", date);
//
//        return ret;
    }

    public static int convertTime(String someDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy H:mm");
        try {
            Date dateTime = sdf.parse(someDate);
            long mili = dateTime.getTime();

            return toSecondTime(mili);
        } catch (Exception ex) {
        }

        return -1;
    }

    public static int convertDate(int timer) {
        int ret = -1;
        try {
            long timesecond = Utils.toLongTime(timer);

            Date datetime = new Date(timesecond);

            Calendar cal = Calendar.getInstance();
            cal.setTime(datetime);

            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

            String timeFormat = dateFormat.format(cal.getTime());

            long mili = dateFormat.parse(timeFormat).getTime();

            ret = toSecondTime(mili);
        } catch (Exception ex) {
        }

        return ret;
    }

    public static boolean checkFileExisted(String path) {
        File f = new File(path);
        return f.exists();
    }
    
    public static boolean checkFilePublic(long itemId, int itemType) {
        try {
            if (itemId <= 0) {
                return false;
            }

            HashMap<String, String> params = new HashMap();
            params.put("itemId", Long.toString(itemId));
            params.put("type", Integer.toString(itemType));
            String url_submit = Configuration.url_agent_server + "/item/permission/public";
            String respCheckPublic = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
            JSONObject objCheckPublic = new JSONObject(respCheckPublic);
            if (objCheckPublic.has("error_code") && objCheckPublic.getInt("error_code") == 0) {
                JSONObject objDataCheck = objCheckPublic.getJSONObject("data");
                int permission = objDataCheck.getInt("permission");
                if (permission == Configuration.PUBLIC_EDIT || permission == Configuration.PUBLIC_VIEW) {
                    return true;
                }
            }
        } catch (IOException | JSONException ex) {
            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static synchronized void writeFile(String path, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(content);
            // no need to close it in java 8.
//            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertListLongToString(List<Long> listItem) {
        String result = "";
        for (int i = 0; i < listItem.size(); i++) {
            long item = listItem.get(i);
            if (i == listItem.size() - 1) {
                result += item;
            } else {
                result += item + ",";
            }
        }
        return result;
    }

    public static String convertJSONArrayToString(JSONArray listItem) {
        String result = "";
        for (int i = 0; i < listItem.length(); i++) {
            try {
                long item = listItem.getLong(i);
                if (i == listItem.length() - 1) {
                    result += item;
                } else {
                    result += item + ",";
                }
            } catch (JSONException ex) {
                java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public static String createFolderNotOverwrite(String pathName) {
        String path = pathName;
        int i = 1;
        File mkfolder = new File(path);
        while (mkfolder.exists()) {
            path = pathName + "(" + i + ")";
            mkfolder = new File(path);
            i++;
        }
        mkfolder.mkdir();
        return path;
    }

    public static String createFolderOverwrite(String pathName) {
        String path = pathName;
        File mkfolder = new File(path);
        if (!mkfolder.exists()) {
            mkfolder.mkdirs();
        }
        return path;
    }
}
