/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.mca;

import com.mongodb.client.MongoCollection;
import dis.configuration.ConfigHelper;
import java.util.Date;
import org.bson.Document;
import dis.mongodb.MongoDBConnector;

/**
 *
 * @author longmd
 */

public class ExceptionsCollection {
	private static ExceptionsCollection instance = null;
	private MongoCollection<Document> collection = null;

	public synchronized static ExceptionsCollection getInstance() {
		if (instance == null) {
			instance = new ExceptionsCollection();
			instance.collection = MongoDBConnector.getInstance().getMongoDatabase().getCollection(
					ConfigHelper.getParamString("mongodb", "exceptions_collection", "Exceptions"));
		}
		return instance;
	}
	
	public void addException(String service, String location, String exception){
		Document document = new Document()
				.append("time", new Date())
				.append("service", service)
				.append("location", location)
				.append("exception", exception);
		try{
			collection.insertOne(document);
		}
		catch (Exception ex){
			System.out.println(ex.toString());
			System.out.println(document.toJson());
		}
	}
}
