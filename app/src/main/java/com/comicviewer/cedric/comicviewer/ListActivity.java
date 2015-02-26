package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
                        mComicList.clear();
                        mAdapter.notifyDataSetChanged();
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
    }

    private void searchComics() {

        // list of filenames
        ArrayList<String> files = new ArrayList<>();
        // list of directories to search from
        ArrayList<String> paths = new ArrayList<>();

        // map to map the filenames to their directories
        Map<String,String> map = new HashMap<String,String>();

        // search for all files in that path
        for (int i=0;i<mFilePaths.size();i++)
        {
            String path = mFilePaths.get(i);
            File f = new File(path);
            f.mkdirs();

            File fileList[] = f.listFiles();

            for (int j=0;j<fileList.length;j++)
            {
                files.add(fileList[j].getName());
                paths.add(path);
            }
        }

        // remove already added comics
        if (mComicList!=null)
        {
            for (int i=0;i<mComicList.size();i++)
            {
                for (int j=0;j<files.size();j++)
                {
                    if (mComicList.get(i).getFileName().equals(files.get(j)))
                    {
                        files.remove(j);
                        paths.remove(j);
                    }
                }
            }
        }

        // map the filenames to their directories
        for (int i=0;i<files.size();i++) {
            map.put(files.get(i),paths.get(i));
        }

        //create treemap to sort the filenames
        Map<String,String> treemap = new TreeMap<String,String>(map);

        int i=0;
        for (String str:treemap.keySet())
        {
            File file = new File(str);
            if (!file.isDirectory()) {
                if (checkRar(str)) {
                    Comic newComic = new Comic(str, map.get(str));
                    new ExtractRarTask().execute(newComic, i);
                    i++;
                }
                else if (checkZip(str))
                {
                    Comic newComic = new Comic(str, map.get(str));
                    new ExtractZipTask().execute(newComic, i);
                    i++;
                }
            }
        }

    }

    private ArrayList<String> getFilePathsFromCSVList(String csvList) {
        
        Log.d("ListActivity", "getFilePathsFromCSVList called");
        ArrayList<String> paths = new ArrayList<>();

        String[] items = csvList.split(",");
        for(int i=0; i < items.length; i++){
            paths.add(items[i]);
        }
        //remove duplicates
        for (int i=0;i<paths.size();i++)
        {
            for (int j=0;j<paths.size();j++)
            {
                if (i!=j)
                {
                    if (paths.get(i).equals(paths.get(j)))
                    {
                        paths.remove(j);
                    }
                }
            }
        }
        return paths;
    }

    private class ExtractZipTask extends AsyncTask<Object, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Object... comicVar) {

            Comic newComic= (Comic)comicVar[0];
            int itemPosition = (Integer) comicVar[1];
            String filename = newComic.getFileName();
            String extractedImageFile = null;
            int pageCount = 0;

            InputStream is;
            ZipInputStream zis;
            try
            {
                String path = newComic.getFilePath();
                is = new FileInputStream(path + "/" + filename);
                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;
                byte[] buffer = new byte[1024];
                int count;

                boolean coverFound = false;

                Pattern p = Pattern.compile("\\d\\d");

                while ((ze = zis.getNextEntry()) != null)
                {
                    filename = ze.getName();

                    String coverFileIndex = filename.substring(filename.length() - 7);

                    // Need to create directories if not exists, or
                    // it will generate an Exception...
                    if (ze.isDirectory())
                    {
                        continue;
                    }

                    Matcher m = p.matcher(coverFileIndex);
                    if (m.find())
                        pageCount++;

                    File output;

                    if (filename.contains("/")) {
                        output = new File(getFilesDir(), filename.substring(filename.lastIndexOf("/")));
                    }
                    else {
                        output = new File(getFilesDir(), filename);
                    }

                    if (coverFileIndex.contains("000") && !coverFound) {

                        FileOutputStream fout = new FileOutputStream(output);

                        while ((count = zis.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
                        }
                        fout.close();

                        if (filename.contains("/"))
                            extractedImageFile = filename.substring(filename.lastIndexOf("/")+1);
                        else
                            extractedImageFile = filename;

                        coverFound = true;
                    } else if (coverFileIndex.contains("01") && !coverFound) {

                        FileOutputStream fout = new FileOutputStream(output);

                        while ((count = zis.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
                        }
                        fout.close();

                        if (filename.contains("/"))
                            extractedImageFile = filename.substring(filename.lastIndexOf("/")+1);
                        else
                            extractedImageFile = filename;

                        coverFound = true;
                    }

                    zis.closeEntry();
                }

                newComic.setCoverImage("file:" + getFilesDir().toString() + "/" + extractedImageFile);
                newComic.setPageCount(pageCount);
                zis.close();

                setComicColor(newComic);

                mComicList.add(newComic);

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

            //mAdapter.notifyDataSetChanged();
            if (itempos!=null)
                mAdapter.notifyItemInserted(itempos);
        }
    }

    private class ExtractRarTask extends AsyncTask<Object, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Object... comicVar) {

            Comic newComic= (Comic)comicVar[0];
            int itemPosition = (Integer) comicVar[1];
            String filename = newComic.getFileName();

            String path = newComic.getFilePath()+ "/" + filename;
            File comic = new File(path);
            try {
                Archive arch = new Archive(comic);
                List<FileHeader> fileheaders = arch.getFileHeaders();
                String extractedImageFile = null;

                int pageCount = 0;
                Pattern p = Pattern.compile("\\d\\d");

                boolean coverFound = false;
                for (int j = 0; j < fileheaders.size(); j++) {

                    String coverFileIndex = fileheaders.get(j).getFileNameString()
                            .substring(fileheaders.get(j).getFileNameString().length() - 7);
                    if (coverFileIndex.contains("000") && !coverFound) {
                        extractedImageFile = fileheaders.get(j).getFileNameString().substring(fileheaders.get(j).getFileNameString().lastIndexOf("\\")+1);
                        File output = new File(getFilesDir(), extractedImageFile);
                        FileOutputStream os = new FileOutputStream(output);

                        arch.extractFile(fileheaders.get(j), os);
                        coverFound = true;
                    } else if (coverFileIndex.contains("01") && !coverFound) {
                        extractedImageFile = fileheaders.get(j).getFileNameString().substring(fileheaders.get(j).getFileNameString().lastIndexOf("\\")+1);
                        File output = new File(getFilesDir(), extractedImageFile);
                        FileOutputStream os = new FileOutputStream(output);

                        arch.extractFile(fileheaders.get(j), os);
                        coverFound = true;
                    }

                    Matcher m = p.matcher(coverFileIndex);
                    if (m.find())
                        pageCount++;

                }
                newComic.setCoverImage("file:" + getFilesDir().toString() + "/" + extractedImageFile);
                newComic.setPageCount(pageCount);

                setComicColor(newComic);

                mComicList.add(newComic);
                return itemPosition;

            }
            catch (Exception e)
            {
                Log.e("ExtractRarTask", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer itempos) {
            super.onPostExecute(itempos);

            //mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemInserted(itempos);
        }
    }

    private void setComicColor(Comic comic)
    {
        try {
            Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
            int color;
            if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                color = Palette.generate(thumbnail, 32).getMutedColor(R.color.Teal);
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                color = Palette.generate(thumbnail, 32).getLightVibrantColor(R.color.Teal);
            }
            else {
                color = getResources().getColor(R.color.WhiteBG);
            }
            comic.setComicColor(color);
        } catch (Exception e) {
            Log.e("Palette", e.getMessage());
        }
    }

    private boolean checkRar(String filename)
    {
        int i=filename.lastIndexOf('.');
        String extension = null;
        File file = new File(filename);
        if (i>0)
            extension = filename.substring(i+1);
        try
        {
            if (extension.equals("rar") || extension.equals("cbr"))
                return true;
        }
        catch (Exception e)
        {
            Log.e("CheckRar", e.getMessage());
            Log.e("CheckRar", filename);
            return false;
        }

        return false;
    }

    private boolean checkZip(String filename)
    {
        int i=filename.lastIndexOf('.');
        String extension = null;
        if (i>0)
            extension = filename.substring(i+1);
        try
        {
            if (extension.equals("zip") || extension.equals("cbz"))
                return true;
        }
        catch (Exception e)
        {
            Log.e("CheckZip", e.getMessage());
            Log.e("CheckZip", filename);
            return false;
        }

        return false;
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
                Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(1000, 1000).centerInside().get();
                int color;
                if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                    color = Palette.generate(thumbnail, 32).getMutedColor(R.color.Teal);
                }
                else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                    color = Palette.generate(thumbnail, 32).getLightVibrantColor(R.color.Teal);
                }
                else {
                    color = getResources().getColor(R.color.WhiteBG);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            ArrayList<CharSequence> charsequencePathsList = new ArrayList<>();
            for (int i=0;i<mFilePaths.size();i++)
            {
                charsequencePathsList.add(mFilePaths.get(i));
                Log.d("Path", mFilePaths.get(i));
            }
            intent.putCharSequenceArrayListExtra("pathList",charsequencePathsList);
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

        if (savedInstanceState==null) {

            mFilePaths = mPrefSetter.getFilePathsFromPreferences(this);

            mComicList = new ArrayList<Comic>();

            mAdapter = new ComicAdapter(this, mComicList);
            mRecyclerView.setAdapter(mAdapter);

            searchComics();
        }
        else
        {
            mComicList = new ArrayList<Comic>();

            for (int i=0;i<savedInstanceState.size();i++)
            {
                if ((Comic)savedInstanceState.getParcelable("Comic "+ (i+1))!=null)
                    mComicList.add((Comic)savedInstanceState.getParcelable("Comic "+ (i+1)));
                mAdapter = new ComicAdapter(getApplicationContext(), mComicList);
                mRecyclerView.setAdapter(mAdapter);
            }
            
            if (savedInstanceState.getString("Filepaths")!=null)
            {
                mFilePaths = getFilePathsFromCSVList(savedInstanceState.getString("Filepaths"));
            }
            else
            {
                mFilePaths = new ArrayList<>();
                mFilePaths.add(Environment.getExternalStorageDirectory().toString() + "/ComicViewer");
            }

        }
    }
}
