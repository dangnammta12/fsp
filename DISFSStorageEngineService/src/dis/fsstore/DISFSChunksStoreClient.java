/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.fsstore;

import FCore.Thrift.ClientFactory;
import FCore.Thrift.TClient;
import dis.configuration.Configuration;
import dis.model.ChunkInfo;
import dis.model.TError;
import dis.mongodb.MongoDBConnector;
import dis.mongodb.response.GetChunkInfoResponse;
import dis.thrift.TDISChunksStoreService;
import org.apache.thrift.protocol.TCompactProtocol;

/**
 *
 * @author longmd
 */
public class DISFSChunksStoreClient {
	private static DISFSChunksStoreClient instance = null;

	public static DISFSChunksStoreClient getInstance() {
		if (instance == null) {
			instance = new DISFSChunksStoreClient();
		}
		return instance;
	}
	
	public TClient getClientWrapper() {
		TClient clientWrapper = ClientFactory.getClient("127.0.0.1", 1193, 7200, TDISChunksStoreService.Client.class, TCompactProtocol.class);
		if (clientWrapper != null) {
			if (!clientWrapper.sureOpen()) {
				return null;
			}
		}
		return clientWrapper;
	}
	
	//upload
	public TError addChunk(ChunkInfo chunkInfo){
		return FileSystemStorageUtils.getInstance().saveFile(Configuration.FS_STORAGE_LOCATION, chunkInfo.fileId + "."+chunkInfo.chunkNumber, chunkInfo.chunkData);
	}
	
	public TError deleteChunks(long fileId, int numberOfChunks) throws Exception{
		TClient clientWrapper = getClientWrapper();
		if (clientWrapper != null) {
			TDISChunksStoreService.Client aClient = (TDISChunksStoreService.Client) clientWrapper.getClient();
			if (aClient != null) {
				try {
					for (int chunkNumber=1; chunkNumber<=numberOfChunks; chunkNumber++){
						aClient.remove(fileId, chunkNumber);
					}
					
					ClientFactory.releaseClient(clientWrapper);
					return MongoDBConnector.SUCCESS;
				}
				catch (Exception ex) {
					clientWrapper.close();
					System.out.println("Exception: " + ex.toString());
				}
			}
		}
		else {
			System.out.println(": clientWrapper is NULL");
		}
		return MongoDBConnector.CONNECTION_ERROR;
	}
}
