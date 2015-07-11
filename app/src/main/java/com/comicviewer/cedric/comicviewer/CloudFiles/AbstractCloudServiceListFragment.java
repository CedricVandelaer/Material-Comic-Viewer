package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Fragment;

import com.comicviewer.cedric.comicviewer.NavigationManager;

/**
 * Created by CV on 11/07/2015.
 */
public abstract class AbstractCloudServiceListFragment extends Fragment {


    public abstract void refresh();
    public abstract NavigationManager getNavigationManager();
}
