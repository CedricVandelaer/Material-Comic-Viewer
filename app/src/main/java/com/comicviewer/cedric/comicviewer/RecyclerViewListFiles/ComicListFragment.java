package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
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
    private ArrayList<String> mFilePaths;
    private boolean mUseRecents;
    private int mProgress;
    private int mTotalComicCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> mExcludedPaths;
    private SearchView mSearchView;
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

        isFiltered = false;

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        createRecyclerView(v);
        createFab(v);

        initialiseAdapter(savedInstanceState);

        initialiseRefresh(v);

        // Inflate the layout for this fragment
        return v;

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
        for (int i=mComicList.size()-1;i>=0;i--)
        {
            mComicList.remove(i);
            mAdapter.notifyItemRemoved(i);
        }

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
    }


    private void searchComics() {

        Map<String,String> map = FileLoader.searchComics(getActivity());

        long startTime = System.currentTimeMillis();

        //create treemap to sort the filenames
        Map<String,String> treemap = new TreeMap(map);

        mTotalComicCount = treemap.size();
        mProgress = 0;

        updateProgressDialog(mProgress, mTotalComicCount);

        for (String str:treemap.keySet())
        {
            File file = new File(map.get(str)+"/"+str);

            if (getComicPositionInList(str)==-1
                    && Utilities.checkExtension(str)
                    && (Utilities.isZipArchive(file) || Utilities.isRarArchive(file))) {

                Comic newComic = new Comic(str, map.get(str));
                new LoadComicTask().execute(newComic);
            }
            else
            {
                mProgress++;
                updateProgressDialog(mProgress, mTotalComicCount);
            }
        }

        long endTime = System.currentTimeMillis();

        Log.d("search comics in list", "time: "+(endTime-startTime));
    }

    private class LoadComicTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            Comic comic =(Comic) params[0];

            ComicLoader.loadComicSync( getActivity(), comic);

            mComicList.add(comic);

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(mComicList.size());
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Object params)
        {
            mProgress++;
            updateProgressDialog(mProgress, mTotalComicCount);
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

    private void updateProgressDialog(int progress, int total)
    {
        if (mProgress==0)
        {
            mSwipeRefreshLayout.post(new Runnable() {
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
        mSwipeRefreshLayout.post(new Runnable() {
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

        addOnRecyclerViewClickListener();
    }

    private void addOnRecyclerViewClickListener()
    {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!mSwipeRefreshLayout.isRefreshing()) {
                    Intent intent = new Intent(getActivity(), DisplayComicActivity.class);

                    if (!isFiltered)
                    {
                        Log.d("ItemClick", mComicList.get(position).getTitle());
                        intent.putExtra("Comic", mComicList.get(position));
                    }
                    else
                    {
                        Log.d("ItemClick", filteredList.get(position).getTitle());
                        intent.putExtra("Comic", filteredList.get(position));
                    }
                    if (mUseRecents)
                    {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    startActivity(intent);
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
        }
    }

    private void filterList(String query)
    {
        if (!query.equals("")) {
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
