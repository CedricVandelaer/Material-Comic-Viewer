package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
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
    private static final String CARD_SIZE = "cardSize";
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String COMICS_ADDED_LIST = "addedComicsList";
    private static final String LONGEST_READ_COMIC = "longestReadComic";
    private static final String PAGE_NUMBER_SETTING="pageNumberSetting";
    private static final String WIDTH_AUTO_FIT_SETTING="widthAutoFit";
    private static final String FOLDER_VIEW_ENABLED="folderViewEnabled";
    private static final String KEEP_SCREEN_ON= "keepScreenOn";
    private static final String ROTATE_LANDSCAPE_PAGE= "rotateLandscapePage";
    private static final String MANGA_LIST="mangaList";
    private static final String NORMAL_LIST="normalList";
    private static final String LAST_USED_GOOGLE_ACCOUNT = "lastUsedGoogleAccount";

    public static final String APP_THEME_COLOR = "appThemeColor";
    public static final String ACCENT_COLOR = "accentColor";
    public static final String FILE_FORMAT_SETTING = "fileFormatSetting";
    public static final String MANGA_SETTING = "mangaEnabled";
    public static final String UNHIDE_LIST = "unhideListSetting";
    public static final String VOLUME_KEY_OPTION = "volumeKeysOption";
    public static final String READING_BACKGROUND_COLOR = "readingBackgroundColor";
    public static final String VIEWPAGER_ANIMATION_SETTING="viewPagerAnimationSetting";
    public static final String TOOLBAR_OPTION = "toolbarOption";
    public static final String COLLECTIONS_LIST = "collectionsList";
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

    public static final String COMIC_VIEWER = "ComicViewer";

    public static boolean getPageQualitySetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PAGE_QUALITY_SETTING, false);
    }

    public static boolean getScrollByTapSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SCROLL_BY_TAP_SETTING, false);
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

    public static boolean getScrollOnZoomSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SCROLL_ON_ZOOM_SETTING, true);
    }

    public static boolean getForcePortraitSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(FORCE_PORTRAIT_SETTING, false);
    }

    public static ArrayList<String> getCollectionNames(Context context)
    {
        JSONArray collections = PreferenceSetter.getCollectionList(context);
        ArrayList<String> collectionNames = new ArrayList<>();

        for (int i=0;i<collections.length();i++)
        {
            try {
                collectionNames.add(collections.getJSONObject(i).keys().next());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return collectionNames;
    }

    public static void addToCollection(Context context, String collectionName, ArrayList<String> filenames, boolean dummy)
    {
        JSONArray collections = getCollectionList(context);

        JSONArray collection = new JSONArray();
        int index = -1;
        try {
            for (int i=0;i<collections.length();i++)
            {
                if (collections.getJSONObject(i).keys().next().equals(collectionName))
                {
                    index = i;
                    collection = collections.getJSONObject(i).getJSONArray(collectionName);
                }
            }

            for (int i=0;i<filenames.size();i++)
            {
                collection.put(filenames.get(i));
            }

            if (index!=-1) {
                JSONObject newCollection = new JSONObject();
                newCollection.put(collectionName, collection);
                collections.put(index, newCollection);
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(COLLECTIONS_LIST, collections.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void addToCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        JSONArray collections = getCollectionList(context);

        JSONArray collection = new JSONArray();
        int index = -1;
        try {
            for (int i=0;i<collections.length();i++)
            {
                if (collections.getJSONObject(i).keys().next().equals(collectionName))
                {
                    index = i;
                    collection = collections.getJSONObject(i).getJSONArray(collectionName);
                }
            }

            for (int i=0;i<comics.size();i++)
            {
                collection.put(comics.get(i).getFileName());
            }

            if (index!=-1) {
                JSONObject newCollection = new JSONObject();
                newCollection.put(collectionName, collection);
                collections.put(index, newCollection);
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(COLLECTIONS_LIST, collections.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void renameCollection(Context context, String collectionName, String newName)
    {

        JSONArray collections = getCollectionList(context);
        JSONArray newCollections = new JSONArray();
        try {
            for (int i=0;i<collections.length();i++)
            {
                if (!collections.getJSONObject(i).keys().next().equals(collectionName))
                {
                    newCollections.put(collections.getJSONObject(i));
                }
                else
                {
                    JSONObject renamedCollection = new JSONObject();
                    renamedCollection.put(newName, collections.getJSONObject(i).getJSONArray(collectionName));
                    newCollections.put(renamedCollection);
                }
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(COLLECTIONS_LIST, newCollections.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void createCollection(Context context, String collectionName)
    {

        JSONArray collections = getCollectionList(context);
        try {
            JSONObject newCollection = new JSONObject();
            newCollection.put(collectionName, new JSONArray());
            collections.put(newCollection);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(COLLECTIONS_LIST, collections.toString());
        editor.apply();
    }

    public static void removeComicsFromCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        JSONArray collections = getCollectionList(context);
        JSONObject collection = null;
        JSONArray collectionArray = null;
        int pos = -1;
        for (int i=0;i<collections.length();i++)
        {
            try {
                if (collections.getJSONObject(i).keys().next().equals(collectionName))
                {
                    pos = i;
                    collection = collections.getJSONObject(i);
                    collectionArray = collection.getJSONArray(collectionName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        if (pos!=-1)
        {
            JSONArray newCollection = new JSONArray();
            try {
                for (int i=0;i<collectionArray.length();i++)
                {
                    for (Comic comic:comics) {
                        if (!collectionArray.get(i).equals(comic.getFileName())) {
                            newCollection.put(collectionArray.get(i));
                        }
                    }
                }

                collection.put(collectionName, newCollection);
                collections.put(pos, collection);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(COLLECTIONS_LIST, collections.toString());
                editor.apply();

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void removeComicFromCollection(Context context, String collectionName, Comic comic)
    {
        JSONArray collections = getCollectionList(context);
        JSONObject collection = null;
        JSONArray collectionArray = null;
        int pos = -1;
        for (int i=0;i<collections.length();i++)
        {
            try {
                if (collections.getJSONObject(i).keys().next().equals(collectionName))
                {
                    pos = i;
                    collection = collections.getJSONObject(i);
                    collectionArray = collection.getJSONArray(collectionName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        if (pos!=-1)
        {
            JSONArray newCollection = new JSONArray();
            try {
                for (int i=0;i<collectionArray.length();i++)
                {
                    if (!collectionArray.get(i).equals(comic.getFileName())) {
                        newCollection.put(collectionArray.get(i));
                    }
                }

                collection.put(collectionName, newCollection);
                collections.put(pos, collection);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(COLLECTIONS_LIST, collections.toString());
                editor.apply();

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void removeCollection(Context context, String collectionName)
    {
        JSONArray collections = getCollectionList(context);
        JSONArray newCollections = new JSONArray();
        try {
            for (int i=0;i<collections.length();i++)
            {
                if (!collections.getJSONObject(i).keys().next().equals(collectionName))
                {
                    newCollections.put(collections.getJSONObject(i));
                }
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(COLLECTIONS_LIST, newCollections.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getCollectionList(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String collectionListString = prefs.getString(COLLECTIONS_LIST, null);

        if (collectionListString == null)
        {
            return new JSONArray();
        }
        else
        {
            try
            {
                return new JSONArray(collectionListString);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                return new JSONArray();
            }
        }
    }


    public static boolean getToolbarOption(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TOOLBAR_OPTION, false);
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

    public static boolean importData(Context context, File xmlFile)
    {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();


            NodeList favoriteNodes = ((Element)doc.getElementsByTagName(FAVORITE_COMIC_LIST).item(0)).getElementsByTagName("FavoriteComic");

            for (int i=0;i<favoriteNodes.getLength();i++)
            {
                Node favoriteComic = favoriteNodes.item(i);
                if (!favoriteComic.getTextContent().equals("")) {
                    saveFavoriteComic(context, favoriteComic.getTextContent());
                }
            }

            NodeList comicsReadNumberNodes = doc.getElementsByTagName(NUMBER_OF_COMICS_READ);
            if (comicsReadNumberNodes.getLength()>0) {
                Node comicsReadNumber = comicsReadNumberNodes.item(0);
                int comicsRead = Integer.parseInt(comicsReadNumber.getTextContent());
                if (getNumberOfComicsRead(context)<comicsRead)
                    incrementNumberOfComicsRead(context, comicsRead-getNumberOfComicsRead(context));
            }


            NodeList comicsStartedNumberNodes = doc.getElementsByTagName(NUMBER_OF_COMICS_STARTED);
            if (comicsStartedNumberNodes.getLength()>0) {
                Node comicsStartedNumber = comicsStartedNumberNodes.item(0);
                int comicsStarted = Integer.parseInt(comicsStartedNumber.getTextContent());
                if (getNumberOfComicsStarted(context)<comicsStarted)
                    incrementNumberOfComicsStarted(context, comicsStarted-getNumberOfComicsStarted(context));
            }


            NodeList pagesReadList = ((Element)doc.getElementsByTagName(PAGES_READ_LIST).item(0)).getElementsByTagName("Comic");

            for (int i=0;i<pagesReadList.getLength();i++)
            {
                Node comic = pagesReadList.item(i);
                if (comic.getNodeType()==Node.ELEMENT_NODE) {
                    Element eComic = (Element)comic;
                    Node name = eComic.getElementsByTagName("Name").item(0);
                    Node page = eComic.getElementsByTagName("Page").item(0);
                    savePagesForComic(context, name.getTextContent(), Integer.parseInt(page.getTextContent()));
                }
            }


            NodeList seriesPagesReadList = ((Element)doc.getElementsByTagName(SERIES_PAGES_READ_LIST).item(0)).getElementsByTagName("Series");;

            for (int i=0;i<seriesPagesReadList.getLength();i++)
            {
                Node series = seriesPagesReadList.item(i);
                if (series.getNodeType()==Node.ELEMENT_NODE) {
                    Element eSeries = (Element)series;
                    Node name = eSeries.getElementsByTagName("Name").item(0);
                    Node pages = eSeries.getElementsByTagName("Pages").item(0);
                    int pagesForSeries = getPagesReadForSeries(context, name.getTextContent());

                    if (pagesForSeries<(Integer.parseInt(pages.getTextContent())))
                        incrementPagesForSeries(context, name.getTextContent(), Integer.parseInt(pages.getTextContent())-pagesForSeries);
                }
            }

            NodeList lastReadComicList = ((Element)doc.getElementsByTagName(READ_COMIC_LIST).item(0)).getElementsByTagName("Comic");;

            for (int i=0;i<lastReadComicList.getLength();i++)
            {
                Node comic = pagesReadList.item(i);
                if (comic.getNodeType()==Node.ELEMENT_NODE) {
                    Element eComic = (Element)comic;
                    Node name = eComic.getElementsByTagName("Name").item(0);
                    Node page = eComic.getElementsByTagName("Page").item(0);
                    saveLastReadComic(context, name.getTextContent(), Integer.parseInt(page.getTextContent()));
                }
            }

            NodeList comicsAddedList = ((Element)doc.getElementsByTagName(COMICS_ADDED_LIST).item(0)).getElementsByTagName("Comic");;

            for (int i=0;i<comicsAddedList.getLength();i++)
            {
                Node comic = comicsAddedList.item(i);
                addAddedComic(context, comic.getTextContent());
            }

            Element longestReadComic = ((Element)doc.getElementsByTagName(LONGEST_READ_COMIC).item(0));;
            Node longestName = longestReadComic.getElementsByTagName("Name").item(0);
            Node longestPages = longestReadComic.getElementsByTagName("Pages").item(0);
            saveLongestReadComic(context, "",Integer.parseInt(longestPages.getTextContent()),longestName.getTextContent(),-1);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean exportData(Context context, String locationPath)
    {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(COMIC_VIEWER);
            doc.appendChild(rootElement);

            //children of comicInfo
            Element favorites = doc.createElement(FAVORITE_COMIC_LIST);
            rootElement.appendChild(favorites);

            List<String> favoritesList = getFavoriteComics(context);

            for (String favorite:favoritesList)
            {
                Element favoriteNode = doc.createElement("FavoriteComic");
                favoriteNode.setTextContent(favorite);
                favorites.appendChild(favoriteNode);
            }

            Element comicsReadNumber = doc.createElement(NUMBER_OF_COMICS_READ);
            comicsReadNumber.setTextContent(""+getNumberOfComicsRead(context));
            rootElement.appendChild(comicsReadNumber);

            Element comicsStartedNumber = doc.createElement(NUMBER_OF_COMICS_STARTED);
            comicsStartedNumber.setTextContent(""+getNumberOfComicsStarted(context));
            rootElement.appendChild(comicsStartedNumber);

            Element pagesRead = doc.createElement(PAGES_READ_LIST);
            rootElement.appendChild(pagesRead);

            Map<String, Integer> pagesReadMap = getPagesReadMap(context);

            for (String key:pagesReadMap.keySet())
            {
                Element comic = doc.createElement("Comic");
                Element name = doc.createElement("Name");
                Element page = doc.createElement("Page");
                name.setTextContent(key);
                page.setTextContent(""+pagesReadMap.get(key));
                comic.appendChild(name);
                comic.appendChild(page);
                pagesRead.appendChild(comic);
            }

            Element seriesPagesRead = doc.createElement(SERIES_PAGES_READ_LIST);
            rootElement.appendChild(seriesPagesRead);

            Map<String, Integer> seriesReadMap = getSeriesPagesReadMap(context);
            for (String key:seriesReadMap.keySet())
            {
                Element series = doc.createElement("Series");
                Element name = doc.createElement("Name");
                Element pages = doc.createElement("Pages");
                name.setTextContent(key);
                pages.setTextContent(""+seriesReadMap.get(key));
                series.appendChild(name);
                series.appendChild(pages);
                seriesPagesRead.appendChild(series);
            }

            Element readComicList = doc.createElement(READ_COMIC_LIST);
            rootElement.appendChild(readComicList);

            Map<String,Integer> readComicMap = getReadComics(context);
            for (String key:readComicMap.keySet())
            {
                Element comic = doc.createElement("Comic");
                Element name = doc.createElement("Name");
                Element page = doc.createElement("Page");
                name.setTextContent(key);
                page.setTextContent(""+readComicMap.get(key));
                comic.appendChild(name);
                comic.appendChild(page);
                readComicList.appendChild(comic);
            }

            Element comicsAdded = doc.createElement(COMICS_ADDED_LIST);
            rootElement.appendChild(comicsAdded);

            List<String> comicsAddedList = getComicsAdded(context);

            for (String title:comicsAddedList)
            {
                Element comic = doc.createElement("Comic");
                comic.setTextContent(title);
                comicsAdded.appendChild(comic);
            }

            Element longestReadComic = doc.createElement(LONGEST_READ_COMIC);
            rootElement.appendChild(longestReadComic);

            Element longestReadName = doc.createElement("Name");
            longestReadName.setTextContent(getLongestReadComicTitle(context));
            longestReadComic.appendChild(longestReadName);

            Element longestReadPages = doc.createElement("Pages");
            longestReadPages.setTextContent(""+getLongestReadComicPages(context));
            longestReadComic.appendChild(longestReadPages);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
            Date date = new Date();
            String outputPath = locationPath+"/"+dateFormat.format(date)+"_cvbackup.xml";

            StreamResult result = new StreamResult(new File(outputPath));

            transformer.transform(source, result);

            Log.d("PreferenceSetter", "XML File saved to "+locationPath);
            return true;

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return false;
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            return false;
        }
    }


    public static boolean getVolumeKeyPreference(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(VOLUME_KEY_OPTION, false);
    }

    public static void addHiddenPath(Context context, String path)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString(UNHIDE_LIST, "");

        csvList+=path+",";
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNHIDE_LIST, csvList);
        editor.apply();
    }

    public static void batchAddHiddenPath(Context context, ArrayList<String> paths)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString(UNHIDE_LIST, "");

        for (String path:paths)
            csvList+=path+",";
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNHIDE_LIST, csvList);
        editor.apply();
    }

    public static void removeHiddenPath(Context context, String path)
    {
        ArrayList<String> paths = getHiddenFiles(context);
        String csvList = "";

        for (int i=0;i<paths.size();i++)
        {
            if (!paths.get(i).equals(path))
                csvList+=paths.get(i)+",";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNHIDE_LIST, csvList);
        editor.apply();
    }

    public static ArrayList<String> getHiddenFiles(Context context)
    {
        ArrayList<String> hiddenList = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString(UNHIDE_LIST, "");

        String[] parts = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            if (!parts[i].equals(""))
            {
                hiddenList.add(parts[i]);
            }
        }

        return hiddenList;
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
        startTime = System.currentTimeMillis();

        List<String> mangaComics = getMangaComicList(context);

        for (int i=0;i<mangaComics.size();i++)
        {
            if (mangaComics.get(i).contains(originalPath))
            {
                String path = mangaComics.get(i);
                removeMangaComic(context, path);
                path = path.replace(originalPath, newPath);
                saveMangaComic(context, path);
            }
        }

        endTime = System.currentTimeMillis();
        Log.d("FOLDER_RENAME", "Manga comics: "+(endTime-startTime));
        startTime = System.currentTimeMillis();

        List<String> normalComics = getNormalComicList(context);

        for (int i=0;i<normalComics.size();i++)
        {
            if (normalComics.get(i).contains(originalPath))
            {
                String path = normalComics.get(i);
                removeNormalComic(context, path);
                path = path.replace(originalPath, newPath);
                saveNormalComic(context, path);
            }
        }

        endTime = System.currentTimeMillis();
        Log.d("FOLDER_RENAME", "Normal comics: "+(endTime-startTime));

    }

    public static boolean isNormalComic(Context context, Comic comic)
    {
        List<String> normalList = getNormalComicList(context);

        for (int i=0;i<normalList.size();i++)
        {
            if (normalList.get(i).equals(comic.getFilePath() + "/" + comic.getFileName()))
            {
                return true;
            }
        }
        return false;
    }

    public static void batchRemoveNormalComics(Context context, ArrayList<Comic> comics)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(NORMAL_LIST, "");
        String newList = "";

        String parts[] = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            boolean shouldBeRemoved = false;
            for (Comic comic:comics) {
                if (parts[i].equals(comic.getFilePath() + "/" + comic.getFileName())) {
                    shouldBeRemoved = true;
                }
            }
            if (!shouldBeRemoved)
                newList += parts[i] + ",";
        }

        editor.putString(NORMAL_LIST, newList);
        editor.apply();
    }

    public static void removeNormalComic(Context context, String path)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(NORMAL_LIST, "");
        String newList = "";

        String parts[] = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            if (!parts[i].equals(path))
            {
                newList+=parts[i]+",";
            }
        }

        editor.putString(NORMAL_LIST, newList);
        editor.apply();
    }

    public static void removeNormalComic(Context context, Comic comic)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(NORMAL_LIST, "");
        String newList = "";

        String parts[] = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            if (!parts[i].equals(comic.getFilePath()+"/"+comic.getFileName()))
            {
                newList+=parts[i]+",";
            }
        }

        editor.putString(NORMAL_LIST, newList);
        editor.apply();
    }

    public static void batchSaveNormalComics(Context context, ArrayList<Comic> comics)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(NORMAL_LIST,"");
        for (Comic comic:comics) {
            csvList += comic.getFilePath() + "/" + comic.getFileName() + ",";
        }
        editor.putString(NORMAL_LIST, csvList);
        editor.apply();
    }

    public static void saveNormalComic(Context context, Comic comic)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(NORMAL_LIST,"");
        csvList+=comic.getFilePath()+"/"+comic.getFileName()+",";
        editor.putString(NORMAL_LIST, csvList);
        editor.apply();
    }

    public static void saveNormalComic(Context context, String path)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(NORMAL_LIST,"");
        csvList+=path+",";
        editor.putString(NORMAL_LIST, csvList);
        editor.apply();
    }

    public static List<String> getNormalComicList(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> normalComicPaths = new ArrayList<>();

        String csvList = prefs.getString(NORMAL_LIST, null);

        if (csvList!=null)
        {
            String paths[] = csvList.split(",");
            for (int i =0;i<paths.length;i++)
            {
                normalComicPaths.add(paths[i]);
            }
        }

        return normalComicPaths;
    }

    public static boolean isMangaComic(Context context, Comic comic)
    {
        List<String> mangaList = getMangaComicList(context);

        for (int i=0;i<mangaList.size();i++)
        {
            if (mangaList.get(i).equals(comic.getFilePath()+"/"+comic.getFileName()))
            {
                return true;
            }
        }
        return false;
    }

    public static void batchRemoveMangaComics(Context context, ArrayList<Comic> comics)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(MANGA_LIST, "");
        String newList = "";

        String parts[] = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            boolean shouldBeRemoved = false;
            for (Comic comic:comics) {
                if (parts[i].equals(comic.getFilePath() + "/" + comic.getFileName())) {
                    shouldBeRemoved = true;
                }
            }
            if (!shouldBeRemoved)
                newList += parts[i] + ",";
        }

        editor.putString(MANGA_LIST, newList);
        editor.apply();
    }

    public static void removeMangaComic(Context context, String path)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(MANGA_LIST, "");
        String newList = "";

        String parts[] = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            if (!parts[i].equals(path))
            {
                newList+=parts[i]+",";
            }
        }

        editor.putString(MANGA_LIST, newList);
        editor.apply();
    }

    public static void removeMangaComic(Context context, Comic comic)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(MANGA_LIST, "");
        String newList = "";

        String parts[] = csvList.split(",");

        for (int i=0;i<parts.length;i++)
        {
            if (!parts[i].equals(comic.getFilePath()+"/"+comic.getFileName()))
            {
                newList+=parts[i]+",";
            }
        }

        editor.putString(MANGA_LIST, newList);
        editor.apply();
    }

    public static void batchSaveMangaComics(Context context, ArrayList<Comic> comics)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(MANGA_LIST,"");
        for (Comic comic:comics) {
            csvList += comic.getFilePath() + "/" + comic.getFileName() + ",";
        }
        editor.putString(MANGA_LIST, csvList);
        editor.apply();
    }

    public static void saveMangaComic(Context context, Comic comic)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(MANGA_LIST,"");
        csvList+=comic.getFilePath()+"/"+comic.getFileName()+",";
        editor.putString(MANGA_LIST, csvList);
        editor.apply();
    }

    public static void saveMangaComic(Context context, String path)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String csvList = preferences.getString(MANGA_LIST,"");
        csvList+=path+",";
        editor.putString(MANGA_LIST, csvList);
        editor.apply();
    }

    public static List<String> getMangaComicList(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> mangaComicPaths = new ArrayList<>();

        String csvList = prefs.getString(MANGA_LIST, null);

        if (csvList!=null)
        {
            String paths[] = csvList.split(",");
            for (int i =0;i<paths.length;i++)
            {
                mangaComicPaths.add(paths[i]);
            }
        }

        return mangaComicPaths;
    }

    public static boolean getMangaSetting(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MANGA_SETTING, false);
    }

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
        removeCloudService(context, service.getEmail(), service.getName());

        ArrayList<CloudService> cloudServicesList = getCloudServices(context);

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
            editor.remove("CloudService " + j);
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
        editor.putString(LONGEST_READ_COMIC, "comicFileName,0, comicTitle, -1");
        editor.putString(COMICS_ADDED_LIST,"");
        editor.putString(SERIES_PAGES_READ_LIST,"");
        editor.putString(PAGES_READ_LIST,"");
        editor.putInt(NUMBER_OF_COMICS_READ,0);
        editor.putInt(NUMBER_OF_COMICS_STARTED, 0);
        editor.apply();
    }


    public static int getLongestReadComicPages(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String longestReadString = prefs.getString(LONGEST_READ_COMIC, "comicFileName,0,comicTitle,comicIssueNumber");

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
            return context.getString(R.string.none);
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

    public static void batchAddAddedComics(Context context, Set<String> comicsToAdd)
    {
        List<String> addedComics = getComicsAdded(context);
        String stringToAdd = "";

        for (String comic : comicsToAdd) {
            if (addedComics.contains(comic))
                continue;
            stringToAdd += comic + ",";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String stringToSave = prefs.getString(COMICS_ADDED_LIST,"")+stringToAdd;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(COMICS_ADDED_LIST, stringToSave);
        editor.apply();

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
                try {
                    int splitPosition = pair.lastIndexOf(":");
                    pagesReadMap.put(pair.substring(0, splitPosition), Integer.parseInt(pair.substring(splitPosition + 1)));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
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

        if (!favoritesString.contains(comicFileName))
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
        return prefs.getInt(NUMBER_OF_COMICS_READ, 0);
    }

    public static boolean usesRecents(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean(USES_RECENTS, true);
    }

    public static List<String> getFavoriteComics(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayList<String> favoriteArrayList = new ArrayList<>();

        String favoritesString = prefs.getString(FAVORITE_COMIC_LIST, "");

        String[] favorites = favoritesString.split(",");

        for (String comic:favorites)
        {
            if (!comic.equals(""))
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

    public static ArrayList<Comic> getSavedComics(Context context)
    {
        ArrayList<Comic> comicList = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int i=0;

        while (prefs.getString("Comic "+i, null)!=null)
        {
            comicList.add(Comic.create(prefs.getString("Comic " + i, null)));
            i++;
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
                if (splitPosition>=0 && splitPosition<=pair.length())
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

    public static void setBackgroundColorPreference(Context context, String value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(BACKGROUND_COLOR, value);
        editor.apply();
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

    public static void removeFilePath(Context context, String path)
    {
        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString(FILEPATHS, defaultPath);
        String[] parts = csvList.split(",");

        StringBuilder newList = new StringBuilder();

        for (int i=0;i<parts.length;i++)
        {
            if (!parts[i].equals(path) || parts[i].equals(defaultPath))
                newList.append(parts[i] + ",");
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FILEPATHS, newList.toString());
        editor.apply();
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

        if (!paths.contains(defaultPath))
            paths.add(defaultPath);
        if (paths.contains(""))
            paths.remove("");

        return paths;
    }


    public static void saveFilePaths(Context context, ArrayList<String> filePaths)
    {
        StringBuilder csvList = new StringBuilder();
        boolean containsComma = false;
        for(int i=0;i<filePaths.size();i++){
            String path = filePaths.get(i);
            if (path.contains(","))
                containsComma = true;
            else {
                csvList.append(filePaths.get(i));
                csvList.append(",");
            }
        }
        if (!containsComma) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
            sharedPreferencesEditor.putString(FILEPATHS, csvList.toString());

            sharedPreferencesEditor.apply();
        }
        else
        {
            Toast.makeText(context, "Warning: filepaths should not contain commas", Toast.LENGTH_LONG).show();
        }
    }

}
