package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Fragment;

import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;

/**
 * Created by CV on 11/07/2015.
 */
public abstract class AbstractCloudServiceListFragment extends BaseFragment {

    public abstract void refresh();
}
