package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.Extractor;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.devspark.robototextview.widget.RobotoTextView;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends FragmentActivity {


    //The comic to be displayed
    private Comic mCurrentComic;

    //The number of pages of the comic
    private int mPageCount;

    private ComicViewPager mPager;
    private ComicStatePagerAdapter mPagerAdapter;

    //Arraylist containing the filenamestrings of the fileheaders of the pages
    private ArrayList<String> mPages;

    private RobotoTextView mPageIndicator;

    private String mPageNumberSetting;

    private Handler mHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_comic);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Intent intent = getIntent();
        
        int lastReadPage;

        mHandler = new Handler();

        mCurrentComic = intent.getParcelableExtra("Comic");

        mPageCount = mCurrentComic.getPageCount();

        mPages = new ArrayList<>();

        mPageIndicator = (RobotoTextView) findViewById(R.id.page_indicator);

        loadImageNames();

        mPageNumberSetting = PreferenceSetter.getPageNumberSetting(this);

        mPager =  (ComicViewPager) findViewById(R.id.comicpager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new ComicStatePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
        if (PreferenceSetter.getReadComics(this).containsKey(mCurrentComic.getFileName()))
        {
            lastReadPage = PreferenceSetter.getReadComics(this).get(mCurrentComic.getFileName());
            mPager.setCurrentItem(lastReadPage);            
        }

        boolean showInRecentsPref = getPreferences(Context.MODE_PRIVATE).getBoolean("useRecents",true);

        if (showInRecentsPref && Build.VERSION.SDK_INT>20) {
            new SetTaskDescriptionTask().execute();
        }




    }

    private class ComicPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setPageNumber();

            int pagesRead = PreferenceSetter.getPagesReadForComic(DisplayComicActivity.this, mCurrentComic.getFileName());

            if (pagesRead==0)
            {
                PreferenceSetter.incrementNumberOfComicsStarted(DisplayComicActivity.this, 1);
            }

            PreferenceSetter.saveLastReadComic(DisplayComicActivity.this, mCurrentComic.getFileName(), mPager.getCurrentItem());

            if (position+1> pagesRead)
            {
                PreferenceSetter.savePagesForComic(DisplayComicActivity.this, mCurrentComic.getFileName(), position+1);
                if (position+1 >= mCurrentComic.getPageCount())
                {
                    PreferenceSetter.incrementNumberOfComicsRead(DisplayComicActivity.this, 1);
                    PreferenceSetter.saveLongestReadComic(DisplayComicActivity.this,
                            mCurrentComic.getFileName(),
                            mCurrentComic.getPageCount(),
                            mCurrentComic.getTitle(),
                            mCurrentComic.getIssueNumber());
                }
                PreferenceSetter.incrementPagesForSeries(DisplayComicActivity.this, mCurrentComic.getTitle(), 1);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void setPageNumber()
    {
        if (mPageNumberSetting.equals(getString(R.string.page_number_setting_1)) && mPageCount>0)
        {
            mPageIndicator.setText(""+(mPager.getCurrentItem()+1)+" of "+mPageCount);
        }
        else if (mPageNumberSetting.equals(getString(R.string.page_number_setting_2)) && mPageCount>0)
        {
            final String currentPageText = ""+(mPager.getCurrentItem()+1)+" of "+mPageCount;
            mPageIndicator.setText(currentPageText);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mPageIndicator.getText().equals(currentPageText))
                    {
                        mPageIndicator.setText("");
                    }
                }
            },3000);
        }
        else
        {
            mPageIndicator.setText("");
        }
    }

    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(DisplayComicActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            if (Build.VERSION.SDK_INT>20) {
                ActivityManager.TaskDescription tdscr = null;
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(mCurrentComic.getTitle(),
                            ImageLoader.getInstance().loadImageSync(mCurrentComic.getCoverImage(), size),
                            mCurrentComic.getComicColor());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (tdscr != null)
                    setTaskDescription(tdscr);
            }

            return null;
        }
    }


    //Function to initialise immersive mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (Build.VERSION.SDK_INT>18) {
            if (hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }

    }


    /**
     * Function to get the filenamestrings of the files in the archive
     */
    private void loadImageNames()
    {
        mPages = Extractor.loadImageNamesFromComic(mCurrentComic);
    }


    private class ComicStatePagerAdapter extends FragmentStatePagerAdapter
    {
        FragmentManager mFragmentManager;

        public ComicStatePagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {

            String filename = mCurrentComic.getFileName();
            String comicPath = mCurrentComic.getFilePath()+ "/" + filename;
            ComicPageFragment fragment = ComicPageFragment.newInstance(comicPath, mPages.get(position), position);
            return fragment;
        }

        @Override
        public int getCount() {
            return mPageCount;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mPageNumberSetting = PreferenceSetter.getPageNumberSetting(this);
        setPageNumber();

        mPager.setOnPageChangeListener(new ComicPageChangeListener());

        if (mPageCount<1)
        {
            MaterialDialog materialDialog = new MaterialDialog.Builder(DisplayComicActivity.this)
                    .title("Error")
                    .content("This file can not be opened by comic viewer")
                    .positiveText("Accept")
                    .positiveColor(PreferenceSetter.getAppThemeColor(DisplayComicActivity.this))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            DisplayComicActivity.this.finish();
                        }
                    }).show();

        }
    }


    private void removeExtractedFiles() {
        
        for (int i=0;i<mPages.size();i++)
        {
            if (i!=0) {
                try {
                    String filename = mPages.get(i);
                    if (filename.contains("#"))
                        filename = filename.replaceAll("#","");
                    File file = new File(getFilesDir().getPath()+"/" + Utilities.removeExtension(mCurrentComic.getFileName()) + "/" + filename);
                    if (file.delete())
                        Log.d("DisplayComic Onstop", "Deleted file " +filename);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        PreferenceSetter.saveLastReadComic(DisplayComicActivity.this, mCurrentComic.getFileName(), mPager.getCurrentItem());

    }

    @Override
    public void onBackPressed()
    {
        removeExtractedFiles();
        if (Build.VERSION.SDK_INT>20)
            finishAfterTransition();
        else
            finish();
    }

}
