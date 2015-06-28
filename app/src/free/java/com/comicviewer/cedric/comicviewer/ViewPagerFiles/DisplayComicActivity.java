package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.Extractor;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.io.File;
import java.lang.Math;
import java.lang.System;
import java.util.ArrayList;
import java.util.Random;


/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends AbstractDisplayComicActivity {

    InterstitialAd mInterstitialAd;
    AdRequest mAdRequest;

    @Override
    protected void initializeAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showAd();
            }
        });
    }

    private void requestNewInterstitial() {
        mAdRequest = new AdRequest.Builder()
                .addTestDevice("AD58B57A983A628FE754A186B509F4C9")
                .addTestDevice("1D3A97B7E240BD8F05EE1F3BF1B68454")
                .build();
        mInterstitialAd.loadAd(mAdRequest);
    }

    @Override
    protected void showAd() {
        Random random = new Random(System.currentTimeMillis());
        int randInt = random.nextInt(9);

        if (randInt<3) {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();
        }

    }

    @Override
    protected void setPagerAnimation() {

    }
}
