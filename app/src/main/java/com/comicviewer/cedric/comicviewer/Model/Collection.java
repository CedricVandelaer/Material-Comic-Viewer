package com.comicviewer.cedric.comicviewer.Model;

import android.os.Parcel;
import android.os.Parcelable;


import com.comicviewer.cedric.comicviewer.SearchFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by CV on 12/07/2015.
 */
public class Collection{

    private String mName;
    private ArrayList<String> mFileList;
    private ArrayList<String> mSeriesFilters;
    private ArrayList<Integer> mYearsFilters;
    private ArrayList<String> mFoldersFilters;

    private static String NAME = "name";
    private static String FILE_LIST = "fileList";
    private static String SERIES_LIST = "seriesList";
    private static String YEAR_LIST = "yearList";
    private static String FOLDER_LIST = "folderList";

    public Collection(String name)
    {
        mName = name;
        mFileList = new ArrayList<>();
        mSeriesFilters = new ArrayList<>();
        mYearsFilters = new ArrayList<>();
        mFoldersFilters = new ArrayList<>();
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name){mName = name;}

    public void addFile(String filename){mFileList.add(filename);}

    public void removeFile(String filename){mFileList.remove(filename);}

    public boolean containsFile(String filename){return mFileList.contains(filename);}

    public void addSeries(String seriesName){mSeriesFilters.add(seriesName);}

    public void removeSeries(String seriesName){mSeriesFilters.remove(seriesName);}

    public void addYear(int year){mYearsFilters.add(year);}

    public void removeYear(int year){mYearsFilters.remove(year);}

    public void addFolder(String folderPath){mFoldersFilters.add(folderPath);}

    public void removeFolder(String folder){mFoldersFilters.remove(folder);}

    public SearchFilter getCollectionFilter()
    {
        SearchFilter filter = new SearchFilter() {
            @Override
            public boolean compare(Object object) {

                if (object instanceof Comic)
                {
                    Comic comic = (Comic) object;
                    return mFileList.contains(comic.getFileName())
                            || mSeriesFilters.contains(comic.getTitle())
                            || mYearsFilters.contains(comic.getYear())
                            || mFoldersFilters.contains(comic.getFilePath());
                }
                return false;
            }
        };

        return filter;
    }

    public static Collection fromJSON(JSONObject collectionJSON)
    {
        try
        {
            Collection collection = new Collection(collectionJSON.getString(NAME));

            JSONArray fileListArray = collectionJSON.getJSONArray(FILE_LIST);
            for (int i=0;i<fileListArray.length();i++)
                collection.addFile(fileListArray.getString(i));

            JSONArray seriesArray = collectionJSON.getJSONArray(SERIES_LIST);
            for (int i=0;i<seriesArray.length();i++)
                collection.addSeries(seriesArray.getString(i));

            JSONArray yearsArray = collectionJSON.getJSONArray(YEAR_LIST);
            for (int i=0;i<yearsArray.length();i++)
                collection.addYear(yearsArray.getInt(i));

            JSONArray folderArray = collectionJSON.getJSONArray(FOLDER_LIST);
            for (int i=0;i<folderArray.length();i++)
                collection.addFolder(folderArray.getString(i));

            return collection;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject toJSON()
    {
        JSONObject collectionJSON = new JSONObject();

        try
        {
            collectionJSON.put(NAME, mName);

            JSONArray fileListArray = new JSONArray();
            for (String file:mFileList)
                fileListArray.put(file);
            collectionJSON.put(FILE_LIST, fileListArray);

            JSONArray seriesArray = new JSONArray();
            for (String series:mSeriesFilters)
                seriesArray.put(series);
            collectionJSON.put(SERIES_LIST, seriesArray);

            JSONArray yearsArray = new JSONArray();
            for (Integer year:mYearsFilters)
                yearsArray.put(year);
            collectionJSON.put(YEAR_LIST, yearsArray);

            JSONArray foldersArray = new JSONArray();
            for (String folder:mFoldersFilters)
                foldersArray.put(folder);
            collectionJSON.put(FOLDER_LIST, foldersArray);

            return collectionJSON;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

