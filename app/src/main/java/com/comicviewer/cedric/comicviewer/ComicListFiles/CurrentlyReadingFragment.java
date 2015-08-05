package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.Bundle;

import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.SearchFilter;

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
    protected void handleArguments(Bundle args) {

    }

    @Override
    void setSearchFilters() {

        mFilters.clear();

        mFilters.add(new SearchFilter(StorageManager.getComicPositionsMap(mApplicationContext)) {
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


    }


    @Override
    void addShowFolderViewButton(boolean enable) {

    }

    @Override
    public Map<String, String> getFiles() {
        return FileLoader.searchComics(mApplicationContext);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
