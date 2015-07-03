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
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
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
    public void onResume()
    {
        super.onResume();
        if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext))
        {
            if (NavigationManager.getInstance().fileStackEmpty())
            {
                mAdapter.clearList();
                NavigationManager.getInstance().resetFileStack();
            }
        }
    }

    @Override
    void setSearchFilters() {

        mFilters.clear();

        mFilters.add(new SearchFilter(PreferenceSetter.getFolderEnabledSetting(getActivity())) {
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
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
                    } else {
                        PreferenceSetter.setFolderEnabledSetting(getActivity(), true);
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                    }
                    NavigationManager.getInstance().resetFileStack();
                    refresh();
                }
            });

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
        if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext))
            return FileLoader.searchComicsAndFolders(mApplicationContext, NavigationManager.getInstance().getPathFromFileStack());
        else
            return FileLoader.searchComics(mApplicationContext);
    }


}
