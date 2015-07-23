package com.comicviewer.cedric.comicviewer.FragmentNavigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Cédric on 19/07/2015.
 */
public abstract class BaseFragment extends Fragment implements BaseNavigationInterface {

    private NavigationManager mNavigationManager;

    public NavigationManager getNavigationManager(){return mNavigationManager;}

    public void setNavigationManager(NavigationManager navigationManager){mNavigationManager = navigationManager;}

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
            Log.d("BaseFragment", "created new NavigationManager");

        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mNavigationManager.setState(NavigationManager.NAVIGATION_STATE.NEUTRAL);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("BaseFragment", "stack size: "+mNavigationManager.getStackSize());
    }
}
