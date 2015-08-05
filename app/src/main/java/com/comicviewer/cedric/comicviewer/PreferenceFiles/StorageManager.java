package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Cédric on 8/02/2015.
 * Helper class for some preferences*
 */
public class StorageManager {

    public static final String SAVED_COMICS = "savedComics";
    public static final String FAVORITE_COMIC_LIST_JSON = "favoriteComicListJson";
    public static final String NUMBER_OF_COMICS_READ = "numberOfComicsRead";
    public static final String NUMBER_OF_COMICS_STARTED = "numberOfComicsStarted";
    public static final String PAGES_READ_LIST_JSON = "pagesReadJSON";
    public static final String SERIES_PAGES_READ_LIST_JSON =  "seriesPagesReadMapJson";
    public static final String CURRENT_POSITION_LIST_JSON = "currentPositionsJson";
    public static final String HIDDEN_LIST_JSON = "hiddenListJson";
    public static final String CLOUD_SERVICES_LIST = "cloudServicesJson";

    private static final String READ_SERIES_NAME = "readSeriesName";
    private static final String READ_SERIES_PAGE = "readSeriesPage";

    private static final String READ_COMIC_NAME = "readComicName";
    private static final String READ_COMIC_PAGE = "readComicPage";

    private static final String COMIC_NAME_POSITION = "comicNamePosition";
    private static final String COMIC_PAGE_POSITION = "comicPagePosition";

    public static final String COMICS_ADDED_LIST_JSON = "addedComicsListJson";
    public static final String MANGA_LIST_JSON = "mangaListJson";
    public static final String NORMAL_LIST_JSON = "normalListJson";

    public static final String LONGEST_READ_COMIC_JSON = "longestReadComicJson";
    public static final String LONGEST_READ_COMIC_TITLE = "longestReadComicName";
    public static final String LONGEST_READ_COMIC_ISSUE_NUMBER = "longestReadComicIssueNumber";
    public static final String LONGEST_READ_COMIC_PAGES = "longestReadComicPageCount";
    public static final String LONGEST_READ_COMIC_FILENAME = "longestReadComicFileName";


    public static final String PAGE_NUMBER_SETTING = "pageNumberSetting";
    public static final String CARD_SIZE = "cardSize";
    public static final String FILEPATHS_JSON = "filepathsJson";
    public static final String APP_THEME_COLOR = "appThemeColor";
    public static final String ACCENT_COLOR = "accentColor";
    public static final String FILE_FORMAT_SETTING = "fileFormatSetting";
    public static final String MANGA_SETTING = "mangaEnabled";
    public static final String VOLUME_KEY_OPTION = "volumeKeysOption";
    public static final String READING_BACKGROUND_COLOR = "readingBackgroundColor";
    public static final String VIEWPAGER_ANIMATION_SETTING="viewPagerAnimationSetting";
    public static final String TOOLBAR_OPTION = "toolbarOption";
    public static final String COLLECTIONS_JSON_LIST = "collectionsJsonList";
    public static final String FORCE_PORTRAIT_SETTING = "forcePortrait";
    public static final String SCROLL_ON_ZOOM_SETTING = "allowScrollOnZoom";
    public static final String SORT_SETTING = "sortSetting";
    public static final String SORT_BY_SERIES = "sortSeries";
    public static final String SORT_BY_FILENAME = "sortFilename";
    public static final String SORT_BY_YEAR = "sortYear";
    public static final String SORT_BY_MODIFIED_DATE = "sortModified";
    public static final String SORT_BY_LAST_ADDED = "sortLastAdded";
    public static final String SCROLL_BY_TAP_SETTING = "scrollByTap";
    public static final String PAGE_QUALITY_SETTING = "highResPages";
    public static final String USES_RECENTS = "useRecents";
    public static final String FOLDER_VIEW_ENABLED = "folderViewEnabled";
    public static final String WIDTH_AUTO_FIT_SETTING="widthAutoFit";
    public static final String KEEP_SCREEN_ON= "keepScreenOn";
    public static final String ROTATE_LANDSCAPE_PAGE= "rotateLandscapePage";
    public static final String COMIC_TO_UPDATE = "comicToUpdate";
    public static final String MULTI_PANE = "multiPane";
    public static final String BACKGROUND_COLOR_INT = "backgroundColorInt";
    public static final String CARD_COLOR = "cardColor";
    public static final String SCROLL_ANIMATION = "scrollAnimation";
    public static final String SECTION_ANIMATION = "sectionAnimation";
    public static final String ZOOM_FACTOR = "zoomFactor";
    public static final String SINGLE_TAP_SCROLL = "singleTapScroll";
    public static final String REVERSE_VOLUME_KEYS = "reverseVolumeKeys";


    public static final String COMIC_VIEWER = "ComicViewer";

