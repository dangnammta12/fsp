/* 
 * File:   ServerApp.h
 * Author: longmd
 *
 * Created on February 23, 2012, 6:18 PM
 */

#ifndef SERVERAPP_H
#define	SERVERAPP_H

#include <vector>
#include <string>
#include <iostream>
#include "Poco/Util/ServerApplication.h"
#include "Poco/Util/HelpFormatter.h"
#include "Poco/NumberParser.h"
#include "FCore/Utility/ServiceConf/FConfKeeper.h"

class ServerApp : public Poco::Util::ServerApplication {
public:
	ServerApp(void);
	ServerApp(const ServerApp& orig);
	virtual ~ServerApp(void);

	void handleShowHelp(const std::string& name, const std::string& value) {
		_showHelp = true;
		Poco::Util::HelpFormatter helpFormatter(options());
		helpFormatter.setCommand(commandName());

		helpFormatter.setUsage("OPTIONS");
		helpFormatter.setHeader("Cac tham so ");
		helpFormatter.format(std::cout);
	}

	void defineOptions(Poco::Util::OptionSet& options);

	void initialize(Poco::Util::Application& application);

	virtual int main(const std::vector<std::string>& args);

private:
	bool _showHelp;
	FCore::Utility::ServiceConf::FConfKeeper* _ckServers;
};

#endif	/* SERVERAPP_H */

