/* 
 * File:   ServerApp.cpp
 * Author: longmd
 * 
 * Created on February 23, 2012, 6:18 PM
 */

#include <string>
#include "FCore/Utility/ServiceConf/Properties.h"
#include "ServerHandler.h"
#include "ServerApp.h"
#include "Configuration.h"

using namespace std;

typedef ServiceModel ServiceModelT;
typedef ServerHandler<ServiceModelT> ServerHandlerT;

ServerApp::ServerApp(void) : _showHelp(false) {
}

ServerApp::~ServerApp(void) {
}

void ServerApp::initialize(Poco::Util::Application& application) {
	if (_showHelp) return;

	loadConfiguration();
	KVStoreFactoryT::initialize(application);
	FCore::Utility::ServiceConf::Properties::initialize(application);

//	try {
//		this->_ckServers = new FCore::Utility::ServiceConf::FConfKeeper();
//		this->_ckServers->setCKHosts(FCore::Utility::ServiceConf::Properties::_ckHosts);
//		CRDHN::FCore::Services::TEndpoint endpoint;
//		endpoint.host = FCore::Utility::ServiceConf::Properties::_serviceHost;
//		endpoint.port = FCore::Utility::ServiceConf::Properties::_servicePort;
//		endpoint.configPort = FCore::Utility::ServiceConf::Properties::_serviceConfigPort;
//		endpoint.name = FCore::Utility::ServiceConf::Properties::_serviceName;
//		if (FCore::Utility::ServiceConf::Properties::_ckProtocol.compare("THRIFT_COMPACT") == 0)
//			endpoint.protocol = CRDHN::FCore::Services::TEProtocol::EThriftCompactProcotol;
//		else if (FCore::Utility::ServiceConf::Properties::_ckProtocol.compare("THRIFT_BINARY") == 0)
//			endpoint.protocol = CRDHN::FCore::Services::TEProtocol::EThriftBinaryProcotol;
//		else if (FCore::Utility::ServiceConf::Properties::_ckProtocol.compare("HTTP") == 0)
//			endpoint.protocol = CRDHN::FCore::Services::TEProtocol::EHttp;
//		else endpoint.protocol = CRDHN::FCore::Services::TEProtocol::EUnknown;
//		endpoint.version = "1.0";
//		endpoint.api = "NULL";
//		endpoint.path = FCore::Utility::ServiceConf::Properties::_ckRegPath;
//		if (this->_ckServers->registerActiveService(endpoint)) {
//			logger().information("Registered with FConfKeeper...");
//		}
//	}
//	catch (...) {
//	}
//
//	if (FCore::Utility::ServiceConf::Properties::_ckHosts.size())
//		Configuration::initialize(application, this->_ckServers);
//	else 
		Configuration::initialize(application);

	boost::shared_ptr< ThriftIfaceT > handler = boost::shared_ptr< ThriftIfaceT >(new ServerHandlerT(new ServiceModelT()));
	boost::shared_ptr< ThriftProcessorT > processor = boost::shared_ptr< ThriftProcessorT >(new ThriftProcessorT(handler));
	ThriftServerT* server = new ThriftServerT(FCore::Utility::ServiceConf::Properties::_servicePort
			, FCore::Utility::ServiceConf::Properties::_serviceWorkerCount
			, handler, processor);
	this->addSubsystem(server);

	Poco::Util::ServerApplication::initialize(application);
}

int ServerApp::main(const vector<string>& args) {
	Poco::Util::ServerApplication::main(args);
	if (!_showHelp)
		waitForTerminationRequest();
	return 0;
}

void ServerApp::defineOptions(Poco::Util::OptionSet& options) {
	Poco::Util::ServerApplication::defineOptions(options);

	options.addOption(Poco::Util::Option("help", "help")
			.description("Show help messages")
			.callback(Poco::Util::OptionCallback<ServerApp>(this, &ServerApp::handleShowHelp)));
}

