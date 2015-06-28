package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
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
        mFab.setColorNormal(PreferenceSetter.getAccentColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(PreferenceSetter.getAccentColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(PreferenceSetter.getAccentColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("Add new collection")
                        .input("Name", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                materialDialog.dismiss();
                                PreferenceSetter.createCollection(getActivity(), charSequence.toString());
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .positiveText(getString(R.string.confirm))
                        .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .negativeText(getString(R.string.cancel))
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .show();
            }
        });
    }
}
