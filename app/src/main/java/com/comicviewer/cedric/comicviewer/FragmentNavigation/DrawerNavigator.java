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

    protected NavigationManager getSectionNavigator()
    {
        return mDrawerSectionNavigation;
    }

    protected NavigationManager getTitleNavigator()
    {
        return mDrawerTitleNavigation;
    }

    public void pushSection(String title, int section)
    {
        mDrawerTitleNavigation.pushToStack(title);
        mDrawerSectionNavigation.pushToStack(section);
    }

    public void popSection()
    {
        mDrawerTitleNavigation.popFromStack();
        mDrawerSectionNavigation.popFromStack();
    }

    public int getCurrentSectionNumber()
    {
        if (mDrawerSectionNavigation.emptyStack())
            return -1;

        return (int) mDrawerSectionNavigation.getValueFromStack();
    }

    public String getCurrentSectionTitle()
    {
        if (mDrawerSectionNavigation.emptyStack())
            return null;
        return (String) mDrawerTitleNavigation.getValueFromStack();
    }

    public void clearSections()
    {
        mDrawerSectionNavigation.reset();
        mDrawerTitleNavigation.reset();
    }

    public boolean isLastSection()
    {
        return (mDrawerSectionNavigation.getStackSize() <= 1);
    }

    public boolean hasNoSection()
    {
        return (mDrawerSectionNavigation.getStackSize() == 0);
    }
}
