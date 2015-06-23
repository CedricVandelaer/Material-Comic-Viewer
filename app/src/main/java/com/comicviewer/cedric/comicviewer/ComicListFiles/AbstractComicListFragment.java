package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PauseOnScrollListener;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PreCachingLayoutManager;
import com.comicviewer.cedric.comicviewer.SearchFilter;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Filter;

/**
 * Created by CV on 22/06/2015.
 */
abstract public class AbstractComicListFragment extends Fragment {

    protected RecyclerView mRecyclerView;
    protected ComicAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected FloatingActionButton mFab;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected SearchView mSearchView;
    protected ArrayList<Object> filteredList;
    protected boolean isFiltered;
    protected Context mApplicationContext;
    protected Handler mHandler;
    protected SearchComicsTask mSearchComicsTask=null;
    protected ImageButton mFolderViewToggleButton;
    protected ArrayList<SearchFilter> mFilters;

    public AbstractComicListFragment() {
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


        mFilters = new ArrayList<>();
        setSearchFilters();

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

    abstract void setSearchFilters();

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mApplicationContext = getActivity().getApplicationContext();
    }

    protected class SearchComicsTask extends AsyncTask {

        @Override
        protected void onPreExecute()
        {
            showProgressSpinner(true);
            enableSearchBar(false);
            addShowFolderViewButton(false);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            /*
            if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext)) {
                searchComicsAndFolders();
            } else {
                searchComics();
            }
            */
            searchComics();

            mSearchComicsTask = null;

            return null;
        }

        @Override
        protected void onPostExecute(Object object)
        {
            showProgressSpinner(false);
            addShowFolderViewButton(true);
            enableSearchBar(true);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mApplicationContext = activity;
    }


    protected void initialiseRefresh(View v)
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
        setSearchFilters();
        if (mSearchComicsTask!=null) {
            mSearchComicsTask.cancel(false);
        }

        if (mAdapter != null) {
            mAdapter.clearList();

            mSearchComicsTask = new SearchComicsTask();
            mSearchComicsTask.execute();
        }
    }

