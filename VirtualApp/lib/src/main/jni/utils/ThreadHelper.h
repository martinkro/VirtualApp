//
// Created by sundayliu on 2017/6/15.
//

#ifndef CUSTOMVIRTUALAPP_THREADHELPER_H
#define CUSTOMVIRTUALAPP_THREADHELPER_H

typedef void* (*PFN_THREAD_ROUTINE)(void* param);
int ThreadHelper_CreateThread(PFN_THREAD_ROUTINE pfn, void* param);

class ThreadHelper {
public:
    static int CreateThread(PFN_THREAD_ROUTINE pfn, void* param);
};




#endif //CUSTOMVIRTUALAPP_THREADHELPER_H
