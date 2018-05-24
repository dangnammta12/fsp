/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.bson.Document;

/**
 *
 * @author namdv
 */
public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class);

    private static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Validate email with regular expression
     *
     * @param email for validation
     * @return true valid email, false invalid email
     */
    public static boolean validateEmail(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

    public static String listStringToString(List<String> strList) {
        String result = "";
        for (int i = 0; i < strList.size(); i++) {
            String str = strList.get(i);
            if (str != null && str.length() > 0) {
                if (result.length() == 0) {
                    result = str;
                } else {
                    result = result + "," + str;
                }
            }
        }
        return result;
    }

    public static String listLongToString(List<Long> listId) {
        String result = "";
        for (int i = 0; i < listId.size(); i++) {
            long value = listId.get(i);
            if (result.length() == 0) {
                result = value + "";
            } else {
                result = result + "," + value;
            }
        }
        return result;
    }

    public static List<Long> arrayStringToListLong(String[] arr) {
        List<Long> result = new ArrayList<>();
        for (String arr1 : arr) {
            if (arr1.trim().length() > 0) {
                result.add(Long.valueOf(arr1));
            }
        }
        return result;
    }
    
    public static boolean checkIps(String ip, Document doc, String field) {

    //    List<String> acceptedIp = (List<String>) disDoc.get("agentAcceptedIp");
        List<String> ips = (List<String>) doc.get(field);
        if (ips.contains(ip)) {
            return true;
        } else {
            return false;
        }
    }
}
