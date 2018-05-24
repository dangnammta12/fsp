/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *
 * @author longmd
 */
public class StringUtils {
	public static String getRandomString() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			Random rand = new Random();
			String randomStr = "DISPerformanceTest - " 
					+ Long.toString(System.currentTimeMillis() + rand.nextLong()) 
					+ Integer.toString(rand.nextInt(1234567890) + 1);
			
			md.update(randomStr.getBytes());
			byte byteData[] = md.digest();

			//convert the byte to hex format method 1
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			return null;
		}
	}
}
