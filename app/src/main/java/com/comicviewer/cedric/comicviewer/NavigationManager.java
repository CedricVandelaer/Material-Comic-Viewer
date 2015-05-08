package com.comicviewer.cedric.comicviewer;

import java.util.Stack;

import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by Cédric on 8/05/2015.
 */
public class NavigationManager {

    private static NavigationManager mSingleton;
    private Stack<String> mFileNavigationStack;
    private Stack<MaterialSection> mSectionNavigationStack;
    public final static String ROOT = "root";

    public static NavigationManager getInstance()
    {
        if (mSingleton==null)
        {
            mSingleton = new NavigationManager();
        }
        return mSingleton;
    }

    public NavigationManager()
    {
        mFileNavigationStack = new Stack<>();
        mSectionNavigationStack = new Stack<>();

        mFileNavigationStack.push(ROOT);
    }

    public void resetFileStack()
    {
        mFileNavigationStack.clear();
        mFileNavigationStack.push(ROOT);
    }

    public void resetSectionStack()
    {
        mSectionNavigationStack.clear();
    }

    public void pushPathToFileStack(String path)
    {
        mFileNavigationStack.push(path);
    }

    public void pushToSectionStack(MaterialSection section)
    {
        mSectionNavigationStack.push(section);
    }

    public String popFromFileStack()
    {
        if (!mFileNavigationStack.empty())
            return mFileNavigationStack.pop();
        else
            return null;
    }

    public MaterialSection popFromSectionStack()
    {
        if (!mSectionNavigationStack.empty())
            return mSectionNavigationStack.pop();
        else
            return null;
    }

    public String getPathFromFileStack()
    {
        return mFileNavigationStack.peek();
    }

    public MaterialSection getSectionFromSectionStack()
    {
        return mSectionNavigationStack.peek();
    }

    public boolean fileStackEmpty()
    {
        return mFileNavigationStack.empty();
    }

    public boolean sectionStackEmpty()
    {
        return mSectionNavigationStack.empty();
    }
}
