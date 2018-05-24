namespace cpp DIS
namespace java dis.thrift

struct TString{
	1: string str;
}

service TDISChunksStoreService {
	bool add(1: i64 fileId, 2: i32 chunkNumber, 3: string chunkData);
	bool remove(1: i64 fileId, 2: i32 chunkNumber);
	string get(1: i64 fileId, 2: i32 chunkNumber);
}