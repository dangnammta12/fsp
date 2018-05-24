/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.app;

import configuration.Configuration;
import crdhn.fsp.transport.FLogServiceClient;

/**
 *
 * @author longmd
 */
public class ServerApp {

	public static void main(String[] args) {
		try {
			FCore.Thrift.TNonblockingServer server = new FCore.Thrift.TNonblockingServer(Configuration._servicePort, Configuration._serviceWorkerCount, Types.handler, Types.processor);
			server.initialize();
			//init workers
			for (int i=1; i<=Configuration._workerNumber; i++) {
				new Worker(i).start();
				System.out.println("Woker :" + i);
			}
			System.out.println("Successful start " + Configuration._serviceName);
		}
		catch (Exception ex) {
			FLogServiceClient.getInstance().printSystemLog("[" + Configuration._serviceName + "] ServerApp::main(): " + ex.toString());
		}
	}
}
