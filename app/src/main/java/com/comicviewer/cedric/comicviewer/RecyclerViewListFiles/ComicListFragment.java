package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ComicListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ComicAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private ArrayList<String> mFilePaths;
    private boolean mUseRecents;
    private int mProgress;
    private int mTotalComicCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> mExcludedPaths;
    private SearchView mSearchView;
    private ArrayList<Comic> filteredList;
    private boolean isFiltered;
    private static ComicListFragment mSingleton = null;
    private Context mApplicationContext;
    private Handler mHandler;

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
        mAdapter.clearComicList();

        mAdapter.notifyDataSetChanged();

        new SearchComicsTask().execute();
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
                        if (!mFilePaths.contains(directory.toString()))
                            mFilePaths.add(directory.toString());
                        if (mExcludedPaths.contains(directory.toString()))
                            mExcludedPaths.remove(directory.toString());
                        PreferenceSetter.saveFilePaths(getActivity(),mFilePaths,mExcludedPaths);
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

        mUseRecents = prefs.getBoolean("useRecents",true);

        mFilePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());

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

        mTotalComicCount = map.size();
        mProgress = 0;

        updateProgressDialog(mProgress, mTotalComicCount);

        for (String str:treemap.keySet())
        {
            //open the new found file
            final String comicPath = map.get(str)+"/"+str;
            File file = new File(comicPath);

            //check if comic is one of the saved comic files and add
            if (savedComicsFileNames.contains(comicPath) && !(currentComicsFileNames.contains(comicPath)))
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

                ComicLoader.setComicColor(getActivity(), comic);

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addComicSorted(finalComic);
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

                if (!PreferenceSetter.getComicsAdded(mApplicationContext).contains(comic.getFileName()))
                {
                    PreferenceSetter.addAddedComic(mApplicationContext, comic.getFileName());
                }

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addComicSorted(finalComic);
                        mRecyclerView.scrollToPosition(0);
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

        Log.d("search comics in list", "time: "+(endTime-startTime));
    }

    private void updateLastReadComics()
    {

        String lastReadComic = PreferenceSetter.getLastReadComic(getActivity());

        for (int i=0;i<mAdapter.getComics().size();i++)
        {
            if (mAdapter.getComics().get(i).getFileName().equals(lastReadComic))
            {
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
        PreferenceSetter.saveComicList(getActivity(), mAdapter.getComics());
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

        StringBuilder csvList = new StringBuilder();
        for(String s : mFilePaths){
            csvList.append(s);
            csvList.append(",");
        }

        savedState.putString("Filepaths",csvList.toString());

        savedState.putBoolean("isRefreshing", mSwipeRefreshLayout.isRefreshing());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        PreferenceSetter.saveFilePaths(getActivity(),mFilePaths, mExcludedPaths);
        PreferenceSetter.saveComicList(getActivity(), mAdapter.getComics());
        enableSearchBar(false);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        PreferenceSetter.saveFilePaths(getActivity(), mFilePaths, mExcludedPaths);
        PreferenceSetter.saveComicList(getActivity(), mAdapter.getComics());
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();

        enableSearchBar(true);


        if (isFiltered)
            filterList("");

        new SearchComicsTask().execute();
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
        mLayoutManager = new PreCachingLayoutManager(getActivity(), height);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(120));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        PauseOnScrollListener scrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, false);

        mRecyclerView.setOnScrollListener(scrollListener);

    }

    private void initialiseAdapter(Bundle savedInstanceState)
    {
        mExcludedPaths = PreferenceSetter.getExcludedPaths(getActivity());
        mFilePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());

        if (savedInstanceState==null)
        {
            mAdapter = new ComicAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            final boolean isRefreshing = savedInstanceState.getBoolean("isRefreshing", false);

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(isRefreshing);
                }
            });

            for (int i=0;i<savedInstanceState.size();i++)
            {
                mAdapter = new ComicAdapter(getActivity());
                if (savedInstanceState.getParcelable("Comic "+ (i+1))!=null)
                    mAdapter.addComic((Comic) savedInstanceState.getParcelable("Comic " + (i + 1)));
                mRecyclerView.setAdapter(mAdapter);
            }
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
            ComicAdapter tempAdapter = new ComicAdapter(getActivity(), filteredList);
            mRecyclerView.swapAdapter(tempAdapter,false);
        }
        else
        {
            mRecyclerView.setAdapter(mAdapter);
            isFiltered = false;
        }

    }

}
