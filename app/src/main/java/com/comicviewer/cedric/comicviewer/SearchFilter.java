package com.comicviewer.cedric.comicviewer;

import com.comicviewer.cedric.comicviewer.Model.Comic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 */
public abstract class SearchFilter<T, K> {

    protected List<T> mCompareList = new ArrayList<>();
    protected Map<T, K> mCompareMap = new HashMap<>();

    public SearchFilter()
    {

    }

    public SearchFilter(List<T> compareList)
    {
        mCompareList = compareList;
    }

    public SearchFilter(Map<T, K> compareMap)
    {
        mCompareMap = compareMap;
    }


    abstract public boolean compare(Object object);

}

