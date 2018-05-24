/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.response;

import dis.model.TError;

/**
 *
 * @author longmd
 */
public class GetNextValueResponse {
	public TError error;
	public long value;

	public void setError(TError error) {
		this.error = error;
	}

	public void setFileId(long value) {
		this.value = value;
	}
}
