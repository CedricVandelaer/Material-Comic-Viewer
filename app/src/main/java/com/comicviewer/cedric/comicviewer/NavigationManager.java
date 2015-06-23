package com.comicviewer.cedric.comicviewer;

import com.comicviewer.cedric.comicviewer.ComicListFiles.AbstractComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.FavoritesListFragment;

import java.util.Stack;

import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by CV on 8/05/2015.
 * Class to manage file browser navigations
 */
public class NavigationManager {

    private static NavigationManager mSingleton;
    private Stack<String> mFileNavigationStack;
    private Stack<MaterialSection> mSectionNavigationStack;
    private Stack<String> mCloudStack;
    private Stack<String> mFavoritesStack;
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
        mCloudStack = new Stack<>();
        mFavoritesStack = new Stack<>();

        mFavoritesStack.push(ROOT);
        mFileNavigationStack.push(ROOT);
        mCloudStack.push("/");
    }

    public void pushPathToStack(String path, AbstractComicListFragment fragment)
    {
        if (fragment instanceof ComicListFragment)
            pushPathToFileStack(path);
        else if (fragment instanceof FavoritesListFragment)
            pushPathToFavoriteStack(path);
    }


    public void resetFileStack()
    {
        mFileNavigationStack.clear();
        mFileNavigationStack.push(ROOT);
    }

    public void resetFavoriteStack()
    {
        mFavoritesStack.clear();
        mFavoritesStack.push(ROOT);
    }

    public void resetSectionStack()
    {
        mSectionNavigationStack.clear();
    }

    public void resetCloudStack()
    {
        mCloudStack.clear();
        mCloudStack.push("/");
    }

    public void resetCloudStackWithString(String root)
    {
        mCloudStack.clear();
        mCloudStack.push(root);
    }

    public String popFromStack(AbstractComicListFragment fragment)
    {
        if (fragment instanceof ComicListFragment)
            return popFromFileStack();
        else if (fragment instanceof FavoritesListFragment)
            return popFromFavoriteStack();
        else
            return null;
    }


    public void pushPathToFileStack(String path)
    {
        mFileNavigationStack.push(path);
    }

    public void pushPathToFavoriteStack(String path)
    {
        mFavoritesStack.push(path);
    }

    public void pushToSectionStack(MaterialSection section)
    {
        mSectionNavigationStack.push(section);
    }

    public void pushPathToCloudStack(String path)
    {
        mCloudStack.push(path);
    }
    public String popFromFileStack()
    {
        if (!mFileNavigationStack.empty())
            return mFileNavigationStack.pop();
        else
            return null;
    }

    public String popFromFavoriteStack()
    {
        if (!mFavoritesStack.empty())
            return mFavoritesStack.pop();
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

    public boolean stackEmpty(AbstractComicListFragment fragment)
    {
        if (fragment instanceof ComicListFragment)
            return fileStackEmpty();
        else if (fragment instanceof FavoritesListFragment)
            return favoriteStackEmpty();
        else
            return true;
    }

    public String popFromCloudStack()
    {
        if (!mCloudStack.empty())
            return mCloudStack.pop();
        else
            return null;
    }

    public String getPathFromFileStack()
    {
        return mFileNavigationStack.peek();
    }

    public String getPathFromFavoriteStack()
    {
        return mFavoritesStack.peek();
    }

    public MaterialSection getSectionFromSectionStack()
    {
        return mSectionNavigationStack.peek();
    }

    public String getPathFromCloudStack()
    {
        return mCloudStack.peek();
    }

    public boolean fileStackEmpty()
    {
        return mFileNavigationStack.empty();
    }

    public boolean favoriteStackEmpty()
    {
        return mFavoritesStack.empty();
    }

    public boolean sectionStackEmpty()
    {
        return mSectionNavigationStack.empty();
    }

    public boolean cloudStackEmpty()
    {
        return mCloudStack.empty();
    }

    public String getFileStackString()
    {
        String paths = "";

        while (!mFileNavigationStack.isEmpty())
            paths+=mFileNavigationStack.pop()+",";

        return paths;
    }

    public void initialiseFileStackFromString(String csvPathList)
    {
        mFileNavigationStack.clear();
        String[] paths = csvPathList.split(",");

        for (int i=paths.length-1;i>=0;i--)
        {
            if (paths[i].equals(""))
                continue;
            else
                mFileNavigationStack.push(paths[i]);
        }
    }
}
