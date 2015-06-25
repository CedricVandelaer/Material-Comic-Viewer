package com.comicviewer.cedric.comicviewer.ComicListFiles;


import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static CollectionsFragment mSingleton;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CollectionsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FloatingActionButton mFab;

    private NavigationManager mNavigationManager;

    private Handler mHandler;

    public static CollectionsFragment getInstance()
    {
        if (mSingleton == null)
            mSingleton = new CollectionsFragment();
        return mSingleton;
    }

    public CollectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collections, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHandler = new Handler();
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        PreferenceSetter.setBackgroundColorPreference(getActivity());

        createRecyclerView(v);
        createFab(v);

        NavigationManager.getInstance().resetCollectionStack();
        mAdapter = new CollectionsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        // Inflate the layout for this fragment
        return v;
    }

    private void createRecyclerView(View v)
    {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);

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

    private void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(PreferenceSetter.getAccentColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(PreferenceSetter.getAccentColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(PreferenceSetter.getAccentColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("Add new collection")
                        .input("Name", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                materialDialog.dismiss();
                                PreferenceSetter.createCollection(getActivity(), charSequence.toString());
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .positiveText(getString(R.string.confirm))
                        .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .negativeText(getString(R.string.cancel))
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .show();
            }
        });
    }

    public void setProgressSpinner(final boolean enable)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(enable);
            }
        });
    }


    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh()
    {
        if (NavigationManager.getInstance().getPathFromCollectionStack().equals(NavigationManager.ROOT)) {
            setProgressSpinner(true);
            mAdapter.notifyDataSetChanged();
            setProgressSpinner(false);
        }
        else
        {
            /*
            // Create new fragment and transaction
            Fragment newFragment = new CollectionsListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(((ViewGroup)getView().getParent()).getId(), newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            */
            ((DrawerActivity)getActivity()).setFragment(CollectionsListFragment.getInstance(), NavigationManager.getInstance().getPathFromCollectionStack());
        }
    }
}
