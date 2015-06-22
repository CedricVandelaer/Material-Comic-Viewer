package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.util.Log;

import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.SearchFilter;

import java.io.File;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 */
public class CurrentlyReadingFragment extends AbstractComicListFragment {

    public static CurrentlyReadingFragment mSingleton;


    public static CurrentlyReadingFragment getInstance()
    {
        if (mSingleton == null)
            mSingleton = new CurrentlyReadingFragment();
        return mSingleton;
    }

    public CurrentlyReadingFragment()
    {

    }

    @Override
    void setSearchFilters() {
        mFilters.add(new SearchFilter(PreferenceSetter.getReadComics(mApplicationContext)) {
            @Override
            public boolean compare(Object object) {

                if (object instanceof Comic) {
                    Comic comic = (Comic) object;
                    if (mCompareMap.containsKey(comic.getFileName())) {
                        if ((Integer) mCompareMap.get(comic.getFileName()) < (comic.getPageCount() - 1))
                            return true;
                    }
                }
                return false;
            }
        });

        mFilters.add(new SearchFilter() {
            @Override
            public boolean compare(Object object) {
                if (object instanceof File)
                    return PreferenceSetter.getFolderEnabledSetting(getActivity());
                return true;
            }
        });

    }


    @Override
    void addShowFolderViewButton(boolean enable) {

    }

    @Override
    public Map<String, String> getFiles() {
        return FileLoader.searchComics(mApplicationContext);
    }

}
