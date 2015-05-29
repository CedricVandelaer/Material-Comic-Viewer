package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileDialog;
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
import java.util.TreeMap;


public class FavoritesListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ComicAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private int mProgress;
    private int mTotalComicCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private ArrayList<Comic> filteredList;
    private boolean isFiltered;
    private static FavoritesListFragment mSingleton = null;
    private Context mApplicationContext;
    private Handler mHandler;
    private SearchComicsTask mSearchComicsTask=null;

    public static FavoritesListFragment getInstance() {
        if(mSingleton == null) {
            mSingleton = newInstance();
        }
        return mSingleton;
    }

    public static FavoritesListFragment newInstance() {
        FavoritesListFragment fragment = new FavoritesListFragment();

        return fragment;
    }

    public FavoritesListFragment() {
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

        initialiseAdapter(savedInstanceState);

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
        protected Object doInBackground(Object[] params) {

            searchComics();
            mSearchComicsTask = null;

            return null;
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
                if (!isFiltered)
                    refresh();
            }
        });
    }

    private void refresh()
    {
        mAdapter.clearList();

        mAdapter.notifyDataSetChanged();

        if (mSearchComicsTask == null)
        {
            mSearchComicsTask = new SearchComicsTask();
            mSearchComicsTask.execute();
        }
    }

    private void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(PreferenceSetter.getAppThemeColor(getActivity()));
        mFab.setColorRipple(Utilities.lightenColor(PreferenceSetter.getAppThemeColor(getActivity())));
        mFab.setColorPressed(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(getActivity())));
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

                        if (!filePaths.contains(directory.toString()))
                            filePaths.add(directory.toString());

                        PreferenceSetter.saveFilePaths(getActivity(),filePaths);
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

    private void searchComics() {


        //map of <filename, filepath>
        Map<String,String> map = FileLoader.searchComics(getActivity());

        TreeMap<String, String> treemap = new TreeMap<>(map);

        long startTime = System.currentTimeMillis();

        List<Comic> currentComics = mAdapter.getComics();
        ArrayList<Comic> savedComics = PreferenceSetter.getSavedComics(getActivity());
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

        List<String> favorites = PreferenceSetter.getFavoriteComics(getActivity());

        mTotalComicCount = map.size();
        mProgress = 0;

        updateProgressDialog(mProgress, mTotalComicCount);

        for (String str:treemap.keySet())
        {
            if (mSearchComicsTask.isCancelled())
                break;

            //open the new found file
            final String comicPath = map.get(str)+"/"+str;
            File file = new File(comicPath);

            if (file.isDirectory() && Utilities.checkImageFolder(file) && !(currentComicsFileNames.contains(comicPath))
                    && favorites.contains(str)) {
                Comic comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());

                ComicLoader.loadComicSync(mApplicationContext, comic);

                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()))
                {
                    PreferenceSetter.addAddedComic(mApplicationContext, comic.getFileName());
                }

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

            }//check if comic is one of the saved comic files and add
            else if (savedComicsFileNames.contains(comicPath)
                    && !(currentComicsFileNames.contains(comicPath))
                    && favorites.contains(str))
            {
                int pos = savedComicsFileNames.indexOf(comicPath);

                try {
                    Thread.sleep(2, 0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Comic comic = savedComics.get(pos);

                ComicLoader.generateComicInfo(mApplicationContext, comic);

                if (ComicLoader.setComicColor(getActivity(), comic))
                    PreferenceSetter.saveComic(getActivity(), comic);


                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()))
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

                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);

            }//if it is a newly added comic
            else if (favorites.contains(str)
                    && getComicPositionInList(str)==-1
                    && Utilities.checkExtension(str)
                    && (Utilities.isZipArchive(file) || Utilities.isRarArchive(file)) )
            {
                Comic comic = new Comic(str, map.get(str));

                ComicLoader.loadComicSync( mApplicationContext, comic);

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

    private void updateLastReadComics()
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private int getComicPositionInList(String filename)
    {
        List<Comic> currentComics = mAdapter.getComics();
        for (int pos=0;pos<currentComics.size();pos++)
        {
            if (currentComics.get(pos).getFileName().equals(filename))
                return pos;
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //PreferenceSetter.saveComicList(getActivity(), mAdapter.getComics());
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);

        List<Comic> currentComics = mAdapter.getComics();

        for (int i=0;i<currentComics.size();i++)
        {
            savedState.putParcelable("Comic "+ (i+1), currentComics.get(i));
        }


        savedState.putBoolean("isRefreshing", mSwipeRefreshLayout.isRefreshing());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        enableSearchBar(false);

        if (mSearchComicsTask != null)
        {
            mSearchComicsTask.cancel(false);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //Don't save the list! The list consists only of favorite comics!
        //PreferenceSetter.saveComicList(getActivity(), mAdapter.getComics());
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();

        enableSearchBar(true);


        if (isFiltered)
            filterList("");

        if (mSearchComicsTask == null)
        {
            mSearchComicsTask = new SearchComicsTask();
            mSearchComicsTask.execute();
        }
    }

    private void enableSearchBar(boolean enabled)
    {
        if (enabled) {
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
            Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            toolbar.removeView(mSearchView);
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
            mLayoutManager = new GridLayoutManager(getActivity(), columnCount);
        }
        else if (dpWidth>=598)
        {
            columnCount = 2;
            mLayoutManager = new GridLayoutManager(getActivity(), columnCount);
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

    private void initialiseAdapter(Bundle savedInstanceState)
    {

        if (savedInstanceState==null)
        {
            mAdapter = new ComicAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter = new ComicAdapter(getActivity());

            for (int i=0;i<savedInstanceState.size();i++)
            {
                if (savedInstanceState.getParcelable("Comic "+ (i+1))!=null)
                    mAdapter.addObject((Comic) savedInstanceState.getParcelable("Comic " + (i + 1)));
            }
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void filterList(String query)
    {
        if (!query.equals("")) {
            isFiltered = true;

            filteredList = new ArrayList<>();
            List<Comic> currentComics = mAdapter.getComics();

            for (int i = 0; i < currentComics.size(); i++) {

                boolean found = false;

                if (currentComics.get(i).getFileName().toLowerCase().contains(query.toLowerCase())) {
                    found = true;
                }
                else if ((currentComics.get(i).getTitle().toLowerCase()+" "+currentComics.get(i).getIssueNumber()).contains(query.toLowerCase()))
                {
                    found=true;
                }
                if (found)
                    filteredList.add(currentComics.get(i));
            }
            ComicAdapter tempAdapter = new ComicAdapter(getActivity(), filteredList, false);
            tempAdapter.setRootAdapter(mAdapter);
            mRecyclerView.swapAdapter(tempAdapter,false);
        }
        else
        {
            mRecyclerView.setAdapter(mAdapter);
            isFiltered = false;
        }

    }

}
