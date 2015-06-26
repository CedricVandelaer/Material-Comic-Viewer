package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SearchFilter;
import com.melnykov.fab.FloatingActionButton;

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

    public static CollectionsListFragment getInstance()
    {
        if (mSingleton == null)
            mSingleton = new CollectionsListFragment();
        return mSingleton;
    }

    @Override
    protected void createFab(View v) {

        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
    }

    @Override
    void setSearchFilters() {

        mCollectionName = NavigationManager.getInstance().getPathFromCollectionStack();

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
    public void onDetach()
    {
        NavigationManager.getInstance().resetCollectionStack();
        super.onDetach();
    }

    @Override
    public void refresh()
    {
        if (NavigationManager.getInstance().getPathFromCollectionStack().equals(NavigationManager.ROOT))
        {
            if (isAdded())
                ((DrawerActivity)getActivity()).setFragment(CollectionsFragment.getInstance(),getString(R.string.collections));
        }
        else
        {
            super.refresh();
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
