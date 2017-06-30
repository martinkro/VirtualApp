package com.personal.tmgp.test;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import android.util.Log;

/**
 * Created by sundayliu on 2017/6/29.
 */

public class MyApp extends Application {
    public static final String LOG_TAG = "VirtualApp";
    public void onCreate()
    {
        Log.d(LOG_TAG, "MyApp::onCreate");
        Log.d(LOG_TAG,"Process Name:" + getCurrentProcessName(this));
        super.onCreate();
    }

    protected  void attachBaseContext(Context base)
    {
        Log.d(LOG_TAG, "MyApp::attachBaseContext");

        super.attachBaseContext(base);
        Log.d(LOG_TAG,"Process Name:" + getCurrentProcessName(this));
    }

    private static String getCurrentProcessName(Context context)
    {
        int pid = android.os.Process.myPid();//获取进程pid
        String processName = null;
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);//获取系统的ActivityManager服务
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()){
            if(appProcess.pid == pid){
                processName = appProcess.processName;
                break;
            }
        }
        return processName;
    }
}
