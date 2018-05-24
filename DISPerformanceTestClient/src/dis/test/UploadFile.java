/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.test;

import dis.model.FileInfo;
import dis.utils.HttpRequestUtils;
import dis.utils.StringUtils;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author longmd
 */

public class UploadFile extends Thread{
	
	@Override
    public void run() {
		FileInfo fileInfo = new FileInfo();
		fileInfo.fileName = StringUtils.getRandomString();
		fileInfo.ownerName = "longmd";
		fileInfo.appKey = "testapp";
		fileInfo.checksumSHA2 = fileInfo.fileName;
		fileInfo.checksumMD5 = fileInfo.fileName;
		fileInfo.fileSize = 1000000;
		fileInfo.chunkSize = 100;
		fileInfo.numberOfChunks = DISPerformanceTestClient.numberOfChunks;
		addFile(fileInfo);
	}
	
	public void addFile(FileInfo fileInfo) {
		try {
			String chunkData = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
			for (int i=0; i < 10; i++){
				chunkData += chunkData;
			}
			HashMap<String, String> params = new HashMap<>();
			params.put("fileName", fileInfo.fileName);
			params.put("ownerName", fileInfo.ownerName);
			params.put("appKey", fileInfo.appKey);
			params.put("sha2", fileInfo.checksumSHA2);
			params.put("md5", fileInfo.checksumMD5);
			params.put("filesize", Long.toString(fileInfo.fileSize));
			params.put("chunksize", Integer.toString(fileInfo.chunkSize));
			params.put("nbchunks", Integer.toString(fileInfo.numberOfChunks));
			String respAddFile = HttpRequestUtils.sendHttpRequest(DISPerformanceTestClient.baseUrl + "/file/addFile", "POST", params);
			
			JSONObject objUploadfile = new JSONObject(respAddFile);
			if (objUploadfile.has("error_code") && objUploadfile.getInt("error_code") == 0) {
				long fileId = objUploadfile.getJSONObject("data").getLong("fileId");
				int numberOfWorkers = DISPerformanceTestClient.numberOfWorkers;
				for (int i=1; i<=numberOfWorkers; i++){
					new UploadChunk(fileId, fileInfo.numberOfChunks, i, numberOfWorkers, chunkData).start();
				}
			}
			else 
				System.out.println("File " + fileInfo.fileName + " : ERROR");
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
    }
}
