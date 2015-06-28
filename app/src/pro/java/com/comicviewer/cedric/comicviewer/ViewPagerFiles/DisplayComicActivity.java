package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.Extractor;
import com.comicviewer.cedric.comicviewer.PageFlipPageTransformer;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.devspark.robototextview.widget.RobotoTextView;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.ArrayList;

/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends AbstractDisplayComicActivity {

    protected void showAd()
    {

    }

    protected void setPagerAnimation()
    {
        if (PreferenceSetter.getPageFlipAnimationSetting(this).equals(getString(R.string.accordion)))
        {
            mPager.setPageTransformer(true, new AccordionTransformer());
        }
        else if(PreferenceSetter.getPageFlipAnimationSetting(this).equals(getString(R.string.cube)))
        {
            mPager.setPageTransformer(true, new CubeOutTransformer());
        }
        else if(PreferenceSetter.getPageFlipAnimationSetting(this).equals(getString(R.string.zoom_out)))
        {
            mPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        }
        else if(PreferenceSetter.getPageFlipAnimationSetting(this).equals(getString(R.string.foreground_to_background)))
        {
            mPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        }
        else if(PreferenceSetter.getPageFlipAnimationSetting(this).equals(getString(R.string.stack)))
        {
            mPager.setPageTransformer(true, new StackTransformer());
        }
        else if(PreferenceSetter.getPageFlipAnimationSetting(this).equals(getString(R.string.page_flip)))
        {
            mPager.setPageTransformer(true, new PageFlipPageTransformer());
        }
        else
        {
            mPager.setPageTransformer(true, new DefaultTransformer());
        }
    }


}
