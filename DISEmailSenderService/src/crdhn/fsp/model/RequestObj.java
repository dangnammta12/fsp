/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.model;

/**
 *
 * @author ddtech
 */
public class RequestObj {
	private String[] recipients;
	private String subject;
	private String content;
	
	public  RequestObj(){
		
	}
	
	public RequestObj(String[] recipients, String subject, String content ){
		this.content = content;
		this.recipients = recipients;
		this.subject = subject;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
