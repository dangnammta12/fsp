/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.test;

import dis.utils.HttpRequestUtils;
import dis.utils.StringUtils;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author longmd
 */
public class UploadChunk extends Thread{
	private final long _fileId;
	private final int _numberOfChunks;
	private final int _startingChunk;
	private final int _period;
	private final String _chunkData;
	
	public UploadChunk(long fileId, int numberOfChunks, int startingChunk, int period, String chunkData) {
        _fileId = fileId;
		_numberOfChunks = numberOfChunks;
		_startingChunk = startingChunk;
		_period = period;
		_chunkData = chunkData;
    }
	
	@Override
    public void run() {
		int chunkNumber = _startingChunk;
		HashMap<String, String> params = new HashMap<>();
		params.put("fileid", Long.toString(_fileId));
		params.put("chunknumber", Integer.toString(chunkNumber));
		params.put("chunkdata", _chunkData);
		long startTime, endTime;
		endTime = System.currentTimeMillis();
		while(chunkNumber <= _numberOfChunks){
			startTime = endTime;
			params.replace("chunknumber", Integer.toString(chunkNumber));
			while (true){
				try {
					String respAddChunk = HttpRequestUtils.sendHttpRequest(DISPerformanceTestClient.baseUrl + "/chunk/addChunk", "POST", params);
					JSONObject objUploadfile = new JSONObject(respAddChunk);
					if (objUploadfile.has("error_code")){
						int errorCode = objUploadfile.getInt("error_code");
						if (errorCode != 0){
							System.out.println("Chunk " + _fileId + "_" + chunkNumber + " : ERROR " + errorCode);
						}
					}
					else 
						System.out.println("Chunk " + _fileId + "_" + chunkNumber + " : No error code");
					break;
				}
				catch (Exception ex) {
					System.out.println("Chunk " + _fileId + "_" + chunkNumber + " : " + ex.toString());
				}
			}
			endTime = System.currentTimeMillis();
			System.out.println("Chunk " + _fileId + "_" + chunkNumber + " : "  + (endTime - startTime));
			chunkNumber += _period;
		}
	}
}
