package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.view.ActionMode;
import android.view.Menu;

import com.bignerdranch.android.multiselector.MultiSelector;

/**
 * Created by CV on 27/06/2015.
 */
public abstract class ComicListActionModeCallback implements ActionMode.Callback {

    private MultiSelector mMultiSelector;
    private boolean mClearOnPrepare = true;
    private static final String TAG = "comicListActionMode";

    public ComicListActionModeCallback(MultiSelector multiSelector)
    {
        mMultiSelector = multiSelector;
    }

    public boolean shouldClearOnPrepare() {
        return mClearOnPrepare;
    }

    public void setClearOnPrepare(boolean clearOnPrepare) {
        mClearOnPrepare = clearOnPrepare;
    }

    public MultiSelector getMultiSelector() {
        return mMultiSelector;
    }

    public void setMultiSelector(MultiSelector multiSelector) {
        mMultiSelector = multiSelector;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        if (mClearOnPrepare) {
            mMultiSelector.clearSelections();
        }
        mMultiSelector.setSelectable(true);
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mMultiSelector.clearSelections();
        mMultiSelector.setSelectable(false);
    }

}
