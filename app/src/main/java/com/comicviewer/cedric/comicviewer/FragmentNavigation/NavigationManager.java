package com.comicviewer.cedric.comicviewer.FragmentNavigation;

import java.util.Stack;

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


}
