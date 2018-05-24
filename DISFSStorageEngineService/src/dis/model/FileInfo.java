/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author longmd
 */
public class FileInfo {

	public long fileId;
	public String fileName;
	public String ownerName;
	public String checksumSHA2;
	public String checksumMD5;
	public long fileSize;
	public int chunkSize;
	public int numberOfChunks;
	public int numberOfUploadedChunks;
	public int downloadCount;
	public int fileStatus;
	public long startUploadingTime;
	public long endUploadingTime;

	public FileInfo() {
		fileId = -1L;
		fileName = "";
		ownerName = "";
		checksumSHA2 = "";
		checksumMD5 = "";
		fileSize = -1L;
		chunkSize = -1;
		numberOfChunks = -1;
		numberOfUploadedChunks = 0;
		downloadCount = 0;
		fileStatus = 0;
		startUploadingTime = -1L;
		endUploadingTime = -1L;
	}
}
