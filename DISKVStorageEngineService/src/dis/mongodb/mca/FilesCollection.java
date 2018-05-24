/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.mca;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dis.configuration.ConfigHelper;
import dis.configuration.Configuration;
import dis.model.ExtendedFileInfo;
import dis.model.FileInfo;
import dis.mongodb.MongoDBConnector;
import dis.mongodb.response.GetFileInfoResponse;
import dis.mongodb.response.GetFileInfosResponse;
import dis.mongodb.response.GetRefFileInfoResponse;
import dis.model.TFileStatus;
import dis.model.TError;
import dis.model.TErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author longmd
 */
public class FilesCollection {
	private static FilesCollection instance = null;
	private MongoCollection<Document> collection = null;
	private ConcurrentHashMap<Long, FileInfo> mapRefFiles;
//	private final UpdateOptions upsertOption = new UpdateOptions().upsert(true);

	public synchronized static FilesCollection getInstance() {
		if (instance == null) {
			instance = new FilesCollection();
			instance.collection = MongoDBConnector.getInstance().getMongoDatabase().getCollection(ConfigHelper.getParamString("mongodb", "files_collection", "Files"));
			try{
				instance.collection.createIndex(new BasicDBObject("files.fileId", 1));
				instance.collection.createIndex(new BasicDBObject("checksumSHA2", 1).append("fileSize", 1));
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
			instance.mapRefFiles = new ConcurrentHashMap<>();
		}
		return instance;
	}

    public GetRefFileInfoResponse getRefFileInfo(long fileId) {
        GetRefFileInfoResponse response = new GetRefFileInfoResponse();
        if (mapRefFiles.contains(fileId)) {
            response.setError(MongoDBConnector.SUCCESS);
            response.setFileInfo(mapRefFiles.get(fileId));
            return response;
        }

		Bson filter = eq("files.fileId", fileId);
		Bson projection = include("_id", "numberOfChunks");
		try (MongoCursor<Document> result = collection.find(filter).projection(projection).iterator()){
			if (!result.hasNext()){
                response.setError(MongoDBConnector.FILE_NOT_FOUND);
                return response;
            }
			
			Document doc = result.next();
			FileInfo refFileInfo = new FileInfo();
			refFileInfo.fileId = doc.getLong("_id");
			refFileInfo.numberOfChunks = doc.getInteger("numberOfChunks");
		
            mapRefFiles.put(fileId, refFileInfo);
            response.setError(MongoDBConnector.SUCCESS);
            response.setFileInfo(refFileInfo);
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

    //upload
    public GetRefFileInfoResponse getRefFileInfo(FileInfo fileInfo) {
//        System.out.println("FileInfoCollection::getRefFileInfo checksumSHA2=" + fileInfo.checksumSHA2);
        GetRefFileInfoResponse response = new GetRefFileInfoResponse();
        response.fileInfo = new FileInfo();

		Bson filter = and(eq("checksumSHA2", fileInfo.checksumSHA2), eq("fileSize", fileInfo.fileSize));
		Bson projection = include("_id", "fileStatus", "chunkSize", "numberOfChunks", "numberOfUploadedChunks");
			
		try(MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator()) {
			while (cursor.hasNext()){
				Document doc = cursor.next();
				if (doc.getInteger("fileStatus") == TFileStatus.FS_UPLOADED.getValue()){
					response.fileInfo.fileId = doc.getLong("_id");
					response.fileInfo.fileStatus = TFileStatus.FS_UPLOADED.getValue();
					response.fileInfo.numberOfChunks = doc.getInteger("numberOfChunks");
					response.fileInfo.numberOfUploadedChunks = doc.getInteger("numberOfUploadedChunks");
					break;
				}
				else
					if (doc.getInteger("chunkSize") == fileInfo.chunkSize){
						response.fileInfo.fileId = doc.getLong("_id");
						response.fileInfo.fileStatus = doc.getInteger("fileStatus");
						response.fileInfo.numberOfChunks = doc.getInteger("numberOfChunks");
						response.fileInfo.numberOfUploadedChunks = doc.getInteger("numberOfUploadedChunks");
					}
			}
			
			if (response.fileInfo.fileId > 0){
				BasicDBObject embeddedObject = new BasicDBObject()
						.append("fileId", fileInfo.fileId)
						.append("fileName", fileInfo.fileName)
						.append("ownerName", fileInfo.ownerName);
				Bson update = Updates.addToSet("files", embeddedObject);
				collection.updateOne(eq("_id", response.fileInfo.fileId), update);
			}
			
            response.setError(MongoDBConnector.SUCCESS);
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

    public synchronized TError addFile(FileInfo fileInfo) {
        System.out.println("FileInfoCollection::addFile fileId=" + fileInfo.fileId);
        try {
//			Document fileDocument = new Document("_id", fileInfo.fileId)
//					.append("fileName", fileInfo.fileName)
//					.append("ownerName", fileInfo.ownerName)
//					.append("checksumSHA2", fileInfo.checksumSHA2)
//					.append("checksumMD5", fileInfo.checksumMD5)
//					.append("fileSize", fileInfo.fileSize)
//					.append("chunkSize", fileInfo.chunkSize)
//					.append("numberOfChunks", fileInfo.numberOfChunks)
//					.append("numberOfUploadedChunks", fileInfo.numberOfUploadedChunks)
//					.append("downloadCount", fileInfo.downloadCount)
//					.append("fileStatus", fileInfo.fileStatus)
//					.append("startUploadingTime", fileInfo.startUploadingTime)
//					.append("endUploadingTime", fileInfo.endUploadingTime);
////					.append("files", fileInfo.files);
//			collection.insertOne(fileDocument);
			
			BasicDBObject embeddedObject = new BasicDBObject()
						.append("fileId", fileInfo.fileId)
						.append("fileName", fileInfo.fileName)
						.append("ownerName", fileInfo.ownerName);
			Bson update = Updates.combine(
					Updates.setOnInsert("fileName", fileInfo.fileName),
					Updates.setOnInsert("ownerName", fileInfo.ownerName),
					Updates.setOnInsert("checksumSHA2", fileInfo.checksumSHA2),
					Updates.setOnInsert("checksumMD5", fileInfo.checksumMD5),
					Updates.setOnInsert("fileSize", fileInfo.fileSize),
					Updates.setOnInsert("chunkSize", fileInfo.chunkSize),
					Updates.setOnInsert("numberOfChunks", fileInfo.numberOfChunks),
					Updates.setOnInsert("numberOfUploadedChunks", fileInfo.numberOfUploadedChunks),
					Updates.setOnInsert("downloadCount", fileInfo.downloadCount),
					Updates.setOnInsert("fileStatus", fileInfo.fileStatus),
					Updates.setOnInsert("startUploadingTime", fileInfo.startUploadingTime),
					Updates.setOnInsert("endUploadingTime", fileInfo.endUploadingTime),
					Updates.addToSet("files", embeddedObject)
			);
			
			collection.updateOne(eq("_id", fileInfo.fileId), update, MongoDBConnector.upsertOptions);
            return MongoDBConnector.SUCCESS;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
            TError errorEx = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
            return errorEx;
        }
    }

    public TError addChunk(long fileId) {
        System.out.println("FileInfoCollection::addChunk fileId=" + fileId);
        try {
			Bson filter = eq("files.fileId", fileId);
			Bson update = inc("numberOfUploadedChunks", 1);
            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() == 0) {
                return MongoDBConnector.FILE_NOT_FOUND;
            }
            return MongoDBConnector.SUCCESS;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
            TError errorEx = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
            return errorEx;
        }
    }

    public TError finishUpload(long fileId) {
        System.out.println("FileInfoCollection::finishUpload fileId=" + fileId);
		Bson filter = eq("files.fileId", fileId);
		try (MongoCursor<Document> result = collection.find(filter).iterator()){
			if (!result.hasNext())
				return MongoDBConnector.FILE_NOT_FOUND;
			
			Document doc = result.next();
            if (doc.getInteger("fileStatus") != TFileStatus.FS_UPLOADING.getValue())
                return MongoDBConnector.SUCCESS;
			
			int numberOfUploadedChunks = doc.getInteger("numberOfUploadedChunks");
			int numberOfChunks = doc.getInteger("numberOfChunks");

            if (numberOfUploadedChunks != numberOfChunks) {
                return MongoDBConnector.MISSING_CHUNK;
            }

			Bson update = combine(
					set("fileStatus", TFileStatus.FS_UPLOADED.getValue()), 
					set("endUploadingTime", System.currentTimeMillis())
			);
			collection.updateOne(filter, update);
            return MongoDBConnector.SUCCESS;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
            TError errorEx = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
            return errorEx;
        }
    }

    public TError deleteFile(long fileId) {
        System.out.println("FileInfoCollection::deleteFile fileId=" + fileId);
        try {
			Bson filter = eq("files.fileId", fileId);
			Bson update = pull("files", new BasicDBObject("fileId", fileId));
			Bson projection = include("files", "numberOfChunks");
			Document doc = collection.findOneAndUpdate(filter, update, MongoDBConnector.foauoReturnAfter.projection(projection));
			if (doc == null) {
                return MongoDBConnector.FILE_NOT_FOUND;
            }
			List<Document> embeddedDocs = (ArrayList<Document>)doc.get("files");
			if (embeddedDocs.isEmpty()){
				long originalFileId = doc.getLong("_id");
				int numberOfChunks = doc.getInteger("numberOfChunks");
				ChunksCollection.getInstance().deleteChunks(originalFileId, numberOfChunks);
				Bson filters = and(eq("_id", originalFileId), size("files", 0));
				collection.deleteOne(filters);
			}
            return MongoDBConnector.SUCCESS;
        } catch (Exception ex) {
            ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
            TError errorEx = new TError(TErrorCode.EC_MONGODB_CONNECTOR_ERROR.getValue(), ex.toString());
            return errorEx;
        }
    }

    //download
    public GetFileInfoResponse getFileInfo(long fileId) {
        System.out.println("FileInfoCollection::getFileInfo fileId=" + fileId);
        GetFileInfoResponse response = new GetFileInfoResponse();
		response.fileInfo = new FileInfo();
        
		Bson filter = eq("files.fileId", fileId);
//		Bson projection = Projections.elemMatch("files", filter);
//		try (MongoCursor<Document> result = collection.find(filter).projection(projection).iterator()){
		try (MongoCursor<Document> result = collection.find(filter).iterator()){
			if (!result.hasNext())
				response.setError(MongoDBConnector.FILE_NOT_FOUND);
			else {
				Document fileDocument = result.next();
//				response.fileInfo.fileId = fileDocument.getLong("_id");
//				response.fileInfo.fileName = fileDocument.getString("fileName");
//				response.fileInfo.ownerName = fileDocument.getString("ownerName");
				response.fileInfo.checksumSHA2 = fileDocument.getString("checksumSHA2");
				response.fileInfo.checksumMD5 = fileDocument.getString("checksumMD5");
				response.fileInfo.fileSize = fileDocument.getLong("fileSize");
				response.fileInfo.chunkSize = fileDocument.getInteger("chunkSize");
				response.fileInfo.numberOfChunks = fileDocument.getInteger("numberOfChunks");
				response.fileInfo.numberOfUploadedChunks = fileDocument.getInteger("numberOfUploadedChunks");
				response.fileInfo.downloadCount = fileDocument.getInteger("downloadCount");
				response.fileInfo.fileStatus = fileDocument.getInteger("fileStatus");
				response.fileInfo.startUploadingTime = fileDocument.getLong("startUploadingTime");
				response.fileInfo.endUploadingTime = fileDocument.getLong("endUploadingTime");
				
				List<Document> docs = (ArrayList<Document>) fileDocument.get("files");
				for (Document doc: docs){
					if (doc.getLong("fileId") == fileId){
						response.fileInfo.fileId = doc.getLong("fileId");
						response.fileInfo.fileName = doc.getString("fileName");
						response.fileInfo.ownerName = doc.getString("ownerName");
						break;
					}
				}
				
				if (response.fileInfo.fileStatus == TFileStatus.FS_UPLOADING.getValue()) {
                    if (response.fileInfo.numberOfUploadedChunks == response.fileInfo.numberOfChunks) {
						Bson update = combine(
								set("fileStatus", TFileStatus.FS_UPLOADED.getValue()), 
								set("endUploadingTime", System.currentTimeMillis()));
						collection.updateOne(filter, update);						
                    } else {
                        // To-do: calculate next chunk number
                        response.fileInfo = new ExtendedFileInfo(response.fileInfo, 1);
                    }
                }
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

    public GetFileInfosResponse getFileInfos(List<Long> fileIds) {
        System.out.println("FileInfoCollection::getFileInfos");
        GetFileInfosResponse response = new GetFileInfosResponse();
        try {
            response.fileInfos = new ArrayList<>();
            GetFileInfoResponse getFileInfoResponse;
            for (long fileId : fileIds) {
                getFileInfoResponse = getFileInfo(fileId);
                if (getFileInfoResponse.error.errorCode == TErrorCode.EC_OK.getValue()) {
                    response.fileInfos.add(getFileInfoResponse.fileInfo);
                }
            }
            response.setError(MongoDBConnector.SUCCESS);
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
