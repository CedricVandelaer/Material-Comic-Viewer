package com.comicviewer.cedric.comicviewer.FragmentNavigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Cédric on 19/07/2015.
 */
public abstract class BaseFragment extends Fragment implements BaseNavigationInterface {

    private NavigationManager mNavigationManager;

    public NavigationManager getNavigationManager(){return mNavigationManager;}

    abstract public boolean onBackPressed();

    @Override
    public void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);
        outstate.putString("NavigationManager", mNavigationManager.toJSON());
    }

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);

        if (savedState!=null && savedState.getString("NavigationManager")!=null)
            mNavigationManager = NavigationManager.fromJSON(savedState.getString("NavigationManager"));
        else if (mNavigationManager == null)
        {
            mNavigationManager = new NavigationManager();
        }
    }
}
