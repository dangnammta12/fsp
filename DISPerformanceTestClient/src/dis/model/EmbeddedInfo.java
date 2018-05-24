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
public class EmbeddedInfo {
	public Long fileId;
	public String fileName;
	public String ownerName;
	
	public EmbeddedInfo() {
		this.fileId = -1L;
		this.fileName = "";
		this.ownerName = "";
	}

	public EmbeddedInfo(Long fileId, String fileName, String ownerName) {
		this.fileId = fileId;
		this.fileName = fileName;
		this.ownerName = ownerName;
	}
}
