/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.mca;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dis.configuration.ConfigHelper;
import dis.configuration.Configuration;
import dis.mongodb.MongoDBConnector;
import dis.mongodb.response.GetNextValueResponse;
import dis.model.TError;
import dis.model.TErrorCode;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author longmd
 */
public class CountersCollection {
	private static CountersCollection instance = null;
	private MongoCollection<Document> collection = null;
	

	public synchronized static CountersCollection getInstance() {
		if (instance == null) {
			instance = new CountersCollection();
			instance.collection = MongoDBConnector.getInstance().getMongoDatabase().getCollection(ConfigHelper.getParamString("mongodb", "counters_collection", "Counters"));
		}
		return instance;
	}
	
	public synchronized GetNextValueResponse getNextValue(String key){
		GetNextValueResponse response = new GetNextValueResponse();
		
		try{
			Bson filter = Filters.eq("_id", key);
			Bson update = Updates.inc("value", 1L);
			
			Document doc = collection.findOneAndUpdate(filter, update, MongoDBConnector.foauoReturnAfter);
			response.fileId = doc.getLong("value");
			response.error = new TError(TErrorCode.EC_OK.getValue());
			return response;
		}
		catch (Exception ex){
			ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			response.error = new TError(TErrorCode.EC_COUNTER_ERROR.getValue(), ex.toString());
			return response;
		}
	}
}
