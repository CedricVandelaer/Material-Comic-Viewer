package com.comicviewer.cedric.comicviewer.ComicListFiles;


import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.CollectionActions;
import com.comicviewer.cedric.comicviewer.ComicActions;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PauseOnScrollListener;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.PreCachingLayoutManager;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AbstractCollectionsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected CollectionsAdapter mAdapter;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected FloatingActionButton mFab;

    protected Handler mHandler;

    private CharSequence[] smartCollectionType = {"Series", "Year", "Folders"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collections, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHandler = new Handler();

        StorageManager.setBackgroundColorPreference(getActivity());

        createRecyclerView(v);
        createFab(v);

        getNavigationManager().reset(NavigationManager.ROOT);
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

        if (StorageManager.getCardAppearanceSetting(getActivity()).equals(getActivity().getString(R.string.card_size_setting_3))) {
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

    abstract protected void createFab(View v);

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
        if (getNavigationManager().getValueFromStack().equals(NavigationManager.ROOT)) {
            mAdapter.notifyDataSetChanged();
            setProgressSpinner(false);
        }
        else
        {
            String path = (String)getNavigationManager().getValueFromStack();
            getNavigationManager().popFromStack();
            ((NewDrawerActivity)getActivity()).setFragmentInSection(CollectionsListFragment.newInstance(path), path);
        }
    }

    protected void showCollectionNameDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Add new collection")
                .input("Name", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.dismiss();
                        StorageManager.createCollection(getActivity(), charSequence.toString());
                        mAdapter.notifyDataSetChanged();
                        showSmartCollectionDialog(charSequence.toString());
                    }
                })
                .positiveText(getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .negativeText(getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .show();
    }

    protected void showSmartCollectionDialog(final String collectionName)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Add filters")
                .items(smartCollectionType)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        //{"Series", "Year", "Folders"}
                        switch (i) {
                            case 0:
                                showSeriesListDialog(collectionName);
                                break;
                            case 1:
                                showYearListDialog(collectionName);
                                break;
                            case 2:
                                showFolderListDialog(collectionName);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .positiveText(getActivity().getResources().getString(R.string.finish))
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .show();
    }

    protected void showSeriesListDialog(final String collectionName)
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(getActivity());
        Set<String> seriesSet = new HashSet<>();

        for (Comic comic:comics) {
            if (!comic.getTitle().trim().equals(""))
                seriesSet.add(comic.getTitle());
        }

        ArrayList<String> series = Utilities.getStringsFromSet(seriesSet);
        Collections.sort(series);
        CharSequence[] seriesCharSequences = new CharSequence[series.size()];

        for (int i=0;i<series.size();i++)
            seriesCharSequences[i] = series.get(i);

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Select series")
                .items(seriesCharSequences)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        ArrayList<String> series = new ArrayList<String>();
                        for (int i = 0; i < charSequences.length; i++)
                            series.add(charSequences[i].toString());
                        CollectionActions.batchAddSeriesFilterToCollection(getActivity(), collectionName, series);
                        showSmartCollectionDialog(collectionName);
                        return false;
                    }
                })
                .negativeText(getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSmartCollectionDialog(collectionName);
                    }
                })
                .positiveText(getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .show();
    }

    protected void showYearListDialog(final String collectionName)
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(getActivity());
        Set<String> yearsSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getYear()!=-1)
                yearsSet.add(""+comic.getYear());
        }

        ArrayList<String> years = Utilities.getStringsFromSet(yearsSet);
        Collections.sort(years);
        CharSequence[] seriesCharSequences = new CharSequence[years.size()];

        for (int i=0;i<years.size();i++)
            seriesCharSequences[i] = years.get(i);

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Select year")
                .items(seriesCharSequences)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        ArrayList<String> years = new ArrayList<String>();
                        for (int i = 0; i < charSequences.length; i++)
                            years.add(charSequences[i].toString());
                        CollectionActions.batchAddYearsFilterToCollection(getActivity(), collectionName, years);
                        showSmartCollectionDialog(collectionName);
                        return false;
                    }
                })
                .negativeText(getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSmartCollectionDialog(collectionName);
                    }
                })
                .positiveText(getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .show();
    }

    protected void showFolderListDialog(final String collectionName)
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(getActivity());
        Set<String> filepathSet = new HashSet<>();

        for (Comic comic:comics) {
            if (!comic.getFilePath().trim().equals(""))
                filepathSet.add(""+comic.getFilePath());
        }

        ArrayList<String> folders = Utilities.getStringsFromSet(filepathSet);
        Collections.sort(folders);
        CharSequence[] seriesCharSequences = new CharSequence[folders.size()];

        for (int i=0;i<folders.size();i++)
            seriesCharSequences[i] = folders.get(i);

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Select folder")
                .items(seriesCharSequences)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        ArrayList<String> folders = new ArrayList<String>();
                        for (int i = 0; i < charSequences.length; i++)
                            folders.add(charSequences[i].toString());
                        CollectionActions.batchAddFolderFilterToCollection(getActivity(), collectionName, folders);
                        showSmartCollectionDialog(collectionName);
                        return false;
                    }
                })
                .negativeText(getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSmartCollectionDialog(collectionName);
                    }
                })
                .positiveText(getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .show();
    }
}