    public static float getZoomFactorPreference(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(ZOOM_FACTOR, "3.0f");
        try
        {
            return Float.parseFloat(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 3.0f;
        }
    }

    public static boolean getBooleanSetting(Context context, String setting, boolean defaultValue)
    {
        if (context== null)
            return false;
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(setting, defaultValue);
    }

    public static void saveBooleanSetting(Context context, String setting, boolean value) {
        if (context == null)
            return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(setting, value);
        editor.apply();
    }

    public static void saveStringSetting(Context context, String setting, String value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(setting, value);
        editor.apply();
    }

    public static String getStringSetting(Context context, String setting, String defaultValue)
    {
        if (context == null)
            return "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(setting, defaultValue);
    }

    public static void saveIntegerSetting(Context context, String setting, String value)
    {
        try {
            Integer i = Integer.parseInt(value);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(setting, i);
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void saveJSONArray(Context context, String setting, List<String> list)
    {
        JSONArray array = new JSONArray();
        for (String string:list)
            array.put(string);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(setting, array.toString());
        editor.apply();
    }

    public static JSONArray getJSONArray(Context context, String setting)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray array = new JSONArray();
        try {
            array = new JSONArray(preferences.getString(setting, new JSONArray().toString()));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return array;
    }

    public static void saveIntegerSetting(Context context, String setting, int value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(setting, value);
        editor.apply();
    }

    public static void saveSortSetting(Context context, String setting)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(SORT_SETTING, setting);
        editor.apply();
    }
    public static String getSortSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SORT_SETTING, SORT_BY_SERIES);
    }

    public static ArrayList<String> getCollectionNames(Context context)
    {
        ArrayList<Collection> collections = getCollectionList(context);
        ArrayList<String> names = new ArrayList<>();

        for (Collection collection:collections)
            names.add(collection.getName());

        return names;
    }

    public static void addToCollection(Context context, String collectionName, ArrayList<String> filenames)
    {
        ArrayList<Collection> collections = getCollectionList(context);

        for (int i=0;i<collections.size();i++)
        {
            if (collections.get(i).getName().equals(collectionName)) {

                for (String filename:filenames)
                    collections.get(i).addFile(filename);
            }
        }

        saveCollections(context, collections);
    }


