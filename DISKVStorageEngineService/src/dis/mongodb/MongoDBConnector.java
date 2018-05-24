/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import dis.configuration.ConfigHelper;
import dis.model.TError;
import dis.model.TErrorCode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author longmd
 */
public class MongoDBConnector {
	private static MongoDBConnector instance = null;
	public static final FindOneAndUpdateOptions foauoReturnAfter = new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
	public static final UpdateOptions upsertOptions = new UpdateOptions().upsert(true);
	private MongoClient mongodbClient = null;
	private MongoDatabase mongodbDatabase = null;
	
	public static final TError SUCCESS;
	public static final TError CONNECTION_ERROR;
	public static final TError MISSING_CHUNK;
	public static final TError CHUNK_EXISTED;
	public static final TError CHUNK_NOT_FOUND;
	public static final TError FILE_NOT_FOUND;
	
	static {
		SUCCESS = new TError(TErrorCode.EC_OK.getValue(), "Successful");
		CONNECTION_ERROR = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), "Unable to connect to MongoDB");
		MISSING_CHUNK = new TError(TErrorCode.EC_MISSING_CHUNK.getValue(), "Missing chunks");
		CHUNK_EXISTED = new TError(TErrorCode.EC_CHUNK_EXISTED.getValue(), "Chunk existed");
		CHUNK_NOT_FOUND = new TError(TErrorCode.EC_CHUNK_NOT_FOUND.getValue(), "Chunk not found");
		FILE_NOT_FOUND = new TError(TErrorCode.EC_FILE_NOT_FOUND.getValue(), "File not found");
	}

	public static MongoDBConnector getInstance() {
		if (instance == null) {
			instance = new MongoDBConnector();
			instance.initMongoClient();
		}
		return instance;
	}
	
	private List<ServerAddress> getListServerAddress(){
		String str = ConfigHelper.getParamString("mongodb", "host_port", "");
		String[] listHostPorts = str.split(",");
		List<ServerAddress> listServers = new ArrayList<>();
		for (String hostport : listHostPorts){
			String[] params = hostport.split(":");
			listServers.add(new ServerAddress(params[0], Integer.valueOf(params[1])));
		}
		return listServers;
	}

	private void initMongoClient() {
		if (mongodbClient == null) {
			//For mongodb's standalone
//			ServerAddress serverAddress = new ServerAddress("127.0.0.1", 27017);

			//For mongodb's cluster
			List<ServerAddress> listServers = getListServerAddress();
			
			MongoClientOptions.Builder optionBuilder = new MongoClientOptions.Builder()
					.connectTimeout(ConfigHelper.getParamInt("mongodb", "connect_timeout"))
					.socketTimeout(ConfigHelper.getParamInt("mongodb", "socket_timeout"))
					.maxWaitTime(ConfigHelper.getParamInt("mongodb", "max_waittime"))
					.serverSelectionTimeout(ConfigHelper.getParamInt("mongodb", "server_selection_timeout"));
			mongodbClient = new MongoClient(listServers, optionBuilder.build());
			mongodbDatabase = mongodbClient.getDatabase(ConfigHelper.getParamString("mongodb", "database_name", ""));
		}
	}
	
	public MongoDatabase getMongoDatabase(){
		return mongodbDatabase;
	}
}