    protected void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(PreferenceSetter.getAccentColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(PreferenceSetter.getAccentColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(PreferenceSetter.getAccentColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(Environment.getExternalStorageDirectory().getPath());

                FileDialog dialog = new FileDialog(getActivity(), path);
                dialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(File directory) {
                        Log.d(getClass().getName(), "Selected directory: " + directory.toString());

                        ArrayList<String> filePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());

                        if (!filePaths.contains(directory.toString()))
                            filePaths.add(directory.toString());
                        PreferenceSetter.saveFilePaths(getActivity(), filePaths);
                        refresh();
                    }
                });
                dialog.setSelectDirectoryOption(true);
                dialog.showDialog();
            }
        });
    }

    protected void setPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceSetter.setBackgroundColorPreference(getActivity());

        if (prefs.getString("cardSize", "Normal cards").equals(getString(R.string.card_size_setting_3)))
        {
            mRecyclerView.setItemViewCacheSize(10);
        }
    }

    protected void updateLastReadComics()
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


    protected void showProgressSpinner(final boolean enable)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(enable);
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
            mSearchComicsTask.cancel(false);
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

    abstract void addShowFolderViewButton(boolean enable);

    protected void enableSearchBar(boolean enabled)
    {
        if (enabled && getActivity()!=null) {
            final Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            toolbar.removeView(mSearchView);
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



    protected void createRecyclerView(View v)
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
        float dpWidth  = outMetrics.widthPixels / density;

        int columnCount = 1;


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

        int hSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);
        int vSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(vSpace, hSpace, columnCount));

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        PauseOnScrollListener scrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, false);

        mRecyclerView.setOnScrollListener(scrollListener);

    }

    protected void initialiseVariables(Bundle savedInstanceState)
    {
        mAdapter = new ComicAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        if (savedInstanceState!=null)
        {
            for (int i=0;i<savedInstanceState.size();i++)
            {

                if (savedInstanceState.getParcelable("Comic "+ (i+1))!=null) {
                    mAdapter.addObject(savedInstanceState.getParcelable("Comic " + (i + 1)));
                }
            }

            for (int i=savedInstanceState.size();i>=0;i--)
            {
                if (savedInstanceState.getSerializable("Folder " + (i + 1))!=null) {
                    mAdapter.addObject(savedInstanceState.getSerializable("Folder " + (i + 1)));
                }
            }
        }
    }


    protected void filterList(String query)
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
            ComicAdapter tempAdapter = new ComicAdapter(this, filteredList);
            tempAdapter.setRootAdapter(mAdapter);
            mRecyclerView.swapAdapter(tempAdapter,false);
        }
        else
        {
            mRecyclerView.setAdapter(mAdapter);
            isFiltered = false;
        }

    }



    public List<String> getFileNamesFromList(List<Object> comicList)
    {
        List<String> fileNames = new ArrayList<>();
        for (int i=0;i<comicList.size();i++)
        {
            if (comicList.get(i) instanceof Comic) {
                Comic comic = (Comic) comicList.get(i);
                fileNames.add(comic.getFilePath() + "/" + comic.getFileName());
            }
            else if (comicList.get(i) instanceof File)
            {
                File folder = (File) comicList.get(i);
                String folderPath = folder.getParentFile().getAbsolutePath()+"/"+folder.getName();
                fileNames.add(folderPath);
            }
        }
        return fileNames;
    }

    public List<String> getFileNamesFromList(List<Comic> comicList, boolean dummy)
    {
        List<String> fileNames = new ArrayList<>();
        for (int i=0;i<comicList.size();i++)
        {
            Comic comic = (Comic) comicList.get(i);
            fileNames.add(comic.getFilePath() + "/" + comic.getFileName());
        }
        return fileNames;
    }

    abstract public Map<String, String> getFiles();

    public void searchComics()
    {
        //map of <filename, filepath>
        Map<String,String> map = getFiles();

        TreeMap<String, String> treemap = new TreeMap<>(map);

        List<Object> currentListItems = mAdapter.getComicsAndFiles();
        ArrayList<Comic> savedComics = PreferenceSetter.getSavedComics(mApplicationContext);
        List<String> savedComicsFileNames = getFileNamesFromList(savedComics, true);
        List<String> currentFileNames = getFileNamesFromList(currentListItems);

        final ArrayList<Comic> comicsToSave = new ArrayList<>();
        final Set<String> comicsToAdd = new HashSet<>();

        boolean hasToLoad = false;

        for (String str:treemap.keySet()) {
            if (mSearchComicsTask != null && mSearchComicsTask.isCancelled()) {
                mAdapter.clearList();
                break;
            }
            //open the new found file
            final String comicPath = map.get(str) + "/" + str;
            File file = new File(comicPath);

            if (!currentFileNames.contains(comicPath)) {
                Comic comic;
                boolean mustSave = false;
                if (Utilities.checkImageFolder(file)) {
                    comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());
                    ComicLoader.loadComicSync(mApplicationContext, comic);
                }
                else if (file.isDirectory())
                {
                    if (checkSearchFilters(file))
                        addToAdapter(file);
                    continue;
                }
                else if (savedComicsFileNames.contains(comicPath)) {
                    int pos = savedComicsFileNames.indexOf(comicPath);

                    comic = savedComics.get(pos);

                    ComicLoader.generateComicInfo(mApplicationContext, comic);

                } else if (Utilities.checkExtension(str)
                        && (Utilities.isZipArchive(file) || Utilities.isRarArchive(file))) {
                    comic = new Comic(str, map.get(str));
                    ComicLoader.loadComicSync(mApplicationContext, comic);
                    mustSave = true;
                } else {
                    continue;
                }

                if (!checkSearchFilters(comic))
                {
                    continue;
                }

                comicsToAdd.add(comic.getFileName());

                if (mustSave || ComicLoader.setComicColor(mApplicationContext, comic)) {
                    comicsToSave.add(comic);
                    hasToLoad = true;
                }

                if (!hasToLoad) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(0);
                        }
                    });
                }

                addToAdapter(comic);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PreferenceSetter.batchSaveComics(mApplicationContext, comicsToSave);
                PreferenceSetter.batchAddAddedComics(mApplicationContext, comicsToAdd);
            }
        }).run();

        updateLastReadComics();
    }

    protected void addToAdapter(final Object object)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.addObjectSorted(object);
            }
        });
    }

    protected boolean checkSearchFilters(Object object)
    {
        for (int i=0;i<mFilters.size();i++)
        {
            if (!mFilters.get(i).compare(object))
                return false;
        }
        return true;
    }

}
