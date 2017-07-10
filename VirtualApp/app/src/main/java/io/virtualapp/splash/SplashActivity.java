package io.virtualapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.lody.virtual.client.core.VirtualCore;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.home.FlurryROMCollector;
import io.virtualapp.home.HomeActivity;
import io.virtualapp.login.LoginActivity;
import jonathanfinerty.once.Once;
import io.virtualapp.LogHelper;

public class SplashActivity extends VActivity {

    protected static boolean isPaseTimeCheck = false;
    public static final String LOG_TAG = "VirtualApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isPaseTimeCheck = false;
        @SuppressWarnings("unused")
        boolean enterGuide = !Once.beenDone(Once.THIS_APP_INSTALL, VCommends.TAG_NEW_VERSION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        VUiKit.defer().when(() -> {
            if (!Once.beenDone("collect_flurry")) {
                FlurryROMCollector.startCollect();
                Once.markDone("collect_flurry");
            }
            long time = System.currentTimeMillis();
            LogHelper.Debug("waitForEngine start");
            VirtualCore.get().waitForEngine();
            LogHelper.Debug("waitForEngine end");

            isPaseTimeCheck = LoginActivity.CheckTime();
            time = System.currentTimeMillis() - time;
            long delta = 1000L - time;
            if (delta > 0) {
                VUiKit.sleep(delta);
            }
        }).done((res) -> {
            if (isPaseTimeCheck)
            {
                startActivity(new Intent(this,LoginActivity.class));
            }
            //HomeActivity.goHome(this);
            //finish();
        });
    }

}
