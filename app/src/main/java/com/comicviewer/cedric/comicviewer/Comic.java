package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.github.junrar.Archive;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    //The year of the comic
    int mYear;

    public Comic(String filename, String filePath)
    {
        mFileName = filename;
        mFilePath = filePath;

        createTitle(filename);
        
        mCoverColor = -1;
        mPageCount = -1;
        
        try {
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(filename);
            if (matcher.find())
            {
                Log.d("Comic",matcher.group(0));
                mYear = Integer.parseInt(matcher.group(0));
            }
            else
            {
                mYear = -1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mYear = -1;
        }
        
        try {
            int i = 0;
            while (!Character.isDigit(filename.charAt(i))) 
                i++;
            int j = i;
            while (Character.isDigit(filename.charAt(j))) 
                j++;
            mIssueNumber = Integer.parseInt(filename.substring(i, j));
        }
        catch (Exception e)
        {
            mIssueNumber = -1;
            Log.e("IssueNumber", e.getMessage());
        }


        mCoverImage = null;
    }


    public Comic(Parcel in)
    {
        readFromParcel(in);
    }

    private void createTitle(String filename)
    {
        String delimiter = "COMICVIEWERDELIM";
        
        if (filename.contains("("))
            mTitle = filename.substring(0,filename.indexOf('('));
        else
            mTitle = filename;
        
        mTitle = mTitle.replaceAll("_"," ");
        
        mTitle = mTitle.trim();
        
        mTitle = mTitle.replaceAll("\\d+", delimiter);
        
        if (mTitle.contains(delimiter)) {
            mTitle = mTitle.substring(0, mTitle.indexOf(delimiter));
        }
        
        mTitle = mTitle.trim();
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
        mYear = in.readInt();
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
    
    public int getYear() { return mYear; }

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
        dest.writeInt(mYear);
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
