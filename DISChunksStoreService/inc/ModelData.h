/* 
 * Chunk:   ModelData.h
 * Author: longmd
 *
 * Created on December 9, 2014, 3:58 PM
 */

#ifndef MODELDATA_H
#define	MODELDATA_H

using namespace std;
using namespace DIS;

class ModelData {
public:
	TString _chunkData;

	ModelData(void) {
		clear();
	}

//	bool assignFrom(const string& chunkData) {
//		_chunkData = chunkData;
//		return true;
//	}
//
//	void assignTo(string& chunkData) const {
//		chunkData = _chunkData;
//	}
//
//	bool clear(void) {
//		_chunkData.clear();
//		return true;
//	}
	
	bool assignFrom(const TString& chunkData) {
		_chunkData.str = chunkData.str;
		return true;
	}

	void assignTo(TString& chunkData) const {
		chunkData.str = _chunkData.str;
	}

	bool clear(void) {
		_chunkData.str.clear();
		return true;
	}
};

#endif	/* MODELDATA_H */

