package com.comicviewer.cedric.comicviewer;

import android.content.Context;

import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by CV on 12/07/2015.
 */
public class CollectionActions {

    public static void batchAddSeriesFilterToCollection(Context context, String collectionName, ArrayList<String> seriesNames)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String seriesName:seriesNames)
                    collection.addSeries(seriesName);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addSeriesFilterToCollection(Context context, String collectionName, String seriesName)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addSeries(seriesName);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddYearsFilterToCollection(Context context, String collectionName, ArrayList<String> yearStrings)
    {

        ArrayList<Integer> years = new ArrayList<>();

        try
        {
            for (String year:yearStrings)
                years.add(Integer.parseInt(year));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        ArrayList<Collection> collections = StorageManager.getCollectionList(context);

        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (Integer year:years)
                    collection.addYear(year);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addYearsFilterToCollection(Context context, String collectionName, String yearString)
    {
        int year;

        try
        {
            year = Integer.parseInt(yearString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);

        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addYear(year);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddFolderFilterToCollection(Context context, String collectionName, ArrayList<String> folderNames)
    {

        ArrayList<Collection> collections = StorageManager.getCollectionList(context);

        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String folderName:folderNames)
                    collection.addFolder(folderName);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addFolderFilterToCollection(Context context, String collectionName, String folderName)
    {

        ArrayList<Collection> collections = StorageManager.getCollectionList(context);

        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addFolder(folderName);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static ArrayList<String> getContainingCollections(Context context, Comic comic)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        ArrayList<String> containingCollections = new ArrayList<>();

        for (Collection collection:collections)
        {
            if (collection.containsFile(comic.getFileName()))
                containingCollections.add(collection.getName());
        }
        return containingCollections;
    }

    public static void addComicsToCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        ArrayList<String> comicsToAdd = new ArrayList<>();
        for (Comic comic:comics) {
            comicsToAdd.add(comic.getFileName());
        }
        StorageManager.addToCollection(context, collectionName, comicsToAdd);
    }

    public static void removeComicsFromCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        StorageManager.removeComicsFromCollection(context, collectionName, comics);
    }

    public static void addComicToCollection(Context context, String collectionName, Comic comic)
    {
        ArrayList<String> comicsToAdd = new ArrayList<>();
        comicsToAdd.add(comic.getFileName());
        StorageManager.addToCollection(context, collectionName, comicsToAdd);
    }

    public static void addFolderToCollection(Context context, String collectionName, String folderPath)
    {
        ArrayList<String> subFilesAndFolders = FileLoader.searchSubFoldersAndFilesRecursive(folderPath);

        ArrayList<String> comicsToAdd = new ArrayList<>();

        for (int i=0;i<subFilesAndFolders.size();i++)
        {
            //Comic comic;
            File file = new File(subFilesAndFolders.get(i));
            if (Utilities.checkImageFolder(file))
            {
                comicsToAdd.add(file.getName());
            }
            else if (Utilities.checkExtension(subFilesAndFolders.get(i))
                    && (Utilities.isRarArchive(file) || Utilities.isZipArchive(file)))
            {
                comicsToAdd.add(file.getName());
            }
        }

        StorageManager.addToCollection(context, collectionName, comicsToAdd);
    }
}
