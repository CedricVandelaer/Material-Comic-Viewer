package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;

/**
 * Created by Cédric on 8/02/2015.
 */
public class Extractor {

    public static ArrayList<String> loadImageNamesFromComic(Context context, Comic comicToExtract)
    {
        String filename = comicToExtract.getFileName();

        String path = comicToExtract.getFilePath()+"/"+filename;
        
        File file = new File(path);

        ArrayList<String> pages;

        if (Utilities.isZipArchive(file))
        {
            pages = loadImageNamesFromComicZip(comicToExtract);
        }
        else
        {
            pages =  loadImageNamesFromComicRar(comicToExtract);
        }

        if ((PreferenceSetter.getMangaSetting(context) && !PreferenceSetter.isNormalComic(context,comicToExtract))
            || (!(PreferenceSetter.getMangaSetting(context)) && PreferenceSetter.isMangaComic(context, comicToExtract)))
        {
            if (pages.size()>0) {
                String coverPage = pages.get(0);
                ArrayList<String> mangaPages = new ArrayList<>();
                for (int i=pages.size()-1;i>=0;i--)
                {
                    mangaPages.add(pages.get(i));
                }

                mangaPages.remove(mangaPages.size()-1);
                mangaPages.add(0, coverPage);

                return mangaPages;
            }
        }

        return pages;
    }

    /**
     * Function to get the filenamestrings of the files in the rar archive
     */
    public static ArrayList<String> loadImageNamesFromComicRar(Comic comicToExtract)
    {
        String filename = comicToExtract.getFileName();
        String path = comicToExtract.getFilePath()+ "/" + filename;

        ArrayList<String> pages= new ArrayList<String>();

        File comic = new File(path);
        try {
            Archive arch = new Archive(comic);
            List<FileHeader> fileheaders = arch.getFileHeaders();

            for (int j = 0; j < fileheaders.size(); j++) {

                if (Utilities.isPicture(fileheaders.get(j).getFileNameString()))
                {
                    String pagefile = fileheaders.get(j).getFileNameString();
                    
                    pages.add(pagefile);
                }
            }

        }
        catch (Exception e)
        {
            Log.e("ExtractRarTask", e.getMessage());
        }
        
        Collections.sort(pages);



        return pages;
    }

    /**
     * Function to get the filenamestrings of the files in the zip archive
     */
    public static ArrayList<String> loadImageNamesFromComicZip(Comic comicToExtract)
    {
        String archivefilename = comicToExtract.getFileName();
        String path = comicToExtract.getFilePath();
        String filename = null;

        ArrayList<String> pages= new ArrayList<String>();

        try {
            ZipFile zip = new ZipFile(path + "/" + archivefilename);
            List<net.lingala.zip4j.model.FileHeader> fileHeaders = zip.getFileHeaders();

            for (int j = 0; j < fileHeaders.size(); j++) {

                if (Utilities.isPicture(fileHeaders.get(j).getFileName()))
                {
                    String pagefile = fileHeaders.get(j).getFileName();

                    if (pagefile.contains("/"))
                        pagefile = pagefile.substring(pagefile.lastIndexOf("/")+1);

                    pages.add(pagefile);
                }
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Collections.sort(pages);
        return pages;
    }



}
