package com.comicviewer.cedric.comicviewer.Model;

import java.util.ArrayList;

/**
 * Created by Cédric on 11/03/2015.
 * A collection of comics* 
 */
public class ComicCollection {
    private String mName;
    private ArrayList<Comic> mComicList;
    
    public ComicCollection(String name, ArrayList<Comic> list)
    {
        mName = name;
        mComicList = list;
    }
    
    public ArrayList<Comic> getComicList()
    {
        return mComicList;
        
    }
    
    public String getName()
    {
        return mName;
        
    }
}
