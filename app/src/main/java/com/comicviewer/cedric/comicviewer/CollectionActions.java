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

    public static void batchAddCharacterToCollection(Context context, String collectionName, ArrayList<String> characters)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String character:characters)
                    collection.addCharacter(character);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddStoryArcToCollection(Context context, String collectionName, ArrayList<String> storyArcs)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String storyArc:storyArcs)
                    collection.addStoryArc(storyArc);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddCoverArtistsToCollection(Context context, String collectionName, ArrayList<String> coverArtists)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String coverArtist:coverArtists)
                    collection.addCoverArtist(coverArtist);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddEditorToCollection(Context context, String collectionName, ArrayList<String> editors)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String editor:editors)
                    collection.addEditor(editor);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddLetterersToCollection(Context context, String collectionName, ArrayList<String> letterers)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String letterer:letterers)
                    collection.addLetterer(letterer);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddColoristsToCollection(Context context, String collectionName, ArrayList<String> colorists)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String colorist:colorists)
                    collection.addColorist(colorist);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddInkersToCollection(Context context, String collectionName, ArrayList<String> inkers)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String inker:inkers)
                    collection.addInker(inker);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddPencillersToCollection(Context context, String collectionName, ArrayList<String> pencillers)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String penciller:pencillers)
                    collection.addPenciller(penciller);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void batchAddWriterFilterToCollection(Context context, String collectionName, ArrayList<String> writers)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName)) {
                for (String writer:writers)
                    collection.addWriter(writer);
            }
        }
        StorageManager.saveCollections(context, collections);
    }

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

    public static void addCharacterFilterToCollection(Context context, String collectionName, String character)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addStoryArc(character);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addStoryArcFilterToCollection(Context context, String collectionName, String storyArc)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addStoryArc(storyArc);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addCoverArtistFilterToCollection(Context context, String collectionName, String coverArtist)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addCoverArtist(coverArtist);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addEditorFilterToCollection(Context context, String collectionName, String editor)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addEditor(editor);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addLettererFilterToCollection(Context context, String collectionName, String letterer)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addLetterer(letterer);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addColoristFilterToCollection(Context context, String collectionName, String colorist)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addColorist(colorist);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addInkerFilterToCollection(Context context, String collectionName, String inker)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addInker(inker);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addPencillerFilterToCollection(Context context, String collectionName, String penciller)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addPenciller(penciller);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addWritersFilterToCollection(Context context, String collectionName, String writer)
    {
        ArrayList<Collection> collections = StorageManager.getCollectionList(context);
        for (Collection collection:collections)
        {
            if (collection.getName().equals(collectionName))
                collection.addWriter(writer);
        }
        StorageManager.saveCollections(context, collections);
    }

    public static void addSeriesFilterToCollection(Context context, String collectionName, String seriesName)
    {
        Collection collection = StorageManager.getCollection(context, collectionName);
        collection.addSeries(seriesName);
        StorageManager.saveCollection(context, collection);
    }

    public static void batchAddYearsFilterToCollection(Context context, String collectionName, ArrayList<String> yearStrings)
    {

        ArrayList<Integer> years = new ArrayList<>();

        for (String year:yearStrings) {
            try {
                years.add(Integer.parseInt(year));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collection collection = StorageManager.getCollection(context, collectionName);

        for (Integer year:years)
            collection.addYear(year);

        StorageManager.saveCollection(context, collection);
    }

    public static void addYearsFilterToCollection(Context context, String collectionName, String yearString)
    {
        int year;
        Collection collection = StorageManager.getCollection(context, collectionName);
        try
        {
            year = Integer.parseInt(yearString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        collection.addYear(year);
        StorageManager.saveCollection(context, collection);
    }

    public static void batchAddFolderFilterToCollection(Context context, String collectionName, ArrayList<String> folderNames)
    {

        Collection collection = StorageManager.getCollection(context, collectionName);

        for (String folderName:folderNames)
            collection.addFolder(folderName);

        StorageManager.saveCollection(context, collection);
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
        Collection collection = StorageManager.getCollection(context, collectionName);
        for (Comic comic:comics) {
            collection.addFile(comic.getFileName());
        }
        StorageManager.saveCollection(context, collection);
    }

    public static void removeComicsFromCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        Collection collection = StorageManager.getCollection(context, collectionName);
        for (Comic comic:comics) {
            collection.removeFile(comic.getFileName());
        }
        StorageManager.saveCollection(context, collection);
    }

    public static void removeComicFromCollection(Context context, String collectionName, Comic comic)
    {
        Collection collection = StorageManager.getCollection(context, collectionName);
        collection.removeFile(comic.getFileName());
        StorageManager.saveCollection(context, collection);
    }

    public static void addComicToCollection(Context context, String collectionName, Comic comic)
    {
        Collection collection = StorageManager.getCollection(context, collectionName);
        collection.addFile(comic.getFileName());
        StorageManager.saveCollection(context, collection);
    }

    public static void addFolderToCollection(Context context, String collectionName, String folderPath)
    {
        Collection collection = StorageManager.getCollection(context, collectionName);

        collection.addFolder(folderPath);

        StorageManager.saveCollection(context, collection);
    }
}
