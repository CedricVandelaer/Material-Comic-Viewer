package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.Model.Comic;
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

/**
 * Created by Cédric on 1/04/2015.
 */
public class ComicLoader {

    public static void loadComicSync(Context context, Comic comic)
    {
        try {
            File file = new File(comic.getFilePath() + "/" + comic.getFileName());

            if (Utilities.isZipArchive(file)) {
                extractZipComic(comic, context);
            } else {
                extractRarComic(context, comic);
            }

            setComicColor(context, comic);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("ComicLoader", "Error loading comic: "+comic.getFileName());

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
            File output = new File(context.getFilesDir(), extractedImageFile);

            // if file!=extracted -> extract
            if (!(output.exists())) {
                if (pages.size() > 0)
                    zipFile.extractFile(pages.get(0), context.getFilesDir().getAbsolutePath());
                else
                    return;
            }

            String coverImage = "file:///" + context.getFilesDir().toString() + "/" + extractedImageFile;

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
            File output = new File(context.getFilesDir(), extractedImageFile);

            // if file!=extracted -> extract
            if (!(output.exists())) {
                FileOutputStream os = new FileOutputStream(output);
                if (pages.size() > 0)
                    arch.extractFile(pages.get(0), os);
                else
                    return;
            }

            String coverImage = "file:///" + context.getFilesDir().toString() + "/" + extractedImageFile;

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

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
            ImageLoader.getInstance().init(config);
        }

        try {
            int color;
            int primaryTextColor;
            int secondaryTextColor;

            if (cardColorSetting.equals(context.getString(R.string.card_color_setting_1)))
            {
                ImageSize imageSize = new ImageSize(850, 500);
                Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(comic.getCoverImage(), imageSize);
                Palette.Swatch mutedSwatch = Palette.generate(thumbnail).getMutedSwatch();
                color = mutedSwatch.getRgb();
                primaryTextColor = mutedSwatch.getTitleTextColor();
                secondaryTextColor = mutedSwatch.getBodyTextColor();
            }
            else if (cardColorSetting.equals(context.getString(R.string.card_color_setting_2)))
            {
                ImageSize imageSize = new ImageSize(850, 500);
                Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(comic.getCoverImage(), imageSize);
                Palette.Swatch lightVibrantSwatch = Palette.generate(thumbnail).getLightVibrantSwatch();
                color = lightVibrantSwatch.getRgb();
                primaryTextColor = lightVibrantSwatch.getTitleTextColor();
                secondaryTextColor = lightVibrantSwatch.getBodyTextColor();
            }
            else if (cardColorSetting.equals(context.getString(R.string.card_color_setting_3)))
            {
                color = context.getResources().getColor(R.color.WhiteBG);
                primaryTextColor = context.getResources().getColor(R.color.Black);
                secondaryTextColor = context.getResources().getColor(R.color.BlueGrey);
            }
            else if (cardColorSetting.equals(context.getString(R.string.card_color_setting_4)))
            {
                color = context.getResources().getColor(R.color.BlueGrey);
                primaryTextColor = context.getResources().getColor(R.color.White);
                secondaryTextColor = context.getResources().getColor(R.color.WhiteBG);
            }
            else
            {
                color = context.getResources().getColor(R.color.Black);
                primaryTextColor = context.getResources().getColor(R.color.White);
                secondaryTextColor = context.getResources().getColor(R.color.WhiteBG);
            }


            if (comic.getComicColor()!=color
                    || comic.getPrimaryTextColor()!=primaryTextColor
                    || comic.getSecondaryTextColor()!=secondaryTextColor) {
                comic.setComicColor(color);
                comic.setPrimaryTextColor(primaryTextColor);
                comic.setSecondaryTextColor(secondaryTextColor);

                return true;
            }

        } catch (Exception e) {
            Log.e("Palette", e.getMessage());
        }
        return false;
    }
}
