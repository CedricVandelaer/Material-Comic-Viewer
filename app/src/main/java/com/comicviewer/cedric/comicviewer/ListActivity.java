package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private ProgressDialog mLoadDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        mPrefSetter = new PreferenceSetter();

        createRecyclerView();
        createFab();

        initialiseAdapter(savedInstanceState);

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

        // list of filenames
        ArrayList<String> files = new ArrayList<>();
        // list of directories to search from
        ArrayList<String> paths = new ArrayList<>();

        // map to map the filenames to their directories
        Map<String,String> map = new HashMap<>();

        // search for all files in that path
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
                    files.add(fileList[j].getName());
                    paths.add(path);
                }
            }
        }

        // map the filenames to their directories
        for (int i=0;i<files.size();i++) {
            map.put(files.get(i),paths.get(i));
        }

        //create treemap to sort the filenames
        Map<String,String> treemap = new TreeMap<>(map);

        mTotalComicCount = countComics(treemap);
        mProgress = 0;
        
        int i=0;
        showProgressDialog(mProgress, mTotalComicCount);
        
        // create array for the zips to extract after the rars
        Hashtable<Integer, Comic> zipsToExtract = new Hashtable<>();
        
        for (String str:treemap.keySet())
        {
            File file = new File(map.get(str)+"/"+str);
            if (!file.isDirectory()) {
                if (!comicFileInList(str)) {
                    
                    boolean isZip = isZipArchive(file);
                    boolean isRar = false;
                    if (!isZip)
                        isRar = isRarArchive(file);
                    
                    if (isRar || isZip) {
                        Comic newComic = new Comic(str, map.get(str));
                        mComicList.add(i,newComic);
                        mAdapter.notifyItemInserted(i);
                        if (isZip)
                        {
                            zipsToExtract.put(i, newComic);
                        } 
                        else
                        {
                            new ExtractRarTask().execute(newComic, i);
                        }
                        i++;
                    } 
                }
            }
        }
        
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
    
    private void showProgressDialog(int progress, int total)
    {

        if (mLoadDialog==null)
        {
            mLoadDialog = new ProgressDialog(this);
            mLoadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mLoadDialog.setCanceledOnTouchOutside(false);
            mLoadDialog.setInverseBackgroundForced(false);
            mLoadDialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        mLoadDialog.setMessage("Loading comic "+mProgress+" of "+mTotalComicCount);
        mLoadDialog.show();
        
        if (!(progress<total)) {
            mLoadDialog.cancel();
            mLoadDialog = null;
        }
        
    }
    
    private int countComics(Map treemap)
    {
        int count = 0;
        for (Object str:treemap.keySet())
        {
            if (!comicFileInList((String)str))
            {
                count++;
            }
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
            String filename=null;
            String path = newComic.getFilePath();
            String extractedImageFile = null;
            int pageCount = 0;

            ArrayList<String> pages = new ArrayList<String>();

            InputStream is;
            ZipInputStream zis;
            byte[] buffer = new byte[4096];

            try
            {
                is = new FileInputStream(path+"/"+ archivefilename);
                zis = new ZipInputStream(is);
                int count;

                ZipEntry ze = null;
                
                // search all comic pages
                while ((ze = zis.getNextEntry()) != null) {
                    filename = ze.getName();

                    if (filename.contains("/")) {
                        filename = filename.substring(filename.lastIndexOf("/") + 1);
                    }

                    if (ze.isDirectory()) {
                        continue;
                    }

                    if (isPicture(filename)) {
                        pageCount++;
                        pages.add(filename);
                    }
                }
                
                // sort the pages
                Collections.sort(pages);

                is = new FileInputStream(path+"/"+ archivefilename);
                zis = new ZipInputStream(is);
                
                // go through pages again
                while ((ze = zis.getNextEntry()) != null) {

                    // get the next entry name
                    filename = ze.getName();

                    if (filename.contains("/")) {
                        filename = filename.substring(filename.lastIndexOf("/") + 1);
                    }

                    // if entry == first page (cover page)->extract
                    if (filename.equals(pages.get(0))) {

                        File output;

                        output = new File(getFilesDir(), filename);

                        // check if file already extracted first
                        if (!output.exists()) {
                            FileOutputStream fout = new FileOutputStream(output);

                            while ((count = zis.read(buffer)) != -1) {
                                fout.write(buffer, 0, count);
                            }
                            fout.close();
                            zis.closeEntry();
                        }
                        extractedImageFile = filename;
                    }

                }
                
                zis.close();

                newComic.setCoverImage("file:" + getFilesDir().toString() + "/" + extractedImageFile);
                newComic.setPageCount(pageCount);

                setComicColor(newComic);


                return itemPosition;
            }
            catch(IOException e)
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
                mProgress++;
                showProgressDialog(mProgress,mTotalComicCount);
            }
        }
    }

    private class ExtractRarTask extends AsyncTask<Object, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Object... comicVar) {

            Comic newComic= (Comic)comicVar[0];
            int itemPosition = (Integer) comicVar[1];
            String filename = newComic.getFileName();
            ArrayList<FileHeader> pages= new ArrayList<FileHeader>();

            String path = newComic.getFilePath()+ "/" + filename;

            File comic = new File(path);

            try {
                Archive arch = new Archive(comic);
                List<FileHeader> fileheaders = arch.getFileHeaders();
                String extractedImageFile = null;

                int pageCount = 0;

                // search for comic pages in the archive
                for (int j = 0; j < fileheaders.size(); j++) {

                    if (isPicture(fileheaders.get(j).getFileNameString())) {
                        if (!fileheaders.get(j).isDirectory()) {
                            pages.add(fileheaders.get(j));
                            pageCount++;
                        }
                    }
                }

                // sort the pages
                Collections.sort(pages, new Comparator<FileHeader>() {
                    @Override
                    public int compare(FileHeader lhs, FileHeader rhs) {
                        int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getFileNameString(), rhs.getFileNameString());
                        if (res == 0) {
                            res = lhs.getFileNameString().compareTo(rhs.getFileNameString());
                        }
                        return res;
                    }
                });

                // the outputfilename
                extractedImageFile = pages.get(0).getFileNameString().substring(pages.get(0).getFileNameString().lastIndexOf("\\") + 1);

                // the output file
                File output = new File(getFilesDir(), extractedImageFile);

                // if file!=extracted -> extract
                if (!output.exists()) {
                    FileOutputStream os = new FileOutputStream(output);
                    arch.extractFile(pages.get(0), os);
                }


                newComic.setCoverImage("file:" + getFilesDir().toString() + "/" + extractedImageFile);
                newComic.setPageCount(pageCount);

                setComicColor(newComic);

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
                mProgress++;
                showProgressDialog(mProgress,mTotalComicCount);
            }
        }
    }

    public static Boolean isRarArchive(File filFile) {

        try {

            byte[] bytSignature = new byte[] {0x52, 0x61, 0x72, 0x21, 0x1a, 0x07, 0x00};
            FileInputStream fisFileInputStream = new FileInputStream(filFile);

            byte[] bytHeader = new byte[20];
            fisFileInputStream.read(bytHeader);

            Short shoFlags = (short) (((bytHeader[10]&0xFF)<<8) | (bytHeader[11]&0xFF));

            //Check if is an archive
            if (Arrays.equals(Arrays.copyOfRange(bytHeader, 0, 7), bytSignature)) {
                //Check if is a spanned archive
                if ((shoFlags & 0x0100) != 0) {
                    //Check if it the first part of a spanned archive
                    if ((shoFlags & 0x0001) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
    
    private boolean isPicture(String filename)
    {
        String extension = "notAPicture";
        try {
            extension = filename.substring(filename.lastIndexOf(".") + 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        if (extension.equals("jpg") || extension.equals("jpeg")
                || extension.equals("png"))
        {
            return true;
        }
        return false;
    }

    private void setComicColor(Comic comic)
    {
        try {
            
            int color;
            if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                color = Palette.generate(thumbnail, 32).getMutedColor(R.color.Teal);
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                color = Palette.generate(thumbnail, 32).getLightVibrantColor(R.color.Teal);
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_3))){
                color = getResources().getColor(R.color.WhiteBG);
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_4)))
            {
                color = getResources().getColor(R.color.BlueGrey);
            }
            else
            {
                color = getResources().getColor(R.color.Black);
            }
            
            comic.setComicColor(color);
        } catch (Exception e) {
            Log.e("Palette", e.getMessage());
        }
    }


    private boolean isZipArchive(File file) {
        try {
            InputStream is = new FileInputStream(file);
            boolean isZipped = new ZipInputStream(is).getNextEntry() != null;
            return isZipped;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
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
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        mPrefSetter.saveFilePaths(this,mFilePaths);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mPrefSetter.saveFilePaths(this, mFilePaths);
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
                if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                    Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                    color = Palette.generate(thumbnail, 32).getMutedColor(R.color.Teal);
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                    Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                    color = Palette.generate(thumbnail, 32).getLightVibrantColor(R.color.Teal);
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_3))) {
                    color = getResources().getColor(R.color.WhiteBG);
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_4))) {
                    color = getResources().getColor(R.color.BlueGrey);
                }
                else {
                    color = getResources().getColor(R.color.Black);
                }
                comic.setComicColor(color);
            } catch (Exception e) {
                Log.e("Palette", e.getMessage());
            }

            return i;
        }
        @Override
        protected void onPostExecute(Object param)
        {
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
            mPrefSetter.saveFilePaths(this,mFilePaths);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_refresh)
        {

            for (int i=mComicList.size()-1;i>=0;i--)
            {
                mComicList.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
            searchComics();
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
        }));
    }

    private void initialiseAdapter(Bundle savedInstanceState)
    {

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
            
        }
    }
}
