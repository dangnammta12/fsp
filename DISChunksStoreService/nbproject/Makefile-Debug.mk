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
CND_CONF=Debug
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
LDLIBSOPTIONS=../corelibs/FUtility/lib/libfutilityd.a ../corelibs/FThrift/lib/libfthriftd.a ../corelibs/FPoco/lib/libfpocod.a ../corelibs/FEvent/lib/libfeventd.a ../corelibs/FZ/lib/libfzd.a ../corelibs/FOpenSSL/libs/libssld.a ../corelibs/FOpenSSL/libs/libcryptod.a ../corelibs/FKVDatabase/lib/libfkvdatabased.a -lpthread -ldl -lrt

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk bin/dischunksstoreserviced

bin/dischunksstoreserviced: ../corelibs/FUtility/lib/libfutilityd.a

bin/dischunksstoreserviced: ../corelibs/FThrift/lib/libfthriftd.a

bin/dischunksstoreserviced: ../corelibs/FPoco/lib/libfpocod.a

bin/dischunksstoreserviced: ../corelibs/FEvent/lib/libfeventd.a

bin/dischunksstoreserviced: ../corelibs/FZ/lib/libfzd.a

bin/dischunksstoreserviced: ../corelibs/FOpenSSL/libs/libssld.a

bin/dischunksstoreserviced: ../corelibs/FOpenSSL/libs/libcryptod.a

bin/dischunksstoreserviced: ../corelibs/FKVDatabase/lib/libfkvdatabased.a

bin/dischunksstoreserviced: ${OBJECTFILES}
	${MKDIR} -p bin
	${LINK.cc} -o bin/dischunksstoreserviced ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/src/Configuration.o: src/Configuration.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/Configuration.o src/Configuration.cpp

${OBJECTDIR}/src/ServerApp.o: src/ServerApp.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/ServerApp.o src/ServerApp.cpp

${OBJECTDIR}/src/ServiceModel.o: src/ServiceModel.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/ServiceModel.o src/ServiceModel.cpp

${OBJECTDIR}/src/main.o: src/main.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/main.o src/main.cpp

${OBJECTDIR}/thrift/gen-cpp/TDISChunksStoreService.o: thrift/gen-cpp/TDISChunksStoreService.cpp 
	${MKDIR} -p ${OBJECTDIR}/thrift/gen-cpp
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/thrift/gen-cpp/TDISChunksStoreService.o thrift/gen-cpp/TDISChunksStoreService.cpp

${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_constants.o: thrift/gen-cpp/dischunksstoreservice_constants.cpp 
	${MKDIR} -p ${OBJECTDIR}/thrift/gen-cpp
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_constants.o thrift/gen-cpp/dischunksstoreservice_constants.cpp

${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_types.o: thrift/gen-cpp/dischunksstoreservice_types.cpp 
	${MKDIR} -p ${OBJECTDIR}/thrift/gen-cpp
	${RM} "$@.d"
	$(COMPILE.cc) -g -DHAVE_CONFIG_H -I../corelibs/FThrift/inc -I../corelibs/FPoco/inc -I../corelibs/FUtility/inc -I../corelibs/FUtility/thrift -Iinc -Ithrift/gen-cpp -I../corelibs/FBoost/inc -I../corelibs/FEvent/inc -I../corelibs/FOpenSSL/inc -I../corelibs/FZ/inc -I../corelibs/FKVDatabase/inc -I../corelibs/FKVDatabase/inc/kyotocabinet -I../corelibs/FKVDatabase/inc/leveldb -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/thrift/gen-cpp/dischunksstoreservice_types.o thrift/gen-cpp/dischunksstoreservice_types.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} bin/dischunksstoreserviced

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
