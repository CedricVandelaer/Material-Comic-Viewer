package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.github.junrar.Archive;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CV on 1/04/2015.
 * class to load the info of the comics
 */
public class ComicLoader {

    public static void loadComicSync(Context context, Comic comic)
    {
        try {
            File file = new File(comic.getFilePath() + "/" + comic.getFileName());

            generateComicInfo(context, comic);

            if (file.isDirectory())
            {
                initialiseImageFolderComic(comic);
            }
            else if (Utilities.isZipArchive(file)) {
                extractZipComic(comic, context);
            } else {
                extractRarComic(context, comic);
            }

            setComicColor(context, comic);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("ComicLoader", "Error loading comic: " + comic.getFileName());
        }
    }

    public static void loadComicSyncNoColor(Context context, Comic comic)
    {
        try {
            File file = new File(comic.getFilePath() + "/" + comic.getFileName());

            generateComicInfo(context, comic);

            if (file.isDirectory())
            {
                initialiseImageFolderComic(comic);
            }
            else if (Utilities.isZipArchive(file)) {
                extractZipComic(comic, context);
            } else {
                extractRarComic(context, comic);
            }

            comic.setColorSetting("None");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("ComicLoader", "Error loading comic: " + comic.getFileName());
        }
    }

    private static void initialiseImageFolderComic(Comic comic)
    {
        File folder = new File(comic.getFilePath()+"/"+comic.getFileName());

        File[] images = folder.listFiles();

        ArrayList<File> imagesList = new ArrayList();

        if (images.length>0) {
            for (int i = 0; i < images.length; i++) {
                imagesList.add(images[i]);
            }

            comic.setPageCount(images.length);

            Collections.sort(imagesList, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getName(), rhs.getName());
                    if (res == 0) {
                        res = lhs.getName().compareTo(rhs.getName());
                    }
                    return res;
                }
            });

            String coverImage = "file:///" + imagesList.get(0).getAbsolutePath();

            comic.setCoverImage(coverImage);
        }
    }

    private static void extractZipComic(Comic comic, Context context) {
        String archivefilename = comic.getFileName();
        String path = comic.getFilePath();
        String extractedImageFile = null;
        int pageCount = 0;

        ArrayList<FileHeader> pages = new ArrayList<net.lingala.zip4j.model.FileHeader>();

        try {
            ZipFile zipFile = new ZipFile(path + "/" + archivefilename);
            List<FileHeader> fileheaders = zipFile.getFileHeaders();

            // search for comic pages in the archive
            for (int j = 0; j < fileheaders.size(); j++) {

                if (Utilities.isPicture(fileheaders.get(j).getFileName())) {
                    if (!fileheaders.get(j).isDirectory()) {
                        pages.add(fileheaders.get(j));
                        pageCount++;
                    }
                }
            }

            // sort the pages
            Collections.sort(pages, new Comparator<FileHeader>() {
                @Override
                public int compare(net.lingala.zip4j.model.FileHeader lhs, net.lingala.zip4j.model.FileHeader rhs) {
                    int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getFileName(), rhs.getFileName());
                    if (res == 0) {
                        res = lhs.getFileName().compareTo(rhs.getFileName());
                    }
                    return res;
                }
            });

            // the outputfilename
            if (pages.size() > 0)
                extractedImageFile = pages.get(0).getFileName().substring(pages.get(0).getFileName().lastIndexOf("\\") + 1);

            // get rid of special chars causing problems
            if (extractedImageFile != null && extractedImageFile.contains("#"))
                extractedImageFile = extractedImageFile.replaceAll("#", "");

            // the output file
            File directory = new File(context.getFilesDir().getPath()+"/"+Utilities.removeExtension(archivefilename));
            directory.mkdir();
            File output = new File(directory.getPath(), extractedImageFile);

            // if file!=extracted -> extract
            if (!(output.exists())) {
                if (pages.size() > 0)
                    zipFile.extractFile(pages.get(0), directory.getPath());
                else
                    return;
            }

            String coverImage = "file:///" + directory + "/" + extractedImageFile;

            if (comic.getCoverImage() == null) {
                comic.setCoverImage(coverImage);
                comic.setPageCount(pageCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractRarComic(Context context, Comic comic) {

        String filename = comic.getFileName();
        ArrayList<com.github.junrar.rarfile.FileHeader> pages = new ArrayList<com.github.junrar.rarfile.FileHeader>();

        String path = comic.getFilePath() + "/" + filename;

        File comicFile = new File(path);

        try {
            Archive arch = new Archive(comicFile);
            List<com.github.junrar.rarfile.FileHeader> fileheaders = arch.getFileHeaders();
            String extractedImageFile = null;

            int pageCount = 0;

            // search for comic pages in the archive
            for (int j = 0; j < fileheaders.size(); j++) {

                if (Utilities.isPicture(fileheaders.get(j).getFileNameString())) {
                    if (!fileheaders.get(j).isDirectory()) {
                        pages.add(fileheaders.get(j));
                        pageCount++;
                    }
                }
            }

            // sort the pages
            Collections.sort(pages, new Comparator<com.github.junrar.rarfile.FileHeader>() {
                @Override
                public int compare(com.github.junrar.rarfile.FileHeader lhs, com.github.junrar.rarfile.FileHeader rhs) {
                    int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getFileNameString(), rhs.getFileNameString());
                    if (res == 0) {
                        res = lhs.getFileNameString().compareTo(rhs.getFileNameString());
                    }
                    return res;
                }
            });

            // the outputfilename
            if (pages.size() > 0)
                extractedImageFile = pages.get(0).getFileNameString().substring(pages.get(0).getFileNameString().lastIndexOf("\\") + 1);

            // get rid of special chars causing problems
            if (extractedImageFile != null && extractedImageFile.contains("#"))
                extractedImageFile = extractedImageFile.replaceAll("#", "");

            // the output file
            File directory = new File(context.getFilesDir() + "/" + Utilities.removeExtension(filename));
            directory.mkdir();
            File output = new File(directory.getPath(), extractedImageFile);

            // if file!=extracted -> extract
            if (!(output.exists())) {
                FileOutputStream os = new FileOutputStream(output);
                if (pages.size() > 0)
                    arch.extractFile(pages.get(0), os);
                else
                    return;
            }

            String coverImage = "file:///" + context.getFilesDir().toString() +"/" + Utilities.removeExtension(filename) + "/" + extractedImageFile;

            if (comic.getCoverImage() == null) {
                comic.setCoverImage(coverImage);
                comic.setPageCount(pageCount);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static boolean setComicColor(Context context,Comic comic)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String cardColorSetting = prefs.getString("cardColor", context.getString(R.string.card_color_setting_1));

        if (!cardColorSetting.equals(comic.getColorSetting())) {
            comic.setColorSetting(cardColorSetting);

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
                ImageLoader.getInstance().init(config);
            }

            try {
                int color;
                int primaryTextColor;

                if (cardColorSetting.equals(context.getString(R.string.card_color_setting_1))) {
                    ImageSize imageSize = new ImageSize(850, 500);
                    Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(comic.getCoverImage(), imageSize);
                    Palette.Swatch mutedSwatch = Palette.generate(thumbnail).getMutedSwatch();
                    if (mutedSwatch == null)
                    {
                        mutedSwatch = Palette.generate(thumbnail).getDarkMutedSwatch();
                    }
                    color = mutedSwatch.getRgb();
                    primaryTextColor = mutedSwatch.getTitleTextColor();
                } else if (cardColorSetting.equals(context.getString(R.string.card_color_setting_2))) {
                    ImageSize imageSize = new ImageSize(850, 500);
                    Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(comic.getCoverImage(), imageSize);
                    Palette.Swatch lightVibrantSwatch = Palette.generate(thumbnail).getLightVibrantSwatch();
                    if (lightVibrantSwatch==null)
                    {
                        lightVibrantSwatch = Palette.generate(thumbnail).getMutedSwatch();
                    }
                    color = lightVibrantSwatch.getRgb();
                    primaryTextColor = lightVibrantSwatch.getTitleTextColor();
                } else if (cardColorSetting.equals(context.getString(R.string.card_color_setting_3))) {
                    color = context.getResources().getColor(R.color.WhiteBG);
                    primaryTextColor = context.getResources().getColor(R.color.Black);
                } else if (cardColorSetting.equals(context.getString(R.string.card_color_setting_4))) {
                    color = context.getResources().getColor(R.color.BlueGreyVeryDark);
                    primaryTextColor = context.getResources().getColor(R.color.White);
                } else if (cardColorSetting.equals(context.getString(R.string.app_theme_setting))) {
                    color = Utilities.darkenColor(StorageManager.getAppThemeColor(context));
                    primaryTextColor = context.getResources().getColor(R.color.White);
                }else {
                    color = context.getResources().getColor(R.color.Black);
                    primaryTextColor = context.getResources().getColor(R.color.White);
                }

                comic.setComicColor(color);
                comic.setTextColor(primaryTextColor);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public static void generateComicInfo(Context context, Comic comic)
    {
        String fileFormat = StorageManager.getFileFormatSetting(context);
        String[] parts = fileFormat.split(",");

        String filename = comic.getFileName();

        try {
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].trim().equals("Title")) {
                    String title = generateTitle(filename);
                    if (title != null)
                        comic.setTitle(title);
                    if (isNumberedTitle(title))
                        filename = Utilities.removeFirstDigits(filename);
                    else
                        filename = Utilities.removeFirstText(filename);
                } else if (parts[i].trim().equals("Issue number")) {
                    int issueNumber = generateIssueNumber(filename);
                    if (issueNumber != -1)
                        comic.setIssueNumber(issueNumber);
                    filename = Utilities.removeFirstDigits(filename);
                } else if (parts[i].trim().equals("Year")) {
                    int year = generateYear(filename);
                    if (year != -1)
                        comic.setYear(year);
                    filename = Utilities.removeFirstDigits(filename);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (comic.getTitle().equals("")) {
            if (comic.getFilePath().contains("mangarock")) {
                comic.setTitle("MangaRock Comic");
                try {
                    comic.setIssueNumber(Integer.parseInt(comic.getFileName()));
                    comic.setYear(-1);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else {
                if (comic.getIssueNumber()!=-1) {
                    comic.setTitle(""+comic.getIssueNumber());
                    comic.setIssueNumber(-1);
                }
                else {
                    comic.setTitle(context.getString(R.string.untitled));
                }
            }
        }

        if (comic.getIssueNumber() == comic.getYear())
        {
            comic.setIssueNumber(-1);
        }
    }

    public static boolean isNumberedTitle(String title)
    {
        int i=0;

        while (i<title.length())
        {
            if (!Character.isDigit(title.charAt(i)))
                return false;
            i++;
        }

        return true;
    }

    public static String generateTitle(String filename)
    {
        String delimiter = "COMICVIEWERDELIM";
        String title;

        try {
            if (filename.contains("("))
                title = filename.substring(0, filename.indexOf('('));
            else
                title = filename;

            title = title.replaceAll("_", " ");

            title = title.replaceAll("#", "");

            title = title.trim();

            title = title.replaceAll("\\d+", delimiter);

            if (title.contains(delimiter)) {
                if (title.indexOf(delimiter) > 0)
                    title = title.substring(0, title.indexOf(delimiter));
                else {
                    title = title.replace(delimiter, "");
                    if (title.contains(delimiter))
                        title = title.substring(0, title.indexOf(delimiter));
                }
            }

            title = title.trim();

            if (title.startsWith("-")) {
                title = title.substring(1, title.length());
            }

            if (title.endsWith("-")) {
                title = title.substring(0, title.length() - 1);
            }

            // check for numbered title
            if (title.replaceAll(delimiter,"").trim().equals(""))
            {
                String numberTitle = "";
                int i=0;
                while (Character.isDigit(filename.charAt(i)))
                {
                    numberTitle += filename.charAt(i);
                    i++;
                }
                return numberTitle;
            }

            title = title.trim();
            return title;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static int generateIssueNumber(String filename)
    {
        try {
            int i = 0;

            while (!Character.isDigit(filename.charAt(i)))
                i++;

            int j=i;
            while (Character.isDigit(filename.charAt(j)))
                j++;

            int issueNumber = -1;

            issueNumber = Integer.parseInt(filename.substring(i, j));
            return issueNumber;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public static int generateYear(String filename)
    {
        try {
            int year = -1;
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(filename);
            if (matcher.find())
            {
                year = Integer.parseInt(matcher.group(0));
            }
            return year;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }
}


