package io.virtualapp.lib.utils; /**
 * Created by sundayliu on 2017/6/27.
 */

import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
        String x = String.format("[%d:%s]%s", Process.myPid(), getCurrentProcessName(),msg);
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

    public static String getCurrentProcessName()
    {
        int pid = android.os.Process.myPid();
        String processName = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;
        try {
            File file = new File("/proc/"+pid+"/cmdline");
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            processName = bufferedReader.readLine().toString();

            int endIndex = processName.indexOf(0);
            processName = processName.substring(0, endIndex);

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return processName;
    }
}
