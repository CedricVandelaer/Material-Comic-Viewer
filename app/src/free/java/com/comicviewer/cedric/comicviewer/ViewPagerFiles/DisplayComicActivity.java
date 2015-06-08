package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.Extractor;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.devspark.robototextview.widget.RobotoTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.io.File;
import java.util.ArrayList;


/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends FragmentActivity {

    //The comic to be displayed
    private Comic mCurrentComic;

    //The number of pages of the comic
    private int mPageCount;

    private ComicViewPager mPager;
    private FragmentStatePagerAdapter mPagerAdapter;

    //Arraylist containing the filenamestrings of the fileheaders of the pages
    private ArrayList<String> mPages;

    private RobotoTextView mPageIndicator;

    //ads


    private Handler mHandler;

    private String mPageNumberSetting;

    private boolean mMangaComic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_comic);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        getWindow().getDecorView().setBackgroundColor(PreferenceSetter.getReadingBackgroundSetting(this));

        AdBuddiz.setPublisherKey("e5ef796f-a43b-4b25-b15f-ebebcbbcabf4");
        AdBuddiz.cacheAds(this); // this = current Activity

        Intent intent = getIntent();
        
        int lastReadPage;

        if (intent.getAction()!= null && intent.getAction().equals(Intent.ACTION_VIEW))
        {
            Uri uri = intent.getData();
            File file = new File(uri.getPath());
            Comic comic = new Comic(file.getName(), new File(file.getParent()).getPath());
            ComicLoader.loadComicSync(this, comic);
            mCurrentComic = comic;
        }
        else {
            mCurrentComic = intent.getParcelableExtra("Comic");
        }

        mPageCount = mCurrentComic.getPageCount();

        mPages = new ArrayList<>();

        mPageIndicator = (RobotoTextView) findViewById(R.id.page_indicator);

        loadImageNames();

        mPager =  (ComicViewPager) findViewById(R.id.comicpager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new ComicStatePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mMangaComic = false;
        if ((PreferenceSetter.getMangaSetting(this) && !PreferenceSetter.isNormalComic(this,mCurrentComic))
                || (!(PreferenceSetter.getMangaSetting(this)) && PreferenceSetter.isMangaComic(this, mCurrentComic)))
        {
            mPager.setCurrentItem(mCurrentComic.getPageCount()-1);
            mMangaComic = true;
        }

        if (PreferenceSetter.getReadComics(this).containsKey(mCurrentComic.getFileName()))
        {
            lastReadPage = PreferenceSetter.getReadComics(this).get(mCurrentComic.getFileName());
            if (mMangaComic)
            {
                mPager.setCurrentItem(mPageCount-1-lastReadPage);
            }
            else {
                mPager.setCurrentItem(lastReadPage);
            }
        }

        mPageNumberSetting = PreferenceSetter.getPageNumberSetting(this);

        boolean showInRecentsPref = getPreferences(Context.MODE_PRIVATE).getBoolean("useRecents",true);

        if (showInRecentsPref) {
            new SetTaskDescriptionTask().execute();
        }


        mHandler = new Handler();

        if (PreferenceSetter.getScreenOnSetting(this))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAd();
            }
        },3000);
    }

    private void showAd()
    {
        if (AdBuddiz.isReadyToShowAd(DisplayComicActivity.this))
            AdBuddiz.showAd(DisplayComicActivity.this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        PreferenceSetter.saveLastReadComic(DisplayComicActivity.this,mCurrentComic.getFileName(),mPager.getCurrentItem());
    }

    private class ComicPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int position) {

            if ((PreferenceSetter.getMangaSetting(DisplayComicActivity.this) && !PreferenceSetter.isNormalComic(DisplayComicActivity.this,mCurrentComic))
                    || (!(PreferenceSetter.getMangaSetting(DisplayComicActivity.this)) && PreferenceSetter.isMangaComic(DisplayComicActivity.this, mCurrentComic)))
            {
                position = mPageCount-1-position;
            }


            setPageNumber();

            int pagesRead = PreferenceSetter.getPagesReadForComic(DisplayComicActivity.this, mCurrentComic.getFileName());

            if (pagesRead==0)
            {
                PreferenceSetter.incrementNumberOfComicsStarted(DisplayComicActivity.this, 1);
            }

            PreferenceSetter.saveLastReadComic(DisplayComicActivity.this,mCurrentComic.getFileName(),position);

            if (position+1==mPageCount)
            {
                showAd();
            }

            if (position+1> pagesRead)
            {
                PreferenceSetter.savePagesForComic(DisplayComicActivity.this, mCurrentComic.getFileName(), position+1);
                if (position+1 >= mCurrentComic.getPageCount())
                {
                    PreferenceSetter.incrementNumberOfComicsRead(DisplayComicActivity.this, 1);
                }
                PreferenceSetter.saveLongestReadComic(DisplayComicActivity.this,
                        mCurrentComic.getFileName(),
                        mCurrentComic.getPageCount(),
                        mCurrentComic.getTitle(),
                        mCurrentComic.getIssueNumber());
                PreferenceSetter.incrementPagesForSeries(DisplayComicActivity.this, mCurrentComic.getTitle(), 1);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
        mPages = Extractor.loadImageNamesFromComic(this, mCurrentComic);
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
                    .content("This file can not be opened by Comic Viewer")
                    .positiveColor(PreferenceSetter.getAppThemeColor(DisplayComicActivity.this))
                    .positiveText("Accept")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            DisplayComicActivity.this.finish();
                        }
                    })
                    .show();
        }


    }

    private void setPageNumber()
    {

        int pageNumber = mPager.getCurrentItem()+1;

        if ((PreferenceSetter.getMangaSetting(DisplayComicActivity.this) && !PreferenceSetter.isNormalComic(DisplayComicActivity.this,mCurrentComic))
                || (!(PreferenceSetter.getMangaSetting(DisplayComicActivity.this)) && PreferenceSetter.isMangaComic(DisplayComicActivity.this, mCurrentComic)))
        {
            pageNumber = mCurrentComic.getPageCount()-mPager.getCurrentItem();
        }

        if (mPageNumberSetting.equals(getString(R.string.page_number_setting_1)) && mPageCount>0)
        {
            mPageIndicator.setText(""+pageNumber+" "+getString(R.string.of)+" "+mPageCount);
        }
        else if (mPageNumberSetting.equals(getString(R.string.page_number_setting_2)) && mPageCount>0)
        {
            final String currentPageText = ""+pageNumber+" "+getString(R.string.of)+" "+mPageCount;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_comic, menu);
        return false;
    }
    
    @Override
    public void onStop()
    {
        int pageNumber = mPager.getCurrentItem();

        if ((PreferenceSetter.getMangaSetting(DisplayComicActivity.this) && !PreferenceSetter.isNormalComic(DisplayComicActivity.this,mCurrentComic))
                || (!(PreferenceSetter.getMangaSetting(DisplayComicActivity.this)) && PreferenceSetter.isMangaComic(DisplayComicActivity.this, mCurrentComic)))
        {
            pageNumber = mCurrentComic.getPageCount()-1-mPager.getCurrentItem();
        }

        PreferenceSetter.saveLastReadComic(DisplayComicActivity.this,mCurrentComic.getFileName(),pageNumber);
        super.onStop();
    }

    private void removeExtractedFiles() {

        File archive = new File(mCurrentComic.getFilePath()+"/"+mCurrentComic.getFileName());
        if (!archive.isDirectory())
        {
            for (int i = 0; i < mPages.size(); i++) {
                if (i != 0) {
                    try {
                        String filename = mPages.get(i);
                        if (filename.contains("#"))
                            filename = filename.replaceAll("#", "");
                        File file = new File(getFilesDir().getPath() + "/" + Utilities.removeExtension(mCurrentComic.getFileName()) + "/" + filename);
                        if (file.delete())
                            Log.d("DisplayComic Onstop", "Deleted file " + filename);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    @Override
    public void onBackPressed()
    {
        if (Build.VERSION.SDK_INT>20) {
            finishAfterTransition();
        }
        else
            finish();
    }

    @Override
    public void onDestroy()
    {
        removeExtractedFiles();
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (PreferenceSetter.getVolumeKeyPreference(this) && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            if (action == KeyEvent.ACTION_DOWN) {
                int page = mPager.getCurrentItem();
                if (mMangaComic) {
                    page++;
                    if (page >= 0 && page < mPageCount) {
                        mPager.setCurrentItem(page);
                    }
                } else {
                    page--;
                    if (page >= 0 && page < mPageCount) {
                        mPager.setCurrentItem(page);
                    }
                }
            }

            return true;

        }
        else if (PreferenceSetter.getVolumeKeyPreference(this) && keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            if (action == KeyEvent.ACTION_DOWN) {
                int page = mPager.getCurrentItem();
                if (mMangaComic) {
                    page--;
                    if (page >= 0 && page < mPageCount) {
                        mPager.setCurrentItem(page);
                    }
                } else {
                    page++;
                    if (page >= 0 && page < mPageCount) {
                        mPager.setCurrentItem(page);
                    }
                }
            }
            return true;
        }
        else
        {
            return super.dispatchKeyEvent(event);
        }
    }

}
