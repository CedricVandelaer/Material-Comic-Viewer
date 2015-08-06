package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

/**
 * Created by CV on 15/07/2015.
 */
public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle instanceState) {
        super.onCreate(instanceState);
        setContentView(R.layout.activity_splash);

        TextView logoText = (TextView) findViewById(R.id.logo_text);

        if (Build.VERSION.SDK_INT>20)
            getWindow().setStatusBarColor(getResources().getColor(R.color.GreyLight));

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent;
                if (StorageManager.getBooleanSetting(SplashActivity.this, StorageManager.INTRO_WAS_SHOWN, false))
                    mainIntent = new Intent(SplashActivity.this, NewDrawerActivity.class);
                else
                    mainIntent = new Intent(SplashActivity.this, IntroActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
