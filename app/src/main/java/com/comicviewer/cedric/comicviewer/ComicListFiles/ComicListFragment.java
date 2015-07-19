package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

//import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SearchFilter;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.io.File;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 */
public class ComicListFragment extends AbstractComicListFragment {

    private static ComicListFragment mSingleton;

    public static ComicListFragment getInstance()
    {
        if (mSingleton== null)
            mSingleton = new ComicListFragment();
        return mSingleton;
    }

    public ComicListFragment()
    {

    }

    @Override
    protected void handleArguments(Bundle args) {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (StorageManager.getBooleanSetting(mApplicationContext, StorageManager.FOLDER_VIEW_ENABLED, true))
        {
            if (getNavigationManager().emptyStack())
            {
                mAdapter.clearList();
                getNavigationManager().reset(NavigationManager.ROOT);
            }
        }
    }

    @Override
    void setSearchFilters() {

        mFilters.clear();

        mFilters.add(new SearchFilter(StorageManager.getBooleanSetting(getActivity(), StorageManager.FOLDER_VIEW_ENABLED, true)) {
            @Override
            public boolean compare(Object object) {
                return !(object instanceof File) || mCompareSetting;
            }
        });
    }

    @Override
    void addShowFolderViewButton(boolean enable) {
        if (enable && getActivity()!=null) {
            int width = Utilities.getPixelValue(getActivity(), 48);
            int height = Utilities.getPixelValue(getActivity(), 32);
            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(width,height,Gravity.RIGHT);
            final Toolbar toolbar = ((NewDrawerActivity) getActivity()).getToolbar();
            toolbar.removeView(mFolderViewToggleButton);
            mFolderViewToggleButton = new ImageView(getActivity());
            mFolderViewToggleButton.setAlpha(0.75f);
            if (StorageManager.getBooleanSetting(getActivity(), StorageManager.FOLDER_VIEW_ENABLED, true))
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
                    if (StorageManager.getBooleanSetting(getActivity(), StorageManager.FOLDER_VIEW_ENABLED, true)) {
                        StorageManager.setFolderEnabledSetting(getActivity(), false);
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
                    } else {
                        StorageManager.setFolderEnabledSetting(getActivity(), true);
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                    }
                    getNavigationManager().reset(NavigationManager.ROOT);
                    refresh();
                }
            });

            toolbar.addView(mFolderViewToggleButton, layoutParamsCollapsed);
        }
        else
        {
            if (getActivity()!=null) {
                Toolbar toolbar = ((NewDrawerActivity) getActivity()).getToolbar();
                toolbar.removeView(mFolderViewToggleButton);
            }
        }
        setSearchFilters();
    }

    @Override
    public Map<String, String> getFiles() {
        if (StorageManager.getBooleanSetting(mApplicationContext, StorageManager.FOLDER_VIEW_ENABLED, true))
            return FileLoader.searchComicsAndFolders(mApplicationContext, (String)getNavigationManager().getValueFromStack());
        else
            return FileLoader.searchComics(mApplicationContext);
    }


    @Override
    public boolean onBackPressed() {

        getNavigationManager().popFromStack();

        if (!getNavigationManager().emptyStack()) {
            refresh();
            return true;
        }

        return false;
    }
}
