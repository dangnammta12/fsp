/* 
 * Chunk:   Types.h
 * Author: longmd
 *
 * Created on November 27, 2014, 4:09 PM
 */

#ifndef TYPES_H
#define	TYPES_H

#include <string>
#include "FCore/Utility/Data/SimpleSerializer.h"
#include "FCore/Utility/Data/NoneSerializer.h"
#include "FCore/Utility/ServiceConf/KVStoreFactory.h"
#include "FCore/Utility/Data/TSerializer.h"
#include "thrift/protocol/TCompactProtocol.h"
#include "FCore/Thrift/TNonblockingServer.h"
#include "FCore/Database/AbstractKVStore.h"
#include "TDISChunksStoreService.h"
#include "ModelData.h"

using namespace DIS;
using namespace std;

typedef string KeyT;
typedef TString ValueT;
typedef ModelData ModelDataT;

typedef apache::thrift::protocol::TCompactProtocol ThriftProtocolT;
typedef apache::thrift::protocol::TCompactProtocolFactory ThriftProtocolFactoryT;
typedef TDISChunksStoreServiceIf ThriftIfaceT;
typedef TDISChunksStoreServiceProcessor ThriftProcessorT;
typedef FCore::Thrift::TNonblockingServer<ThriftIfaceT, ThriftProcessorT, ThriftProtocolFactoryT> ThriftServerT;

typedef FCore::Utility::Data::NoneSerializer KeySerializerT;
typedef FCore::Utility::Data::TSerializer<ValueT, ThriftProtocolT> ValueSerializerT;
typedef FCore::Utility::ServiceConf::KVStoreFactory<KeyT, ModelDataT, KeySerializerT, ValueSerializerT> KVStoreFactoryT;
typedef FCore::Database::KVDataVisitor<KeyT, ModelDataT> KVDataVisitorT;

#endif	/* TYPES_H */

