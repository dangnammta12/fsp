/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.http.response;

/**
 *
 * @author longmd
 */
public class HttpAddFileResponse {
	public long fileId;
	public int fileStatus;
	public int nextUploadChunkNumber;

	public HttpAddFileResponse(long fileId, int fileStatus, int nextUploadChunkNumber) {
		this.fileId = fileId;
		this.fileStatus = fileStatus;
		this.nextUploadChunkNumber = nextUploadChunkNumber;
	}
}
