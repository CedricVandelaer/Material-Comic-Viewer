package com.comicviewer.cedric.comicviewer.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cédric on 23/01/2015.
 * Base class to represent the information of a comic
 */
public class Comic implements Parcelable 
{
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

    //The colorsetting that the comic has currently loaded
    String mColorSetting;

    //The color of the card displaying the comic
    int mCoverColor;
    
    //The color of the primary text
    int mPrimaryTextColor;

    //The color of the secondary text
    int mSecondaryTextColor;
    
    //The year of the comic
    int mYear;

    public Comic(String filename, String filePath)
    {
        mFileName = filename;
        mFilePath = filePath;

        createTitle(filename);
        
        mCoverColor = -1;
        mPrimaryTextColor=-1;
        mSecondaryTextColor=-1;
        mPageCount = -1;
        mColorSetting = null;
        
        try {
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(filename);
            if (matcher.find())
            {
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
            String filenameAfterTitle = filename;

            if (filename.contains(mTitle))
                filenameAfterTitle = filename.substring(filename.indexOf(mTitle));

            while (!Character.isDigit(filenameAfterTitle.charAt(i))) 
                i++;
            
            int j = i;
            
            while (Character.isDigit(filenameAfterTitle.charAt(j))) 
                j++;
            

            mIssueNumber = Integer.parseInt(filenameAfterTitle.substring(i, j));
        }
        catch (Exception e)
        {
            mIssueNumber = -1;
            Log.e("IssueNumber", e.getMessage());
        }

        mCoverImage = null;
    }

    public Comic(String title, String coverImage, String filePath, String fileName, int issueNumber,
                 int pageCount, String colorSetting, int coverColor, int primaryTextColor, int secondaryTextColor, int year)
    {
        mTitle = title;
        mCoverImage = coverImage;
        mFilePath = filePath;
        mFileName = fileName;
        mIssueNumber = issueNumber;
        mPageCount = pageCount;
        mColorSetting = colorSetting;
        mCoverColor = coverColor;
        mPrimaryTextColor = primaryTextColor;
        mSecondaryTextColor = secondaryTextColor;
        mYear = year;
    }

    public String serialize()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public Comic create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, Comic.class);
    }

    public Comic(Parcel in)
    {
        readFromParcel(in);
    }

    private void createTitle(String filename)
    {
        String delimiter = "COMICVIEWERDELIM";
        
        if (filename.contains("("))
            mTitle = filename.substring(0, filename.indexOf('('));
        else
            mTitle = filename;
        
        mTitle = mTitle.replaceAll("_"," ");
        
        mTitle = mTitle.trim();
        
        mTitle = mTitle.replaceAll("\\d+", delimiter);
        
        if (mTitle.contains(delimiter)) 
        {
            if (mTitle.indexOf(delimiter)>0)
                mTitle = mTitle.substring(0, mTitle.indexOf(delimiter));
            else
            {
                mTitle = mTitle.replace(delimiter,"");
                if (mTitle.contains(delimiter))
                    mTitle = mTitle.substring(0, mTitle.indexOf(delimiter));
            }
        }
        
        mTitle = mTitle.trim();

        if (mTitle.startsWith("-"))
        {
            mTitle = mTitle.substring(1,mTitle.length());
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
        mColorSetting = in.readString();
        mCoverColor = in.readInt();
        mPrimaryTextColor = in.readInt();
        mSecondaryTextColor = in.readInt();
        mYear = in.readInt();
    }

    public void setPrimaryTextColor(int color) { mPrimaryTextColor = color;}
    
    public int getPrimaryTextColor() { return mPrimaryTextColor;}
    
    public void setSecondaryTextColor(int color) { mSecondaryTextColor = color;}
    
    public int getSecondaryTextColor() { return mSecondaryTextColor;}
    
    public void setComicColor(int color)
    {
        mCoverColor = color;
    }
    
    public int getComicColor() { return mCoverColor;}

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

    public String getColorSetting()
    {
        return mColorSetting;
    }

    public void setColorSetting(String colorSetting)
    {
        mColorSetting = colorSetting;
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
        dest.writeString(mColorSetting);
        dest.writeInt(mCoverColor);
        dest.writeInt(mPrimaryTextColor);
        dest.writeInt(mSecondaryTextColor);
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