    public static void renameCollection(Context context, String collectionName, String newName)
    {
        ArrayList<Collection> collections = getCollectionList(context);

        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.setName(newName);
        }
        saveCollections(context, collections);
    }

    public static void createCollection(Context context, String collectionName)
    {
        Collection newCollection = new Collection(collectionName);
        ArrayList<Collection> collections = getCollectionList(context);
        collections.add(newCollection);
        saveCollections(context, collections);
    }

    public static void removeComicsFromCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        ArrayList<Collection> collections = getCollectionList(context);
        for (int i=0;i<collections.size();i++)
        {
            if (collections.get(i).getName().equals(collectionName)) {

                for (Comic comic:comics)
                    collections.get(i).removeFile(comic.getFileName());
            }
        }

        saveCollections(context, collections);
    }

    public static void removeComicFromCollection(Context context, String collectionName, Comic comic)
    {
        ArrayList<Collection> collections = getCollectionList(context);
        for (int i=0;i<collections.size();i++)
        {
            if (collections.get(i).getName().equals(collectionName))
                collections.get(i).removeFile(comic.getFileName());
        }

        saveCollections(context, collections);
    }

    public static void removeCollection(Context context, String collectionName)
    {
        ArrayList<Collection> collections = getCollectionList(context);
        ArrayList<Collection> collectionsToSave = new ArrayList<>();
        for (int i=0;i<collections.size();i++)
        {
            if (!collections.get(i).getName().equals(collectionName))
                collectionsToSave.add(collections.get(i));
        }

        saveCollections(context, collectionsToSave);
    }

    public static void saveCollections(Context context, ArrayList<Collection> collections)
    {
        JSONArray collectionsJSONArray = createCollectionJSONArray(collections);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(COLLECTIONS_JSON_LIST, collectionsJSONArray.toString());
        editor.apply();
    }

    public static ArrayList<Collection> getCollectionList(Context context)
    {
        JSONArray collectionsArray = getJSONArray(context, COLLECTIONS_JSON_LIST);

        ArrayList<Collection> collections = new ArrayList<>();

        for (int i=0;i<collectionsArray.length();i++) {
            try {
                Collection collection = Collection.fromJSON(collectionsArray.getJSONObject(i));
                if (collection!=null)
                    collections.add(collection);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return collections;

    }

    public static String getPageFlipAnimationSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(VIEWPAGER_ANIMATION_SETTING, context.getString(R.string.none));
    }

    public static boolean getDynamicBackgroundSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(READING_BACKGROUND_COLOR, context.getString(R.string.dynamic)).equals(context.getString(R.string.dynamic));
    }

    public static int getReadingBackgroundSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getString(READING_BACKGROUND_COLOR, context.getString(R.string.dynamic)).equals(context.getString(R.string.black)))
        {
            return context.getResources().getColor(R.color.Black);
        }
        else
        {
            return context.getResources().getColor(R.color.White);
        }
    }

    public static void addHiddenPath(Context context, String path)
    {
        ArrayList<String> currentPaths = getHiddenFiles(context);
        currentPaths.add(path);
        saveHiddenFileList(context, currentPaths);
    }

    public static void batchAddHiddenPath(Context context, ArrayList<String> paths)
    {
        ArrayList<String> currentPaths = getHiddenFiles(context);
        for (String path:paths)
            currentPaths.add(path);
        saveHiddenFileList(context, currentPaths);
    }

    public static void batchRemoveHiddenPaths(Context context, ArrayList<String> paths)
    {
        ArrayList<String> currentPaths = getHiddenFiles(context);
        for (String path:paths)
            currentPaths.remove(path);
        saveHiddenFileList(context, currentPaths);
    }

    public static void removeHiddenPath(Context context, String path)
    {
        ArrayList<String> paths = getHiddenFiles(context);
        paths.remove(path);
        saveHiddenFileList(context, paths);
    }

    public static JSONArray getHiddenFilesJson(Context context)
    {
        return getJSONArray(context, HIDDEN_LIST_JSON);
    }

    public static ArrayList<String> getHiddenFiles(Context context)
    {
        ArrayList<String> hiddenList = new ArrayList<>();

        JSONArray array = getHiddenFilesJson(context);
        for (int i=0;i<array.length();i++)
        {
            try {
                hiddenList.add(array.getString(i));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return hiddenList;
    }

    public static void saveHiddenFileList(Context context, List<String> hiddenFiles)
    {
        saveJSONArray(context, HIDDEN_LIST_JSON, hiddenFiles);
    }

    public static void renamePaths(Context context, String originalPath, String newPath)
    {
        long startTime = System.currentTimeMillis();

        if (getHiddenFiles(context).contains(originalPath)) {
            removeHiddenPath(context, originalPath);
            addHiddenPath(context, newPath);
        }

        long endTime = System.currentTimeMillis();
        Log.d("FOLDER_RENAME", "Hidden files: "+(endTime-startTime));
        startTime = System.currentTimeMillis();

        ArrayList<String> filePaths = getFilePathsFromPreferences(context);
        for (int i=0;i<filePaths.size();i++)
        {
            if (filePaths.get(i).equals(originalPath))
            {
                filePaths.remove(i);
                filePaths.add(newPath);
            }
        }
        saveFilePaths(context, filePaths);

        endTime = System.currentTimeMillis();
        Log.d("FOLDER_RENAME", "Filepaths: "+(endTime-startTime));
        startTime = System.currentTimeMillis();

        ArrayList<Comic> savedComics = getSavedComics(context);
        ArrayList<Comic> comicsToSave = new ArrayList<>();
        Set<Comic> comicsToRemove = new HashSet<>();

        for (int i=0;i<savedComics.size();i++)
        {
            if (savedComics.get(i).getFilePath().equals(originalPath))
            {
                Comic comic = savedComics.get(i);
                comicsToRemove.add(savedComics.get(i));
                Comic renamedComic = new Comic(comic);
                renamedComic.setFilePath(newPath);
                comicsToSave.add(renamedComic);
            }
        }

        batchRemoveSavedComics(context, comicsToRemove);
        batchSaveComics(context, comicsToSave);

        endTime = System.currentTimeMillis();
        Log.d("FOLDER_RENAME", "Saved comics: "+(endTime-startTime));
    }

    public static boolean isNormalComic(Context context, Comic comic)
    {
        List<String> normalList = getNormalComicList(context);
        return normalList.contains(comic.getFileName());
    }

    public static void batchRemoveNormalComics(Context context, ArrayList<Comic> comics)
    {
        List<String> normalComics = getNormalComicList(context);
        for (Comic comic:comics)
            normalComics.add(comic.getFileName());
        saveNormalComicList(context, normalComics);
    }

    public static void removeNormalComic(Context context, String name)
    {
        List<String> normalComics = getNormalComicList(context);
        normalComics.add(name);
        saveNormalComicList(context, normalComics);
    }

    public static void removeNormalComic(Context context, Comic comic)
    {
        List<String> normalComics = getNormalComicList(context);
        normalComics.remove(comic.getFileName());
        saveNormalComicList(context, normalComics);
    }

    public static void batchSaveNormalComics(Context context, ArrayList<Comic> comics)
    {
        List<String> normalComics = getNormalComicList(context);
        for (Comic comic:comics)
            normalComics.add(comic.getFileName());
        saveNormalComicList(context, normalComics);
    }

    public static void saveNormalComic(Context context, Comic comic)
    {
        List<String> normalComics = getNormalComicList(context);
        normalComics.add(comic.getFileName());
        saveNormalComicList(context, normalComics);
    }

    public static void saveNormalComic(Context context, String name)
    {
        List<String> normalComics = getNormalComicList(context);
        normalComics.add(name);
        saveNormalComicList(context, normalComics);
    }

    public static void saveNormalComicList(Context context, List<String> normalComics)
    {
        saveJSONArray(context, NORMAL_LIST_JSON, normalComics);
    }

    public static JSONArray getNormalComicJson(Context context)
    {
        return getJSONArray(context, NORMAL_LIST_JSON);
    }

    public static List<String> getNormalComicList(Context context)
    {
        List<String> normalComicPaths = new ArrayList<>();

        JSONArray array = getNormalComicJson(context);
        for (int i=0;i<array.length();i++)
        {
            try {
                normalComicPaths.add(array.getString(i));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return normalComicPaths;
    }

    public static boolean isMangaComic(Context context, Comic comic)
    {
        List<String> mangaList = getMangaComicList(context);
        return mangaList.contains(comic.getFileName());
    }

    public static void batchRemoveMangaComics(Context context, ArrayList<Comic> comics)
    {
        List<String> mangas = getMangaComicList(context);
        for (Comic comic:comics)
            mangas.remove(comic.getFileName());
        saveMangaList(context, mangas);
    }

    public static void removeMangaComic(Context context, String name)
    {
        List<String> mangas = getMangaComicList(context);
        mangas.remove(name);
        saveMangaList(context, mangas);
    }

    public static void removeMangaComic(Context context, Comic comic)
    {
        List<String> mangas = getMangaComicList(context);
        mangas.remove(comic.getFileName());
        saveMangaList(context, mangas);
    }

    public static void batchSaveMangaComics(Context context, ArrayList<Comic> comics)
    {
        List<String> mangas = getMangaComicList(context);

        for (Comic comic:comics) {
            mangas.add(comic.getFileName());
        }
        saveMangaList(context, mangas);
    }

    public static void saveMangaComic(Context context, Comic comic)
    {
        List<String> mangas = getMangaComicList(context);
        mangas.add(comic.getFileName());
        saveMangaList(context, mangas);
    }

    public static void saveMangaComic(Context context, String name)
    {
        List<String> mangas = getMangaComicList(context);
        mangas.add(name);
        saveMangaList(context, mangas);
    }

    public static void saveMangaList(Context context, List<String> mangaComics)
    {
        saveJSONArray(context, MANGA_LIST_JSON, mangaComics);
    }

    public static JSONArray getMangaComicJson(Context context)
    {
        return getJSONArray(context, MANGA_LIST_JSON);
    }

    public static List<String> getMangaComicList(Context context)
    {
        List<String> mangaComicPaths = new ArrayList<>();

        JSONArray mangaJson = getMangaComicJson(context);

        for (int i=0;i<mangaJson.length();i++) {
            try {
                mangaComicPaths.add(mangaJson.getString(i));
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return mangaComicPaths;
    }


    public static String getFileFormatSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(FILE_FORMAT_SETTING, context.getResources().getString(R.string.file_format_1));
    }

    public static void removeCloudService(Context context, String email, String servicename)
    {
        ArrayList<CloudService> cloudServicesList = getCloudServices(context);
        for (int i=0;i<cloudServicesList.size();i++)
        {
            if (cloudServicesList.get(i).getName().equals(servicename)
                    && cloudServicesList.get(i).getEmail().equals(email))
            {
                cloudServicesList.remove(i);
            }
        }
        saveCloudServicesList(context, cloudServicesList);
    }

    public static void saveCloudService(Context context, CloudService service)
    {
        ArrayList<CloudService> cloudServicesList = getCloudServices(context);

        for (int i=0;i<cloudServicesList.size();i++)
        {
            if (cloudServicesList.get(i).getName().equals(service.getName())
                    && cloudServicesList.get(i).getEmail().equals(service.getEmail()))
                cloudServicesList.remove(i);
        }
        cloudServicesList.add(service);

        saveCloudServicesList(context, cloudServicesList);
    }

    public static ArrayList<CloudService> getCloudServices(Context context)
    {
        if (context == null)
            return new ArrayList<>();
        ArrayList<CloudService> cloudServices = new ArrayList<>();
        JSONArray array = getCloudServicesJson(context);

        for (int i=0;i<array.length();i++)
        {
            try {
                cloudServices.add(CloudService.fromJSON(array.getJSONObject(i)));
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return cloudServices;
    }

    public static JSONArray getCloudServicesJson(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray array = new JSONArray();
        try {
            array = new JSONArray(prefs.getString(CLOUD_SERVICES_LIST, new JSONArray().toString()));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return array;
    }

    public static void saveCloudServicesList(Context context, List<CloudService> cloudServiceList)
    {
        JSONArray array = new JSONArray();
        for (CloudService cloudService:cloudServiceList)
            array.put(cloudService.toJSON());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CLOUD_SERVICES_LIST, array.toString());
        editor.apply();
    }

    public static void setFolderEnabledSetting(Context context, boolean enabled)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(FOLDER_VIEW_ENABLED, enabled);
        editor.apply();
    }

    public static String getPageNumberSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PAGE_NUMBER_SETTING, context.getString(R.string.page_number_setting_1));
    }

    public static int getAppThemeColor(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String color = prefs.getString(APP_THEME_COLOR, "" + context.getResources().getColor(R.color.Teal));
        return Integer.parseInt(color);
    }

    public static int getAccentColor(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String color = prefs.getString(ACCENT_COLOR, "" + getAppThemeColor(context));
        return Integer.parseInt(color);
    }

    public static void saveAppThemeColor(Context context, CharSequence color)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(APP_THEME_COLOR, color.toString());
        editor.apply();
    }

    public static void saveAppAccentColor(Context context, CharSequence color)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ACCENT_COLOR, color.toString());
        editor.apply();
    }

    public static void resetStats(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LONGEST_READ_COMIC_JSON, new JSONObject().toString());
        editor.putString(COMICS_ADDED_LIST_JSON, new JSONArray().toString());
        editor.putString(SERIES_PAGES_READ_LIST_JSON, new JSONArray().toString());
        editor.putString(PAGES_READ_LIST_JSON, new JSONArray().toString());
        editor.putInt(NUMBER_OF_COMICS_READ, 0);
        editor.putInt(NUMBER_OF_COMICS_STARTED, 0);
        editor.apply();
    }


    public static int getLongestReadComicPages(Context context)
    {
        JSONObject longestComic = getLongestReadJson(context);
        if (longestComic.has(LONGEST_READ_COMIC_PAGES))
        {
            try {
                return longestComic.getInt(LONGEST_READ_COMIC_PAGES);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static String getLongestReadComicTitle(Context context)
    {
        JSONObject longestComic = getLongestReadJson(context);
        String title = "";
        try {
            if (longestComic.has(LONGEST_READ_COMIC_TITLE))
                title+= longestComic.getString(LONGEST_READ_COMIC_TITLE);
            if (longestComic.has(LONGEST_READ_COMIC_ISSUE_NUMBER))
                title+= " "+longestComic.getInt(LONGEST_READ_COMIC_ISSUE_NUMBER);
            return title;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "None";
    }

    public static JSONObject getLongestReadJson(Context context)
    {
        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            jsonObject = new JSONObject(preferences.getString(LONGEST_READ_COMIC_JSON, new JSONObject().toString()));
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void saveLongestReadComic(Context context, String filename, int pageCount, String title, int issueNumber)
    {

        if (pageCount > getLongestReadComicPages(context))
        {
            JSONObject longestComic = new JSONObject();
            try
            {
                if (filename!=null)
                    longestComic.put(LONGEST_READ_COMIC_FILENAME, filename);
                if (pageCount!=-1)
                    longestComic.put(LONGEST_READ_COMIC_PAGES, pageCount);
                if (title!= null)
                    longestComic.put(LONGEST_READ_COMIC_TITLE, title);
                if (issueNumber!=-1)
                    longestComic.put(LONGEST_READ_COMIC_ISSUE_NUMBER, issueNumber);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(LONGEST_READ_COMIC_JSON, longestComic.toString());
                editor.apply();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static JSONArray getComicsAddedJson(Context context)
    {
        return getJSONArray(context, COMICS_ADDED_LIST_JSON);
    }

    public static List<String> getComicsAdded(Context context)
    {
        ArrayList<String> addedComics = new ArrayList<>();
        JSONArray array = getComicsAddedJson(context);

        for (int i=0;i<array.length();i++)
        {
            try {
                addedComics.add(array.getString(i));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return addedComics;
    }

    public static void batchAddAddedComics(Context context, Set<String> comicsToAdd)
    {
        List<String> addedComics = getComicsAdded(context);

        for (String comic : comicsToAdd) {
            if (addedComics.contains(comic))
                continue;
            addedComics.add(comic);
        }

        saveAddedComicsList(context, addedComics);
    }

    public static void saveAddedComicsList(Context context, List<String> addedComics)
    {
        saveJSONArray(context, COMICS_ADDED_LIST_JSON, addedComics);
    }

    public static void addAddedComic(Context context, String filename)
    {
        List<String> addedComics = getComicsAdded(context);

        if (!addedComics.contains(filename))
        {
            addedComics.add(filename);
            saveAddedComicsList(context, addedComics);
        }

    }

    public static int getPagesReadForComic(Context context, String filename)
    {
        Map<String, Integer> pagesReadMap = getPagesReadMap(context);

        if (pagesReadMap.containsKey(filename))
            return pagesReadMap.get(filename);
        else
            return 0;
    }

    public static int getPagesReadForSeries(Context context, String seriesName)
    {
        Map<String, Integer> pagesReadMap = getSeriesPagesReadMap(context);

        if (pagesReadMap.containsKey(seriesName))
            return pagesReadMap.get(seriesName);
        else
            return 0;
    }

    public static void resetSavedPagesForComic(Context context, String filename)
    {
        Map<String, Integer> pagesReadMap = getPagesReadMap(context);

        if (pagesReadMap.containsKey(filename)) {
            pagesReadMap.remove(filename);
            savePagesReadMap(context, pagesReadMap);
        }
    }

    public static void savePagesForComic(Context context, String filename, int pages)
    {
        Map<String, Integer> pagesReadMap = getPagesReadMap(context);

        if (pagesReadMap.containsKey(filename))
        {
            if (pages>pagesReadMap.get(filename))
            {
                pagesReadMap.put(filename, pages);
                savePagesReadMap(context, pagesReadMap);
            }
        }
        else
        {
            pagesReadMap.put(filename, pages);
            savePagesReadMap(context, pagesReadMap);
        }
    }

    public static void incrementPagesForSeries(Context context, String title, int increment)
    {

        Map<String, Integer> pagesReadMap = getSeriesPagesReadMap(context);

        int pages = 0;
        if (pagesReadMap.containsKey(title))
            pages = pagesReadMap.get(title);

        pages+=increment;

        pagesReadMap.put(title, pages);

        saveSeriesPagesReadMap(context, pagesReadMap);

    }

    public static void saveSeriesPagesReadMap(Context context, Map<String, Integer> seriesPagesReadMap)
    {
        JSONArray array = new JSONArray();

        for (String key:seriesPagesReadMap.keySet())
        {
            JSONObject comic = new JSONObject();

            try
            {
                comic.put(READ_SERIES_NAME, key);
                comic.put(READ_SERIES_PAGE, seriesPagesReadMap.get(key));
                array.put(comic);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERIES_PAGES_READ_LIST_JSON, array.toString());
        editor.apply();
    }

    public static void decrementPagesForSeries(Context context, String title, int decrement)
    {

        Map<String, Integer> pagesReadMap = getSeriesPagesReadMap(context);

        int pages = 0;
        if (pagesReadMap.containsKey(title))
            pages = pagesReadMap.get(title);

        pages-=decrement;

        pagesReadMap.put(title, pages);

        savePagesReadMap(context, pagesReadMap);
    }

    public static void savePagesReadMap(Context context, Map<String, Integer> pagesReadMap)
    {
        JSONArray array = new JSONArray();

        for (String key:pagesReadMap.keySet())
        {
            JSONObject comic = new JSONObject();

            try
            {
                comic.put(READ_COMIC_NAME, key);
                comic.put(READ_COMIC_PAGE, pagesReadMap.get(key));
                array.put(comic);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PAGES_READ_LIST_JSON, array.toString());
        editor.apply();
    }

    public static JSONArray getPagesReadJson(Context context) {
        return getJSONArray(context, PAGES_READ_LIST_JSON);
    }

    public static Map<String, Integer> getPagesReadMap(Context context)
    {
        Map<String, Integer> pagesReadMap = new HashMap<String, Integer>();

        JSONArray array = getPagesReadJson(context);

        for (int i=0;i<array.length();i++)
        {
            try {
                JSONObject comic = array.getJSONObject(i);
                pagesReadMap.put(comic.getString(READ_COMIC_NAME), comic.getInt(READ_COMIC_PAGE));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return pagesReadMap;
    }

    public static JSONArray getSeriesPagesReadJson(Context context)
    {
        return getJSONArray(context, SERIES_PAGES_READ_LIST_JSON);
    }

    public static Map<String, Integer> getSeriesPagesReadMap(Context context)
    {
        Map<String, Integer> pagesReadMap = new HashMap<String, Integer>();

        JSONArray array = getSeriesPagesReadJson(context);

        for (int i=0;i<array.length();i++)
        {
            try {
                JSONObject comic = array.getJSONObject(i);
                pagesReadMap.put(comic.getString(READ_SERIES_NAME), comic.getInt(READ_SERIES_PAGE));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return pagesReadMap;
    }


    public static String getCardAppearanceSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(CARD_SIZE, context.getString(R.string.card_size_setting_2));
    }

    public static void saveFavoriteComic(Context context, String comicFileName)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String favoritesString = prefs.getString(FAVORITE_COMIC_LIST_JSON, new JSONArray().toString());

        JSONArray array;

        try {
            array = new JSONArray(favoritesString);
            for (int i=0;i<array.length();i++)
            {
                if (array.getString(i).equals(comicFileName))
                    return;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            array = new JSONArray();
        }

        array.put(comicFileName);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FAVORITE_COMIC_LIST_JSON, array.toString());
        editor.apply();

    }

    public static void saveFavoriteComicList(Context context, List<String> favoritesList)
    {

        JSONArray array = new JSONArray();

        for (String favorite:favoritesList)
            array.put(favorite);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FAVORITE_COMIC_LIST_JSON, array.toString());
        editor.apply();
    }


    public static void incrementNumberOfComicsRead(Context context, int increment)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentReadComics = getNumberOfComicsRead(context);
        currentReadComics += increment;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NUMBER_OF_COMICS_READ, currentReadComics);
        editor.apply();
    }

    public static void decrementNumberOfComicsRead(Context context, int decrement)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentReadComics = getNumberOfComicsRead(context);
        currentReadComics -= decrement;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NUMBER_OF_COMICS_READ, currentReadComics);
        editor.apply();
    }

    public static void incrementNumberOfComicsStarted(Context context, int increment)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentStartedComics = getNumberOfComicsStarted(context);
        currentStartedComics += increment;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NUMBER_OF_COMICS_STARTED, currentStartedComics);
        editor.apply();
    }

    public static void decrementNumberOfComicsStarted(Context context, int decrement)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentStartedComics = getNumberOfComicsStarted(context);
        currentStartedComics -= decrement;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NUMBER_OF_COMICS_STARTED, currentStartedComics);
        editor.apply();
    }

    public static int getNumberOfComicsStarted(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(NUMBER_OF_COMICS_STARTED, 0);
    }

    public static int getNumberOfComicsRead(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(NUMBER_OF_COMICS_READ, 0);
    }

    public static JSONArray getFavoriteComicsJson(Context context)
    {
        return getJSONArray(context, FAVORITE_COMIC_LIST_JSON);
    }

    public static List<String> getFavoriteComics(Context context)
    {
        ArrayList<String> favoriteArrayList = new ArrayList<>();

        JSONArray array = getFavoriteComicsJson(context);
        for (int i=0;i<array.length();i++)
        {
            try {
                favoriteArrayList.add(array.getString(i));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return favoriteArrayList;

    }

    public static void removeFavoriteComic(Context context, String comicFileName)
    {
        List<String> favorites = getFavoriteComics(context);
        favorites.remove(comicFileName);
        saveFavoriteComicList(context, favorites);
    }

    public static void saveComic(Context context, Comic comic)
    {
        List<Comic> savedComics = getSavedComics(context);
        boolean found = false;
        int foundPos = 0;
        for (int i=0;!found && i<savedComics.size();i++)
        {
            if (savedComics.get(i).getFilePath().equals(comic.getFilePath())
                    && savedComics.get(i).getFileName().equals(comic.getFileName()))
            {
                found = true;
                foundPos = i;
            }
        }
        if (found)
        {
            savedComics.remove(foundPos);
        }
        savedComics.add(comic);
        saveComicList(context, savedComics);

    }


    public static void batchSaveComics(Context context, ArrayList<Comic> comics)
    {
        List<Comic> savedComics = getSavedComics(context);

        for (Comic comic:comics) {
            boolean found = false;
            int foundPos = 0;
            for (int i = 0; !found && i < savedComics.size(); i++) {
                if (savedComics.get(i).getFilePath().equals(comic.getFilePath())
                        && savedComics.get(i).getFileName().equals(comic.getFileName())) {
                    found = true;
                    foundPos = i;
                }
            }
            if (found) {
                savedComics.remove(foundPos);
            }
            savedComics.add(comic);
        }
        saveComicList(context, savedComics);
    }

    public static void saveComicList(Context context, List<Comic> comicList)
    {

        JSONArray comicsJSONArray = createComicJSONArray(comicList);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(SAVED_COMICS, comicsJSONArray.toString());

        editor.apply();

    }

    public static JSONArray createComicJSONArray(List<Comic> comicList)
    {
        JSONArray array = new JSONArray();

        for (Comic comic:comicList)
        {
            array.put(comic.toJSON());
        }
        return array;
    }

    public static JSONArray createCollectionJSONArray(List<Collection> collectionList)
    {
        JSONArray array = new JSONArray();

        for (Collection collection:collectionList)
        {
            array.put(collection.toJSON());
        }
        return array;
    }


    public static void batchRemoveSavedComics(Context context, Set<Comic> comicsToRemove)
    {
        List<Comic> currentComicList = getSavedComics(context);
        List<Comic> comicsToKeep = new ArrayList<>();

        for (int i=0;i<currentComicList.size();i++)
        {
            boolean found = false;
            Iterator<Comic> iterator = comicsToRemove.iterator();
            while(!found && iterator.hasNext())
            {
                Comic comicToRemove = iterator.next();
                if (comicToRemove.getFileName().equals(currentComicList.get(i).getFileName())
                        && comicToRemove.getFilePath().equals(currentComicList.get(i).getFilePath()))
                {
                    found =true;
                }
            }

            if (!found)
            {
                comicsToKeep.add(currentComicList.get(i));
            }
        }
        saveComicList(context, comicsToKeep);
    }

    public static JSONArray getSavedComicsJson(Context context)
    {
        return getJSONArray(context, SAVED_COMICS);
    }

    public static ArrayList<Comic> getSavedComics(Context context)
    {
        ArrayList<Comic> comicList = new ArrayList<>();

        JSONArray comicsJSONArray = getSavedComicsJson(context);


        for (int i=0;i<comicsJSONArray.length();i++)
        {
            try {
                Comic comic = Comic.fromJSON(comicsJSONArray.getJSONObject(i));
                if (comic!=null)
                    comicList.add(comic);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return comicList;
    }

    public static void batchRemoveSavedComics(Context context, List<Comic> comicsToRemove)
    {
        List<Comic> currentComicList = getSavedComics(context);
        List<Comic> comicsToKeep = new ArrayList<>();

        for (Comic comic:currentComicList)
        {
            boolean mustBeRemoved = false;

            for (int i=0;!mustBeRemoved && i<comicsToRemove.size();i++)
            {
                if (comic.getFileName().equals(comicsToRemove.get(i).getFileName())
                        && comic.getFilePath().equals(comicsToRemove.get(i).getFilePath()))
                {
                    mustBeRemoved = true;
                }
            }
            if (!mustBeRemoved)
                comicsToKeep.add(comic);
        }
        saveComicList(context, comicsToKeep);
    }

    public static void removeSavedComic(Context context, Comic comicToRemove)
    {
        List<Comic> currentComicList = getSavedComics(context);
        List<Comic> comicsToKeep = new ArrayList<>();

        for (Comic comic:currentComicList)
        {
            if (!(comic.getFileName().equals(comicToRemove.getFileName())
                    && comic.getFilePath().equals(comicToRemove.getFilePath())))
            {
                comicsToKeep.add(comic);
            }
        }
        saveComicList(context, comicsToKeep);
    }

    //saves comic filename and pagenumber
    public static void saveComicPosition(Context context, String comicName, int pageNumber)
    {
        JSONArray lastReadJSON = getComicPositionJson(context);

        JSONObject comic = new JSONObject();

        try
        {
            comic.put(COMIC_NAME_POSITION, comicName);
            comic.put(COMIC_PAGE_POSITION, pageNumber);

            lastReadJSON.put(comic);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString(CURRENT_POSITION_LIST_JSON, lastReadJSON.toString());

        saveComicToUpdate(context, comicName);

        sharedPreferencesEditor.apply();

    }

    public static void saveComicToUpdate(Context context, String filename)
    {
        List<String> currentFileNames = getComicsToUpdate(context, false);

        if (currentFileNames == null)
            currentFileNames = new ArrayList<>();

        if (!currentFileNames.contains(filename))
            currentFileNames.add(filename);
        else
            return;

        JSONArray array = new JSONArray();

        for (String file:currentFileNames)
            array.put(file);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString(COMIC_TO_UPDATE, array.toString());
        sharedPreferencesEditor.apply();
    }

    public static List<String> getComicsToUpdate(Context context, boolean clearData)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String comicsJson = prefs.getString(COMIC_TO_UPDATE, null);

        if (comicsJson==null)
            return null;

        List<String> filenames = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(comicsJson);

            for (int i=0;i<array.length();i++)
                filenames.add(array.getString(i));

            return filenames;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally {
            if (clearData)
            {
                SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
                sharedPreferencesEditor.putString(COMIC_TO_UPDATE, null);
                sharedPreferencesEditor.apply();
            }
        }


    }

    public static JSONArray getComicPositionJson(Context context)
    {
        return getJSONArray(context, CURRENT_POSITION_LIST_JSON);
    }

    public static Map<String, Integer> getComicPositionsMap(Context context)
    {
        Map<String, Integer> lastReadMap = new HashMap<String, Integer>();

        JSONArray array = getComicPositionJson(context);

        for (int i=0;i<array.length();i++)
        {
            try {
                JSONObject comic = array.getJSONObject(i);
                lastReadMap.put(comic.getString(COMIC_NAME_POSITION), comic.getInt(COMIC_PAGE_POSITION));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return lastReadMap;
    }

    public static void removeReadComic(Context context, String filename)
    {
        Map<String, Integer> readMap = getComicPositionsMap(context);
        readMap.remove(filename);
        saveComicPositionsMap(context, readMap);
    }

    public static void saveComicPositionsMap(Context context, Map<String, Integer> positionsMap)
    {
        JSONArray array = new JSONArray();

        for (String key:positionsMap.keySet())
        {
            try {
                JSONObject comic = new JSONObject();
                comic.put(COMIC_NAME_POSITION, key);
                comic.put(COMIC_PAGE_POSITION, positionsMap.get(key));
                array.put(comic);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(CURRENT_POSITION_LIST_JSON, array.toString());
        editor.apply();
    }

    public static void setBackgroundColorPreference(Activity activity)
    {
        View layout = activity.getWindow().getDecorView().getRootView();
        
        int color = getBackgroundColorPreference(activity);
        
        layout.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT>20) {
            if (color != activity.getResources().getColor(R.color.WhiteBG))
                activity.getWindow().setNavigationBarColor(getBackgroundColorPreference(activity));
            else
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.Black));
        }
        
    }

    public static void setBackgroundColorPreference(Context context, String value)
    {
        try
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            int bgColor = Integer.parseInt(value);
            editor.putInt(BACKGROUND_COLOR_INT, bgColor);
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static boolean hasWhiteBackgroundSet(Context context)
    {
        int bgColor = getBackgroundColorPreference(context);

        if (bgColor == context.getResources().getColor(R.color.WhiteBG))
            return true;
        if (bgColor == context.getResources().getColor(R.color.White))
            return true;
        return false;
    }

    public static int getBackgroundColorPreference(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(BACKGROUND_COLOR_INT, context.getResources().getColor(R.color.WhiteBG));
    }

    public static void removeFilePath(Context context, String path)
    {
        ArrayList<String> paths = getFilePathsFromPreferences(context);
        paths.remove(path);
        saveFilePaths(context, paths);
    }

    public static void batchRemoveFilePaths(Context context, ArrayList<String> paths)
    {
        ArrayList<String> currentPaths = getFilePathsFromPreferences(context);
        for (String path:paths)
            currentPaths.remove(path);
        saveFilePaths(context, currentPaths);
    }


    public static ArrayList<String> getFilePathsFromPreferences(Context context) {

        JSONArray array = getJSONArray(context, FILEPATHS_JSON);
        ArrayList<String> paths = new ArrayList<>();
        
        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ComicViewer";

        for (int i=0;i<array.length();i++)
        {
            try {
                paths.add(array.getString(i));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        if (!paths.contains(defaultPath))
            paths.add(defaultPath);

        return paths;
    }


    public static void saveFilePaths(Context context, ArrayList<String> filePaths)
    {
        saveJSONArray(context, FILEPATHS_JSON, filePaths);
    }

}
