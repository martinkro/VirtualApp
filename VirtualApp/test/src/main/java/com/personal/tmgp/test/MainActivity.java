package com.personal.tmgp.test;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "VirtualApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "MainActivity::OnCreate");
        Log.d(LOG_TAG,"Process Name:" + getCurrentProcessName(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
