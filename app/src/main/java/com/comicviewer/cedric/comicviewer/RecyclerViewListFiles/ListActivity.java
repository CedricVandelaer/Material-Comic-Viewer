package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.AboutActivity;
import com.comicviewer.cedric.comicviewer.Comic;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsActivity;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ListActivity extends Activity {

    ArrayList<Comic> mComicList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private String mCardColorSetting;
    private ArrayList<String> mFilePaths;
    private PreferenceSetter mPrefSetter;
    private boolean mUseRecents;
    private int mProgress;
    private int mTotalComicCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mFirstLoad;
    private ArrayList<String> mExcludedPaths;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        mFirstLoad = true;
        
        mPrefSetter = new PreferenceSetter();

        createRecyclerView();
        createFab();

        initialiseAdapter(savedInstanceState);
        
        initialiseRefresh();

    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        
    }

    private void initialiseRefresh()
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchComics();
            }
        });
        
    }

    private void createFab() {
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(Environment.getExternalStorageDirectory().getPath());
                FileDialog dialog = new FileDialog(ListActivity.this, path);
                dialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(File directory) {
                        Log.d(getClass().getName(), "selected dir " + directory.toString());
                        mFilePaths.add(directory.toString());
                        if (mExcludedPaths.contains(directory.toString()))
                            mExcludedPaths.remove(directory.toString());
                        searchComics();
                    }
                });
                dialog.setSelectDirectoryOption(true);
                dialog.showDialog();

            }
        });
    }

    private void setPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCardColorSetting = prefs.getString("cardColor", getString(R.string.card_color_setting_1));

        mPrefSetter.setBackgroundColorPreference(this);

        mUseRecents = prefs.getBoolean("useRecents",true);
        
        mFilePaths = mPrefSetter.getFilePathsFromPreferences(this);
    }

    private void searchComics() {
        

        
        
        mFilePaths = searchSubFolders(mFilePaths);

        
        Map<String,String> map = findFilesInPaths();

        //create treemap to sort the filenames
        Map<String,String> treemap = new TreeMap(map);

        mTotalComicCount = countComics(treemap);
        mProgress = 0;

        
        int i=0;

        if (mFirstLoad) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            mFirstLoad = false;
        }
        else
        {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        
        // create array for the zips to extract after the rars
        Hashtable<Integer, Comic> zipsToExtract = new Hashtable<>();
        
        for (String str:treemap.keySet())
        {
            
            File file = new File(map.get(str)+"/"+str);

            if (!file.isDirectory()) {
                if (Utilities.checkExtension(str)) {
                    if (!comicFileInList(str)) {

                        boolean isZip = Utilities.isZipArchive(file);
                        boolean isRar = false;
                        if (!isZip)
                            isRar = Utilities.isRarArchive(file);

                        if (isRar || isZip) {
                            Comic newComic = new Comic(str, map.get(str));
                            mComicList.add(i, newComic);
                            mAdapter.notifyItemInserted(i);
                            if (isZip) {
                                zipsToExtract.put(i, newComic);
                            } else {
                                new ExtractRarTask().execute(newComic, i);
                            }
                            i++;
                        } else {
                            mProgress++;
                            showProgressDialog(mProgress, mTotalComicCount);
                        }
                    } else {
                        i++;
                        mProgress++;
                        showProgressDialog(mProgress, mTotalComicCount);
                    }
                }else
                {
                    mProgress++;
                    showProgressDialog(mProgress, mTotalComicCount);
                }

            }
        }
        
        showProgressDialog(mProgress,mTotalComicCount);
        
        // extract found zips
        Enumeration e = zipsToExtract.keys();
        
        while (e.hasMoreElements())
        {
            Integer key = (Integer) e.nextElement();
            new ExtractZipTask().execute(zipsToExtract.get(key),key);
        }
        
        //Check to remove removed items
        removeOldComics(treemap);
        
        //Check for doubles
        removeDoubleComics();


    }
    
    private Map findFilesInPaths()
    {
        // list of filenames
        ArrayList<String> files = new ArrayList<>();
        // list of directories to search from
        ArrayList<String> paths = new ArrayList<>();

        // map to map the filenames to their directories
        Map<String,String> map = new HashMap<>();

        // search for all files in all paths
        for (int i=0;i<mFilePaths.size();i++)
        {
            String path = mFilePaths.get(i);
            File f = new File(path);
            f.mkdirs();

            File fileList[] = f.listFiles();

            if (fileList!=null)
            {
                for (int j=0;j<fileList.length;j++)
                {
                    if (!fileList[j].isDirectory())
                    {
                        files.add(fileList[j].getName());
                        paths.add(path);
                    }
                }
            }
        }

        // map the filenames to their directories
        for (int i=0;i<files.size();i++) {
            map.put(files.get(i),paths.get(i));
        }

        return map;
    }
    
    private ArrayList<String> searchSubFolders(ArrayList<String> paths)
    {
        ArrayList<String> allFoldersInPaths = new ArrayList<>();
        
        for (int i=0;i<paths.size();i++)
        {
            File root = new File(paths.get(i));
            
            if (!mExcludedPaths.contains(paths.get(i))) {

                if (root.isDirectory()) {

                    allFoldersInPaths.add(paths.get(i));
                    File[] subFiles = root.listFiles();
                    ArrayList<String> subFolders = new ArrayList<>();

                    for (int j = 0; j < subFiles.length; j++) {
                        subFolders.add(subFiles[j].toString());
                    }
                    allFoldersInPaths.addAll(searchSubFolders(subFolders));
                }
            }
            
        }
        
        return allFoldersInPaths;
    }
    
    private void showProgressDialog(int progress, int total)
    {
        if (progress>=total) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    
    private int countComics(Map treemap)
    {
        int count = 0;
        for (Object str:treemap.keySet())
        {
                count++;
            
        }
        return count;
    }
    
    private void removeOldComics(Map treemap)
    {
        for (int j=0;j<mComicList.size();j++)
        {
            Comic comicToRemove = mComicList.get(j);
            boolean isInList = false;

            for (int k=0;k<treemap.keySet().size();k++)
            {
                if (treemap.containsKey(comicToRemove.getFileName()))
                {
                    isInList=true;
                }
            }
            if (!isInList)
            {
                mComicList.remove(j);
                mAdapter.notifyItemRemoved(j);
            }
        }
        
    }
    
    private void removeDoubleComics()
    {
        for (int j=0;j<mComicList.size();j++)
        {
            Comic currentComic = mComicList.get(j);
            boolean isDouble = false;
            for (int k=0;k<mComicList.size();k++)
            {
                if (j!=k)
                {
                    if(currentComic.getFileName().equals(mComicList.get(k).getFileName()))
                    {
                        isDouble = true;
                    }
                }
                
            }
            if (isDouble) {
                mComicList.remove(j);
                mAdapter.notifyItemRemoved(j);
            }
        }   
    }
    
    private boolean comicFileInList(String filename)
    {
        if (mComicList!=null)
        {
            for (int i=0;i<mComicList.size();i++)
            {
                if (mComicList.get(i).getFileName().trim().equals(filename.trim()))
                {
                    return true;
                }
                
            }
        }
        return false;
    }

    private class ExtractZipTask extends AsyncTask<Object, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Object... comicVar) {

            Comic newComic= (Comic)comicVar[0];
            int itemPosition = (Integer) comicVar[1];
            String archivefilename = newComic.getFileName();
            String path = newComic.getFilePath();
            String extractedImageFile = null;
            int pageCount = 0;
            boolean isAlreadyExtracted = false;

            ArrayList<net.lingala.zip4j.model.FileHeader> pages = new ArrayList<net.lingala.zip4j.model.FileHeader>();

            try {
                ZipFile zipFile = new ZipFile(path + "/" + archivefilename);
                List<net.lingala.zip4j.model.FileHeader> fileheaders = zipFile.getFileHeaders();

                // search for comic pages in the archive
                for (int j = 0; j < fileheaders.size(); j++) {

                    if (Utilities.isPicture(fileheaders.get(j).getFileName())) {
                        if (!fileheaders.get(j).isDirectory()) {
                            pages.add(fileheaders.get(j));
                            pageCount++;
                        }
                    }
                }

                // sort the pages
                Collections.sort(pages, new Comparator<net.lingala.zip4j.model.FileHeader>() {
                    @Override
                    public int compare(net.lingala.zip4j.model.FileHeader lhs, net.lingala.zip4j.model.FileHeader rhs) {
                        int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getFileName(), rhs.getFileName());
                        if (res == 0) {
                            res = lhs.getFileName().compareTo(rhs.getFileName());
                        }
                        return res;
                    }
                });

                // the outputfilename
                extractedImageFile = pages.get(0).getFileName().substring(pages.get(0).getFileName().lastIndexOf("\\") + 1);

                /*
                if (extractedImageFile.contains("/"))
                    extractedImageFile = extractedImageFile.substring(extractedImageFile.lastIndexOf("/")+1);
                */
                
                // get rid of special chars causing problems
                if (extractedImageFile.contains("#"))
                    extractedImageFile = extractedImageFile.replaceAll("#","");

                // the output file
                File output = new File(getFilesDir(), extractedImageFile);

                // if file!=extracted -> extract
                if (!(isAlreadyExtracted=output.exists())) {
                    zipFile.extractFile(pages.get(0), getFilesDir().getAbsolutePath());
                }

                String coverImage = "file:" + getFilesDir().toString() + "/" + extractedImageFile;

                if (newComic.getCoverImage()==null) {
                    newComic.setCoverImage(coverImage);
                    newComic.setPageCount(pageCount);

                    setComicColor(newComic);
                }
                else
                {
                    return null;
                }

                return itemPosition;

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer itempos) {
            super.onPostExecute(itempos);

            if (itempos!=null) {
                mAdapter.notifyItemChanged(itempos);
            }
            mProgress++;
            showProgressDialog(mProgress,mTotalComicCount);
        }
    }

    public class ExtractRarTask extends AsyncTask<Object, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Object... comicVar) {

            Comic newComic= (Comic)comicVar[0];
            int itemPosition = (Integer) comicVar[1];
            String filename = newComic.getFileName();
            ArrayList<com.github.junrar.rarfile.FileHeader> pages= new ArrayList<com.github.junrar.rarfile.FileHeader>();
            boolean isAlreadyExtracted=false;

            String path = newComic.getFilePath()+ "/" + filename;

            File comic = new File(path);

            try {
                Archive arch = new Archive(comic);
                List<com.github.junrar.rarfile.FileHeader> fileheaders = arch.getFileHeaders();
                String extractedImageFile = null;

                int pageCount = 0;

                // search for comic pages in the archive
                for (int j = 0; j < fileheaders.size(); j++) {

                    if (Utilities.isPicture(fileheaders.get(j).getFileNameString())) {
                        if (!fileheaders.get(j).isDirectory()) {
                            pages.add(fileheaders.get(j));
                            pageCount++;
                        }
                    }
                }

                // sort the pages
                Collections.sort(pages, new Comparator<com.github.junrar.rarfile.FileHeader>() {
                    @Override
                    public int compare(com.github.junrar.rarfile.FileHeader lhs, com.github.junrar.rarfile.FileHeader rhs) {
                        int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getFileNameString(), rhs.getFileNameString());
                        if (res == 0) {
                            res = lhs.getFileNameString().compareTo(rhs.getFileNameString());
                        }
                        return res;
                    }
                });

                // the outputfilename
                extractedImageFile = pages.get(0).getFileNameString().substring(pages.get(0).getFileNameString().lastIndexOf("\\") + 1);

                // get rid of special chars causing problems
                if (extractedImageFile.contains("#"))
                    extractedImageFile = extractedImageFile.replaceAll("#","");

                // the output file
                File output = new File(getFilesDir(), extractedImageFile);

                // if file!=extracted -> extract
                if (!(isAlreadyExtracted=output.exists())) {
                    FileOutputStream os = new FileOutputStream(output);
                    arch.extractFile(pages.get(0), os);
                }

                String coverImage = "file:" + getFilesDir().toString() + "/" + extractedImageFile;

                if (newComic.getCoverImage()==null) {
                    newComic.setCoverImage(coverImage);
                    newComic.setPageCount(pageCount);

                    setComicColor(newComic);
                }
                else {
                    return null;
                }



                return itemPosition;

            } catch (Exception e) {
                Log.d("ExtractRarTask",e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Integer itempos) {
            super.onPostExecute(itempos);

            //mAdapter.notifyDataSetChanged();
            if (itempos!=null) {
                mAdapter.notifyItemChanged(itempos);
            }
            mProgress++;
            showProgressDialog(mProgress,mTotalComicCount);
        }
    }


    private void setComicColor(Comic comic)
    {
        try {
            int color;
            int primaryTextColor;
            int secondaryTextColor;

            if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                Palette.Swatch mutedSwatch = Palette.generate(thumbnail).getMutedSwatch();
                color = mutedSwatch.getRgb();
                primaryTextColor = mutedSwatch.getTitleTextColor();
                secondaryTextColor = mutedSwatch.getBodyTextColor();
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                Palette.Swatch lightVibrantSwatch = Palette.generate(thumbnail).getLightVibrantSwatch();
                color = lightVibrantSwatch.getRgb();
                primaryTextColor = lightVibrantSwatch.getTitleTextColor();
                secondaryTextColor = lightVibrantSwatch.getBodyTextColor();
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_3))) {
                color = getResources().getColor(R.color.WhiteBG);
                primaryTextColor = getResources().getColor(R.color.Black);
                secondaryTextColor = getResources().getColor(R.color.BlueGrey);
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_4))) {
                color = getResources().getColor(R.color.BlueGrey);
                primaryTextColor = getResources().getColor(R.color.White);
                secondaryTextColor = getResources().getColor(R.color.WhiteBG);
            }
            else {
                color = getResources().getColor(R.color.Black);
                primaryTextColor = getResources().getColor(R.color.White);
                secondaryTextColor = getResources().getColor(R.color.WhiteBG);
            }
            comic.setComicColor(color);
            comic.setPrimaryTextColor(primaryTextColor);
            comic.setSecondaryTextColor(secondaryTextColor);
        } catch (Exception e) {
            Log.e("Palette", e.getMessage());
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);

        for (int i=0;i<mComicList.size();i++)
        {
            savedState.putParcelable("Comic "+ (i+1), mComicList.get(i));
        }

        StringBuilder csvList = new StringBuilder();
        for(String s : mFilePaths){
            csvList.append(s);
            csvList.append(",");
        }

        savedState.putString("Filepaths",csvList.toString());
        
        savedState.putBoolean("Loaded",mFirstLoad);
    }
    
    
    
    @Override
    public void onPause()
    {
        super.onPause();
        mPrefSetter.saveFilePaths(this,mFilePaths, mExcludedPaths);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mPrefSetter.saveFilePaths(this, mFilePaths, mExcludedPaths);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();
        searchComics();
        calcComicColorsAsync();
        addOnRecyclerViewClickListener();
        
    }

    private void calcComicColorsAsync() {
        for (int i=0;i<mComicList.size();i++)
        {
            new SetColorTask().execute(mComicList.get(i),i);
        }
    }

    private class SetColorTask extends AsyncTask<Object,Void,Object>
    {
        @Override
        protected Object doInBackground(Object... params) {
            Comic comic = (Comic)params[0];
            int i = (Integer)params[1];

            try {
                int color;
                int primaryTextColor;
                int secondaryTextColor;
                
                if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                    Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                    Palette.Swatch mutedSwatch = Palette.generate(thumbnail).getMutedSwatch();
                    color = mutedSwatch.getRgb();
                    primaryTextColor = mutedSwatch.getTitleTextColor();
                    secondaryTextColor = mutedSwatch.getBodyTextColor();
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                    Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                    Palette.Swatch lightVibrantSwatch = Palette.generate(thumbnail).getLightVibrantSwatch();
                    color = lightVibrantSwatch.getRgb();
                    primaryTextColor = lightVibrantSwatch.getTitleTextColor();
                    secondaryTextColor = lightVibrantSwatch.getBodyTextColor();
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_3))) {
                    color = getResources().getColor(R.color.WhiteBG);
                    primaryTextColor = getResources().getColor(R.color.Black);
                    secondaryTextColor = getResources().getColor(R.color.BlueGrey);
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_4))) {
                    color = getResources().getColor(R.color.BlueGrey);
                    primaryTextColor = getResources().getColor(R.color.White);
                    secondaryTextColor = getResources().getColor(R.color.WhiteBG);
                }
                else {
                    color = getResources().getColor(R.color.Black);
                    primaryTextColor = getResources().getColor(R.color.White);
                    secondaryTextColor = getResources().getColor(R.color.WhiteBG);
                }
                if (comic.getComicColor()!=color
                        && comic.getPrimaryTextColor()!=primaryTextColor
                        && comic.getSecondaryTextColor()!=secondaryTextColor) {
                    comic.setComicColor(color);
                    comic.setPrimaryTextColor(primaryTextColor);
                    comic.setSecondaryTextColor(secondaryTextColor);
                }
                else
                {
                    return null;
                }
            } catch (Exception e) {
                Log.e("Palette", e.getMessage());
            }

            return i;
        }
        @Override
        protected void onPostExecute(Object param)
        {
            if (param!=null)
                mAdapter.notifyItemChanged(((int)param));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
            // Display the fragment as the main content.
            mPrefSetter.saveFilePaths(this,mFilePaths, mExcludedPaths);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id==R.id.action_about)
        {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void createRecyclerView()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.comic_list_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);


        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(120));
        mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));


    }

    private void addOnRecyclerViewClickListener()
    {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!mSwipeRefreshLayout.isRefreshing()) {
                    if (position < mComicList.size()) {
                        Log.d("ItemClick", mComicList.get(position).getTitle());
                        Intent intent = new Intent(getApplicationContext(), DisplayComicActivity.class);
                        intent.putExtra("Comic", mComicList.get(position));

                        if (mUseRecents) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        }
                        startActivity(intent);

                    }
                }
                else
                {
                    Toast loadMessage = Toast.makeText(ListActivity.this,"Please wait until loading has finished...",Toast.LENGTH_SHORT);
                    loadMessage.show();
                }
            }
        }));
    }

    private void initialiseAdapter(Bundle savedInstanceState)
    {
        
        mExcludedPaths = mPrefSetter.getExcludedPaths(this);
        mFilePaths = mPrefSetter.getFilePathsFromPreferences(this);
        
        if (savedInstanceState==null) {

            mComicList = new ArrayList<>();

            mAdapter = new ComicAdapter(this, mComicList);
            mRecyclerView.setAdapter(mAdapter);

        }
        else
        {
            mComicList = new ArrayList<>();

            for (int i=0;i<savedInstanceState.size();i++)
            {
                if (savedInstanceState.getParcelable("Comic "+ (i+1))!=null)
                    mComicList.add((Comic)savedInstanceState.getParcelable("Comic "+ (i+1)));
                mAdapter = new ComicAdapter(this, mComicList);
                mRecyclerView.setAdapter(mAdapter);
            }

            mFirstLoad = savedInstanceState.getBoolean("Loaded");
            
        }
    }

    
    
}
