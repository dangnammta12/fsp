/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Updates.inc;
import crdhn.dis.configuration.Configuration;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import firo.utils.config.Config;

/**
 *
 * @author namdv
 */
public class CountersCollection {

    private static CountersCollection instance = null;
    private MongoCollection<Document> collection = null;

    public synchronized static CountersCollection getInstance() {
        if (instance == null) {
            instance = new CountersCollection();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParamString("mongodb", "counters_collection", "Counters"));
        }
        return instance;
    }

    public synchronized long getNextValue(String key) {

        try {
            Document objDocument = collection.findOneAndUpdate(eq("_id", key), inc("value", 1L), MongoDBConnector.foauoReturnAfter);
            return objDocument.getLong("value");
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(
                    Configuration.SERVICE_NAME,
                    ex.getStackTrace()[0].toString(),
                    ex.toString());
            ex.printStackTrace();
        }
        return -1L;
    }
}
