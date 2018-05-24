/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.model;

import configuration.Configuration;
import crdhn.fsp.thrift.TMSErrorCode;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ddtech
 */
public class EmailSender extends EmailSenderAbs{

	private static EmailSender _instance = null;
	private final LinkedBlockingQueue<RequestObj> _queue = new LinkedBlockingQueue<>();
	private final String host = Configuration._ServiceMailServerHost;
	private final int port = Configuration._ServiceMailServerPort;
	private final String user = Configuration._ServiceMailServerAccount;
	private final String password = Configuration._ServiceMailServerPassword;
	private final String from = Configuration._ServiceMailServerAccount;

	public static EmailSender getInstance() {
		if (_instance == null) {
			_instance = new EmailSender();
		}
		return _instance;
	}
	
	public static void putQueue(RequestObj obj){
		EmailSender.getInstance()._queue.add(obj);
	}
	public static RequestObj getQueue(){
	 return	EmailSender.getInstance()._queue.poll();
	}
	
	@Override
	public TMSErrorCode sendMailTo(String[] recipient, String subject, String content) {
		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", password);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");

		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		};
		Session session = Session.getDefaultInstance(props, authenticator);
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[recipient.length];
			// To get the array of addresses
			for (int i = 0; i < recipient.length; i++) {
				toAddress[i] = new InternetAddress(recipient[i]);
			}
			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient[i]));
			}
			message.setSubject(subject);
			message.setContent( content, "text/html; charset=utf-8" );
			Transport transport = session.getTransport("smtp");
			transport.connect(from, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			System.out.println("Done");
			return TMSErrorCode.FP_MAILSENDER_OK;
		}
		catch (AddressException ae) {
			ae.printStackTrace();
			return TMSErrorCode.FP_MAILSENDER_SYSTEM_FAILED;
		}
		catch (MessagingException me) {
			me.printStackTrace();
			return TMSErrorCode.FP_MAILSENDER_SYSTEM_FAILED;
		}
	}
	
	@Override
	public int sendMail(String recipient, String subject, String content) {
		String[] recipientList = recipient.split(",");
		RequestObj req = new RequestObj(recipientList, subject, content);
		try {
			System.out.println(_queue);
			EmailSender.putQueue(req);
			return 0;
		}
		catch (Exception ex) {
			Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
			return 1;
		}
	}

}
