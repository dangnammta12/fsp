package configuration;

public class Configuration {

	public static String _ServiceMailServerHost;
	public static int _ServiceMailServerPort;
	public static String _ServiceMailServerAccount;
	public static String _ServiceMailServerPassword;

	public static String _ServiceMailServerPrivateKeyFile;
	public static String _ServiceMailServerCertificateFile;
	public static String _ServiceMailServerCALocation;
	public static String _ServiceMailServerContentType;
	public static String _ServiceMailServerCipherList;

	public static String _serviceName;
	public static String _serviceHost;
	public static int _servicePort;
	public static int _serviceWorkerCount;
	public static int _dbTimeout;

	public static String _mongodbHost;
	public static int _mongodbPort;
	public static String _mongodbDatabaseName;

	public static int _workerWaitingTime;
	public static int _workerNumber;

	public static int _mongodbConnectTimeout;
	public static int _mongodbSocketTimeout;
	public static int _mongodbMaxWaitTime;
	public static int _mongodbServerSelectionTimeout;

	static void init() {
		_ServiceMailServerHost = Config.getParamString("external", "mailserver_host", "");
		_ServiceMailServerPort = Config.getParamInt("external", "mailserver_port");
		_ServiceMailServerAccount = Config.getParamString("external", "mailserver_account", "");
		_ServiceMailServerPassword = Config.getParamString("external", "mailserver_password", "");
		_ServiceMailServerContentType = Config.getParamString("external", "mailserver_contenttype", "");
		_ServiceMailServerCipherList = Config.getParamString("external", "mailserver_cipherlist", "");

		_serviceName = Config.getParamString("service", "name", "");
		_serviceHost = Config.getParamString("service", "host", "");
		_servicePort = Config.getParamInt("service", "port");
		
		_serviceWorkerCount = Config.getParamInt("workers", "worker_count");
		_workerWaitingTime = Config.getParamInt("workers", "waiting_time");
		_workerNumber = Config.getParamInt("workers", "number_of_workers");

		_mongodbHost = Config.getParamString("external", "mongodb_host", "");
		_mongodbPort = Config.getParamInt("external", "mongodb_port");
		_mongodbDatabaseName = Config.getParamString("external", "mongodb_databasename", "");
		_mongodbConnectTimeout = Config.getParamInt("external", "mongodb_connect_timeout");
		_mongodbSocketTimeout = Config.getParamInt("external", "mongodb_socket_timeout");
		_mongodbMaxWaitTime = Config.getParamInt("external", "mongodb_max_waittime");
		_mongodbServerSelectionTimeout = Config.getParamInt("external", "mongodb_server_selection_timeout");

	}

	static {
		init();
	}
}
