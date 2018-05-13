//
// Created by sundayliu on 2017/6/15.
//

#include "log.h"
#include <stdio.h>

const char* LOG_TAG = "VA-Native";

void log_hex_dump(const uint8_t* data, uint32_t size)
{
    if (data == NULL || size == 0)
    {
        return;
    }

    char str[16*4] = {0};
    uint32_t i;
    uint32_t j = 0;
    for (i = 0; i < size; i++)
    {
        snprintf(str+j*3, 5, "%02X ", data[i]);
        j++;
        j = j % 16;
        if (j == 0)
        {
            DEBUG_LOG("%p:%s", (const void*)(data+i), str);
        }
    }
}

