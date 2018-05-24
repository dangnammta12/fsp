/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.fsp.transport;

import FCore.Thrift.ClientFactory;
import FCore.Thrift.TClient;
import configuration.Config;
import crdhn.common.log.thrift.TFLogService;
import org.apache.thrift.protocol.TCompactProtocol;

/**
 *
 * @author longmd
 */
public class FLogServiceClient {

	private static FLogServiceClient _instance = new FLogServiceClient();
	private static String _host = "";
	private static int _port = -1;
	private static String _systemLog = "";
	private static boolean _enable = true;

	static {
		_host = Config.getParamString("external", "flogservice_host", "");
		_port = Config.getParamInt("external", "flogservice_port");
		_systemLog = Config.getParamString("external", "flogservice_systemlog", "SystemLog");
		_enable = Config.getParamInt("external", "flogservice_enable") > 0;
	}

	public static FLogServiceClient getInstance() {
		return _instance;
	}

	public TClient getClientWrapper() {
		TClient clientWrapper = ClientFactory.getClient(_host, _port, 7200, TFLogService.Client.class, TCompactProtocol.class);
		if (clientWrapper != null) {
			if (!clientWrapper.sureOpen()) {
				return null;
			}
		}
		return clientWrapper;
	}

	public void printSystemLog(String message) {
		printLog(_systemLog, message);
	}

	public void printLog(String category, String message) {
		if (!_enable)
			return;
		TClient clientWrapper = getClientWrapper();
		if (clientWrapper != null) {
			TFLogService.Client aClient = (TFLogService.Client) clientWrapper.getClient();
			if (aClient != null) {
				try {
					aClient.printLog(category, message);
				}
				catch (Exception ex1) {
					clientWrapper.close();
					clientWrapper = getClientWrapper();
					aClient = (TFLogService.Client) clientWrapper.getClient();
					try {
						aClient.printLog(category, message);
					}
					catch (Exception ex2) {
						System.err.println("[Exception] FLogServiceClient.printLog():" + ex2.getMessage());
					}
				}
				ClientFactory.releaseClient(clientWrapper);
			}
		}
		else {
			System.err.println("[Exception] FLogServiceClient.printLog(): clientWrapper is NULL");
		}
	}

	public void printLog_ow(String category, String message) {
		if (!_enable)
			return;
		TClient clientWrapper = getClientWrapper();
		if (clientWrapper != null) {
			TFLogService.Client aClient = (TFLogService.Client) clientWrapper.getClient();
			if (aClient != null) {
				try {
					aClient.printLog_ow(category, message);
				}
				catch (Exception ex1) {
					clientWrapper.close();
					clientWrapper = getClientWrapper();
					aClient = (TFLogService.Client) clientWrapper.getClient();
					try {
						aClient.printLog_ow(category, message);
					}
					catch (Exception ex2) {
						System.err.println("[Exception] FLogServiceClient.printLog_ow():" + ex2.getMessage());
					}
				}
				ClientFactory.releaseClient(clientWrapper);
			}
		}
		else {
			System.err.println("[Exception] FLogServiceClient.printLog_ow(): clientWrapper is NULL");
		}
	}
}
