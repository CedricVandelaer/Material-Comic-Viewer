package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Cédric on 8/02/2015.
 * Helper class for some preferences*
 */
public class PreferenceSetter {

    private static final String FAVORITE_COMIC_LIST = "favoriteComicList";
    private static final String NUMBER_OF_COMICS_READ = "numberOfComicsRead";
    private static final String NUMBER_OF_COMICS_STARTED = "numberOfComicsStarted";
    private static final String PAGES_READ_LIST = "pagesReadMap";
    private static final String SERIES_PAGES_READ_LIST =  "seriesPagesReadMap";
    private static final String USES_RECENTS = "useRecents";
    private static final String READ_COMIC_LIST = "lastReadComicList";
    private static final String LAST_READ_COMIC = "lastReadComic";
    private static final String FILEPATHS = "Filepaths";
    private static final String EXCLUDED_FILEPATHS = "Excludedpaths";
    private static final String CARD_SIZE = "cardSize";
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String COMICS_ADDED_LIST = "addedComicsList";
    private static final String LONGEST_READ_COMIC = "longestReadComic";
    private static final String PAGE_NUMBER_SETTING="pageNumberSetting";
    private static final String WIDTH_AUTO_FIT_SETTING="widthAutoFit";
    private static final String FOLDER_VIEW_ENABLED="folderViewEnabled";
    private static final String KEEP_SCREEN_ON= "keepScreenOn";
    private static final String ROTATE_LANDSCAPE_PAGE= "rotateLandscapePage";

    public static final String APP_THEME_COLOR = "appThemeColor";
    public static final String FILE_FORMAT_SETTING = "fileFormatSetting";

