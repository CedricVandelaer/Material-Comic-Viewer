package com.comicviewer.cedric.comicviewer.FragmentNavigation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

/**
 * Created by CV on 8/05/2015.
 * Class to manage file browser navigations
 */
public class NavigationManager<T>{

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

    public int getStackSize()
    {
        return mStack.size();
    }

    public String toJSON()
    {

        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        while(!mStack.empty()) {
            jsonArray.put(mStack.pop());
        }

        try {
            json.put("StackValues", jsonArray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }

    public static NavigationManager fromJSON(String jsonString)
    {
        NavigationManager manager = new NavigationManager();

        try {
            JSONArray values = new JSONArray();

            JSONObject json = new JSONObject(jsonString);
            values = json.getJSONArray("StackValues");

            for (int i=values.length()-1;i>=0;i--)
            {
                manager.pushToStack(values.get(i));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return manager;
    }
}
