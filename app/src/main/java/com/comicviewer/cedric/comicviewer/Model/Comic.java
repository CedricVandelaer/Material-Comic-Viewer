package com.comicviewer.cedric.comicviewer.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private static String EDITED_TITLE = "editedTitle";
    private static String EDITED_YEAR = "editedYear";
    private static String EDITED_ISSUE_NUMBER = "editedIssueNumber";
    private static String DESCRIPTION = "description";
    private static String WRITER = "writer";
    private static String PENCILLER = "penciller";
    private static String INKER = "inker";
    private static String COLORIST = "colorist";
    private static String LETTERER = "letterer";
    private static String EDITOR = "editor";
    private static String COVER_ARTIST = "coverArtist";
    private static String STORY_ARCS = "storyArcs";
    private static String CHARACTERS = "characters";
    private static String ADDITIONAL_INFO = "additionInfo";

    String mTitle;//The title of the comic series

    String mCoverImage;//The path to the extracted cover image file

    String mFilePath;//The path to the folder of the comic file

    String mFileName;//The filename of the comic

    int mIssueNumber;//The issue number of the comic, -1 if not found

    int mPageCount;//The page count of the comic

    String mColorSetting;//The colorsetting that the comic has currently loaded

    int mCoverColor;//The color of the card displaying the comic

    int mTextColor;//The color of the primary text

    int mYear;//The year of the comic

    //User edited data
    private String mEditedTitle;
    private int mEditedYear;
    private int mEditedIssueNumber;

    //Additional metadata
    private String mDescription;
    private String mWriter;
    private String mPenciller;
    private String mInker;
    private String mColorist;
    private String mLetterer;
    private String mEditor;
    private String mCoverArtist;
    private ArrayList<String> mStoryArcs;
    private ArrayList<String> mCharacters;
    private String mAdditionalInfo;

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
        this.mEditedTitle = otherComic.getEditedTitle();
        this.mEditedYear = otherComic.getEditedYear();
        this.mEditedIssueNumber = otherComic.getEditedIssueNumber();

        mDescription = otherComic.getDescription();
        mWriter = otherComic.getWriter();
        mPenciller = otherComic.getPenciller();
        mInker = otherComic.getInker();
        mColorist = otherComic.getColorist();
        mLetterer = otherComic.getLetterer();
        mEditor = otherComic.getEditor();
        mCoverArtist = otherComic.getCoverArtist();
        mStoryArcs = otherComic.getStoryArcs();
        mCharacters = otherComic.getCharacters();
        mAdditionalInfo = otherComic.getAdditionalInfo();
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

        setBasicInfo();

        mCoverImage = null;
        this.mEditedTitle = null;
        this.mEditedYear = -1;
        this.mEditedIssueNumber = -1;

        mDescription = null;
        mWriter = null;
        mPenciller = null;
        mInker = null;
        mColorist = null;
        mLetterer = null;
        mEditor = null;
        mCoverArtist = null;
        mStoryArcs = null;
        mCharacters = null;
        mAdditionalInfo = null;
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
        dest.writeString(mEditedTitle);
        dest.writeInt(mEditedIssueNumber);
        dest.writeInt(mEditedYear);

        dest.writeString(mDescription);
        dest.writeString(mWriter);
        dest.writeString(mPenciller);
        dest.writeString(mInker);
        dest.writeString(mColorist);
        dest.writeString(mLetterer);
        dest.writeString(mEditor);
        dest.writeString(mCoverArtist);
        dest.writeStringList(mStoryArcs);
        dest.writeStringList(mCharacters);
        dest.writeString(mAdditionalInfo);
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
        mEditedTitle = in.readString();
        mEditedIssueNumber = in.readInt();
        mEditedYear = in.readInt();

        mDescription = in.readString();
        mWriter = in.readString();
        mPenciller = in.readString();
        mInker = in.readString();
        mColorist = in.readString();
        mLetterer = in.readString();
        mEditor = in.readString();
        mCoverArtist = in.readString();
        mStoryArcs = in.createStringArrayList();
        mCharacters = in.createStringArrayList();
        mAdditionalInfo = in.readString();
    }

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

            if (comicJSON.has(EDITED_TITLE))
                comic.setEditedTitle(comicJSON.getString(EDITED_TITLE));
            if (comicJSON.has(EDITED_ISSUE_NUMBER))
                comic.setEditedIssueNumber(comicJSON.getInt(EDITED_ISSUE_NUMBER));
            if (comicJSON.has(EDITED_YEAR))
                comic.setEditedYear(comicJSON.getInt(EDITED_YEAR));

            if (comicJSON.has(DESCRIPTION))
                comic.setDescription(comicJSON.getString(DESCRIPTION));
            if (comicJSON.has(WRITER))
                comic.setWriter(comicJSON.getString(WRITER));
            if (comicJSON.has(PENCILLER))
                comic.setPenciller(comicJSON.getString(PENCILLER));
            if (comicJSON.has(INKER))
                comic.setInker(comicJSON.getString(INKER));
            if (comicJSON.has(COLORIST))
                comic.setColorist(comicJSON.getString(COLORIST));
            if (comicJSON.has(LETTERER))
                comic.setLetterer(comicJSON.getString(LETTERER));
            if (comicJSON.has(EDITOR))
                comic.setEditor(comicJSON.getString(EDITOR));
            if (comicJSON.has(COVER_ARTIST))
                comic.setCoverArtist(comicJSON.getString(COVER_ARTIST));

            if (comicJSON.has(STORY_ARCS))
            {
                JSONArray array = comicJSON.getJSONArray(STORY_ARCS);
                ArrayList<String> story_arcs = new ArrayList<>();

                for (int i=0;i<array.length();i++)
                {
                    story_arcs.add(array.getString(i));
                }
                comic.setStoryArcs(story_arcs);
            }

            if (comicJSON.has(CHARACTERS))
            {
                JSONArray array = comicJSON.getJSONArray(CHARACTERS);
                ArrayList<String> characters = new ArrayList<>();

                for (int i=0;i<array.length();i++)
                {
                    characters.add(array.getString(i));
                }
                comic.setCharacters(characters);
            }

            if (comicJSON.has(ADDITIONAL_INFO))
                comic.setAdditionalInfo(comicJSON.getString(ADDITIONAL_INFO));

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

            if (mEditedTitle!=null)
                comicJSON.put(EDITED_TITLE, mEditedTitle);
            if (mEditedIssueNumber!=-1)
                comicJSON.put(EDITED_ISSUE_NUMBER, mEditedIssueNumber);
            if (mEditedYear!=-1)
                comicJSON.put(EDITED_YEAR, mEditedYear);

            if (mDescription!=null)
                comicJSON.put(DESCRIPTION, mDescription);
            if (mWriter!=null)
                comicJSON.put(WRITER, mWriter);
            if (mPenciller!=null)
                comicJSON.put(PENCILLER, mPenciller);
            if (mInker!=null)
                comicJSON.put(INKER, mInker);
            if (mColorist!=null)
                comicJSON.put(COLORIST, mColorist);
            if (mLetterer!=null)
                comicJSON.put(LETTERER, mLetterer);
            if (mEditor!=null)
                comicJSON.put(EDITOR, mEditor);
            if (mCoverArtist!=null)
                comicJSON.put(COVER_ARTIST, mCoverArtist);
            if (mStoryArcs!=null)
            {
                JSONArray array = new JSONArray();
                for (int i=0;i<mStoryArcs.size();i++)
                    array.put(mStoryArcs.get(i));
                comicJSON.put(STORY_ARCS, array);
            }
            if (mCharacters!=null)
            {
                JSONArray array = new JSONArray();
                for (int i=0;i<mCharacters.size();i++)
                    array.put(mCharacters.get(i));
                comicJSON.put(CHARACTERS, array);
            }
            if (mAdditionalInfo!=null)
                comicJSON.put(ADDITIONAL_INFO, mAdditionalInfo);

            return comicJSON;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
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
        mEditedTitle = null;
        mEditedIssueNumber = -1;
        mEditedYear = -1;

        mDescription = null;
        mWriter = null;
        mPenciller = null;
        mInker = null;
        mColorist = null;
        mLetterer = null;
        mEditor = null;
        mCoverArtist = null;
        mStoryArcs = null;
        mCharacters = null;
        mAdditionalInfo = null;
    }

    public Comic(Parcel in)
    {
        readFromParcel(in);
    }


    public String getEditedTitle(){

        if (mEditedTitle==null)
            return mTitle;
        else
            return mEditedTitle;
    }

    public int getEditedYear(){
        if (mEditedYear==-1)
            return mYear;
        else
            return mEditedYear;
    }

    public int getEditedIssueNumber(){

        if (mEditedIssueNumber==-1)
            return mIssueNumber;
        else
            return mEditedIssueNumber;
    }

    public void setEditedTitle(String title){mEditedTitle = title;}

    public void setEditedYear(int year){mEditedYear = year;}

    public void setEditedIssueNumber(int issueNumber){mEditedIssueNumber = issueNumber;}

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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getWriter() {
        return mWriter;
    }

    public void setWriter(String writer) {
        this.mWriter = writer;
    }

    public String getPenciller() {
        return mPenciller;
    }

    public void setPenciller(String penciller) {
        this.mPenciller = penciller;
    }

    public String getInker() {
        return mInker;
    }

    public void setInker(String inker) {
        this.mInker = inker;
    }

    public String getColorist() {
        return mColorist;
    }

    public void setColorist(String colorist) {
        this.mColorist = colorist;
    }

    public String getLetterer() {
        return mLetterer;
    }

    public void setLetterer(String letterer) {
        this.mLetterer = letterer;
    }

    public String getEditor() {
        return mEditor;
    }

    public void setEditor(String editor) {
        this.mEditor = editor;
    }

    public String getCoverArtist() {
        return mCoverArtist;
    }

    public void setCoverArtist(String coverArtist) {
        this.mCoverArtist = coverArtist;
    }

    public ArrayList<String> getStoryArcs() {
        return mStoryArcs;
    }

    public void setStoryArcs(ArrayList<String> storyArcs) {
        this.mStoryArcs = storyArcs;
    }

    public ArrayList<String> getCharacters() {
        return mCharacters;
    }

    public void setCharacters(ArrayList<String> characters) {
        this.mCharacters = characters;
    }

    public String getAdditionalInfo() {
        return mAdditionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.mAdditionalInfo = additionalInfo;
    }

    private void setBasicInfo() {
        try {
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(mFileName);
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
            String filenameAfterTitle = mFileName;

            if (mFileName.contains(mTitle))
                filenameAfterTitle = mFileName.substring(mFileName.indexOf(mTitle));

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
}
