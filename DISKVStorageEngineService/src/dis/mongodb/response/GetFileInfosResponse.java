/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.response;

import dis.model.FileInfo;
import dis.model.TError;
import java.util.List;

/**
 *
 * @author longmd
 */
public class GetFileInfosResponse {
	public TError error;
	public List<FileInfo> fileInfos;

	public void setError(TError error) {
		this.error = error;
	}

	public void setFileInfos(List<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}
}
