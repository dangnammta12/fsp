/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import firo.utils.config.Config;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author namdv
 */
public class MongoDBConnector {

    public static final FindOneAndUpdateOptions foauoReturnAfter = new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
    public static final UpdateOptions updateUpsert = new UpdateOptions().upsert(true);
    private static MongoDBConnector instance = null;
    private MongoClient mongodbClient = null;
    private MongoDatabase mongodbDatabase = null;

    public static MongoDBConnector getInstance() {
		if (instance == null) {
			instance = new MongoDBConnector();
			instance.initMongoClient();
		}
		return instance;
	}
	
	private List<ServerAddress> getListServerAddress(){
		String str = Config.getParamString("mongodb", "host_port", "");
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
					.connectTimeout(Config.getParamInt("mongodb", "connect_timeout"))
					.socketTimeout(Config.getParamInt("mongodb", "socket_timeout"))
					.maxWaitTime(Config.getParamInt("mongodb", "max_waittime"))
					.serverSelectionTimeout(Config.getParamInt("mongodb", "server_selection_timeout"));
			mongodbClient = new MongoClient(listServers, optionBuilder.build());
			mongodbDatabase = mongodbClient.getDatabase(Config.getParamString("mongodb", "database_name", ""));
		}
	}
	
	public MongoDatabase getDatabase(){
		return mongodbDatabase;
	}

}
