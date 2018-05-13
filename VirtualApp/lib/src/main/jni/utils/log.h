//
// Created by sundayliu on 2017/6/15.
//

#ifndef CUSTOMVIRTUALAPP_LOG_H
#define CUSTOMVIRTUALAPP_LOG_H

#include <android/log.h>
#include <inttypes.h>

#ifdef __cplusplus
extern "C"{
#endif

void log_hex_dump(const uint8_t* addr, uint32_t size);

#ifdef __cplusplus
};
#endif

#define ENABLE_LOG

#ifdef ENABLE_LOG
extern const char* LOG_TAG;
#define DEBUG_LOG(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define ERROR_LOG(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOG_HEX_DUMP(data,size) log_hex_dump((data),(size))
#else
#define DEBUG_LOG(...) ((void)0)
#define ERROR_LOG(...) ((void)0)
#define LOG_HEX_DUMP(data,size) ((void)0)
#endif

#endif //CUSTOMVIRTUALAPP_LOG_H
