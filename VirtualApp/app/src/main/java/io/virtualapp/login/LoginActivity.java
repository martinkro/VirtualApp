package io.virtualapp.login;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lody.virtual.client.NativeEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import io.virtualapp.R;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.home.HomeActivity;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * Created by sundayliu on 2017/7/10.
 */

public class LoginActivity extends VActivity implements View.OnClickListener{

    public static final String AES_KEY = "cf4e79159b7a56a5";
    public static long ExpirationTime = 0;
    public static final String FILENAME_AddonLicenseKeys = "AddonLicenseKeys.txt";
    public static final String FILENAME_LicenseKey = "LicenseKey.txt";
    public static final String FILENAME_UserData = "UserData.txt";
    public static final int SHOW_RESPONSE = 0;
    public static String UserLicenseKey;
    private Button btn_Confirm;
    private EditText edit_key;
    private Handler handler;
    private TextView text_error;
    private TextView text_tips;

    class URLRequestThread implements  Runnable
    {
        String mAuthID;
        URLRequestThread(String authID)
        {
            mAuthID = authID;
        }
        public void run()
        {

        }
    }

    // Login Activity
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_Confirm = (Button)findViewById(R.id.btn_Confirm);
        edit_key = (EditText)findViewById(R.id.edit_key);
        text_error = (TextView)findViewById(R.id.text_error);
        text_tips = (TextView)findViewById(R.id.text_tips);
        String strLincense = ReadFile(FILENAME_LicenseKey);

        text_error.setVisibility(View.INVISIBLE);
        text_tips.setVisibility(View.INVISIBLE);
        btn_Confirm.setVisibility(View.VISIBLE);
        edit_key.setVisibility(View.VISIBLE);
        btn_Confirm.setOnClickListener(this);

        try
        {
            strLincense = AES.Decrypt(strLincense,AES_KEY);
        }
        catch (Exception e)
        {
            strLincense = "";
            e.printStackTrace();

        }

        if (strLincense!=null && strLincense.length() > 0)
        {
            edit_key.setText(strLincense);
            // doLogin(strLincense);
        }
    }
    public void onClick(View v)
    {
        String authID = this.edit_key.getText().toString();
        if (authID.length() > 0)
        {
            doLogin(authID);
        }
    }
    protected void doLogin(String AuthID)
    {
        text_error.setVisibility(View.INVISIBLE);
        text_tips.setVisibility(View.VISIBLE);
        btn_Confirm.setVisibility(View.INVISIBLE);
        edit_key.setVisibility(View.INVISIBLE);

        sendRequestWithHttpURLConnection("?ChannelID=10006&AuthID=" + AuthID);

    }
    protected  void ShowHomeActivity()
    {
        HomeActivity.goHome(this);
        finish();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KEYCODE_BACK)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KEYCODE_BACK)
        {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    private void sendRequestWithHttpURLConnection(String arg)
    {

    }
    private void writeFileToSDCard(String data)
    {

    }
    protected void CheckLoginResult(String strReslut)
    {
        if (strReslut != null) {
            try {
                if (strReslut.indexOf("ERROR") == -1 && strReslut.indexOf("OK|") != -1) {
                    String[] strArray = strReslut.split("\\|");
                    ExpirationTime = Long.parseLong(strArray[1]);
                    WriteFile(FILENAME_UserData, NativeEngine.getOutPut(strArray[3]).getBytes());
                    UserLicenseKey = this.edit_key.getText().toString();
                    WriteLinGameAddonLicenseKeys();
                    ShowHomeActivity();
                    return;
                }
            } catch (Exception e) {
                Log.e("VirtualApp", "strReslut" + strReslut);
                e.printStackTrace();
                this.text_error.setVisibility(View.VISIBLE);
                this.text_tips.setVisibility(View.INVISIBLE);
                this.btn_Confirm.setVisibility(View.VISIBLE);
                this.edit_key.setVisibility(View.VISIBLE);
                TryDeleteLicenseFile(FILENAME_LicenseKey);
                return;
            }
        }
        this.text_error.setVisibility(0);
        this.text_tips.setVisibility(4);
        this.btn_Confirm.setVisibility(0);
        this.edit_key.setVisibility(0);
        TryDeleteLicenseFile(FILENAME_LicenseKey);

    }
    public static void WriteFile(String fileName,byte[] buffer)throws IOException
    {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "GameAddons");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File dirConfig = new File(dir.getAbsoluteFile(), "Config");
        if (!dirConfig.exists()) {
            dirConfig.mkdir();
        }
        File file = new File(dirConfig.getAbsoluteFile(), fileName);
        Log.d("VA", file.getAbsolutePath());
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer, 0, buffer.length);
        fos.flush();
        fos.close();

    }
    public static String ReadFile(String fileName)
    {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "GameAddons");
        if (dir.exists()) {
            File dirConfig = new File(dir.getAbsoluteFile(), "Config");
            if (dirConfig.exists()) {
                File file = new File(dirConfig.getAbsoluteFile(), fileName);
                if (file.exists()) {
                    try {
                        FileInputStream fin = new FileInputStream(file);
                        byte[] buffer = new byte[fin.available()];
                        fin.read(buffer);
                        String str = new String(buffer);
                        fin.close();
                        return str;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return "";
    }
    public static boolean IsLicenseFileExists(String fileName)
    {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "GameAddons");
        if (dir.exists()) {
            File dirConfig = new File(dir.getAbsoluteFile(), "Config");
            if (dirConfig.exists() && new File(dirConfig.getAbsoluteFile(), fileName).exists()) {
                return true;
            }
        }
        return false;

    }
    public static void TryDeleteLicenseFile(String fileName)
    {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "GameAddons");
        if (dir.exists()) {
            File dirConfig = new File(dir.getAbsoluteFile(), "Config");
            if (dirConfig.exists()) {
                File file = new File(dirConfig.getAbsoluteFile(), fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

    }
    public static boolean CheckTime()
    {
        MalformedURLException e;
        URL url;
        IOException e2;
        try {
            URL url2 = new URL("http://www.baidu.com");
            try {
                URLConnection uc = url2.openConnection();
                uc.connect();
                long netTime = new Date(uc.getDate()).getTime();
                Log.d("VA", "netTime  " + netTime);
                long localTime = Calendar.getInstance().getTime().getTime();
                Log.d("VA", "localTime  " + localTime);
                if (netTime - localTime < 600000) {
                    return true;
                }
                return false;
            } catch (MalformedURLException e3) {
                e = e3;
                url = url2;
                e.printStackTrace();
                return false;
            } catch (IOException e4) {
                e2 = e4;
                url = url2;
                e2.printStackTrace();
                return false;
            }
        } catch (MalformedURLException e5) {
            e = e5;
            e.printStackTrace();
            return false;
        } catch (IOException e6) {
            e2 = e6;
            e2.printStackTrace();
            return false;
        }

        //return true;
    }
    public static void WriteLinGameAddonLicenseKeys()
    {
        try {
            WriteFile(FILENAME_AddonLicenseKeys, ("com.tencent.tmgp.cf|" + UserLicenseKey + ":com.tencent.tmgp.sgame|" + UserLicenseKey + ":com.ztgame.jielan|" + UserLicenseKey).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
