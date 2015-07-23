package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PauseOnScrollListener;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PreCachingLayoutManager;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.SlideLeftAnimator;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.SlideRightAnimator;
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
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by CV on 22/06/2015.
 */
abstract public class AbstractComicListFragment extends BaseFragment {

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
    protected ImageView mFolderViewToggleButton;
    protected ImageView mSortButton;
    protected ArrayList<SearchFilter> mFilters;

    protected MultiSelector mMultiSelector;

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

        handleArguments(getArguments());

        mMultiSelector = new MultiSelector();

        mFilters = new ArrayList<>();
        setSearchFilters();

        isFiltered = false;
        mHandler = new Handler();

        createRecyclerView(v);
        createFab(v);

        initialiseRefresh(v);

        initialiseVariables(savedInstanceState);

        return v;

    }

    protected abstract void handleArguments(Bundle args);

    abstract void setSearchFilters();

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mApplicationContext = getActivity().getApplicationContext();
    }

    protected class SearchComicsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            searchComics();

            mSearchComicsTask = null;

            return null;
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mApplicationContext = activity;
    }


    protected void showActionBarButtons(final boolean enabled)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                enableSearchBar(enabled);
                enableSortingOptions(enabled);
                addShowFolderViewButton(enabled);

            }
        });

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
            try {
                mSearchComicsTask.get(3000, TimeUnit.MILLISECONDS);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (mAdapter != null) {
            prepareForAdapterChange();
            mAdapter.clearList();

            mSearchComicsTask = new SearchComicsTask();
            mSearchComicsTask.execute();
        }
    }

    protected void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(StorageManager.getAccentColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(StorageManager.getAccentColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(StorageManager.getAccentColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File path = new File(Environment.getExternalStorageDirectory().getPath());

                FileDialog dialog = new FileDialog(getActivity(), path);
                dialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(final File directory) {
                        Log.d(getClass().getName(), "Selected directory: " + directory.toString());

                        MaterialDialog rootPrompt = new MaterialDialog.Builder(getActivity())
                                .title("Add folder")
                                .content("Do you wish to include the root folder?")
                                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                                .positiveText(getString(R.string.yes))
                                .negativeText(getString(R.string.no))
                                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        ArrayList<String> filePaths = StorageManager.getFilePathsFromPreferences(getActivity());

                                        if (!filePaths.contains(directory.toString()))
                                            filePaths.add(directory.toString());
                                        StorageManager.saveFilePaths(getActivity(), filePaths);
                                        refresh();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        ArrayList<String> paths = FileLoader.getDirectSubFolders(directory.toString());
                                        ArrayList<String> filePaths = StorageManager.getFilePathsFromPreferences(getActivity());

                                        for (int i = 0; i < paths.size(); i++) {
                                            if (!filePaths.contains(paths.get(i))) {
                                                filePaths.add(paths.get(i));
                                            }
                                        }
                                        StorageManager.saveFilePaths(getActivity(), filePaths);
                                        refresh();
                                    }
                                }).show();


                    }
                });
                dialog.setSelectDirectoryOption(true);
                dialog.showDialog();
            }
        });
    }

    protected void setPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        StorageManager.setBackgroundColorPreference(getActivity());

        if (prefs.getString("cardSize", "Normal cards").equals(getString(R.string.card_size_setting_3)))
        {
            mRecyclerView.setItemViewCacheSize(10);
        }
    }

    protected void updateLastReadComics()
    {

        String lastReadComic = StorageManager.getStringSetting(mApplicationContext, StorageManager.LAST_READ_COMIC, getString(R.string.none));

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

    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mSearchComicsTask != null)
        {
            mSearchComicsTask.cancel(false);
        }

        showActionBarButtons(false);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }


    @Override
    public void onResume()
    {
        Log.d("AbstractComicList", "stack size: " + getNavigationManager().getStackSize());

        super.onResume();

        setPreferences();

        showActionBarButtons(true);

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
        if (getActivity() == null)
            return;

        final Toolbar toolbar = ((NewDrawerActivity) getActivity()).getToolbar();

        if (enabled) {

            int width = Utilities.getPixelValue(getActivity(), 48);
            int height = Utilities.getPixelValue(getActivity(), 32);

            //final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(width,height,Gravity.RIGHT);

            toolbar.removeView(mSearchView);
            mSearchView = new SearchView(getActivity());
            int searchImgId = android.support.v7.appcompat.R.id.search_button;
            ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
            v.getLayoutParams().height = height;
            v.getLayoutParams().width = width;
            v.setScaleType(ImageView.ScaleType.FIT_CENTER);
            v.setImageResource(R.drawable.ic_magnify);
            mSearchView.setAlpha(0.75f);
            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(Gravity.RIGHT);

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    filterList("");
                    if (mFolderViewToggleButton != null)
                        mFolderViewToggleButton.setVisibility(View.VISIBLE);
                    if (mSortButton != null)
                        mSortButton.setVisibility(View.VISIBLE);
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
            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSortButton != null)
                        mSortButton.setVisibility(View.INVISIBLE);
                    if (mFolderViewToggleButton != null)
                        mFolderViewToggleButton.setVisibility(View.INVISIBLE);
                }
            });

            toolbar.addView(mSearchView, layoutParamsCollapsed);

        }
        else
        {
            toolbar.removeView(mSearchView);
        }
    }

    protected void enableSortingOptions(boolean enabled)
    {
        if (getActivity()==null)
            return;

        final Toolbar toolbar = ((NewDrawerActivity) getActivity()).getToolbar();

        if (enabled) {

            int width = Utilities.getPixelValue(getActivity(), 48);
            int height = Utilities.getPixelValue(getActivity(), 32);

            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(width,height,Gravity.RIGHT);

            mSortButton = new ImageView(getActivity());
            mSortButton.setAlpha(0.75f);
            mSortButton.setImageResource(R.drawable.ic_sort);
            mSortButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSortPopup();
                }
            });

            toolbar.addView(mSortButton, layoutParamsCollapsed);

        }
        else
        {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toolbar.removeView(mSortButton);
                }
            });
        }
    }

    public void showSortPopup(){

        if (getActivity()==null)
            return;
        CharSequence[] sortingOptions = {"Series", "Filename", "Year", "Last added", "Last modified date"};
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Sort by")
                .items(sortingOptions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        materialDialog.dismiss();
                        String[] sortOptions = {
                                StorageManager.SORT_BY_SERIES,
                                StorageManager.SORT_BY_FILENAME,
                                StorageManager.SORT_BY_YEAR,
                                StorageManager.SORT_BY_LAST_ADDED,
                                StorageManager.SORT_BY_MODIFIED_DATE
                        };
                        StorageManager.saveSortSetting(getActivity(), sortOptions[i]);
                        refresh();
                    }
                })
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .negativeText(getString(R.string.cancel))
                .show();
    }

    protected void createRecyclerView(View v)
    {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.comic_list_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        int height;

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        if (StorageManager.getCardAppearanceSetting(getActivity()).equals(getActivity().getString(R.string.card_size_setting_3))) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            Log.d("Layoutmanager", "Extra height: " + height);
        }
        else
        {
            height = 0;
        }

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


        PauseOnScrollListener scrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, false);

        mRecyclerView.setOnScrollListener(scrollListener);

    }

    protected void initialiseVariables(Bundle savedInstanceState)
    {
        mAdapter = new ComicAdapter(this, mMultiSelector);
        mRecyclerView.setAdapter(mAdapter);

        prepareForAdapterChange();

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
            ComicAdapter tempAdapter = new ComicAdapter(this, filteredList, mMultiSelector);
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
        ArrayList<Comic> savedComics = StorageManager.getSavedComics(mApplicationContext);
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
                    showProgressSpinner(true);
                    showActionBarButtons(false);
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

        if (hasToLoad)
            showActionBarButtons(true);

        showProgressSpinner(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageManager.batchSaveComics(mApplicationContext, comicsToSave);
                StorageManager.batchAddAddedComics(mApplicationContext, comicsToAdd);
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

    private void prepareForAdapterChange()
    {

            if (getNavigationManager().getState() == NavigationManager.NAVIGATION_STATE.NEUTRAL
                    || isFiltered)
            {
                mRecyclerView.setItemAnimator(new FadeInAnimator());
            }
            else if (getNavigationManager().getState() == NavigationManager.NAVIGATION_STATE.DOWN)
            {
                mRecyclerView.setItemAnimator(new SlideLeftAnimator());
            }
            else if (getNavigationManager().getState() == NavigationManager.NAVIGATION_STATE.UP)
            {
                mRecyclerView.setItemAnimator(new SlideRightAnimator());
            }
            if (mRecyclerView.getItemAnimator()!=null) {
                mRecyclerView.getItemAnimator().setAddDuration(150);
                mRecyclerView.getItemAnimator().setRemoveDuration(150);
            }


    }

}
