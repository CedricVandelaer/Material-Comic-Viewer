package com.comicviewer.cedric.comicviewer;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends FragmentActivity {


    //The comic to be displayed
    private Comic mCurrentComic;

    //The number of pages of the comic
    private int mPageCount;

    private ViewPager mPager;
    private SmartFragmentStatePagerAdapter mPagerAdapter;

    //Arraylist containing the filenamestrings of the fileheaders of the pages
    private ArrayList<String> mPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_comic);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Intent intent = getIntent();

        mCurrentComic = intent.getParcelableExtra("Comic");

        mPageCount = mCurrentComic.getPageCount();

        mPages = new ArrayList<String>();

        loadImageNames();

        mPager =  (ViewPager) findViewById(R.id.comicpager);
        mPagerAdapter = new ComicStatePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        boolean showInRecentsPref = getPreferences(Context.MODE_PRIVATE).getBoolean("useRecents",true);
        Log.d("Show in recents preference: ", ""+showInRecentsPref);
        if (showInRecentsPref) {
            new SetTaskDescritionTask().execute();
        }
    }

    private class SetTaskDescritionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            ActivityManager.TaskDescription tdscr = null;
            try {
                tdscr = new ActivityManager.TaskDescription(mCurrentComic.getTitle(),
                        Picasso.with(getApplicationContext()).load(mCurrentComic.getCoverImage()).resize(85,50).get(),
                        mCurrentComic.mCoverColor);
            } catch (IOException e) {
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

}
