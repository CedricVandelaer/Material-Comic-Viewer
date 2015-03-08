package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Comic;
import com.comicviewer.cedric.comicviewer.ComicSearchView;
import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.github.junrar.Archive;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComicListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComicListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ArrayList<Comic> mComicList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private String mCardColorSetting;
    private ArrayList<String> mFilePaths;
    private boolean mUseRecents;
    private int mProgress;
    private int mTotalComicCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mFirstLoad;
    private ArrayList<String> mExcludedPaths;
    private ComicSearchView mSearchView;
    private ArrayList<Comic> filteredList;
    private boolean isFiltered;

    public static ComicListFragment newInstance() {
        ComicListFragment fragment = new ComicListFragment();

        return fragment;
    }

    public ComicListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_comic_list, container, false);

        mFirstLoad = true;
        isFiltered = false;
        
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        createRecyclerView(v);
        createFab(v);

        initialiseAdapter(savedInstanceState);

        initialiseRefresh(v);
        
        // Inflate the layout for this fragment
        return v;

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    private void initialiseRefresh(View v)
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchComics();
            }
        });

    }

    private void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(Environment.getExternalStorageDirectory().getPath());
                FileDialog dialog = new FileDialog(getActivity(), path);
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mCardColorSetting = prefs.getString("cardColor", getString(R.string.card_color_setting_1));

        PreferenceSetter.setBackgroundColorPreference(getActivity());

        mUseRecents = prefs.getBoolean("useRecents",true);

        mFilePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());
    }

    private void searchComics() {

        ArrayList<String> subFolders = searchSubFolders(mFilePaths);

        Map<String,String> map = findFilesInPaths(subFolders);

        //create treemap to sort the filenames
        Map<String,String> treemap = new TreeMap(map);

        mTotalComicCount = countComics(treemap);
        mProgress = 0;

        updateProgressDialog(mProgress, mTotalComicCount);
   

        int i=0;
        
        // create array for the zips to extract after the rars
        Hashtable<Integer, Comic> rarsToExtract = new Hashtable<>();
        Hashtable<Integer, Comic> zipsToExtract = new Hashtable<>();

        for (String str:treemap.keySet())
        {

            File file = new File(map.get(str)+"/"+str);

            if (getComicPositionInList(str)==-1) {
                if (Utilities.checkExtension(str)) {
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
                            rarsToExtract.put(i, newComic);
                        }
                        i++;
                    } else {
                        mProgress++;
                        updateProgressDialog(mProgress, mTotalComicCount);
                    }
                }
                else
                {
                    mProgress++;
                    updateProgressDialog(mProgress, mTotalComicCount);
                }
            }
            else
            {
                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);
            }
        }


        // extract found rars
        Enumeration e = rarsToExtract.keys();

        while (e.hasMoreElements())
        {
            Integer key = (Integer) e.nextElement();
            new ExtractRarTask().execute(rarsToExtract.get(key),key);
        }
        
        
        // extract found zips
        e = zipsToExtract.keys();

        while (e.hasMoreElements())
        {
            Integer key = (Integer) e.nextElement();
            new ExtractZipTask().execute(zipsToExtract.get(key),key);
        }

    }
    
    private void removeDoubleComics()
    {
        for (int i=0;i<mComicList.size();i++)
        {
            for (int j=i+1;j<mComicList.size()-1;j++)
            {
                if (mComicList.get(i).getFileName().equals(mComicList.get(j).getFileName()))
                {
                    mComicList.remove(j);
                    mAdapter.notifyItemRemoved(j);
                }
            }
        }
        
    }
    
    private int getComicPositionInList(String filename)
    {
        for (int pos=0;pos<mComicList.size();pos++)
        {
            if (mComicList.get(pos).getFileName().equals(filename))
                return pos;
        }
        return -1;
    }


    private Map findFilesInPaths(ArrayList<String> pathsToSearch)
    {
        // list of filenames
        ArrayList<String> files = new ArrayList<>();
        // list of directories to search from
        ArrayList<String> paths = new ArrayList<>();

        // map to map the filenames to their directories
        Map<String,String> map = new HashMap<>();

        // search for all files in all paths
        for (int i=0;i<pathsToSearch.size();i++)
        {
            String path = pathsToSearch.get(i);
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

    private void updateProgressDialog(int progress, int total)
    {
        if (mProgress==0)
        {
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
        }
        
        if (progress>=total) {
            onLoadingFinished();
        }
    }
    
    private void onLoadingFinished()
    {
        mSwipeRefreshLayout.setRefreshing(false);
        removeDoubleComics();
        removeOldComics();
        calcComicColorsAsync();        
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

    private void removeOldComics()
    {
        ArrayList<String> subFolders = searchSubFolders(mFilePaths);

        Map<String,String> map = findFilesInPaths(subFolders);

        //create treemap to sort the filenames
        Map<String,String> treemap = new TreeMap(map);
        
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
                if (pages.size()>0)
                    extractedImageFile = pages.get(0).getFileName().substring(pages.get(0).getFileName().lastIndexOf("\\") + 1);
                
                // get rid of special chars causing problems
                if (extractedImageFile!=null && extractedImageFile.contains("#"))
                    extractedImageFile = extractedImageFile.replaceAll("#","");

                // the output file
                File output = new File(getActivity().getFilesDir(), extractedImageFile);

                // if file!=extracted -> extract
                if (!(isAlreadyExtracted=output.exists())) {
                    if (pages.size()>0)
                        zipFile.extractFile(pages.get(0), getActivity().getFilesDir().getAbsolutePath());
                    else
                        return null;
                }

                String coverImage = "file:///" + getActivity().getFilesDir().toString() + "/" + extractedImageFile;

                if (newComic.getCoverImage()==null) {
                    newComic.setCoverImage(coverImage);
                    newComic.setPageCount(pageCount);
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
            
            if (isAdded()) {
                if (itempos != null) {
                    mAdapter.notifyItemChanged(itempos);
                }
                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);
            }
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
                if (pages.size()>0)
                    extractedImageFile = pages.get(0).getFileNameString().substring(pages.get(0).getFileNameString().lastIndexOf("\\") + 1);

                // get rid of special chars causing problems
                if (extractedImageFile!=null && extractedImageFile.contains("#"))
                    extractedImageFile = extractedImageFile.replaceAll("#","");

                // the output file
                File output = new File(getActivity().getFilesDir(), extractedImageFile);

                // if file!=extracted -> extract
                if (!(isAlreadyExtracted=output.exists())) {
                    FileOutputStream os = new FileOutputStream(output);
                    if (pages.size()>0)
                        arch.extractFile(pages.get(0), os);
                    else 
                        return null;
                }

                String coverImage = "file:///" + getActivity().getFilesDir().toString() + "/" + extractedImageFile;

                if (newComic.getCoverImage()==null) {
                    newComic.setCoverImage(coverImage);
                    newComic.setPageCount(pageCount);

                }
                else
                {
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

            if (isAdded()) {
                if (itempos != null) {
                    mAdapter.notifyItemChanged(itempos);
                }
                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);
            }
        }
    }


    /**
     * Function to change the colortheme of a comic* 
     * @param comic the comic for which the color should be calculated
     * @return true if the color has changed, false if otherwise
     */
    private boolean setComicColor(Comic comic)
    {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
            ImageLoader.getInstance().init(config);
        }
        
        try {
            int color;
            int primaryTextColor;
            int secondaryTextColor;

            if (mCardColorSetting.equals(getString(R.string.card_color_setting_1))) {
                ImageSize imageSize = new ImageSize(850,500);
                Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(comic.getCoverImage(),imageSize);
                Palette.Swatch mutedSwatch = Palette.generate(thumbnail).getMutedSwatch();
                color = mutedSwatch.getRgb();
                primaryTextColor = mutedSwatch.getTitleTextColor();
                secondaryTextColor = mutedSwatch.getBodyTextColor();
            }
            else if(mCardColorSetting.equals(getString(R.string.card_color_setting_2))) {
                ImageSize imageSize = new ImageSize(850,500);
                Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(comic.getCoverImage(),imageSize);
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
                    || comic.getPrimaryTextColor()!=primaryTextColor
                    || comic.getSecondaryTextColor()!=secondaryTextColor) {
                comic.setComicColor(color);
                comic.setPrimaryTextColor(primaryTextColor);
                comic.setSecondaryTextColor(secondaryTextColor);
            }
            else
            {
                return false;
            }
        } catch (Exception e) {
            Log.e("Palette", e.getMessage());
        }
        
        return true;
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
        PreferenceSetter.saveFilePaths(getActivity(),mFilePaths, mExcludedPaths);
        enableSearchBar(false);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        PreferenceSetter.saveFilePaths(getActivity(), mFilePaths, mExcludedPaths);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();
        searchComics();
        addOnRecyclerViewClickListener();
    
        enableSearchBar(true);

        if (isFiltered)
            filterList("");
    }
    
    private void enableSearchBar(boolean enabled)
    {
        if (enabled) {
            final Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            mSearchView = new ComicSearchView(getActivity());
            
            
            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(Gravity.RIGHT);

            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    filterList("");
                    return false;
                }
            });
            
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterList(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    
                    filterList(newText);
                    return false;
                }
            });
            
            
            toolbar.addView(mSearchView, layoutParamsCollapsed);
        }
        else
        {
            Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            toolbar.removeView(mSearchView);
        }
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
            
            if (setComicColor(comic))
                return i;
            else
                return null;
        }
        @Override
        protected void onPostExecute(Object param)
        {
            if (param!=null && isAdded())
            {
                mAdapter.notifyItemChanged(((int)param));
            }
        }
    }

    private void createRecyclerView(View v)
    {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.comic_list_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());


        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(120));
        mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));


    }

    private void addOnRecyclerViewClickListener()
    {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!mSwipeRefreshLayout.isRefreshing()) {
                    
                    if (!isFiltered) {
                        if (position < mComicList.size()) {
                            Log.d("ItemClick", mComicList.get(position).getTitle());
                            Intent intent = new Intent(getActivity(), DisplayComicActivity.class);
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
                        if (position < filteredList.size()) {
                            Log.d("ItemClick", filteredList.get(position).getTitle());
                            Intent intent = new Intent(getActivity(), DisplayComicActivity.class);
                            intent.putExtra("Comic", filteredList.get(position));

                            if (mUseRecents) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            }
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    Toast loadMessage = Toast.makeText(getActivity(),"Please wait until loading has finished...",Toast.LENGTH_SHORT);
                    loadMessage.show();
                }
            }
        }));
    }

    private void initialiseAdapter(Bundle savedInstanceState)
    {

        mExcludedPaths = PreferenceSetter.getExcludedPaths(getActivity());
        mFilePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());

        if (savedInstanceState==null) {

            mComicList = new ArrayList<>();

            mAdapter = new ComicAdapter(getActivity(), mComicList);
            mRecyclerView.setAdapter(mAdapter);

        }
        else
        {
            mComicList = new ArrayList<>();

            for (int i=0;i<savedInstanceState.size();i++)
            {
                if (savedInstanceState.getParcelable("Comic "+ (i+1))!=null)
                    mComicList.add((Comic)savedInstanceState.getParcelable("Comic "+ (i+1)));
                mAdapter = new ComicAdapter(getActivity(), mComicList);
                mRecyclerView.setAdapter(mAdapter);
            }
            
            mFirstLoad = savedInstanceState.getBoolean("Loaded");

        }
    }
    
    private void filterList(String query)
    {
        if (query!="") {
            isFiltered = true;
            
            filteredList = new ArrayList<>();
            
            for (int i = 0; i < mComicList.size(); i++) {
                
                boolean found = false;
                

                if (mComicList.get(i).getFileName().toLowerCase().contains(query.toLowerCase())) {
                    found = true;
                }
                else if ((mComicList.get(i).getTitle().toLowerCase()+" "+mComicList.get(i).getIssueNumber()).contains(query.toLowerCase()))
                {
                    found=true;
                }
                if (found)
                    filteredList.add(mComicList.get(i));
            }
            RecyclerView.Adapter tempAdapter = new ComicAdapter(getActivity(), filteredList);
            mRecyclerView.swapAdapter(tempAdapter,false);
        }
        else
        {
            mRecyclerView.setAdapter(mAdapter);
            isFiltered = false;
        }
        
    }

}
