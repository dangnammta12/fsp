/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.model;

import crdhn.fsp.thrift.TMSErrorCode;

/**
 *
 * @author ddtech
 */
public abstract class EmailSenderAbs {
	public abstract TMSErrorCode sendMailTo(String recipient[], String subject, String content);
	public abstract int sendMail(String recipient, String subject, String content);
}
