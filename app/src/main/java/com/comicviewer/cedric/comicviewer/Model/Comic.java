package com.comicviewer.cedric.comicviewer.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cédric on 23/01/2015.
 * Base class to represent the information of a comic
 */
public class Comic implements Parcelable 
{
    private static String TITLE = "title";
    private static String COVER_IMAGE = "coverImage";
    private static String FILE_PATH = "filePath";
    private static String FILE_NAME = "fileName";
    private static String ISSUE_NUMBER = "issueNumber";
    private static String PAGE_COUNT = "pageCount";
    private static String COLOR_SETTING = "colorSetting";
    private static String COVER_COLOR = "coverColor";
    private static String TEXT_COLOR = "textColor";
    private static String YEAR = "year";


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
    int mTextColor;
    
    //The year of the comic
    int mYear;

    public Comic(Comic otherComic)
    {
        this.mTitle = otherComic.getTitle();
        this.mCoverImage = otherComic.getCoverImage();
        this.mFilePath = otherComic.getFilePath();
        this.mFileName = otherComic.getFileName();
        this.mIssueNumber = otherComic.getIssueNumber();
        this.mPageCount = otherComic.getPageCount();
        this.mColorSetting = otherComic.getColorSetting();
        this.mCoverColor = otherComic.getComicColor();
        this.mTextColor = otherComic.getTextColor();
        this.mYear = otherComic.getYear();
    }

    public Comic(String filename, String filePath)
    {
        mFileName = filename;
        mFilePath = filePath;

        createTitle(filename);
        
        mCoverColor = -1;
        mTextColor=-1;
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

        if (mIssueNumber == mYear)
        {
            mIssueNumber = -1;
        }

        mCoverImage = null;
    }

    public Comic(String title, String coverImage, String filePath, String fileName, int issueNumber,
                 int pageCount, String colorSetting, int coverColor, int textColor, int year)
    {
        mTitle = title;
        mCoverImage = coverImage;
        mFilePath = filePath;
        mFileName = fileName;
        mIssueNumber = issueNumber;
        mPageCount = pageCount;
        mColorSetting = colorSetting;
        mCoverColor = coverColor;
        mTextColor = textColor;
        mYear = year;
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
        
        mTitle = mTitle.replaceAll("_", " ");

        mTitle = mTitle.replaceAll("#", "");
        
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

        if (mTitle.endsWith("-"))
        {
            mTitle = mTitle.substring(0,mTitle.length()-1);
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
        mTextColor = in.readInt();
        mYear = in.readInt();
    }

    public void setTitle(String title){mTitle = title;}

    public void setTextColor(int color) { mTextColor = color;}
    
    public int getTextColor() { return mTextColor;}

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

    public void setIssueNumber(int issueNumber){mIssueNumber = issueNumber;}

    public String getFileName()
    {
        return mFileName;
    }
    
    public int getYear() { return mYear; }

    public void setYear(int year)
    {
        mYear = year;
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

    public String getColorSetting()
    {
        return mColorSetting;
    }

    public void setFilePath(String path){mFilePath = path;}

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
        dest.writeInt(mTextColor);
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

    public static Comic fromJSON(JSONObject comicJSON)
    {
        try
        {
            Comic comic = new Comic(comicJSON.getString(FILE_NAME), comicJSON.getString(FILE_PATH));
            comic.setTitle(comicJSON.getString(TITLE));
            if (comicJSON.has(COVER_IMAGE))
                comic.setCoverImage(comicJSON.getString(COVER_IMAGE));
            comic.setIssueNumber(comicJSON.getInt(ISSUE_NUMBER));
            comic.setPageCount(comicJSON.getInt(PAGE_COUNT));
            if (comicJSON.has(COLOR_SETTING))
                comic.setColorSetting(comicJSON.getString(COLOR_SETTING));
            comic.setComicColor(comicJSON.getInt(COVER_COLOR));
            comic.setTextColor(comicJSON.getInt(TEXT_COLOR));
            comic.setYear(comicJSON.getInt(YEAR));

            return comic;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject toJSON()
    {
        JSONObject comicJSON = new JSONObject();

        try
        {
            comicJSON.put(TITLE, mTitle);
            if (mCoverImage!=null)
                comicJSON.put(COVER_IMAGE, mCoverImage);
            comicJSON.put(FILE_PATH, mFilePath);
            comicJSON.put(FILE_NAME, mFileName);
            comicJSON.put(ISSUE_NUMBER, mIssueNumber);
            comicJSON.put(PAGE_COUNT, mPageCount);
            if (mColorSetting!=null)
                comicJSON.put(COLOR_SETTING, mColorSetting);
            comicJSON.put(COVER_COLOR, mCoverColor);
            comicJSON.put(TEXT_COLOR, mTextColor);
            comicJSON.put(YEAR, mYear);

            return comicJSON;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
