package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SearchFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

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
    public void onResume()
    {
        super.onResume();
        if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext))
        {
            if (NavigationManager.getInstance().favoriteStackEmpty())
            {
                mAdapter.clearList();
                NavigationManager.getInstance().resetFavoriteStack();
            }
        }
    }

    @Override
    void setSearchFilters() {

        mFilters.clear();

        mFilters.add(new SearchFilter(PreferenceSetter.getFolderEnabledSetting(getActivity())) {
            @Override
            public boolean compare(Object object) {
                if (object instanceof File)
                    return mCompareSetting;
                return true;
            }
        });

        mFilters.add(new SearchFilter(PreferenceSetter.getFavoriteComics(getActivity())) {
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
            final Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            toolbar.removeView(mFolderViewToggleButton);
            mFolderViewToggleButton = new ImageView(getActivity());
            mFolderViewToggleButton.setAlpha(0.75f);

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
                        NavigationManager.getInstance().resetFavoriteStack();
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
                        refresh();
                    } else {
                        PreferenceSetter.setFolderEnabledSetting(getActivity(), true);
                        NavigationManager.getInstance().resetFavoriteStack();
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                        refresh();
                    }
                }
            });

            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(120,100, Gravity.RIGHT);
            toolbar.addView(mFolderViewToggleButton, layoutParamsCollapsed);
        }
        else
        {
            if (getActivity()!=null) {
                Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
                toolbar.removeView(mFolderViewToggleButton);
            }
        }
        setSearchFilters();
    }

    @Override
    public Map<String, String> getFiles() {
        if (PreferenceSetter.getFolderEnabledSetting(getActivity()))
            return FileLoader.searchComicsAndFolders(getActivity(), NavigationManager.getInstance().getPathFromFavoriteStack());
        else
            return FileLoader.searchComics(mApplicationContext);
    }
}
