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
public class TError {
	public int errorCode;
	public String errorMessage;

	public TError(int errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = "";
	}

	public TError(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
