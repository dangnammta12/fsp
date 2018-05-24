/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.test;

/**
 *
 * @author longmd
 */
public class DISPerformanceTestClient {
	public static int numberOfFiles = 10;
	public static int numberOfChunks = 1000;
	public static int numberOfWorkers = 5;
	public static String baseUrl = "http://127.0.0.1:1101";
//	public static String baseUrl = "http://10.86.222.213:1101";
	
	public static void main(String[] args) {
		try {
			for (int i=1; i<=numberOfFiles; i++){
				new UploadFile().start();
			}
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
}