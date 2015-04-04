package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.Extractor;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

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
    private SmartFragmentStatePagerAdapter mPagerAdapter;

    //Arraylist containing the filenamestrings of the fileheaders of the pages
    private ArrayList<String> mPages;

    private RobotoTextView mPageIndicator;

    private boolean mShowPageNumbers;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_comic);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        Intent intent = getIntent();
        
        int lastReadPage;

        mCurrentComic = intent.getParcelableExtra("Comic");

        mPageCount = mCurrentComic.getPageCount();

        mPages = new ArrayList<>();

        mPageIndicator = (RobotoTextView) findViewById(R.id.page_indicator);

        loadImageNames();

        mPager =  (ComicViewPager) findViewById(R.id.comicpager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new ComicStatePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
        if (PreferenceSetter.getReadComics(this).containsKey(mCurrentComic.getFileName()))
        {
            lastReadPage = PreferenceSetter.getReadComics(this).get(mCurrentComic.getFileName());
            mPager.setCurrentItem(lastReadPage);            
        }

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.destroy();

        boolean showInRecentsPref = getPreferences(Context.MODE_PRIVATE).getBoolean("useRecents",true);

        if (showInRecentsPref) {
            new SetTaskDescriptionTask().execute();
        }

        intent.putExtra("comicName", mCurrentComic.getFileName());

        setResult(Activity.RESULT_OK, intent);

    }


    private class ComicPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            if (mShowPageNumbers)
                mPageIndicator.setText("" + (position+1)+" of "+ mPageCount);
            else
                mPageIndicator.setText("");
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
            
            ActivityManager.TaskDescription tdscr = null;
            try {
                ImageSize size = new ImageSize(64,64);
                tdscr = new ActivityManager.TaskDescription(mCurrentComic.getTitle(),
                        ImageLoader.getInstance().loadImageSync(mCurrentComic.getCoverImage(),size),
                        mCurrentComic.getComicColor());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (tdscr!=null)
                setTaskDescription(tdscr);

            return null;
        }
    }


    //Function to initialise immersive mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }


    /**
     * Function to get the filenamestrings of the files in the archive
     */
    private void loadImageNames()
    {
        mPages = Extractor.loadImageNamesFromComic(mCurrentComic);
    }


    private class ComicStatePagerAdapter extends SmartFragmentStatePagerAdapter
    {
        public ComicStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            String filename = mCurrentComic.getFileName();
            String comicPath = mCurrentComic.getFilePath()+ "/" + filename;
            return ComicPageFragment.newInstance(comicPath, mPages.get(position), position);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mShowPageNumbers = prefs.getBoolean("showPageNumber",true);

        if (mShowPageNumbers)
            mPageIndicator.setText(""+(mPager.getCurrentItem()+1)+" of "+mPageCount);
        mPager.setOnPageChangeListener(new ComicPageChangeListener());
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
        super.onStop();

    }



    private void removeExtractedFiles() {
        
        for (int i=0;i<mPages.size();i++)
        {
            if (i!=0) {
                try {
                    String filename = mPages.get(i);
                    if (filename.contains("#"))
                        filename = filename.replaceAll("#","");
                    File file = new File(getFilesDir().getPath() + "/" + filename);
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
    public void onBackPressed()
    {
        PreferenceSetter.saveLastReadComic(this, mCurrentComic.getFileName(), mPager.getCurrentItem());
        removeExtractedFiles();
        finishAfterTransition();
    }

}