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

    //additional metadata filters
    private ArrayList<String> mWriterFilters;
    private ArrayList<String> mPencillerFilters;
    private ArrayList<String> mInkerFilters;
    private ArrayList<String> mColoristFilters;
    private ArrayList<String> mLettererFilters;
    private ArrayList<String> mEditorFilters;
    private ArrayList<String> mCoverArtistFilters;
    private ArrayList<String> mStoryArcsFilters;
    private ArrayList<String> mCharactersFilters;

    private static String NAME = "name";
    private static String FILE_LIST = "fileList";
    private static String SERIES_LIST = "seriesList";
    private static String YEAR_LIST = "yearList";
    private static String FOLDER_LIST = "folderList";

    private static String WRITERS_LIST = "writerList";
    private static String PENCILLERS_LIST = "pencillerList";
    private static String INKERS_LIST = "inkerList";
    private static String COLORISTS_LIST = "coloristsList";
    private static String LETTERERS_LIST = "lettererList";
    private static String EDITORS_LIST = "editorList";
    private static String COVER_ARTISTS_LIST = "coverArtistList";
    private static String STORY_ARC_LIST = "storyArcList";
    private static String CHARACTERS_LIST = "characterList";


    public Collection(String name)
    {
        mName = name;
        mFileList = new ArrayList<>();
        mSeriesFilters = new ArrayList<>();
        mYearsFilters = new ArrayList<>();
        mFoldersFilters = new ArrayList<>();

        mWriterFilters = new ArrayList<>();
        mPencillerFilters = new ArrayList<>();
        mInkerFilters = new ArrayList<>();
        mColoristFilters = new ArrayList<>();
        mLettererFilters = new ArrayList<>();
        mEditorFilters = new ArrayList<>();
        mCoverArtistFilters = new ArrayList<>();
        mStoryArcsFilters = new ArrayList<>();
        mCharactersFilters = new ArrayList<>();
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name){mName = name;}

    public void addWriter(String writer){
        if (!mWriterFilters.contains(writer))
            mWriterFilters.add(writer);
    }

    public void addPenciller(String penciller){
        if (!mPencillerFilters.contains(penciller))
            mPencillerFilters.add(penciller);
    }

    public void addInker(String inker){
        if (!mInkerFilters.contains(inker))
            mInkerFilters.add(inker);
    }

    public void addColorist(String colorist){
        if (!mColoristFilters.contains(colorist))
            mColoristFilters.add(colorist);
    }

    public void addLetterer(String letterer){
        if (!mLettererFilters.contains(letterer))
            mLettererFilters.add(letterer);
    }

    public void addEditor(String editor){
        if (!mEditorFilters.contains(editor))
            mEditorFilters.add(editor);
    }

    public void addCoverArtist(String coverArtist){
        if (!mCoverArtistFilters.contains(coverArtist))
            mCoverArtistFilters.add(coverArtist);
    }

    public void addStoryArc(String storyArc){
        if (!mStoryArcsFilters.contains(storyArc))
            mStoryArcsFilters.add(storyArc);
    }

    public void addCharacter(String character){
        if (!mCharactersFilters.contains(character))
            mCharactersFilters.add(character);
    }

    public void addFile(String filename){
        if (!mFileList.contains(filename))
            mFileList.add(filename);
    }

    public void removeFile(String filename){mFileList.remove(filename);}

    public boolean containsFile(String filename){return mFileList.contains(filename);}

    public void addSeries(String seriesName){
        if (!mSeriesFilters.contains(seriesName))
            mSeriesFilters.add(seriesName);
    }

    public void removeSeries(String seriesName){mSeriesFilters.remove(seriesName);}

    public void addYear(int year){
        if (!mYearsFilters.contains(year))
            mYearsFilters.add(year);
    }

    public ArrayList<Integer> getYearsFilters()
    {
        return mYearsFilters;
    }

    public ArrayList<String> getSeriesFilters()
    {
        return mSeriesFilters;
    }

    public ArrayList<String> getWriterFilters() {
        return mWriterFilters;
    }

    public ArrayList<String> getPencillerFilters() {
        return mPencillerFilters;
    }

    public ArrayList<String> getInkerFilters() {
        return mInkerFilters;
    }

    public ArrayList<String> getColoristFilters() {
        return mColoristFilters;
    }

    public ArrayList<String> getLettererFilters() {
        return mLettererFilters;
    }

    public ArrayList<String> getEditorFilters() {
        return mEditorFilters;
    }

    public ArrayList<String> getCoverArtistFilters() {
        return mCoverArtistFilters;
    }

    public ArrayList<String> getStoryArcsFilters() {
        return mStoryArcsFilters;
    }

    public ArrayList<String> getCharactersFilters() {
        return mCharactersFilters;
    }

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
                            || mSeriesFilters.contains(comic.getEditedTitle().trim())
                            || mYearsFilters.contains(comic.getEditedYear())
                            || mFoldersFilters.contains(comic.getFilePath())
                            || mWriterFilters.contains(comic.getWriter())
                            || mPencillerFilters.contains(comic.getPenciller())
                            || mInkerFilters.contains(comic.getInker())
                            || mColoristFilters.contains(comic.getColorist())
                            || mLettererFilters.contains(comic.getLetterer())
                            || mEditorFilters.contains(comic.getEditor())
                            || mCoverArtistFilters.contains(comic.getCoverArtist())
                            || containsStoryArc(comic)
                            || containsCharacter(comic);
                }
                return false;
            }
        };

        return filter;
    }

    public ArrayList<String> getAllFilters()
    {
        ArrayList<String> filters = new ArrayList<>();

        filters.addAll(mFileList);
        filters.addAll(mSeriesFilters);
        for (int i=0;i<mYearsFilters.size();i++)
            filters.add(""+mYearsFilters.get(i));
        filters.addAll(mFoldersFilters);
        filters.addAll(mWriterFilters);
        filters.addAll(mPencillerFilters);
        filters.addAll(mInkerFilters);
        filters.addAll(mColoristFilters);
        filters.addAll(mLettererFilters);
        filters.addAll(mEditorFilters);
        filters.addAll(mCoverArtistFilters);
        filters.addAll(mStoryArcsFilters);
        filters.addAll(mCharactersFilters);

        return filters;
    }

    private boolean containsStoryArc(Comic comic)
    {
        if (comic.getStoryArcs()!=null) {
            for (String storyArc : comic.getStoryArcs()) {
                if (mStoryArcsFilters.contains(storyArc))
                    return true;
            }
        }
        return false;
    }

    private boolean containsCharacter(Comic comic)
    {
        if (comic.getCharacters()!=null) {
            for (String character : comic.getCharacters()) {
                if (mCharactersFilters.contains(character))
                    return true;
            }
        }
        return false;
    }

    public static Collection fromJSON(JSONObject collectionJSON)
    {
        try
        {
            Collection collection = new Collection(collectionJSON.getString(NAME));

            if (collectionJSON.has(FILE_LIST)) {
                JSONArray fileListArray = collectionJSON.getJSONArray(FILE_LIST);
                for (int i = 0; i < fileListArray.length(); i++)
                    collection.addFile(fileListArray.getString(i));
            }

            if (collectionJSON.has(SERIES_LIST)) {
                JSONArray seriesArray = collectionJSON.getJSONArray(SERIES_LIST);
                for (int i = 0; i < seriesArray.length(); i++)
                    collection.addSeries(seriesArray.getString(i));
            }

            if (collectionJSON.has(YEAR_LIST)) {
                JSONArray yearsArray = collectionJSON.getJSONArray(YEAR_LIST);
                for (int i = 0; i < yearsArray.length(); i++)
                    collection.addYear(yearsArray.getInt(i));
            }

            if (collectionJSON.has(FOLDER_LIST)) {
                JSONArray folderArray = collectionJSON.getJSONArray(FOLDER_LIST);
                for (int i = 0; i < folderArray.length(); i++)
                    collection.addFolder(folderArray.getString(i));
            }

            if (collectionJSON.has(WRITERS_LIST)) {
                JSONArray writerArray = collectionJSON.getJSONArray(WRITERS_LIST);
                for (int i = 0; i < writerArray.length(); i++)
                    collection.addWriter(writerArray.getString(i));
            }

            if (collectionJSON.has(PENCILLERS_LIST)) {
                JSONArray pencillerArray = collectionJSON.getJSONArray(PENCILLERS_LIST);
                for (int i = 0; i < pencillerArray.length(); i++)
                    collection.addPenciller(pencillerArray.getString(i));
            }

            if (collectionJSON.has(INKERS_LIST)) {
                JSONArray inkerArray = collectionJSON.getJSONArray(INKERS_LIST);
                for (int i = 0; i < inkerArray.length(); i++)
                    collection.addInker(inkerArray.getString(i));
            }

            if (collectionJSON.has(COLORISTS_LIST)) {
                JSONArray coloristArray = collectionJSON.getJSONArray(COLORISTS_LIST);
                for (int i = 0; i < coloristArray.length(); i++)
                    collection.addColorist(coloristArray.getString(i));
            }

            if (collectionJSON.has(LETTERERS_LIST)) {
                JSONArray lettererArray = collectionJSON.getJSONArray(LETTERERS_LIST);
                for (int i = 0; i < lettererArray.length(); i++)
                    collection.addLetterer(lettererArray.getString(i));
            }

            if (collectionJSON.has(EDITORS_LIST)) {
                JSONArray editorArray = collectionJSON.getJSONArray(EDITORS_LIST);
                for (int i = 0; i < editorArray.length(); i++)
                    collection.addEditor(editorArray.getString(i));
            }

            if (collectionJSON.has(COVER_ARTISTS_LIST)) {
                JSONArray coverArtistArray = collectionJSON.getJSONArray(COVER_ARTISTS_LIST);
                for (int i = 0; i < coverArtistArray.length(); i++)
                    collection.addCoverArtist(coverArtistArray.getString(i));
            }

            if (collectionJSON.has(STORY_ARC_LIST)) {
                JSONArray storyArcArray = collectionJSON.getJSONArray(STORY_ARC_LIST);
                for (int i = 0; i < storyArcArray.length(); i++)
                    collection.addStoryArc(storyArcArray.getString(i));
            }

            if (collectionJSON.has(CHARACTERS_LIST)) {
                JSONArray characterArray = collectionJSON.getJSONArray(CHARACTERS_LIST);
                for (int i = 0; i < characterArray.length(); i++)
                    collection.addCharacter(characterArray.getString(i));
            }

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

            JSONArray writersArray = new JSONArray();
            for (String writer:mWriterFilters)
                writersArray.put(writer);
            collectionJSON.put(WRITERS_LIST, writersArray);

            JSONArray pencillerArray = new JSONArray();
            for (String penciller:mPencillerFilters)
                foldersArray.put(penciller);
            collectionJSON.put(PENCILLERS_LIST, pencillerArray);

            JSONArray inkerArray = new JSONArray();
            for (String inker:mInkerFilters)
                inkerArray.put(inker);
            collectionJSON.put(INKERS_LIST, inkerArray);

            JSONArray coloristArray = new JSONArray();
            for (String colorist:mColoristFilters)
                coloristArray.put(colorist);
            collectionJSON.put(COLORISTS_LIST, coloristArray);

            JSONArray letterersArray = new JSONArray();
            for (String letterer:mLettererFilters)
                letterersArray.put(letterer);
            collectionJSON.put(LETTERERS_LIST, letterersArray);

            JSONArray editorsArray = new JSONArray();
            for (String editor:mEditorFilters)
                editorsArray.put(editor);
            collectionJSON.put(EDITORS_LIST, editorsArray);

            JSONArray coverArtistArray = new JSONArray();
            for (String coverArtist:mCoverArtistFilters)
                coverArtistArray.put(coverArtist);
            collectionJSON.put(COVER_ARTISTS_LIST, coverArtistArray);

            JSONArray storyArcArray = new JSONArray();
            for (String storyArc:mStoryArcsFilters)
                storyArcArray.put(storyArc);
            collectionJSON.put(STORY_ARC_LIST, storyArcArray);

            JSONArray characterArray = new JSONArray();
            for (String character:mCharactersFilters)
                characterArray.put(character);
            collectionJSON.put(CHARACTERS_LIST, characterArray);

            return collectionJSON;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

