/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.model;

/**
 *
 * @author longmd
 */
public class ExtendedFileInfo extends FileInfo{
	public int nextUploadChunkNumber = -1;

	public ExtendedFileInfo(FileInfo fileInfo, int nextUploadChunkNumber) {
		this.fileId = fileInfo.fileId;
		this.fileName = fileInfo.fileName;
		this.ownerName = fileInfo.ownerName;
		this.checksumSHA2 = fileInfo.checksumSHA2;
		this.checksumMD5 = fileInfo.checksumMD5;
		this.fileSize = fileInfo.fileSize;
		this.chunkSize = fileInfo.chunkSize;
		this.numberOfChunks = fileInfo.numberOfChunks;
		this.numberOfUploadedChunks = fileInfo.numberOfUploadedChunks;
		this.downloadCount = fileInfo.downloadCount;
		this.fileStatus = fileInfo.fileStatus;
		this.startUploadingTime = fileInfo.startUploadingTime;
		this.endUploadingTime = fileInfo.endUploadingTime;
		this.nextUploadChunkNumber = nextUploadChunkNumber;
	}	
}
