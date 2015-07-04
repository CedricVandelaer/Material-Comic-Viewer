package com.comicviewer.cedric.comicviewer;

import android.content.Context;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;

import java.io.File;
import java.util.List;

/**
 * Created by CV on 2/07/2015.
 */
public class Sorter {

    public static int sortedInsert(Context context, List<Object> list, Object obj)
    {
        if (PreferenceSetter.getSortSetting(context).equals(PreferenceSetter.SORT_BY_SERIES))
            return sortByComicSeriesInsert(list, obj);
        else if (PreferenceSetter.getSortSetting(context).equals(PreferenceSetter.SORT_BY_FILENAME))
            return sortByFilenameInsert(list, obj);
        else if (PreferenceSetter.getSortSetting(context).equals(PreferenceSetter.SORT_BY_YEAR))
            return sortByYearInsert(list, obj);
        else if (PreferenceSetter.getSortSetting(context).equals(PreferenceSetter.SORT_BY_MODIFIED_DATE))
            return sortByModifiedDate(list, obj);
        else if (PreferenceSetter.getSortSetting(context).equals(PreferenceSetter.SORT_BY_LAST_ADDED))
            return sortByLastAdded(list, obj, context);
        else
            return sortByComicSeriesInsert(list,obj);
    }

    private static int sortByLastAdded(List<Object> list, Object obj, Context context) {
        if (obj instanceof File)
            return insertFileByFilename(list, (File) obj);
        else if (obj instanceof Comic)
            return insertComicByLastAdded(list, (Comic)obj, context);
        return -1;
    }

    private static int insertComicByLastAdded(List<Object> list, Comic obj, Context context) {

        List<String> addedComics = PreferenceSetter.getComicsAdded(context);

        for (int i=list.size()-1;i>=0;i--)
        {
            if (list.get(i) instanceof File)
            {
                list.add(i+1, obj);
                return i+1;
            }
            else if (list.get(i) instanceof Comic)
            {
                Comic comic = (Comic) list.get(i);
                if (addedComics.indexOf(obj.getFileName())<addedComics.indexOf(comic.getFileName()))
                {
                    list.add(i+1, obj);
                    return i+1;
                }
            }
        }

        list.add(0, obj);
        return 0;
    }

    public static int sortByComicSeriesInsert(List<Object> list, Object obj)
    {
        if (obj instanceof Comic) {

            return insertComicBySeries(list, (Comic) obj);
        }
        else
        {
            return insertFileByFilename(list, (File) obj);
        }
    }


    public static int sortByFilenameInsert(List<Object> list, Object obj)
    {
        if (obj instanceof File)
        {
            return insertFileByFilename(list, (File) obj);
        }
        else if (obj instanceof Comic)
        {
            return insertComicByFilename(list, (Comic) obj);
        }
        else
            return -1;

    }

    public static int sortByYearInsert(List<Object> list, Object obj)
    {
        if (obj instanceof Comic) {

            return insertComicByYear(list, (Comic) obj);
        }
        else
        {
            return insertFileByFilename(list, (File) obj);
        }
    }

    public static int insertComicBySeries(List<Object> list, Comic comic)
    {

        for (int i = list.size() - 1; i >= 0; i--) {

            if (list.get(i) instanceof File) {
                list.add(i+1, comic);
                return (i+1);
            }
            else {
                Comic comicInList = (Comic) list.get(i);

                if (comic.getTitle().compareToIgnoreCase(comicInList.getTitle()) > 0) {
                    list.add(i + 1, comic);
                    return (i + 1);
                } else if (comic.getTitle().compareToIgnoreCase(comicInList.getTitle()) == 0)
                {
                    if (comic.getIssueNumber() >= comicInList.getIssueNumber()) {
                        list.add(i + 1, comic);
                        return (i + 1);
                    } else if (i == 0) {
                        list.add(i, comic);
                        return (i);
                    }
                }
            }
        }

        list.add(0, comic);
        return 0;
    }

    public static int insertFileByFilename(List<Object> list, File file)
    {
        for (int i=list.size()-1;i>=0;i--)
        {
            if (list.get(i) instanceof File)
            {
                if (file.getName().compareToIgnoreCase((((File)(list.get(i))).getName()))>=0)
                {
                    list.add(i+1, file);
                    return i+1;
                }
            }
        }
        list.add(0, file);
        return 0;
    }

    public static int insertComicByFilename(List<Object> list, Comic comic)
    {
        for (int i=list.size()-1;i>=0;i--)
        {
            if (list.get(i) instanceof File)
            {
                list.add((i+1), comic);
                return (i+1);
            }
            else if (list.get(i) instanceof Comic)
            {
                if (comic.getFileName().compareToIgnoreCase(((Comic)list.get(i)).getFileName())>=0)
                {
                    list.add(i+1, comic);
                    return i+1;
                }
            }
        }
        list.add(0, comic);
        return 0;
    }

    public static int insertComicByYear(List<Object> list, Comic comic)
    {
        for (int i=list.size()-1;i>=0;i--)
        {
            if (list.get(i) instanceof File)
            {
                list.add((i+1), comic);
                return (i+1);
            }
            else if (list.get(i) instanceof Comic)
            {
                if (comic.getYear()>=(((Comic)list.get(i)).getYear()))
                {
                    list.add(i+1, comic);
                    return i+1;
                }
            }
        }
        list.add(0, comic);
        return 0;
    }

    public static int sortByModifiedDate(List<Object> list, Object obj)
    {
        long modifiedDate;
        if (obj instanceof File)
        {
            modifiedDate = ((File)obj).lastModified();
        }
        else if (obj instanceof Comic)
        {
            modifiedDate = new File(((Comic)obj).getFilePath()+"/"+((Comic)obj).getFileName()).lastModified();
        }
        else
        {
            return -1;
        }

        for (int i=list.size()-1;i>=0;i--)
        {
            long listFileModifiedDate;
            if (list.get(i) instanceof File)
            {
                listFileModifiedDate = ((File)list.get(i)).lastModified();
            }
            else if (list.get(i) instanceof Comic)
            {
                listFileModifiedDate = new File(((Comic)list.get(i)).getFilePath()+"/"+((Comic)list.get(i)).getFileName()).lastModified();
            }
            else
            {
                continue;
            }
            if (listFileModifiedDate>modifiedDate)
            {
                list.add(i,obj);
                return i;
            }
        }

        list.add(0, obj);
        return 0;
    }
}
