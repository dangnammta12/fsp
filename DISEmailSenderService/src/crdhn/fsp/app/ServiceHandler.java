/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.app;

import crdhn.fsp.model.EmailSender;
import org.apache.thrift.TException;

/**
 *
 * @author longmd
 */
public class ServiceHandler implements Types.ThriftIfaceT {
	
	@Override
	public int sendMail(String recipient, String subject, String content) throws TException {
		try{
			return EmailSender.getInstance().sendMail(recipient, subject, content);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	
	
}
