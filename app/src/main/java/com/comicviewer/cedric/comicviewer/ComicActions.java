package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by CV on 15/05/2015.
 * Class to do all kinds of actions on comics eg. mark read, delete
 */
public class ComicActions {

    public static void makeNormalComics(Context context, ArrayList<Comic> comics)
    {
        PreferenceSetter.batchRemoveMangaComics(context, comics);
        PreferenceSetter.batchSaveNormalComics(context, comics);
    }

    public static void makeMangaComics(Context context, ArrayList<Comic> comics)
    {
        PreferenceSetter.batchRemoveNormalComics(context, comics);
        PreferenceSetter.batchSaveMangaComics(context, comics);
    }

    public static void makeMangaComic(Context context, Comic comic)
    {
        PreferenceSetter.removeNormalComic(context, comic);
        PreferenceSetter.saveMangaComic(context, comic);
    }

    public static void makeNormalComic(Context context, Comic comic)
    {
        PreferenceSetter.saveNormalComic(context, comic);
        PreferenceSetter.removeMangaComic(context, comic);
    }

    public static void removeComic(Context context, Comic comic)
    {
        String coverImageFileName = comic.getCoverImage();
        if (coverImageFileName != null && coverImageFileName.startsWith("file:///")) {
            coverImageFileName = coverImageFileName.replace("file:///", "");
        }

        try {
            if (coverImageFileName != null) {
                File coverImageFile = new File(coverImageFileName);
                if (coverImageFile.exists())
                    coverImageFile.delete();
            }

            File archiveFile = new File(comic.getFilePath() + "/" + comic.getFileName());
            if (archiveFile.exists())
                archiveFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PreferenceSetter.removeSavedComic(context, comic);

    }

    public static void removeComics(Context context, ArrayList<Comic> comics)
    {
        for (Comic comic:comics) {
            String coverImageFileName = comic.getCoverImage();
            if (coverImageFileName != null && coverImageFileName.startsWith("file:///")) {
                coverImageFileName = coverImageFileName.replace("file:///", "");
            }

            try {
                if (coverImageFileName != null) {
                    File coverImageFile = new File(coverImageFileName);
                    if (coverImageFile.exists())
                        coverImageFile.delete();
                }

                File archiveFile = new File(comic.getFilePath() + "/" + comic.getFileName());
                if (archiveFile.exists())
                    archiveFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PreferenceSetter.batchRemoveSavedComics(context, comics);
    }

    public static ArrayList<String> getContainingCollections(Context context, Comic comic)
    {
        JSONArray collections = PreferenceSetter.getCollectionList(context);
        ArrayList<String> containingCollections = new ArrayList<>();

        for (int i=0;i<collections.length();i++)
        {
            try {
                JSONObject collection = collections.getJSONObject(i);
                String collectionName = collection.keys().next();
                JSONArray collectionArray = collection.getJSONArray(collectionName);
                boolean inArray = false;
                for (int j=0;j<collectionArray.length();j++)
                {
                    if (collectionArray.getString(j).equals(comic.getFileName()))
                        inArray = true;
                }
                if (inArray)
                    containingCollections.add(collectionName);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return containingCollections;
    }

    public static void addComicsToCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        ArrayList<String> comicsToAdd = new ArrayList<>();
        for (Comic comic:comics) {
            comicsToAdd.add(comic.getFileName());
        }
        PreferenceSetter.addToCollection(context, collectionName, comicsToAdd, false);
    }

    public static void removeComicsFromCollection(Context context, String collectionName, ArrayList<Comic> comics)
    {
        PreferenceSetter.removeComicsFromCollection(context, collectionName, comics);
    }

    public static void addComicToCollection(Context context, String collectionName, Comic comic)
    {
        ArrayList<String> comicsToAdd = new ArrayList<>();
        comicsToAdd.add(comic.getFileName());
        PreferenceSetter.addToCollection(context, collectionName, comicsToAdd, false);
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

        PreferenceSetter.addToCollection(context, collectionName, comicsToAdd, false);
    }

    public static void markFolderUnread(Context context, String folderPath)
    {
        ArrayList<String> subFilesAndFolders = FileLoader.searchSubFoldersAndFilesRecursive(folderPath);

        for (int i=0;i<subFilesAndFolders.size();i++)
        {
            Comic comic;
            File file = new File(subFilesAndFolders.get(i));
            if (Utilities.checkImageFolder(file))
            {
                comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());
                ComicLoader.loadComicSyncNoColor(context, comic);
                markComicUnread(context, comic);
            }
            else if (Utilities.checkExtension(subFilesAndFolders.get(i))
                    && (Utilities.isRarArchive(file) || Utilities.isZipArchive(file)))
            {
                comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());
                ComicLoader.loadComicSyncNoColor(context, comic);
                markComicUnread(context, comic);
            }

        }
    }

    public static void markFolderRead(Context context, String folderPath)
    {
        ArrayList<String> subFilesAndFolders = FileLoader.searchSubFoldersAndFilesRecursive(folderPath);

        for (int i=0;i<subFilesAndFolders.size();i++)
        {
            Comic comic;
            File file = new File(subFilesAndFolders.get(i));
            if (Utilities.checkImageFolder(file))
            {
                comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());
                ComicLoader.loadComicSyncNoColor(context, comic);
                markComicRead(context, comic, false);
            }
            else if (Utilities.checkExtension(subFilesAndFolders.get(i))
                    && (Utilities.isRarArchive(file) || Utilities.isZipArchive(file)))
            {
                comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());
                ComicLoader.loadComicSyncNoColor(context, comic);
                markComicRead(context, comic, false);
            }

        }
    }

