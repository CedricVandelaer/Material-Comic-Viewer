package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


/**
 * Created by Cédric on 24/07/2015.
 */
public class Exporter {

    private static final String DATE = "Date";
    private static final String COMICS = "Comics";
    private static final String COLLECTIONS = "Collections";
    private static final String FAVORITES = "Favorites";
    private static final String NUMBER_COMICS_READ = "NumberOfComicsRead";
    private static final String NUMBER_COMICS_STARTED = "NumberOfComicsStarted";
    private static final String PAGES_READ_LIST = "PagesReadList";
    private static final String SERIES_READ_LIST = "SeriesPagesReadList";
    private static final String COMIC_POSITIONS_LIST = "ComicPositionsList";
    private static final String ADDED_COMICS_LIST = "AddedComics";
    private static final String MANGA_COMIC_LIST = "MangaComicList";
    private static final String NORMAL_COMIC_LIST = "NormalComicList";
    private static final String CLOUD_SERVICES_LIST = "CloudServices";
    private static final String LONGEST_READ_COMIC = "LongestComicRead";

    public static boolean exportData(Context context, String fileName, String locationPath)
    {
        try {
            JSONObject root = new JSONObject();

            root.put(DATE, new SimpleDateFormat("dd-MM-yyyy").format(new Date(System.currentTimeMillis())));
            root.put(COMICS, StorageManager.getSavedComicsJson(context));
            root.put(COLLECTIONS, StorageManager.getJSONArray(context, StorageManager.COLLECTIONS_JSON_LIST));
            root.put(FAVORITES, StorageManager.getFavoriteComicsJson(context));
            root.put(NUMBER_COMICS_READ, StorageManager.getNumberOfComicsRead(context));
            root.put(NUMBER_COMICS_STARTED, StorageManager.getNumberOfComicsStarted(context));
            root.put(PAGES_READ_LIST, StorageManager.getPagesReadJson(context));
            root.put(SERIES_READ_LIST, StorageManager.getSeriesPagesReadJson(context));
            root.put(COMIC_POSITIONS_LIST, StorageManager.getComicPositionJson(context));
            root.put(ADDED_COMICS_LIST, StorageManager.getComicsAddedJson(context));
            root.put(MANGA_COMIC_LIST, StorageManager.getMangaComicJson(context));
            root.put(NORMAL_COMIC_LIST, StorageManager.getNormalComicJson(context));
            root.put(CLOUD_SERVICES_LIST, StorageManager.getCloudServicesJson(context));
            root.put(LONGEST_READ_COMIC, StorageManager.getLongestReadJson(context));

            String outputPath = locationPath+"/"+fileName;

            if (!outputPath.endsWith(".cvexport"))
                outputPath+=".cvexport";

            File output = new File(outputPath);
            if (!output.exists())
                output.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(output);
            byte[] content = root.toString().getBytes();

            outputStream.write(content);
            outputStream.flush();
            outputStream.close();

            Log.d("StorageManager", "Backup-file saved to " + locationPath);
            return true;

        } catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean importData(Context context, File exportedData)
    {
        try {

            String json = readExportedFile(exportedData);
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has(COMICS))
                StorageManager.saveStringSetting(context, StorageManager.SAVED_COMICS, jsonObject.getString(COMICS));
            if (jsonObject.has(FAVORITES))
                StorageManager.saveStringSetting(context, StorageManager.FAVORITE_COMIC_LIST_JSON, jsonObject.getString(FAVORITES));
            if (jsonObject.has(COLLECTIONS))
                StorageManager.saveStringSetting(context, StorageManager.COLLECTIONS_JSON_LIST, jsonObject.getString(COLLECTIONS));
            if (jsonObject.has(NUMBER_COMICS_READ))
                StorageManager.saveIntegerSetting(context, StorageManager.NUMBER_OF_COMICS_READ, jsonObject.getString(NUMBER_COMICS_READ));
            if (jsonObject.has(NUMBER_COMICS_STARTED))
                StorageManager.saveIntegerSetting(context, StorageManager.NUMBER_OF_COMICS_STARTED, jsonObject.getString(NUMBER_COMICS_STARTED));
            if (jsonObject.has(PAGES_READ_LIST))
                StorageManager.saveStringSetting(context, StorageManager.PAGES_READ_LIST_JSON, jsonObject.getString(PAGES_READ_LIST));
            if (jsonObject.has(SERIES_READ_LIST))
                StorageManager.saveStringSetting(context, StorageManager.SERIES_PAGES_READ_LIST_JSON, jsonObject.getString(SERIES_READ_LIST));
            if (jsonObject.has(COMIC_POSITIONS_LIST))
                StorageManager.saveStringSetting(context, StorageManager.CURRENT_POSITION_LIST_JSON, jsonObject.getString(COMIC_POSITIONS_LIST));
            if (jsonObject.has(ADDED_COMICS_LIST))
                StorageManager.saveStringSetting(context, StorageManager.COMICS_ADDED_LIST_JSON, jsonObject.getString(ADDED_COMICS_LIST));
            if (jsonObject.has(MANGA_COMIC_LIST))
                StorageManager.saveStringSetting(context, StorageManager.MANGA_LIST_JSON, jsonObject.getString(MANGA_COMIC_LIST));
            if (jsonObject.has(NORMAL_COMIC_LIST))
                StorageManager.saveStringSetting(context, StorageManager.NORMAL_LIST_JSON, jsonObject.getString(NORMAL_COMIC_LIST));
            if (jsonObject.has(CLOUD_SERVICES_LIST))
                StorageManager.saveStringSetting(context, StorageManager.CLOUD_SERVICES_LIST, jsonObject.getString(CLOUD_SERVICES_LIST));
            if (jsonObject.has(LONGEST_READ_COMIC))
                StorageManager.saveStringSetting(context, StorageManager.LONGEST_READ_COMIC_JSON, jsonObject.getString(LONGEST_READ_COMIC));

            return true;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private static String readExportedFile(File exportedData)
    {
        try {
            FileInputStream inputStream = new FileInputStream(exportedData);
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


}