    public static String getFileFormatSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(FILE_FORMAT_SETTING, context.getResources().getString(R.string.file_format_1));
    }

    public static boolean getRotatePageSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ROTATE_LANDSCAPE_PAGE, false);
    }

    public static boolean getScreenOnSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEEP_SCREEN_ON, true);
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

        removeCloudService(context, service.getEmail(), service.getName());
        cloudServicesList.add(service);

        saveCloudServicesList(context, cloudServicesList);
    }

    public static ArrayList<CloudService> getCloudServices(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayList<CloudService> cloudServices = new ArrayList<>();

        int i =0;
        while (prefs.getString("CloudService "+i, null)!=null)
        {
            cloudServices.add(CloudService.create(prefs.getString("CloudService "+i,null)));
            i++;
        }

        return cloudServices;
    }

    public static void saveCloudServicesList(Context context, List<CloudService> cloudServiceList)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        int j = 0;

        while (prefs.getString("CloudService "+j,null)!=null)
        {
            editor.remove("CloudService "+j);
            j++;
        }

        for (int i=0;i<cloudServiceList.size();i++)
        {
            editor.putString("CloudService "+i,cloudServiceList.get(i).serialize());
        }

        editor.apply();

    }

    public static boolean getAutoFitSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(WIDTH_AUTO_FIT_SETTING, true);
    }

    public static boolean getFolderEnabledSetting(Context context)
    {
        if (context!=null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getBoolean(FOLDER_VIEW_ENABLED, true);
        }
        return false;
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
        String color = prefs.getString(APP_THEME_COLOR,""+context.getResources().getColor(R.color.Teal));
        return Integer.parseInt(color);
    }

    public static void saveAppThemeColor(Context context, CharSequence color)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(APP_THEME_COLOR, color.toString());
        editor.apply();
    }

    public static void resetStats(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LONGEST_READ_COMIC, "comicFileName,0, comicTitle, -1");
        editor.putString(COMICS_ADDED_LIST,"");
        editor.putString(SERIES_PAGES_READ_LIST,"");
        editor.putString(PAGES_READ_LIST,"");
        editor.putInt(NUMBER_OF_COMICS_READ,0);
        editor.putInt(NUMBER_OF_COMICS_STARTED,0);
        editor.apply();
    }


    public static int getLongestReadComicPages(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String longestReadString = prefs.getString(LONGEST_READ_COMIC,"comicFileName,0,comicTitle,comicIssueNumber");

        return Integer.parseInt(longestReadString.split(",")[1]);
    }

    public static String getLongestReadComicTitle(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String longestReadString = prefs.getString(LONGEST_READ_COMIC,"comicFileName,0,comicTitle,-1");

        String pieces[] = longestReadString.split(",");
        if (Integer.parseInt(pieces[1])!=0)
        {
            if (Integer.parseInt(pieces[3])!= -1)
            {
                return pieces[2]+" "+Integer.parseInt(pieces[3]);
            }
            else
            {
                return pieces[2];
            }
        }
        else
        {
            return "None";
        }
    }

    public static void saveLongestReadComic(Context context, String filename, int pageCount, String title, int issueNumber)
    {

        if (pageCount > getLongestReadComicPages(context))
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LONGEST_READ_COMIC, filename+","+pageCount+","+title+","+issueNumber);
            editor.apply();
        }
    }


    public static List<String> getComicsAdded(Context context)
    {
        ArrayList<String> addedComics = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String comicListString = prefs.getString(COMICS_ADDED_LIST, "");

        String[] comicList = comicListString.split(",");
        for (String comic:comicList)
        {
            if (!comic.trim().equals(""))
                addedComics.add(comic);
        }

        return addedComics;
    }

    public static void addAddedComic(Context context, String filename)
    {
        List<String> addedComics = getComicsAdded(context);

        if (addedComics.contains(filename))
            return;
        else
        {
            addedComics.add(filename);
            String stringToSave = "";

            for (String comic:addedComics)
            {
                if (!comic.trim().equals(""))
                    stringToSave+= comic+ ",";
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(COMICS_ADDED_LIST, stringToSave);
            editor.apply();
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

            String stringToSave = "";

            for (String key:pagesReadMap.keySet())
            {
                stringToSave += key+":"+pagesReadMap.get(key)+",";
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(PAGES_READ_LIST, stringToSave);

            editor.apply();
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

                String stringToSave = "";

                for (String key:pagesReadMap.keySet())
                {
                    stringToSave += key+":"+pagesReadMap.get(key)+",";
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(PAGES_READ_LIST, stringToSave);

                editor.apply();
            }
        }
        else
        {
            pagesReadMap.put(filename, pages);

            String stringToSave = "";

            for (String key:pagesReadMap.keySet())
            {
                stringToSave += key+":"+pagesReadMap.get(key)+",";
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(PAGES_READ_LIST, stringToSave);

            editor.apply();
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

        String stringToSave = "";

        for (String key:pagesReadMap.keySet())
        {
            stringToSave += key+":"+pagesReadMap.get(key)+",";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(SERIES_PAGES_READ_LIST, stringToSave);

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

        String stringToSave = "";

        for (String key:pagesReadMap.keySet())
        {
            stringToSave += key+":"+pagesReadMap.get(key)+",";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(SERIES_PAGES_READ_LIST, stringToSave);

        editor.apply();

    }

    public static Map<String, Integer> getPagesReadMap(Context context)
    {
        Map<String, Integer> pagesReadMap = new HashMap<String, Integer>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //List of format "comicname:comicpage,comicname:comicpage,..."
        String pagesReadString = prefs.getString(PAGES_READ_LIST, "");

        String[] pagesReadPairs = pagesReadString.split(",");

        for (String pair:pagesReadPairs)
        {
            try {
                if (!pair.isEmpty() && pair.contains(":")) {
                    int splitPosition = pair.lastIndexOf(":");
                    pagesReadMap.put(pair.substring(0, splitPosition), Integer.parseInt(pair.substring(splitPosition + 1)));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return pagesReadMap;
    }

    public static Map<String, Integer> getSeriesPagesReadMap(Context context)
    {
        Map<String, Integer> pagesReadMap = new HashMap<String, Integer>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String pagesReadString = prefs.getString(SERIES_PAGES_READ_LIST, "");

        String[] pagesReadPairs = pagesReadString.split(",");

        for (String pair:pagesReadPairs)
        {
            if (!pair.isEmpty()) {
                int splitPosition = pair.lastIndexOf(":");
                pagesReadMap.put(pair.substring(0, splitPosition), Integer.parseInt(pair.substring(splitPosition + 1)));
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

        String favoritesString = prefs.getString(FAVORITE_COMIC_LIST, "");

        favoritesString += (","+comicFileName);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FAVORITE_COMIC_LIST,favoritesString);
        editor.apply();

    }

    public static void saveFavoriteComicList(Context context, List<String> favoritesList)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        String favoriteString = "";

        for (String favorite:favoritesList)
        {
            favoriteString+= (favorite+",");
        }
        editor.putString(FAVORITE_COMIC_LIST, favoriteString);
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
        return prefs.getInt(NUMBER_OF_COMICS_READ,0);
    }

    public static boolean usesRecents(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean(USES_RECENTS,true);
    }

    public static List<String> getFavoriteComics(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayList<String> favoriteArrayList = new ArrayList<>();

        String favoritesString = prefs.getString(FAVORITE_COMIC_LIST, "");

        String[] favorites = favoritesString.split(",");

        for (String comic:favorites)
        {
            favoriteArrayList.add(comic);
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

    public static void saveComicList(Context context, List<Comic> comicList)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();

        int i=0;

        while (prefs.getString("Comic "+i, null)!=null)
        {
            sharedPreferencesEditor.remove("Comic "+i);
            i++;
        }

        for (i=0;i<comicList.size();i++)
        {
            String serializedComic = comicList.get(i).serialize();
            sharedPreferencesEditor.putString("Comic "+i, serializedComic);
        }

        sharedPreferencesEditor.apply();

    }

    public static ArrayList<Comic> getSavedComics(Context context)
    {
        ArrayList<Comic> comicList = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int i=0;

        while (prefs.getString("Comic "+i, null)!=null)
        {
            comicList.add(Comic.create(prefs.getString("Comic "+i,null)));
            i++;
        }

        return comicList;
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
    public static void saveLastReadComic(Context context, String comicName, int pageNumber)
    {
        Map<String, Integer> lastReadMap = getReadComics(context);

        lastReadMap.put(comicName, pageNumber);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        StringBuilder csvList = new StringBuilder();

        for (String key:lastReadMap.keySet())
        {
            csvList.append(key+":"+lastReadMap.get(key)+",");
        }

        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString(READ_COMIC_LIST, csvList.toString());

        sharedPreferencesEditor.putString(LAST_READ_COMIC, comicName);

        sharedPreferencesEditor.apply();

    }

    public static String getLastReadComic(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(LAST_READ_COMIC, "none");

    }

    public static Map<String, Integer> getReadComics(Context context)
    {
        Map<String, Integer> lastReadMap = new HashMap<String, Integer>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //List of format "comicname:comicpage,comicname:comicpage,..."
        String lastReadComics = prefs.getString(READ_COMIC_LIST, "");

        String[] lastReadPairs = lastReadComics.split(",");

        for (String pair:lastReadPairs)
        {
            if (!pair.isEmpty()) {
                int splitPosition = pair.lastIndexOf(":");
                lastReadMap.put(pair.substring(0, splitPosition), Integer.parseInt(pair.substring(splitPosition + 1)));
            }
        }

        return lastReadMap;
    }

    public static void removeReadComic(Context context, String filename)
    {
        Map<String, Integer> readMap = getReadComics(context);
        readMap.remove(filename);
        saveReadComicMap(context, readMap);
    }

    public static void saveReadComicMap(Context context, Map<String, Integer> readMap)
    {
        String lastReadComicsString = "";

        for (String key:readMap.keySet())
        {
            lastReadComicsString+= key+":"+readMap.get(key)+",";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(READ_COMIC_LIST, lastReadComicsString);
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

    public static int getBackgroundColorPreference(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String bgcolor = prefs.getString(BACKGROUND_COLOR, context.getString(R.string.backgroundcolor_setting2));
        int color;
        
        if (bgcolor.equals(context.getString(R.string.backgroundcolor_setting1)))
        {
            color = context.getResources().getColor(R.color.BlueGrey);
        }
        else if (bgcolor.equals(context.getString(R.string.backgroundcolor_setting2)))
        {
            color = context.getResources().getColor(R.color.Black);
        }
        else if(bgcolor.equals(context.getString(R.string.backgroundcolor_setting4)))
        {
            color = context.getResources().getColor(R.color.Brown);
        }
        else if(bgcolor.equals(context.getString(R.string.backgroundcolor_setting5)))
        {
            color = context.getResources().getColor(R.color.Grey);
        }
        else
        {
            color = context.getResources().getColor(R.color.WhiteBG);
        }
        
        return color;
    }

    public static ArrayList<String> getFilePathsFromPreferences(Context context) {
        ArrayList<String> paths = new ArrayList<>();
        
        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString(FILEPATHS, defaultPath);

        String[] items = csvList.split(",");
        for(int i=0; i < items.length; i++){
            paths.add(items[i]);
        }

        //remove duplicates
        for (int i=0;i<paths.size()-1;i++)
        {
            for (int j=i+1;j<paths.size();j++)
            {
                if (i!=j)
                {
                    if (paths.get(i).equals(paths.get(j)))
                    {
                        paths.remove(j);
                    }
                }
            }
        }

        if (!paths.contains(defaultPath))
            paths.add(defaultPath);
        if (paths.contains(""))
            paths.remove("");

        return paths;
    }

    public static ArrayList getExcludedPaths(Context context)
    {
        ArrayList<String> paths = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString(EXCLUDED_FILEPATHS, null);
        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";

        if (csvList!=null) {
            String[] items = csvList.split(",");
            for (int i = 0; i < items.length; i++) {
                paths.add(items[i]);
            }
            //remove duplicates
            for (int i = 0; i < paths.size() - 1; i++) {
                for (int j = i + 1; j < paths.size(); j++) {
                    if (i != j) {
                        if (paths.get(i).equals(paths.get(j))) {
                            paths.remove(j);
                        }
                    }
                }
            } 
        }

        if (paths.contains(defaultPath))
            paths.remove(defaultPath);
        if (paths.contains(""))
            paths.remove("");

        return paths;
        
    }

    
    public static void saveFilePaths(Context context, ArrayList<String> filepaths, ArrayList<String> excludedPaths)
    {
        saveFilePaths(context, filepaths);
        saveExcludedFilePaths(context, excludedPaths);
    }

    public static void saveFilePaths(Context context, ArrayList<String> filePaths)
    {
        StringBuilder csvList = new StringBuilder();
        for(int i=0;i<filePaths.size();i++){
            csvList.append(filePaths.get(i));
            csvList.append(",");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString(FILEPATHS, csvList.toString());

        sharedPreferencesEditor.apply();
    }

    public static void saveExcludedFilePaths(Context context, ArrayList<String> excludedFilePaths)
    {
        if (excludedFilePaths.contains(""))
            excludedFilePaths.remove("");

        StringBuilder csvList = new StringBuilder();
        for(int i=0;i<excludedFilePaths.size();i++){
            csvList.append(excludedFilePaths.get(i));
            csvList.append(",");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString(EXCLUDED_FILEPATHS, csvList.toString());

        sharedPreferencesEditor.apply();
    }

}
