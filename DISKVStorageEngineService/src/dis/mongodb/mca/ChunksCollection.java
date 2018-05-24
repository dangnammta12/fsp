/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.mca;

import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import dis.configuration.ConfigHelper;
import dis.configuration.Configuration;
import dis.model.ChunkInfo;
import dis.mongodb.MongoDBConnector;
import dis.mongodb.response.GetChunkInfoResponse;
import dis.model.TError;
import dis.model.TErrorCode;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author longmd
 */
public class ChunksCollection {

	private static ChunksCollection instance = null;
	private MongoCollection<Document> collection = null;
	private final UpdateOptions upsertOption = new UpdateOptions().upsert(true);

	public synchronized static ChunksCollection getInstance() {
		if (instance == null) {
			instance = new ChunksCollection();
			instance.collection = MongoDBConnector.getInstance().getMongoDatabase().getCollection(
					ConfigHelper.getParamString("mongodb", "chunks_collection", "Chunks"));
//					.withWriteConcern(new WriteConcern(0).withJournal(true));
		}
		return instance;
	}
	
	//upload
	public TError addChunk(ChunkInfo chunkInfo){
		System.out.println("ChunkInfoCollection::addChunk fileId=" + chunkInfo.fileId + ", chunkNumber = " + chunkInfo.chunkNumber);
//		Long startTime = System.currentTimeMillis();
		try {
			Document chunkDocument = new Document("_id", chunkInfo.fileId+"_"+chunkInfo.chunkNumber)
					.append("fileId", chunkInfo.fileId)
					.append("chunkNumber", chunkInfo.chunkNumber)
					.append("chunkData", chunkInfo.chunkData);
//			InsertOneOptions ioo = new InsertOneOptions().
			collection.insertOne(chunkDocument);
//			Long endTime = System.currentTimeMillis();
			return MongoDBConnector.SUCCESS;
		} catch (MongoWriteException ex){
			if (ex.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY))
				return MongoDBConnector.CHUNK_EXISTED;
			else {
				ExceptionsCollection.getInstance().addException(
						Configuration.SERVICE_NAME,
						ex.getStackTrace()[0].toString(),
						ex.toString());
				TError errorEx = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
				return errorEx;
			}
		} catch (Exception ex) {
			ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			TError errorEx = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
			return errorEx;
		}
	}
	
	public TError deleteChunks(long fileId, int numberOfChunks) throws Exception{
//		Bson filter = Filters.eq("fileId", fileId);
//		collection.deleteMany(filter);
		for (int chunkNumber=1; chunkNumber<=numberOfChunks; chunkNumber++){
			Bson filter = Filters.and(Filters.eq("fileId", fileId), Filters.eq("chunkNumber", chunkNumber));
			collection.deleteOne(filter);
		}
		return MongoDBConnector.SUCCESS;
	}
	
	//download
	public GetChunkInfoResponse getChunk(long fileId, int chunkNumber){
		System.out.println("ChunkInfoCollection::getChunk fileId=" + fileId + ", chunkNumber = " + chunkNumber);
		GetChunkInfoResponse response = new GetChunkInfoResponse();
		try {
			Bson filter = Filters.and(Filters.eq("fileId", fileId), Filters.eq("chunkNumber", chunkNumber));
			MongoCursor<Document> result = collection.find(filter).iterator();
			if (!result.hasNext())
				response.setError(MongoDBConnector.CHUNK_NOT_FOUND);
			else {
				Document doc = result.next();
				response.chunkInfo = new ChunkInfo();
				response.chunkInfo.fileId = doc.getLong("fileId");
				response.chunkInfo.chunkNumber = doc.getInteger("chunkNumber");
				response.chunkInfo.chunkData = doc.getString("chunkData");
				response.setError(MongoDBConnector.SUCCESS);
			}
				
			return response;
		} catch (Exception ex) {
			ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			response.error = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
			return response;
		}
	}
}