package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.Bundle;
import android.view.View;

import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 */
public class CollectionsListFragment extends AbstractComicListFragment{

    private String mCollectionName;

    public static CollectionsListFragment newInstance(String collectionName)
    {
        CollectionsListFragment f = new CollectionsListFragment();

        Bundle args = new Bundle();
        args.putString("CollectionName", collectionName);
        f.setArguments(args);

        return f;
    }

    public CollectionsListFragment()
    {

    }

    @Override
    protected void handleArguments(Bundle args) {

        if (args.getString("CollectionName")!=null)
            mCollectionName = args.getString("CollectionName");
    }

    @Override
    protected void createFab(View v) {

        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
    }

    @Override
    void setSearchFilters() {

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
        super.onDetach();
    }

    @Override
    public void refresh()
    {
        super.refresh();
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
