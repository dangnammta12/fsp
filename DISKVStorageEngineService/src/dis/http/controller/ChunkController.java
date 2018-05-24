/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.http.controller;

import dis.configuration.Configuration;
import dis.model.TError;
import dis.model.TErrorCode;
import dis.http.response.DataResponse;
import dis.kvstore.DISChunksStoreClient;
import dis.model.ChunkInfo;
import dis.mongodb.mca.ChunksCollection;
import dis.mongodb.mca.ExceptionsCollection;
import dis.mongodb.mca.FilesCollection;
import dis.mongodb.response.GetChunkInfoResponse;
import dis.mongodb.response.GetRefFileInfoResponse;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;

/**
 *
 * @author longmd
 */
public class ChunkController extends Controller {

    public ChunkController() {
        rootPath = "/chunk";
    }
	
	@RouteInfo(method = "get,post", path = "/addChunk")
	public Route addChunk() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try {
				ChunkInfo chunkInfo = new ChunkInfo();
				chunkInfo.fileId = ServletUtil.getLongParameter(request, "fileid");
				chunkInfo.chunkNumber = ServletUtil.getIntParameter(request, "chunknumber");
				chunkInfo.chunkData = ServletUtil.getStringParameter(request, "chunkdata");

				if (chunkInfo.fileId <= 0
						|| chunkInfo.chunkNumber <= 0
						|| chunkInfo.chunkData.isEmpty()) {
					return DataResponse.PARAM_ERROR;
				}

//				GetRefFileInfoResponse getRefFileInfoResponse = FilesCollection.getInstance().getRefFileInfo(chunkInfo.fileId);
//				if (getRefFileInfoResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
//					return new DataResponse(getRefFileInfoResponse.error.errorCode, getRefFileInfoResponse.error.errorMessage);
//				}
//				chunkInfo.fileId = getRefFileInfoResponse.fileInfo.fileId;
//				if (chunkInfo.chunkNumber > getRefFileInfoResponse.fileInfo.numberOfChunks) {
//					return new DataResponse(TErrorCode.EC_CHUNK_NOT_FOUND.getValue(), "ChunkNumber is out of range");
//				}

//				TError error = ChunksCollection.getInstance().addChunk(chunkInfo);
				TError error = DISChunksStoreClient.getInstance().addChunk(chunkInfo);
				if (error.errorCode == TErrorCode.EC_CHUNK_EXISTED.getValue()) {
					return DataResponse.SUCCESS;
				}

				if (error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(error.errorCode, error.errorMessage);
				}

//				error = FilesCollection.getInstance().addChunk(chunkInfo.fileId);
//				if (error.errorCode != TErrorCode.EC_OK.getValue()) {
//					return new DataResponse(error.errorCode, error.errorMessage);
//				}
				return DataResponse.SUCCESS;
			} catch (Exception ex) {
				ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			
				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
    }

	@RouteInfo(method = "get,post", path = "/getChunk")
	public Route getChunk() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try {
				long fileId = ServletUtil.getLongParameter(request, "fileid");
				int chunkNumber = ServletUtil.getIntParameter(request, "chunknumber");

				if (fileId <= 0 || chunkNumber <= 0) {
					return DataResponse.PARAM_ERROR;
				}

				GetRefFileInfoResponse getRefFileInfoResponse = FilesCollection.getInstance().getRefFileInfo(fileId);
				if (getRefFileInfoResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(getRefFileInfoResponse.error.errorCode, getRefFileInfoResponse.error.errorMessage);
				}

				if (chunkNumber > getRefFileInfoResponse.fileInfo.numberOfChunks) {
					return new DataResponse(TErrorCode.EC_CHUNK_NOT_FOUND.getValue(), "ChunkNumber is out of range");
				}

				GetChunkInfoResponse getChunkInfoResponse = ChunksCollection.getInstance().getChunk(getRefFileInfoResponse.fileInfo.fileId, chunkNumber);
				if (getChunkInfoResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(getChunkInfoResponse.error.errorCode, getChunkInfoResponse.error.errorMessage);
				} else {
					getChunkInfoResponse.chunkInfo.fileId = fileId;
					return new DataResponse(getChunkInfoResponse.chunkInfo);
				}
			} catch (Exception ex) {
				ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
    }
}
