package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.view.View;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SearchFilter;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

        ArrayList<Collection> collections = StorageManager.getCollectionList(getActivity());

        Collection collection = null;

        for (int i=0;i<collections.size();i++)
        {
            if (collections.get(i).getName().equals(mCollectionName))
                collection = collections.get(i);
        }

        if (collection!=null) {
            mFilters.add(collection.getCollectionFilter());
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
