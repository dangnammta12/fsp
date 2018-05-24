#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux
CND_DLIB_EXT=so
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/Configuration.o \
	${OBJECTDIR}/src/ServerApp.o \
	${OBJECTDIR}/src/ServiceModel.o \
	${OBJECTDIR}/src/main.o \
	${OBJECTDIR}/thrift/gen-cpp/TDISChunksStoreService.o \
	${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_constants.o \
	${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_types.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=../corelibs/FUtility/lib/libfutility.a ../corelibs/FThrift/lib/libfthrift.a ../corelibs/FPoco/lib/libfpoco.a ../corelibs/FEvent/lib/libfevent.a ../corelibs/FZ/lib/libfz.a ../corelibs/FOpenSSL/libs/libssl.a ../corelibs/FOpenSSL/libs/libcrypto.a ../corelibs/FKVDatabase/lib/libfkvdatabase.a -lpthread -ldl -lrt

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk bin/dischunksstoreservice

bin/dischunksstoreservice: ../corelibs/FUtility/lib/libfutility.a

bin/dischunksstoreservice: ../corelibs/FThrift/lib/libfthrift.a

bin/dischunksstoreservice: ../corelibs/FPoco/lib/libfpoco.a

bin/dischunksstoreservice: ../corelibs/FEvent/lib/libfevent.a

bin/dischunksstoreservice: ../corelibs/FZ/lib/libfz.a

bin/dischunksstoreservice: ../corelibs/FOpenSSL/libs/libssl.a

bin/dischunksstoreservice: ../corelibs/FOpenSSL/libs/libcrypto.a

bin/dischunksstoreservice: ../corelibs/FKVDatabase/lib/libfkvdatabase.a

bin/dischunksstoreservice: ${OBJECTFILES}
	${MKDIR} -p bin
	${LINK.cc} -o bin/dischunksstoreservice ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/src/Configuration.o: src/Configuration.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/Configuration.o src/Configuration.cpp

${OBJECTDIR}/src/ServerApp.o: src/ServerApp.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/ServerApp.o src/ServerApp.cpp

${OBJECTDIR}/src/ServiceModel.o: src/ServiceModel.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/ServiceModel.o src/ServiceModel.cpp

${OBJECTDIR}/src/main.o: src/main.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/main.o src/main.cpp

${OBJECTDIR}/thrift/gen-cpp/TDISChunksStoreService.o: thrift/gen-cpp/TDISChunksStoreService.cpp 
	${MKDIR} -p ${OBJECTDIR}/thrift/gen-cpp
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/thrift/gen-cpp/TDISChunksStoreService.o thrift/gen-cpp/TDISChunksStoreService.cpp

${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_constants.o: thrift/gen-cpp/dischunksstoreservice_constants.cpp 
	${MKDIR} -p ${OBJECTDIR}/thrift/gen-cpp
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_constants.o thrift/gen-cpp/dischunksstoreservice_constants.cpp

${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_types.o: thrift/gen-cpp/dischunksstoreservice_types.cpp 
	${MKDIR} -p ${OBJECTDIR}/thrift/gen-cpp
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_types.o thrift/gen-cpp/dischunksstoreservice_types.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} bin/dischunksstoreservice

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
