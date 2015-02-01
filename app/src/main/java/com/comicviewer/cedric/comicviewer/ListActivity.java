package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ListActivity extends Activity {

    ArrayList<Comic> mComicList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTransitions();

        setContentView(R.layout.activity_list);
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);



        createRecyclerView();

        initialiseAdapter(savedInstanceState);




    }

    private void setPreferences() {
        View layout = getWindow().getDecorView().getRootView();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bgcolor = prefs.getString("backgroundColor", getString(R.string.backgroundcolor_setting2));

        if (bgcolor.equals(getString(R.string.backgroundcolor_setting1)))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.BlueGrey));
        }
        else if (bgcolor.equals(getString(R.string.backgroundcolor_setting2)))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.Black));
        }
        else
        {
            layout.setBackgroundColor(getResources().getColor(R.color.WhiteBG));
        }
    }

    private void setTransitions() {
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    }


    private void searchComics(String path) {
        File f = new File(path);
        f.mkdirs();
        File file[] = f.listFiles();

        Arrays.sort(file);

        for (int i=0; i < file.length; i++)
        {
            if (checkRar(file[i].getName())) {
                Comic newComic = new Comic(file[i].getName(), path);
                new ExtractRarTask().execute(newComic, i);
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

            String path = newComic.getFilePath()+ "/" + filename;
            File comic = new File(path);
            try {
                Archive arch = new Archive(comic);
                List<FileHeader> fileheaders = arch.getFileHeaders();
                String extractedImageFile = filename.substring(filename.lastIndexOf("\\")+1);
                File output = new File(getFilesDir(), extractedImageFile);
                FileOutputStream os = new FileOutputStream(output);

                int pageCount = 0;
                Pattern p = Pattern.compile("\\d\\d");

                boolean coverFound = false;
                for (int j = 0; j < fileheaders.size(); j++) {

                    String coverFileIndex = fileheaders.get(j).getFileNameString()
                            .substring(fileheaders.get(j).getFileNameString().length() - 7);
                    if (coverFileIndex.contains("000") && !coverFound) {
                        arch.extractFile(fileheaders.get(j), os);
                        coverFound = true;
                    } else if (coverFileIndex.contains("01") && !coverFound) {
                        arch.extractFile(fileheaders.get(j), os);
                        coverFound = true;
                    }

                    Matcher m = p.matcher(coverFileIndex);
                    if (m.find())
                        pageCount++;

                }
                newComic.setCoverImage("file:" + getFilesDir().toString() + "/" + filename.substring(0, filename.lastIndexOf('.')) + "-cover.jpg");
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
            Bitmap thumbnail = Picasso.with(getApplicationContext()).load(comic.getCoverImage()).resize(150, 150).get();
            int color = Palette.generate(thumbnail, 32).getMutedColor(R.color.Teal);
            comic.setComicColor(color);
        } catch (Exception e) {
            Log.e("Palette", e.getMessage());
        }
    }

    private boolean checkRar(String filename)
    {
        int i=filename.lastIndexOf('.');
        String extension = null;
        if (i>0)
            extension = filename.substring(i+1);
        if (extension.equals("rar") || extension.equals("cbr"))
            return true;
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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();
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
            startActivity(intent);
            return true;
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

        mRecyclerView.addItemDecoration(new DividerItemDecoration(80));
        mRecyclerView.setItemAnimator(new ScaleInOutItemAnimator(mRecyclerView));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < mComicList.size()) {
                    Log.d("ItemClick", mComicList.get(position).getTitle());
                    Picasso.with(getApplicationContext()).load(mComicList.get(position).getCoverImage()).fetch();
                    Intent intent = new Intent(getApplicationContext(), DisplayComicActivity.class);
                    intent.putExtra("Comic", mComicList.get(position));
                    Bundle transitionbundle = ActivityOptions.makeSceneTransitionAnimation(ListActivity.this).toBundle();
                    startActivity(intent, transitionbundle);
                }
            }
        }));
    }

    private void initialiseAdapter(Bundle savedInstanceState)
    {
        if (savedInstanceState==null) {
            mComicList = new ArrayList<Comic>();

            // specify an adapter (see also next example)
            mAdapter = new ComicAdapter(getApplicationContext(), mComicList);
            mRecyclerView.setAdapter(mAdapter);

            String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";

            searchComics(defaultPath);
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
        }
    }
}
