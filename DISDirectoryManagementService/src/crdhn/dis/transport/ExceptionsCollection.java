/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.client.MongoCollection;
import firo.utils.config.Config;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author namdv
 */

public class ExceptionsCollection {
	private static ExceptionsCollection instance = null;
	private MongoCollection<Document> collection = null;

	public static ExceptionsCollection getInstance() {
		if (instance == null) {
			instance = new ExceptionsCollection();
			instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "exceptions_collection", "Exceptions"));
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
