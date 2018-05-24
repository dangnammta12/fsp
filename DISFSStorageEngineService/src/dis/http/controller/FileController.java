/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.http.controller;

import dis.configuration.Configuration;
import dis.model.TError;
import dis.model.TErrorCode;
import dis.model.FileInfo;
import dis.http.response.HttpAddFileResponse;
import dis.http.response.DataResponse;
import dis.mongodb.mca.CountersCollection;
import dis.mongodb.mca.FilesCollection;
import dis.mongodb.response.GetFileInfoResponse;
import dis.mongodb.response.GetFileInfosResponse;
import dis.mongodb.response.GetNextValueResponse;
import dis.mongodb.response.GetRefFileInfoResponse;
import dis.model.TFileStatus;
import dis.mongodb.mca.ExceptionsCollection;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.util.List;

/**
 *
 * @author longmd
 */
public class FileController extends Controller {

	public FileController() {
        rootPath = "/file";
    }
	
	@RouteInfo(method = "get,post", path = "/addFile")
	public Route addFile() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try{
				FileInfo fileInfo = new FileInfo();
				fileInfo.fileName = ServletUtil.getStringParameter(request, "fileName", "");
				fileInfo.ownerName = ServletUtil.getStringParameter(request, "ownerName", "");
				fileInfo.checksumSHA2 = ServletUtil.getStringParameter(request, "sha2", "");
				fileInfo.checksumMD5 = ServletUtil.getStringParameter(request, "md5", "");
				fileInfo.fileSize = ServletUtil.getLongParameter(request, "filesize");
				fileInfo.chunkSize = ServletUtil.getIntParameter(request, "chunksize");
				fileInfo.numberOfChunks = ServletUtil.getIntParameter(request, "nbchunks");

				if (fileInfo.fileName.isEmpty()
						|| fileInfo.ownerName.isEmpty()
						|| fileInfo.checksumSHA2.isEmpty()
						|| fileInfo.checksumMD5.isEmpty()
						|| fileInfo.fileSize <= 0
						|| fileInfo.chunkSize <= 0
						|| fileInfo.numberOfChunks <= 0) {
					return DataResponse.PARAM_ERROR;
				}

				//get new fileId
				GetNextValueResponse getNextValueResponse = CountersCollection.getInstance().getNextValue(Configuration.FILE_ID_COUNTER_KEY);
				if (getNextValueResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(getNextValueResponse.error.errorCode, getNextValueResponse.error.errorMessage);
				}
				fileInfo.fileId = getNextValueResponse.fileId;

				//Find the reference FileInfo with the same checksum
				GetRefFileInfoResponse getRefFileInfoResponse = FilesCollection.getInstance().getRefFileInfo(fileInfo);
				if (getRefFileInfoResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(getRefFileInfoResponse.error.errorCode, getRefFileInfoResponse.error.errorMessage);
				}

				if (getRefFileInfoResponse.fileInfo.fileId > 0) {
					return new DataResponse(new HttpAddFileResponse(fileInfo.fileId, getRefFileInfoResponse.fileInfo.fileStatus, 1));
					//todo: recalcul nextUploadChunkNumber
				}

				//new file
				fileInfo.fileStatus = TFileStatus.FS_UPLOADING.getValue();
				fileInfo.startUploadingTime = System.currentTimeMillis();
		//        fileInfo.files.add(new EmbeddedInfo(fileInfo.fileId, fileInfo.fileName, fileInfo.ownerName));

				//push to mongodb
				TError error = FilesCollection.getInstance().addFile(fileInfo);

				if (error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(error.errorCode, error.errorMessage);
				}
				return new DataResponse(new HttpAddFileResponse(fileInfo.fileId, fileInfo.fileStatus, 1));
			}
			catch (Exception ex){
				ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			
				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
	}

	@RouteInfo(method = "get,post", path = "/finishUpload")
	public Route finishUpload() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try{
				long fileId = ServletUtil.getLongParameter(request, "fileid");
				if (fileId <= 0) {
					return DataResponse.PARAM_ERROR;
				}
				TError error = FilesCollection.getInstance().finishUpload(fileId);
				return new DataResponse(error.errorCode, error.errorMessage);
			}
			catch (Exception ex){
				ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			
				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
	}

	@RouteInfo(method = "get,post", path = "/deleteFile")
	public Route deleteFile() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try{
				long fileId = ServletUtil.getLongParameter(request, "fileid");
				if (fileId < 0) {
					return DataResponse.PARAM_ERROR;
				}

				//Find the reference FileInfo with the same checksum
				TError error = FilesCollection.getInstance().deleteFile(fileId);
				return new DataResponse(error.errorCode, error.errorMessage);
			}
			catch (Exception ex){
				ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			
				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
	}

	@RouteInfo(method = "get,post", path = "/getFile")
	public Route getFile() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try{
				Long fileId = ServletUtil.getLongParameter(request, "fileid");
				if (fileId <= 0) {
					return DataResponse.PARAM_ERROR;
				}

				GetFileInfoResponse getFileInfoResponse = FilesCollection.getInstance().getFileInfo(fileId);
				if (getFileInfoResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(getFileInfoResponse.error.errorCode, getFileInfoResponse.error.errorMessage);
				}
				return new DataResponse(getFileInfoResponse.fileInfo);
			}
			catch (Exception ex){
				ExceptionsCollection.getInstance().addException(
					Configuration.SERVICE_NAME,
					ex.getStackTrace()[0].toString(),
					ex.toString());
			
				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
    }

	@RouteInfo(method = "get,post", path = "/getFiles")
	public Route getFiles() {
        return (Request request, Response response) -> {
			response.header("Content-Type", "application/json");
			try{
				List<Long> fileIds = ServletUtil.getListLongParameter(request, "fileids", ",");
				if (fileIds == null) {
					return DataResponse.PARAM_ERROR;
				}

				GetFileInfosResponse getFileInfosResponse = FilesCollection.getInstance().getFileInfos(fileIds);
				if (getFileInfosResponse.error.errorCode != TErrorCode.EC_OK.getValue()) {
					return new DataResponse(getFileInfosResponse.error.errorCode, getFileInfosResponse.error.errorMessage);
				}
				return new DataResponse(getFileInfosResponse.fileInfos);
			}
			catch (Exception ex){
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
