/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.fsstore;

import FCore.Thrift.ClientFactory;
import FCore.Thrift.TClient;
import dis.model.ChunkInfo;
import dis.model.TError;
import dis.mongodb.MongoDBConnector;
import dis.thrift.TDISChunksStoreService;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author longmd
 */
public class FileSystemStorageUtils {
	private static FileSystemStorageUtils instance = null;

	public static FileSystemStorageUtils getInstance() {
		if (instance == null) {
			instance = new FileSystemStorageUtils();
		}
		return instance;
	}
	
	public TError saveFile(String folderName, String fileName, String data){
		try (BufferedWriter writer = new BufferedWriter( new FileWriter(folderName + fileName))){
			writer.write(data);
			return MongoDBConnector.SUCCESS;
		}
		catch (IOException ex) {
			Logger.getLogger(FileSystemStorageUtils.class.getName()).log(Level.SEVERE, null, ex);
			return MongoDBConnector.FILE_NOT_FOUND;
		}
		
	}
}