    public static void markComicUnread(Context context, Comic comic)
    {
        PreferenceSetter.removeReadComic(context, comic.getFileName());

        int pagesRead = PreferenceSetter.getPagesReadForComic(context, comic.getFileName());

        PreferenceSetter.resetSavedPagesForComic(context, comic.getFileName());

        if (pagesRead > 0) {
            PreferenceSetter.decrementNumberOfComicsStarted(context, 1);
        }

        if (pagesRead >= comic.getPageCount()) {
            PreferenceSetter.decrementNumberOfComicsRead(context, 1);
        }

        PreferenceSetter.decrementPagesForSeries(context, comic.getTitle(), pagesRead);
    }

    public static void markComicRead(Context context, Comic comic, boolean showToast)
    {
        if (PreferenceSetter.getReadComics(context).containsKey(comic.getFileName())) {

            if (PreferenceSetter.getReadComics(context).get(comic.getFileName())+1>= comic.getPageCount())
            {
                //Do nothing, already marked as read
                if (showToast) {
                    Toast message = Toast.makeText(context, context.getString(R.string.already_read_toast), Toast.LENGTH_SHORT);
                    message.show();
                }
            }
            else
            {
                //Comic was opened but not yet fully read
                PreferenceSetter.saveLastReadComic(context,comic.getFileName(),comic.getPageCount()-1);

                int pagesRead = PreferenceSetter.getPagesReadForComic(context, comic.getFileName());

                PreferenceSetter.savePagesForComic(context, comic.getFileName(), comic.getPageCount());

                if (pagesRead == 0) {
                    PreferenceSetter.incrementNumberOfComicsStarted(context, 1);
                }

                if (pagesRead < comic.getPageCount()) {
                    PreferenceSetter.incrementNumberOfComicsRead(context, 1);
                }

                int extraPagesRead = comic.getPageCount() - pagesRead;
                PreferenceSetter.incrementPagesForSeries(context, comic.getTitle(), extraPagesRead);

                PreferenceSetter.saveLongestReadComic(context,
                        comic.getFileName(),
                        comic.getPageCount(),
                        comic.getTitle(),
                        comic.getIssueNumber());
            }
        }
        else {
            PreferenceSetter.saveLongestReadComic(context,
                    comic.getFileName(),
                    comic.getPageCount(),
                    comic.getTitle(),
                    comic.getIssueNumber());

            //Comic wasn't opened yet
            PreferenceSetter.saveLastReadComic(context,comic.getFileName(),comic.getPageCount()-1);
            PreferenceSetter.savePagesForComic(context, comic.getFileName(), comic.getPageCount());
            PreferenceSetter.incrementNumberOfComicsStarted(context, 1);
            PreferenceSetter.incrementNumberOfComicsRead(context, 1);
            PreferenceSetter.incrementPagesForSeries(context, comic.getTitle(), comic.getPageCount());
        }
    }
}
