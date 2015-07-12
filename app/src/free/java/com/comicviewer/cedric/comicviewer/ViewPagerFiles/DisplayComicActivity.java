package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import com.comicviewer.cedric.comicviewer.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.lang.System;
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
