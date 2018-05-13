//
// Created by sundayliu on 2017/9/6.
//

#include "mono_profiler.h"
#include "utils/log.h"

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <dlfcn.h>
#include <errno.h>
#include "MSHook.h"

// eglib/src/glib.h
#include <stdint.h>
typedef int8_t gint8;
typedef uint8_t guint8;
typedef uint32_t guint32;
typedef float gfloat;
typedef double gdouble;
typedef int32_t gboolean;

struct _MonoImage
{
    int ref_count;
    void* raw_data_handle;
    char* raw_data;
    guint32 raw_data_len;

};

typedef _MonoImage MonoImage;

// mono/metadata/image.h
typedef enum
{
    MONO_IMAGE_OK,
    MONO_IMAGE_ERROR_ERRNO,
    MONO_IMAGE_MISSING_ASSEMBLYREF,
    MONO_IMAGE_IMAGE_INVALID
}MonoImageOpenStatus;

typedef void* (*PFN_MONO_ASSEMBLY_LOAD_FROM_FULL)(MonoImage* image, const char* fname, MonoImageOpenStatus* status, gboolean refonly);
PFN_MONO_ASSEMBLY_LOAD_FROM_FULL g_pfnOriginMonoAssemblyLoadFromFull = NULL;

typedef void* (*PFN_MONO_IMAGE_OPEN_FROM_DATA_WITH_NAME)(char* data, guint32 data_len, gboolean need_copy,MonoImageOpenStatus* status,gboolean refonly,const char* name);
PFN_MONO_IMAGE_OPEN_FROM_DATA_WITH_NAME g_pfnOriginMonoImageOpenFromDataWithName = NULL;

void dump_dll(const char* name, const char* data, uint32_t len);

void* stub_mono_image_open_from_data_with_name(char* data,
                                               guint32 data_len,
                                               gboolean need_copy,
                                               MonoImageOpenStatus* status,
                                               gboolean refonly,
                                               const char* name)
{
    if (name != NULL)
    {
        DEBUG_LOG("[image]name:%s", name);
    }

    if (g_pfnOriginMonoImageOpenFromDataWithName != NULL)
    {
        void* ret =  g_pfnOriginMonoImageOpenFromDataWithName(data, data_len, need_copy,status,refonly,name);
        MonoImage* image = (MonoImage*)ret;
        if(image != NULL)
        {
            if(strstr(name,"Assembly-CSharp.dll") != NULL || strstr(name,"Assembly-CSharp-firstpass.dll") != NULL)
            {
                DEBUG_LOG("[image]data:%p,len:%u", image->raw_data,image->raw_data_len);
                dump_dll(name, image->raw_data,image->raw_data_len);
            }
        }

        return ret;
    }
    return NULL;

}

void dump_dll(const char* name, const char* data, uint32_t len)
{
    const char* p = strrchr(name,'/');
    if(p != NULL) p++;
    else p = name;
    char path[1025] = {0};
    snprintf(path, sizeof(path)-1,"/sdcard/sdk/%s", p);
    DEBUG_LOG("path:%s", path);
    FILE* f = fopen(path, "wb");
    if(f != NULL)
    {
        uint32_t unit = 1024 * 1024;
        uint32_t cnt = len / unit;
        uint32_t remain = len % unit;
        uint32_t i = 0;
        for(i = 0; i < cnt; i++)
        {
            fwrite(data+i*unit,1,unit,f);
        }
        if(remain > 0)
        {
            fwrite(data+i*unit,1,remain,f);
        }
        fclose(f);
    } else
    {
        ERROR_LOG("fopen fail:%s", strerror(errno));
    }
}
void* stub_mono_assembly_load_full(MonoImage* image,
                                   const char* fname,
                                   MonoImageOpenStatus* status,
                                   gboolean refonly)
{
    if (image != NULL)
    {
        DEBUG_LOG("[assembly]data:%p", image->raw_data);
        DEBUG_LOG("[assembly]data len:%u", image->raw_data_len);
    }

    DEBUG_LOG("[assembly]status:%d", *status);
    DEBUG_LOG("[assembly]refonly:%s", refonly?"true":"false");

    if(fname != NULL)
    {
        DEBUG_LOG("[assembly]name:%s", fname);
        if(strstr(fname, "Assembly-CSharp.dll") != NULL || strstr(fname,"Assembly-CSharp-firstpass.dll") != NULL)
        {
            dump_dll(fname, image->raw_data, image->raw_data_len);
        }
    }

    if (g_pfnOriginMonoAssemblyLoadFromFull != NULL)
    {
        return g_pfnOriginMonoAssemblyLoadFromFull(image, fname, status,refonly);
    }
    return NULL;
}



void HookMono(void* handle)
{
    DEBUG_LOG("[%d]HookMono Enter", getpid());

    //ThreadHelper_CreateThread(Thread_HookMono, NULL);

    // find target function
    void* func = NULL;
    // mono_assembly_load_from_full
    func = dlsym(handle, "mono_assembly_load_from_full");
    DEBUG_LOG("func mono_assembly_load_from_full address:%p", func);

    if (func != NULL)
    {
        inlineHookDirect((unsigned int)func, (void*)stub_mono_assembly_load_full,(void**)&g_pfnOriginMonoAssemblyLoadFromFull);
    }
    /*

    func = dlsym(handle, "mono_image_open_from_data_with_name");
    DEBUG_LOG("func mono_image_open_from_data_with_name address:%p", func);

    if (func != NULL)
    {
        inline_hook2(func, (void*)stub_mono_image_open_from_data_with_name,(void**)&g_pfnOriginMonoImageOpenFromDataWithName);
        DEBUG_LOG("origin mono_image_open_from_data_with_name address:%p", g_pfnOriginMonoImageOpenFromDataWithName);
    }

    */
    DEBUG_LOG("[%d]HookMono Leave", getpid());
}

void install_mono_profiler(void* handle)
{
    HookMono(handle);
}
