package com.comicviewer.cedric.comicviewer.FragmentNavigation;

/**
 * Created by Cédric on 21/07/2015.
 */
public class DrawerNavigator {

    public static DrawerNavigator mSingleton;

    private NavigationManager mDrawerTitleNavigation;
    private NavigationManager mDrawerSectionNavigation;

    public static DrawerNavigator getInstance()
    {
        if (mSingleton == null)
            mSingleton = new DrawerNavigator();
        return mSingleton;
    }

    public DrawerNavigator()
    {
        mDrawerSectionNavigation = new NavigationManager();
        mDrawerTitleNavigation = new NavigationManager();
    }

    public NavigationManager getSectionNavigator()
    {
        return mDrawerSectionNavigation;
    }

    public NavigationManager getTitleNavigator()
    {
        return mDrawerTitleNavigation;
    }
}
