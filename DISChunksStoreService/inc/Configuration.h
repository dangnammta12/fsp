/* 
 * File:   Configuration.h
 * Author: Pham Minh Tuan
 *
 * Created on April 26, 2012, 1:15 PM
 */

#ifndef CONFIGURATION_H
#define	CONFIGURATION_H

#include <string>
#include "Poco/Util/Application.h"
#include "TClientTypes.h"
#include "FCore/Utility/ServiceConf/FConfKeeper.h"

class Configuration {
public:
	static void initialize(Poco::Util::Application& application, FCore::Utility::ServiceConf::FConfKeeper* pCKServers = NULL);

public:
	static FCore::Utility::ServiceConf::FConfKeeper* _pCKServers;
};

#endif	/* CONFIGURATION_H */
