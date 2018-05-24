/* 
 * Chunk:   ServerHandler.h
 * Author: longmd
 *
 * Created on November 20, 2014, 4:26 PM
 */

#ifndef SERVERHANDLER_H
#define	SERVERHANDLER_H

#include "Types.h"
#include "ServiceModel.h"

using namespace DIS;

template < class ServiceModelT >
class ServerHandler : public ThriftIfaceT {
public:

	ServerHandler(ServiceModelT* pServiceModel) : _pServiceModel(pServiceModel) {
	}

	~ServerHandler(void) {
	}
	
	bool add(const int64_t fileId, const int32_t chunkNumber, const std::string& chunkData){
		stringstream ss;
		ss << fileId << "-" << chunkNumber;
		if (_pServiceModel)
			return _pServiceModel->add(ss.str(), chunkData);
		return false;
	}
	
	bool remove(const int64_t fileId, const int32_t chunkNumber){
		stringstream ss;
		ss << fileId << "-" << chunkNumber;
		if (_pServiceModel)
			return _pServiceModel->remove(ss.str());
		return false;
	}
	
	void get(std::string& _return, const int64_t fileId, const int32_t chunkNumber){
		stringstream ss;
		ss << fileId << "-" << chunkNumber;
		if (_pServiceModel)
			_pServiceModel->get(_return, ss.str());
	}

private:
	ServiceModelT* _pServiceModel;
};

#endif	/* SERVERHANDLER_H */

