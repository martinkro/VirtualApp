//
// Created by sundayliu on 2017/6/15.
//

#include "ThreadHelper.h"
#include "log.h"
#include <pthread.h>
#include <errno.h>

int ThreadHelper_CreateThread(PFN_THREAD_ROUTINE pfn, void* param)
{
    return ThreadHelper::CreateThread(pfn, param);
}

int ThreadHelper::CreateThread(PFN_THREAD_ROUTINE pfn, void *param)
{
    int err = 0;
    pthread_t tid = 0;
    pthread_attr_t attr;
    err = pthread_attr_init(&attr);
    if(err != 0) goto __error__;
    //err = pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    //if(err != 0) goto __error__;
    err = pthread_create(&tid,&attr,pfn,param);
    if(err != 0) goto __error__;
    err = pthread_attr_destroy(&attr);

    __error__:
    if (err != 0)
    {
        ERROR_LOG("[%d] Create Thread fail:%s", strerror(errno));
    }

    return err;
}
