package com.comicviewer.cedric.comicviewer.ComicListFiles;

import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.SearchFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 */
public class CollectionsListFragment extends AbstractComicListFragment{

    public static CollectionsListFragment mSingleton;
    private String mCollectionName;

    public CollectionsListFragment getInstance()
    {
        if (mSingleton == null)
            mSingleton = new CollectionsListFragment();
        return mSingleton;
    }

    public void setCollectionName(String name)
    {
        mCollectionName = name;
    }

    @Override
    void setSearchFilters() {

        JSONArray collections = PreferenceSetter.getCollectionList(mApplicationContext);
        JSONObject collection = null;
        for (int i=0;i<collections.length();i++)
        {
            try {
                if (collections.getJSONObject(i).keys().next().equals(mCollectionName))
                {
                    collection = collections.getJSONObject(i);
                    break;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        ArrayList<String> fileNames = new ArrayList<>();

        try {
            JSONArray jsonFileNames = collection.getJSONArray(mCollectionName);
            for (int i=0;i<jsonFileNames.length();i++)
            {
                fileNames.add(jsonFileNames.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (collection!=null) {
            mFilters.add(new SearchFilter(fileNames) {
                @Override
                public boolean compare(Object object) {
                    if (object instanceof Comic) {
                        Comic comic = (Comic) object;
                        return mCompareList.contains(comic.getFileName());
                    }
                    return false;
                }
            });
        }
    }

    @Override
    void addShowFolderViewButton(boolean enable) {

    }

    @Override
    public Map<String, String> getFiles() {
        return FileLoader.searchComics(mApplicationContext);
    }

}
