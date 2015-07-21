package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SearchFilter;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 * List of all favorite comics
 */
public class FavoritesListFragment extends AbstractComicListFragment {

    private static FavoritesListFragment mSingleton;

    public static FavoritesListFragment getInstance()
    {
        if (mSingleton == null)
            mSingleton = new FavoritesListFragment();
        return mSingleton;
    }

    public FavoritesListFragment()
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
                if (object instanceof File)
                    return mCompareSetting;
                return true;
            }
        });

        mFilters.add(new SearchFilter(StorageManager.getFavoriteComics(getActivity())) {
            @Override
            public boolean compare(Object object) {
                if (object instanceof Comic)
                    return mCompareList.contains(((Comic) object).getFileName());
                else if (object instanceof File)
                {
                    File folder = (File) object;
                    ArrayList<String> subfiles = FileLoader.searchSubFoldersAndFilesRecursive(folder.getAbsolutePath());
                    for (int i=0;i<subfiles.size();i++)
                    {
                        File file = new File(subfiles.get(i));
                        if (mCompareList.contains(file.getName()))
                            return true;
                    }
                }

                return false;
            }
        });


    }

    @Override
    void addShowFolderViewButton(boolean enable) {
        if (enable && getActivity()!=null) {
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

            int width = Utilities.getPixelValue(getActivity(), 48);
            int height = Utilities.getPixelValue(getActivity(), 32);

            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(width,height, Gravity.RIGHT);

            toolbar.addView(mFolderViewToggleButton, layoutParamsCollapsed);
        }
        else
        {
            if (getActivity()!=null) {
                final Toolbar toolbar = ((NewDrawerActivity) getActivity()).getToolbar();

                toolbar.removeView(mFolderViewToggleButton);
            }
        }
        setSearchFilters();
    }

    @Override
    public Map<String, String> getFiles() {
        if (StorageManager.getBooleanSetting(getActivity(), StorageManager.FOLDER_VIEW_ENABLED, true))
            return FileLoader.searchComicsAndFolders(getActivity(), (String)getNavigationManager().getValueFromStack());
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
