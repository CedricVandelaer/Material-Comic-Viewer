package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.github.junrar.Archive;

import java.io.File;

/**
 * Created by Cédric on 23/01/2015.
 * Base class to represent the information of a comic
 */
public class Comic implements Parcelable {
    //The title of the comic series
    String mTitle;

    //The path to the extracted cover image file
    String mCoverImage;

    //The path to the folder of the comic file
    String mFilePath;

    //The filename of the comic
    String mFileName;

    //The issue number of the comic, -1 if not found
    int mIssueNumber;

    //The page count of the comic
    int mPageCount;

    //The color of the card displaying the comic
    int mCoverColor;

    public Comic(String filename, String filePath)
    {
        mFileName = filename;

        createTitle(filename);
        mCoverColor = -1;
        mPageCount = 0;
        try {
            mIssueNumber = Integer.parseInt(mTitle.substring(mTitle.lastIndexOf(' ') + 1).replaceAll("\\D+", ""));
        }
        catch (Exception e)
        {
            mIssueNumber = -1;
            Log.e("IssueNumber", e.getMessage());
        }
        mTitle = mTitle.substring(0,mTitle.lastIndexOf(' '));

        mCoverImage = null;
        mFilePath = filePath;
    }


    public Comic(Parcel in)
    {
        readFromParcel(in);
    }

    private void createTitle(String filename)
    {
        mTitle = filename.substring(0,filename.lastIndexOf('.'));
        mTitle = filename.replace('_',' ');
        if (mTitle.contains("("))
            mTitle = mTitle.substring(0,mTitle.indexOf('('));
        mTitle=mTitle.replace(""+0,"");
        mTitle=mTitle.trim();
    }

    private void readFromParcel(Parcel in)
    {
        mTitle = in.readString();
        mCoverImage = in.readString();
        mFilePath = in.readString();
        mFileName = in.readString();
        mIssueNumber = in.readInt();
        mPageCount = in.readInt();
        mCoverColor = in.readInt();
    }

    public void setComicColor(int color)
    {
        mCoverColor = color;
    }

    public int getPageCount()
    {
        return mPageCount;
    }

    public void setPageCount(int m)
    {
        mPageCount = m;
    }

    public int getIssueNumber()
    {
        return mIssueNumber;
    }

    public String getFileName()
    {
        return mFileName;
    }

    public String getFilePath()
    {
        return mFilePath;
    }

    public void setCoverImage(String path)
    {
        mCoverImage = path;
    }

    public String getTitle()
    {
        return mTitle;
    }
    public String getCoverImage()
    {
        return mCoverImage;
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
        dest.writeString(mTitle);
        dest.writeString(mCoverImage);
        dest.writeString(mFilePath);
        dest.writeString(mFileName);
        dest.writeInt(mIssueNumber);
        dest.writeInt(mPageCount);
        dest.writeInt(mCoverColor);
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
