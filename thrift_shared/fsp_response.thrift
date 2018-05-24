include "fsp_shared.thrift"
namespace java crdhn.fsp.thrift.response
namespace cpp CRDHN.FSP.Response

enum TErrorCode {
	EC_OK = 0;
	EC_SYSTEM = 1;
	EC_PARAM_ERROR = 2;
	EC_FILE_NOT_FOUND = 3;
	EC_CHUNK_NOT_FOUND = 4;
	EC_CHUNK_EXISTED = 5;
	EC_MISSING_CHUNK = 6;

	EC_MONGODB_CONNECTOR_ERROR = 20;
	EC_COUNTER_ERROR = 21;
}

struct TError{
	1: required i32 errorCode = TErrorCode.EC_OK;
	2: optional string errorMessage;
}

struct TSCGetNextValueResponse{
	1: required TError error;
	2: required fsp_shared.TFID fileId = -1;
}

struct TMDBGetFileResponse{
	1: required TError error;
	2: required fsp_shared.TFileInfo fileInfo;
}

struct TMDBGetChunkResponse{
	1: required TError error;
	2: required fsp_shared.TChunkInfo chunkInfo;
}
