package dis.mongodb.mca;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.include;
import com.mongodb.client.model.Sorts;
import dis.configuration.ConfigHelper;
import dis.configuration.Configuration;
import dis.model.StatisticInfo;
import dis.model.TError;
import dis.model.TErrorCode;
import dis.mongodb.MongoDBConnector;
import dis.mongodb.response.GetStatisticInfoResponse;
import dis.mongodb.response.GetStatisticInfosResponse;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author haont25
 */
public class StatisticCollection {

	private static StatisticCollection instance = null;
	private MongoCollection<Document> collection = null;

	public synchronized static StatisticCollection getInstance() {
		if (instance == null) {
			instance = new StatisticCollection();
			instance.collection = MongoDBConnector.getInstance().getMongoDatabase().getCollection(ConfigHelper.getParamString("mongodb", "statistic_collection", "Statistics"));
		}
		return instance;
	}

	public TError addNewStatistic(GetStatisticInfoResponse stat) {

		try {

			Document doc = new Document("_id", stat.statisticInfo.statisticId)
					.append("numberOfFile", stat.statisticInfo.numberOfFile)
					.append("numberOfChunk", stat.statisticInfo.numberOfChunk)
					.append("totalFileSize", stat.statisticInfo.totalFileSize)
					.append("avgFileSize", stat.statisticInfo.avgFileSize)
					.append("storageSize", stat.statisticInfo.storageSize)
					.append("time", stat.statisticInfo.time);

			StatisticCollection.getInstance().collection.insertOne(doc);
			return MongoDBConnector.SUCCESS;
		}
		catch (MongoWriteException ex) {
			if (ex.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
				return MongoDBConnector.STATISTIC_EXISTED;
			}
			else {
				ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, ex.getStackTrace()[0].toString(), ex.toString());
				TError error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
				return error;
			}
		}
		catch (Exception e) {
			ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
			TError error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), e.toString());
			return error;
		}

	}

	public GetStatisticInfoResponse getStatistic(String time) {
		GetStatisticInfoResponse response = new GetStatisticInfoResponse();
		try {
			Bson filter = Filters.eq("time", time);
			MongoCursor<Document> result = collection.find(filter).iterator();
			if (!result.hasNext()) {
				response.setError(MongoDBConnector.STATISTIC_NOT_FOUND);
			}
			else {
				Document doc = result.next();
				response.statisticInfo = new StatisticInfo();
				response.statisticInfo.statisticId = doc.getString("_id");
				response.statisticInfo.avgFileSize = doc.getDouble("avgFileSize");
				response.statisticInfo.numberOfChunk = doc.getLong("numberOfChunk");
				response.statisticInfo.numberOfFile = doc.getLong("numberOfFile");
				response.statisticInfo.storageSize = doc.getDouble("storageSize");
				response.statisticInfo.totalFileSize = doc.getDouble("totalFileSize");
				response.statisticInfo.time = doc.getString("time");
				response.setError(MongoDBConnector.SUCCESS);
			}

			return response;
		}
		catch (Exception e) {
			ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
			response.error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), e.toString());
			return response;
		}
	}

	public GetStatisticInfosResponse getStatisticInRange(String startTime, String endTime) {
		GetStatisticInfosResponse response = new GetStatisticInfosResponse();

		try {
			Bson filter = Filters.and(Filters.gte("time", startTime), Filters.lte("time", endTime));
			response.statisticInfo = new ArrayList<>();
			try (MongoCursor<Document> result = collection.find(filter).sort(descending("time")).iterator()) {
				if (!result.hasNext()) {
					response.setError(MongoDBConnector.STATISTIC_NOT_FOUND);
				}
				else {
					while (result.hasNext()) {
						Document doc = result.next();
						StatisticInfo info = new StatisticInfo();
						info.statisticId = doc.getString("_id");
						info.avgFileSize = doc.getDouble("avgFileSize");
						info.numberOfChunk = doc.getLong("numberOfChunk");
						info.numberOfFile = doc.getLong("numberOfFile");
						info.storageSize = doc.getDouble("storageSize");
						info.totalFileSize = doc.getDouble("totalFileSize");
						info.time = doc.getString("time");
						response.statisticInfo.add(info);
					}
					response.setError(MongoDBConnector.SUCCESS);
				}
			}
			
			return response;
		}
		catch (Exception e) {
			ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
			response.error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), e.toString());
			return response;
		}
	}

	public GetStatisticInfosResponse getAllStatistics() {
		GetStatisticInfosResponse response = new GetStatisticInfosResponse();		
		
		try {
			response.statisticInfo = new ArrayList<>();
			
			try (MongoCursor<Document> result = collection.find().sort(descending("time")).iterator()) {
				if (!result.hasNext()) {
					response.setError(MongoDBConnector.STATISTIC_NOT_FOUND);
				}
				else {
					
					while (result.hasNext()) {
						Document doc = result.next();
						StatisticInfo info = new StatisticInfo();
						info.statisticId = doc.getString("_id");
						info.avgFileSize = doc.getDouble("avgFileSize");
						info.numberOfChunk = doc.getLong("numberOfChunk");
						info.numberOfFile = doc.getLong("numberOfFile");
						info.storageSize = doc.getDouble("storageSize");
						info.totalFileSize = doc.getDouble("totalFileSize");
						info.time = doc.getString("time");
						response.statisticInfo.add(info);
					}
					response.setError(MongoDBConnector.SUCCESS);
				}
			}
			
			return response;
		}
		catch (Exception e) {
			ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
			response.error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), e.toString());
			return response;
		}
	}
	
	public static GetStatisticInfoResponse getStatFile(GetStatisticInfoResponse response) {
		try {
			Document doc = MongoDBConnector.getInstance().getMongoDatabase().runCommand(new Document("collStats", ConfigHelper.getParamString("mongodb", "files_collection", "Files")));

			if (doc == null)
				response.setError(MongoDBConnector.FILE_COLLECTION_NOT_FOUND);
			else
				response.statisticInfo.numberOfFile = (long) doc.getInteger("count");

			return response;
		}
		catch (Exception e) {
			ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
			response.error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), e.toString());
			return response;
		}

	}
	
	public static GetStatisticInfoResponse getStatChunks(GetStatisticInfoResponse response) {
		try {
			Document doc = MongoDBConnector.getInstance().getMongoDatabase().runCommand(new Document("collStats", ConfigHelper.getParamString("mongodb", "chunks_collection", "Chunks")));
			if (doc == null)
				response.setError(MongoDBConnector.CHUNK_COLLECTION_NOT_FOUND);
			else {
				response.statisticInfo.numberOfChunk = (long) doc.getInteger("count");
				response.statisticInfo.totalFileSize = doc.getDouble("size");
				response.statisticInfo.storageSize = doc.getDouble("storageSize");
			}
			return response;
		}
		catch (Exception e) {
			ExceptionsCollection.getInstance().addException(Configuration.SERVICE_NAME, e.getStackTrace()[0].toString(), e.toString());
			response.error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), e.toString());
			return response;
		}
	}
}
