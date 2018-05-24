/* 
 * Chunk:   ServiceModel.h
 * Author: longmd
 *
 * Created on December 25, 2014, 9:28 AM
 */

#ifndef SERVICEMODEL_H
#define	SERVICEMODEL_H

#include "Types.h"

class ServiceModel {
public:
	bool add(const std::string& chunkId, const std::string& chunkData);
	bool remove(const std::string& chunkId);
	void get(std::string& response, const std::string& chunkId);
};

#endif	/* SERVICEMODEL_H */

