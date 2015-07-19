package com.comicviewer.cedric.comicviewer.FragmentNavigation;

import com.comicviewer.cedric.comicviewer.ComicListFiles.AbstractComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.CollectionsListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.FavoritesListFragment;

import java.util.Stack;

import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by CV on 8/05/2015.
 * Class to manage file browser navigations
 */
public class NavigationManager<T> {

    private Stack<T> mStack;
    public final static String ROOT = "root";

    public NavigationManager()
    {
        mStack = new Stack<>();
    }

    public void reset()
    {
        mStack.clear();
    }

    public void reset(T initialValue)
    {
        mStack.clear();
        mStack.push(initialValue);
    }


    public void pushToStack(T value)
    {
        mStack.push(value);
    }

    public T popFromStack()
    {
        if (!mStack.empty())
            return mStack.pop();
        else
            return null;
    }

    public T getValueFromStack()
    {
        if (!mStack.empty())
            return mStack.peek();
        else
            return null;
    }

    public boolean emptyStack()
    {
        return mStack.empty();
    }


    /*
    private static NavigationManager mSingleton;
    private Stack<String> mFileNavigationStack;
    private Stack<MaterialSection> mSectionNavigationStack;
    private Stack<String> mCloudStack;
    private Stack<String> mFavoritesStack;
    private Stack<String> mCollectionStack;
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
        mCollectionStack = new Stack<>();

        mCollectionStack.push(ROOT);
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

    public void clearCloudStack()
    {
        mCloudStack.clear();
    }
    public void resetCollectionStack()
    {
        mCollectionStack.clear();
        mCollectionStack.push(ROOT);
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
        else if (fragment instanceof CollectionsListFragment)
            return popFromCollectionStack();
        else
            return null;
    }

    public void pushToCollectionStack(String path) {mCollectionStack.push(path);}

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

    public String popFromCollectionStack()
    {
        if (!mCollectionStack.empty())
            return mCollectionStack.pop();
        else
            return null;
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

    public String getPathFromCollectionStack() {return mCollectionStack.peek();}

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

    public boolean collectionStackEmpty() {return mCollectionStack.empty();}
    */

}
