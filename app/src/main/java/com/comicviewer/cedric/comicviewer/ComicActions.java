package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;

/**
 * Created by CV on 15/05/2015.
 * Class to do all kinds of actions on comics eg. mark read, delete
 */
public class ComicActions {

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

    public static void markComicRead(Context context, Comic comic)
    {
        if (PreferenceSetter.getReadComics(context).containsKey(comic.getFileName())) {

            if (PreferenceSetter.getReadComics(context).get(comic.getFileName())+1>= comic.getPageCount())
            {
                //Do nothing, already marked as read
                Toast message = Toast.makeText(context, "You have already read this comic!", Toast.LENGTH_SHORT);
                message.show();
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
