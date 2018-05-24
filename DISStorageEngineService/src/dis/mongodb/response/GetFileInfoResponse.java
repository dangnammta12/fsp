/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.response;

import dis.model.FileInfo;
import dis.model.TError;

/**
 *
 * @author longmd
 */
public class GetFileInfoResponse {
	public TError error;
	public FileInfo fileInfo;

	public void setError(TError error) {
		this.error = error;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
}
