/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Updates.inc;
import crdhn.dis.configuration.Configuration;
import firo.utils.config.Config;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author longmd
 */
public class MongoDBConnector {

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


//    public boolean initCounter(final MongoDatabase database, String key) {
//        MongoCollection<Document> counterCollection = database.getCollection(Configuration.MONGODB_COUNTER_COLLECTION_NAME);
//        Document checkDocument = counterCollection.find(eq("_id", key)).first();
//        if (checkDocument == null) {
//            Document counter = new Document("_id", key).append("seq", 1l);
//            counterCollection.insertOne(counter);
//        }
//        return true;
//    }

    public synchronized long getNextValue(String key, final MongoDatabase database) {

        try {
            MongoCollection<Document> counterCollection = database.getCollection(Configuration.MONGODB_COUNTER_COLLECTION_NAME);
            Document objDocument = counterCollection.findOneAndUpdate(eq("_id", key), inc("seq", 1l));
            System.out.println("getNextValue.key="+key+"\t value="+objDocument.get("seq"));
            return (long)objDocument.get("seq");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
