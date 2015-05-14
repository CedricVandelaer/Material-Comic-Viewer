package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PauseOnScrollListener;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PreCachingLayoutManager;
import com.comicviewer.cedric.comicviewer.Utilities;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;


public class ComicListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ComicAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private int mProgress;
    private int mTotalComicCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private ArrayList<Object> filteredList;
    private boolean isFiltered;
    private static ComicListFragment mSingleton = null;
    private Context mApplicationContext;
    private Handler mHandler;
    private SearchComicsTask mSearchComicsTask=null;
    private ImageButton mFolderViewToggleButton;
    private static String ROOT="root";

    public static ComicListFragment getInstance() {
        if(mSingleton == null) {
            mSingleton = newInstance();
        }
        return mSingleton;
    }

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

        isFiltered = false;
        mHandler = new Handler();

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        createRecyclerView(v);
        createFab(v);

        initialiseRefresh(v);

        initialiseVariables(savedInstanceState);

        // Inflate the layout for this fragment
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mApplicationContext = getActivity().getApplicationContext();
    }

    private class SearchComicsTask extends AsyncTask{

        @Override
        protected void onPreExecute()
        {
            enableSearchBar(false);
            addShowFolderViewButton(false);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext)) {
                searchComicsAndFolders();
            } else {
                searchComics();
            }

            mSearchComicsTask = null;

            return null;
        }

        @Override
        protected void onPostExecute(Object object)
        {
            addShowFolderViewButton(true);
            enableSearchBar(true);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity==null)
            Log.d("OnAttach", "Activity is null");
        else
            Log.d("OnAttach", "Activity is not null");
        mApplicationContext = activity;
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
                if (!isFiltered)
                    refresh();
            }
        });
    }

    public void refresh()
    {
        if (mSearchComicsTask!=null) {
            mSearchComicsTask.cancel(true);
        }

        mAdapter.clearList();

        mSearchComicsTask = new SearchComicsTask();
        mSearchComicsTask.execute();


    }

    private void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(PreferenceSetter.getAppThemeColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(PreferenceSetter.getAppThemeColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(Environment.getExternalStorageDirectory().getPath());
                FileDialog dialog = new FileDialog(getActivity(), path);
                dialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(File directory) {
                        Log.d(getClass().getName(), "selected dir " + directory.toString());

                        ArrayList<String> filePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());
                        ArrayList<String> excludedPaths = PreferenceSetter.getExcludedPaths(getActivity());

                        if (!filePaths.contains(directory.toString()))
                            filePaths.add(directory.toString());
                        if (excludedPaths.contains(directory.toString()))
                            excludedPaths.remove(directory.toString());
                        PreferenceSetter.saveFilePaths(getActivity(),filePaths,excludedPaths);
                        refresh();
                    }
                });
                dialog.setSelectDirectoryOption(true);
                dialog.showDialog();

            }
        });
    }

    private void setPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceSetter.setBackgroundColorPreference(getActivity());

        if (prefs.getString("cardSize", "Normal cards").equals(getString(R.string.card_size_setting_3)))
        {
            mRecyclerView.setItemViewCacheSize(10);
        }
    }

    private void updateLastReadComics()
    {

        String lastReadComic = PreferenceSetter.getLastReadComic(mApplicationContext);

        for (int i=0;i<mAdapter.getComicsAndFiles().size();i++)
        {
            if (mAdapter.getComicsAndFiles().get(i) instanceof Comic) {

                Comic comic = (Comic) mAdapter.getComicsAndFiles().get(i);
                if (comic.getFileName().equals(lastReadComic)) {
                    final int pos = i;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(pos);
                        }
                    });
                }
            }
        }
    }

    private int getComicPositionInList(String filename)
    {
        List<Object> currentComics = mAdapter.getComicsAndFiles();

        for (int pos=0;pos<currentComics.size();pos++)
        {
            if (currentComics.get(pos) instanceof Comic) {
                if (((Comic)(currentComics.get(pos))).getFileName().equals(filename))
                    return pos;
            }
            else
            {
                File folder = (File) currentComics.get(pos);
                if (folder.getName().equals(filename))
                {
                    return pos;
                }
            }
        }
        return -1;
    }

    private void updateProgressDialog(int progress, int total)
    {
        if (mProgress==0)
        {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        if (progress>=total) {
            onLoadingFinished();
        }
    }

    private void onLoadingFinished()
    {

        /*
        if (mSearchComicsTask != null && !mSearchComicsTask.isCancelled() && !PreferenceSetter.getFolderEnabledSetting(mApplicationContext
        )) {
            PreferenceSetter.saveComicList(mApplicationContext, mAdapter.getComics());
        }
        */

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);

        List<Object> currentList = mAdapter.getComicsAndFiles();

        for (int i=0;i<currentList.size();i++)
        {
            if (currentList.get(i) instanceof File)
            {
                savedState.putSerializable("Folder "+ (i+1), (File)currentList.get(i));
            }
            else if (currentList.get(i) instanceof Comic)
            {
                savedState.putParcelable("Comic "+ (i+1),(Comic) currentList.get(i));
            }
        }


        savedState.putBoolean("isRefreshing", mSwipeRefreshLayout.isRefreshing());
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mSearchComicsTask != null)
        {
            mSearchComicsTask.cancel(true);
        }

        enableSearchBar(false);
        addShowFolderViewButton(false);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();

        if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext))
        {
            if (NavigationManager.getInstance().fileStackEmpty())
            {
                mAdapter.clearList();
                NavigationManager.getInstance().resetFileStack();
            }
        }

        addShowFolderViewButton(true);
        enableSearchBar(true);

        if (isFiltered)
            filterList("");

        if (mSearchComicsTask == null) {
            mSearchComicsTask= new SearchComicsTask();
            mSearchComicsTask.execute();
        }
    }

    public Handler getHandler()
    {
        return mHandler;
    }

    private void addShowFolderViewButton(boolean enable) {

        if (enable && getActivity()!=null) {
            final Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            mFolderViewToggleButton = new ImageButton(getActivity());
            mFolderViewToggleButton.setAlpha(0.75f);
            if (Build.VERSION.SDK_INT>15)
                mFolderViewToggleButton.setBackground(null);
            else
                mFolderViewToggleButton.getBackground().setAlpha(0);

            if (PreferenceSetter.getFolderEnabledSetting(getActivity()))
            {
                mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
            }
            else
            {
                mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
            }

            mFolderViewToggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferenceSetter.getFolderEnabledSetting(getActivity())) {
                        PreferenceSetter.setFolderEnabledSetting(getActivity(), false);
                        NavigationManager.getInstance().resetFileStack();
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
                        refresh();
                    } else {
                        PreferenceSetter.setFolderEnabledSetting(getActivity(), true);
                        NavigationManager.getInstance().resetFileStack();
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                        refresh();
                    }
                }
            });

            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(Gravity.RIGHT);
            toolbar.addView(mFolderViewToggleButton, layoutParamsCollapsed);
        }
        else
        {
            if (getActivity()!=null) {
                Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
                toolbar.removeView(mFolderViewToggleButton);
            }
        }
    }

    private void enableSearchBar(boolean enabled)
    {
        if (enabled && getActivity()!=null) {
            final Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            mSearchView = new SearchView(getActivity());

            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(Gravity.RIGHT);

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
            if (getActivity()!=null) {
                Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
                toolbar.removeView(mSearchView);
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
        int height;

        if (PreferenceSetter.getCardAppearanceSetting(getActivity()).equals(getActivity().getString(R.string.card_size_setting_3))) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            Log.d("Layoutmanager", "Extra height: " + height);
        }
        else
        {
            height = 0;
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        //in pixels
        float dpWidthPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, outMetrics);
        float cardWidthPixels = getResources().getDimension(R.dimen.list_width);
        int columnCount = 1;

        //14 dp in pixels
        int vSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, outMetrics);

        Log.d("List fragment:","Device width dp:"+dpWidth);


        if (dpWidth>=1280)
        {
            columnCount = 3;
            mLayoutManager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        }
        else if (dpWidth>=598)
        {
            columnCount = 2;
            mLayoutManager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        }
        else
        {
            mLayoutManager = new PreCachingLayoutManager(getActivity(), height);
        }

        //in pixels
        int hSpace = (int) Math.abs((dpWidthPixels-cardWidthPixels)/(columnCount+1));

        mRecyclerView.addItemDecoration(new DividerItemDecoration(vSpace, hSpace, columnCount));

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        PauseOnScrollListener scrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, false);

        mRecyclerView.setOnScrollListener(scrollListener);

    }

    private void initialiseVariables(Bundle savedInstanceState)
    {

        mAdapter = new ComicAdapter(mApplicationContext);
        mRecyclerView.setAdapter(mAdapter);


        if (!(savedInstanceState==null))
        {
            for (int i=0;i<savedInstanceState.size();i++)
            {

                if (savedInstanceState.getParcelable("Comic "+ (i+1))!=null)
                    mAdapter.addObject(savedInstanceState.getParcelable("Comic " + (i + 1)));
            }

            for (int i=savedInstanceState.size();i>=0;i--)
            {
                if (savedInstanceState.getSerializable("Folder "+ (i+1))!=null)
                    mAdapter.addObject(savedInstanceState.getSerializable("Folder " + (i + 1)));
            }

        }
    }


    private void filterList(String query)
    {
        if (!query.equals("")) {
            isFiltered = true;

            filteredList = new ArrayList<>();
            List<Object> currentComics = mAdapter.getComicsAndFiles();

            for (int i = 0; i < currentComics.size(); i++) {

                boolean found = false;

                if (currentComics.get(i) instanceof Comic) {
                    Comic comic = (Comic) currentComics.get(i);

                    if (comic.getFileName().toLowerCase().contains(query.toLowerCase())) {
                        found = true;
                    } else if ((comic.getTitle().toLowerCase() + " " + comic.getIssueNumber()).contains(query.toLowerCase())) {
                        found = true;
                    }
                }
                else
                {
                    File folder = (File) currentComics.get(i);
                    if (folder.getName().toLowerCase().contains(query.toLowerCase()))
                    {
                        found = true;
                    }
                }
                if (found)
                    filteredList.add(currentComics.get(i));
            }
            ComicAdapter tempAdapter = new ComicAdapter(getActivity(), filteredList);
            tempAdapter.setRootAdapter(mAdapter);
            mRecyclerView.swapAdapter(tempAdapter,false);
        }
        else
        {
            mRecyclerView.setAdapter(mAdapter);
            isFiltered = false;
        }

    }

    private void searchComics() {
        //map of <filename, filepath>
        Map<String,String> map = FileLoader.searchComics(mApplicationContext);

        TreeMap<String, String> treemap = new TreeMap<>(map);

        long startTime = System.currentTimeMillis();

        List<Comic> currentComics = mAdapter.getComics();
        ArrayList<Comic> savedComics = PreferenceSetter.getSavedComics(mApplicationContext);
        List<String> savedComicsFileNames = new ArrayList<>();

        for (int i=0;i<savedComics.size();i++)
        {
            savedComicsFileNames.add(savedComics.get(i).getFilePath()+"/"+savedComics.get(i).getFileName());
        }

        List<String> currentComicsFileNames = new ArrayList<>();

        for (int i=0;i<currentComics.size();i++)
        {
            currentComicsFileNames.add(currentComics.get(i).getFilePath()+"/"+currentComics.get(i).getFileName());
        }

        mTotalComicCount = map.size();
        mProgress = 0;

        updateProgressDialog(mProgress, mTotalComicCount);

        for (String str:treemap.keySet())
        {
            if (mSearchComicsTask!= null && mSearchComicsTask.isCancelled()) {
                mAdapter.clearList();
                break;
            }

            //open the new found file
            final String comicPath = map.get(str)+"/"+str;
            File file = new File(comicPath);

            //check if comic is one of the saved comic files and add
            if (savedComicsFileNames.contains(comicPath) && !(currentComicsFileNames.contains(comicPath)))
            {
                int pos = savedComicsFileNames.indexOf(comicPath);

                Comic comic = savedComics.get(pos);

                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()))
                {
                    PreferenceSetter.addAddedComic(mApplicationContext, comic.getFileName());
                }

                ComicLoader.generateComicInfo(mApplicationContext, comic);

                if (ComicLoader.setComicColor(mApplicationContext, comic))
                    PreferenceSetter.saveComic(mApplicationContext, comic);

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                    }
                });

                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);

            }//if it is a newly added comic
            else if (getComicPositionInList(str)==-1
                    && Utilities.checkExtension(str)
                    && (Utilities.isZipArchive(file) || Utilities.isRarArchive(file))) {

                Comic comic = new Comic(str, map.get(str));

                ComicLoader.loadComicSync( mApplicationContext, comic);

                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()) && comic.getPageCount()>0)
                {
                    PreferenceSetter.addAddedComic(mApplicationContext, comic.getFileName());
                }

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                    }
                });

                PreferenceSetter.saveComic(getActivity(), comic);

                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);

            }
            else // if it's not a valid comic file
            {
                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);
            }
        }

        updateLastReadComics();

        long endTime = System.currentTimeMillis();

        Log.d("search comics in list", "time: "+(endTime-startTime));
    }

    private void searchComicsAndFolders() {
        if (NavigationManager.getInstance().fileStackEmpty())
            return;
        //map of <filename, filepath>
        Map<String,String> map = FileLoader.searchComicsAndFolders(mApplicationContext, NavigationManager.getInstance().getPathFromFileStack());

        TreeMap<String, String> treemap = new TreeMap<>(map);

        long startTime = System.currentTimeMillis();

        List<Object> currentObjects = mAdapter.getComicsAndFiles();
        ArrayList<Comic> savedComics = PreferenceSetter.getSavedComics(mApplicationContext);
        List<String> savedComicsFileNames = new ArrayList<>();

        for (int i=0;i<savedComics.size();i++)
        {
            savedComicsFileNames.add(savedComics.get(i).getFilePath()+"/"+savedComics.get(i).getFileName());
        }

        List<String> currentComicsFileNames = new ArrayList<>();
        List<String> currentFolderNames = new ArrayList<>();

        for (int i=0;i<currentObjects.size();i++)
        {
            if (currentObjects.get(i) instanceof Comic) {
                Comic comic = (Comic) currentObjects.get(i);
                currentComicsFileNames.add(comic.getFilePath() + "/" + comic.getFileName());
            }
            else if(currentObjects.get(i) instanceof File)
            {
                File folder = (File) currentObjects.get(i);
                currentFolderNames.add(folder.getName());
            }
        }

        mTotalComicCount = treemap.size();
        mProgress = 0;

        updateProgressDialog(mProgress, mTotalComicCount);

        for (String str:treemap.keySet())
        {
            if (mSearchComicsTask!= null && mSearchComicsTask.isCancelled())
                break;

            //open the new found file
            final String comicPath = map.get(str)+"/"+str;
            final File file = new File(comicPath);

            //this is a folder
            if (file.isDirectory() && !(currentFolderNames.contains(file.getName()))) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(file);
                        mRecyclerView.scrollToPosition(0);
                    }
                });

                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);

            }//check if comic is one of the saved comic files and add
            else if (savedComicsFileNames.contains(comicPath) && !(currentComicsFileNames.contains(comicPath)))
            {
                int pos = savedComicsFileNames.indexOf(comicPath);


                Comic comic = savedComics.get(pos);

                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()))
                {
                    PreferenceSetter.addAddedComic(mApplicationContext, comic.getFileName());
                }

                ComicLoader.generateComicInfo(mApplicationContext, comic);

                if (ComicLoader.setComicColor(mApplicationContext, comic))
                    PreferenceSetter.saveComic(mApplicationContext, comic);

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                        mRecyclerView.scrollToPosition(0);
                    }
                });

                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);

            }//if it is a newly added comic
            else if (getComicPositionInList(str)==-1
                    && Utilities.checkExtension(str)
                    && (Utilities.isZipArchive(file) || Utilities.isRarArchive(file))) {

                Comic comic = new Comic(str, map.get(str));

                ComicLoader.loadComicSync( mApplicationContext, comic);

                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()) && comic.getPageCount()>0)
                {
                    PreferenceSetter.addAddedComic(mApplicationContext, comic.getFileName());
                }

                PreferenceSetter.saveComic(mApplicationContext, comic);

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                    }
                });


                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);

            }
            else // if it's not a valid comic file
            {
                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);
            }
        }

        updateLastReadComics();

        long endTime = System.currentTimeMillis();

        Log.d("comics/files-search", "time: "+(endTime-startTime));
    }

}


