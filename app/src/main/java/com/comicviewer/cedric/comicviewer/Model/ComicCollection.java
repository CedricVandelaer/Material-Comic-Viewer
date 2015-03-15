package com.comicviewer.cedric.comicviewer.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Cédric on 11/03/2015.
 * A collection of comics* 
 */
public class ComicCollection implements Parcelable{
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

    public ComicCollection(Parcel in)
    {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in)
    {
        mName = in.readString();
        mComicList = (ArrayList<Comic>)in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /*
    Function to make a comic parcelable (Eg. for passing it to another activity)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeSerializable(mComicList);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Comic createFromParcel(Parcel in) {
            return new Comic(in);
        }
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };
}
