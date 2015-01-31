package com.comicviewer.cedric.comicviewer;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


public class DisplayComicActivity extends FragmentActivity {


    private Comic mCurrentComic;
    private int mPageCount;
    private ViewPager mPager;
    private SmartFragmentStatePagerAdapter mPagerAdapter;
    private ArrayList<String> mPages;
    private ComicPageFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_display_comic);

        setTransitions();

        Intent intent = getIntent();

        mCurrentComic = intent.getParcelableExtra("Comic");

        mPageCount = mCurrentComic.getPageCount();

        mPages = new ArrayList<String>();

        loadPageNamesSync();

        mPager =  (ViewPager) findViewById(R.id.comicpager);
        mPager.setOnPageChangeListener(new ComicPageChangeListener());
        mPagerAdapter = new ComicStatePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }


    private void loadPageNamesSync()
    {
        String filename = mCurrentComic.getFileName();
        String path = mCurrentComic.getFilePath()+ "/" + filename;
        File comic = new File(path);
        try {
            Archive arch = new Archive(comic);
            List<FileHeader> fileheaders = arch.getFileHeaders();

            Pattern p = Pattern.compile("\\d\\d");

            for (int j = 0; j < fileheaders.size(); j++) {

                String pageFileIndex = fileheaders.get(j).getFileNameString()
                        .substring(fileheaders.get(j).getFileNameString().length() - 7);
                Matcher m = p.matcher(pageFileIndex);
                if (m.find())
                {
                    mPages.add(fileheaders.get(j).getFileNameString());
                }

            }

        }
        catch (Exception e)
        {
            Log.e("ExtractRarTask", e.getMessage());
        }
    }

    private class ComicPageChangeListener implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position)
        {
            
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
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
        loadPageNamesSync();
        mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar!=null)
            actionBar.hide();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_comic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finishAfterTransition();
    }

    private void setTransitions()
    {
        getWindow().setExitTransition(new Explode());
        getWindow().setEnterTransition(new Slide());
    }
}
