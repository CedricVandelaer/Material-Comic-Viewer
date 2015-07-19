package com.comicviewer.cedric.comicviewer.FragmentNavigation;

import android.support.v4.app.Fragment;

/**
 * Created by Cédric on 19/07/2015.
 */
public abstract class BaseFragment extends Fragment implements BaseNavigationInterface {

    private NavigationManager mNavigationManager = new NavigationManager();

    public NavigationManager getNavigationManager(){return mNavigationManager;}

    abstract public boolean onBackPressed();

}
