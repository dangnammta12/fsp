/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.app;

import configuration.Configuration;
import crdhn.fsp.model.EmailSender;
import crdhn.fsp.model.RequestObj;
import crdhn.fsp.thrift.TMSErrorCode;
import crdhn.fsp.transport.FLogServiceClient;

/**
 *
 * @author longmd
 */
public class Worker extends Thread {

	private final int _workerNumber;

	public Worker(int workerNumber) {
		_workerNumber = workerNumber;
	}

	@Override
	public void run() {
		while (true) {
			try {
				RequestObj reqs = EmailSender.getQueue();
				if (reqs != null) {
					if ((EmailSender.getInstance().sendMailTo(reqs.getRecipients(), reqs.getSubject(), reqs.getContent()))
							== TMSErrorCode.FP_MAILSENDER_OK) {
						System.out.println("Send email successful");
					}
					else
						System.out.println("Send mail fail");
				}
				Thread.sleep(Configuration._workerWaitingTime);
			}
			catch (Exception ex) {
				StackTraceElement traceElement = ex.getStackTrace()[0];
				FLogServiceClient.getInstance().printSystemLog("[" + Configuration._serviceName + "] " + this.getClass().getSimpleName() + "." + traceElement.getMethodName() + "(): " + ex.getMessage());
			}
		}
	}
}
