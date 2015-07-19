package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by CV on 28/06/2015.
 */
public class CollectionsFragment extends AbstractCollectionsFragment{

    private static CollectionsFragment mSingleton;

    public static CollectionsFragment getInstance()
    {
        if (mSingleton==null)
            mSingleton = new CollectionsFragment();
        return mSingleton;
    }

    public CollectionsFragment()
    {

    }

    protected void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(StorageManager.getAccentColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(StorageManager.getAccentColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(StorageManager.getAccentColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCollectionNameDialog();
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
