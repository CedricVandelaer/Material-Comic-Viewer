package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

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
        StorageManager.batchRemoveMangaComics(context, comics);
        StorageManager.batchSaveNormalComics(context, comics);
    }

    public static void makeMangaComics(Context context, ArrayList<Comic> comics)
    {
        StorageManager.batchRemoveNormalComics(context, comics);
        StorageManager.batchSaveMangaComics(context, comics);
    }

    public static void makeMangaComic(Context context, Comic comic)
    {
        StorageManager.removeNormalComic(context, comic);
        StorageManager.saveMangaComic(context, comic);
    }

    public static void makeNormalComic(Context context, Comic comic)
    {
        StorageManager.saveNormalComic(context, comic);
        StorageManager.removeMangaComic(context, comic);
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
        StorageManager.removeSavedComic(context, comic);

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
        StorageManager.batchRemoveSavedComics(context, comics);
    }


    public static ArrayList<Comic> getAllSimpleComics(Context context)
    {
        ArrayList<Comic> comics = new ArrayList<>();
        ArrayList<String> filepaths = StorageManager.getFilePathsFromPreferences(context);
        for (String path:filepaths)
        {
            comics.addAll(getAllComicsInFolderRecursive(path));
        }
        return comics;
    }

    public static ArrayList<Comic> getAllComicsInFolderRecursive(String root)
    {
        ArrayList<String> subFilesAndFolders = FileLoader.searchSubFoldersAndFilesRecursive(root);

        ArrayList<Comic> comics = new ArrayList<>();

        for (int i=0;i<subFilesAndFolders.size();i++)
        {
            //Comic comic;
            File file = new File(subFilesAndFolders.get(i));
            if (Utilities.checkImageFolder(file))
            {
                comics.add(new Comic(file.getName(),file.getParentFile().getAbsolutePath()));
            }
            else if (Utilities.checkExtension(subFilesAndFolders.get(i))
                    && (Utilities.isRarArchive(file) || Utilities.isZipArchive(file)))
            {
                comics.add(new Comic(file.getName(),file.getParentFile().getAbsolutePath()));
            }
        }

        return comics;
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
        StorageManager.removeReadComic(context, comic.getFileName());

        int pagesRead = StorageManager.getPagesReadForComic(context, comic.getFileName());

        StorageManager.resetSavedPagesForComic(context, comic.getFileName());

        if (pagesRead > 0) {
            StorageManager.decrementNumberOfComicsStarted(context, 1);
        }

        if (pagesRead >= comic.getPageCount()) {
            StorageManager.decrementNumberOfComicsRead(context, 1);
        }

        StorageManager.decrementPagesForSeries(context, comic.getTitle(), pagesRead);
    }

    public static void markComicRead(Context context, Comic comic, boolean showToast)
    {
        if (StorageManager.getReadComics(context).containsKey(comic.getFileName())) {

            if (StorageManager.getReadComics(context).get(comic.getFileName())+1>= comic.getPageCount())
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
                StorageManager.saveLastReadComic(context, comic.getFileName(), comic.getPageCount());

                int pagesRead = StorageManager.getPagesReadForComic(context, comic.getFileName());

                StorageManager.savePagesForComic(context, comic.getFileName(), comic.getPageCount());

                if (pagesRead == 0) {
                    StorageManager.incrementNumberOfComicsStarted(context, 1);
                }

                if (pagesRead < comic.getPageCount()) {
                    StorageManager.incrementNumberOfComicsRead(context, 1);
                }

                int extraPagesRead = comic.getPageCount() - pagesRead;
                StorageManager.incrementPagesForSeries(context, comic.getTitle(), extraPagesRead);

                StorageManager.saveLongestReadComic(context,
                        comic.getFileName(),
                        comic.getPageCount(),
                        comic.getTitle(),
                        comic.getIssueNumber());
            }
        }
        else {
            StorageManager.saveLongestReadComic(context,
                    comic.getFileName(),
                    comic.getPageCount(),
                    comic.getTitle(),
                    comic.getIssueNumber());

            //Comic wasn't opened yet
            StorageManager.saveLastReadComic(context, comic.getFileName(), comic.getPageCount() - 1);
            StorageManager.savePagesForComic(context, comic.getFileName(), comic.getPageCount());
            StorageManager.incrementNumberOfComicsStarted(context, 1);
            StorageManager.incrementNumberOfComicsRead(context, 1);
            StorageManager.incrementPagesForSeries(context, comic.getTitle(), comic.getPageCount());
        }
    }
}
