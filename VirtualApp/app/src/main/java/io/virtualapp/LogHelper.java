package io.virtualapp; /**
 * Created by sundayliu on 2017/6/27.
 */

import android.util.Log;
import android.os.Process;
public class LogHelper {
    public static final String TAG = "VirtualApp";

    enum LOG_LEVEL
    {
        LOG_LEVEL_DEBUG,
        LOG_LEVEL_ERROR,
    }
    public static void Debug(String msg)
    {
        print(LOG_LEVEL.LOG_LEVEL_DEBUG, msg);
    }

    public static void Error(String msg)
    {
        print(LOG_LEVEL.LOG_LEVEL_ERROR, msg);
    }

    private static void print(LOG_LEVEL level, String msg)
    {
        String x = String.format("[%d]%s", Process.myPid(), msg);
        switch(level)
        {
            case LOG_LEVEL_DEBUG:
                Log.d(TAG, x);
                break;
            case LOG_LEVEL_ERROR:
                Log.e(TAG, x);
                break;
            default:
                break;

        }
    }
}
