/* 
 * Chunk:   ServiceModel.cpp
 * Author: longmd
 * 
 * Created on December 25, 2014, 9:28 AM
 */

#include "ServiceModel.h"

using namespace std;

bool ServiceModel::add(const std::string& chunkId, const std::string& chunkData){
	class data_visitor : public KVDataVisitorT {
	public:

		data_visitor(const std::string& chunkData): _chunkData(chunkData){
		}

		bool visit(const KVDataVisitorT::_KeyT& key, KVDataVisitorT::_ValueT& value) {
			TString tstr;
			tstr.str = _chunkData;
			return (_result = value.assignFrom(tstr));
		}
		
		const std::string& _chunkData;
		
		bool _result;
	};

	if (KVStoreFactoryT::size()) {
		data_visitor visitor(chunkData);
		KVStoreFactoryT::visit(chunkId, &visitor);
		return visitor._result;
	}
	return false;
}

bool ServiceModel::remove(const std::string& chunkId){
	class data_visitor : public KVDataVisitorT {
	public:

		data_visitor(void) : _result(false) {
		}

		bool visit(const KVDataVisitorT::_KeyT& key, KVDataVisitorT::_ValueT& value) {
			return (_result = value.clear());
		}

		bool _result;
	};

	if (KVStoreFactoryT::size()) {
		data_visitor visitor;
		KVStoreFactoryT::visit(chunkId, &visitor);
		return visitor._result;
	}
	return false;
}

void ServiceModel::get(std::string& response, const std::string& chunkId){
	class data_visitor : public KVDataVisitorT {
	public:

		data_visitor(std::string& response) : _response(response) {
		}

		bool visit(const KVDataVisitorT::_KeyT& key, KVDataVisitorT::_ValueT& value) {
			TString tstr;
			value.assignTo(tstr);
			_response = tstr.str;
			return false;
		}

		std::string& _response;
	};

	if (KVStoreFactoryT::size()) {
		data_visitor visitor(response);
		KVStoreFactoryT::visit(chunkId, &visitor);
		return;
	}
	response = "";
}