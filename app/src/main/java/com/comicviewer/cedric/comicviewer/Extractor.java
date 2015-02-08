package com.comicviewer.cedric.comicviewer;

import android.util.Log;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cédric on 8/02/2015.
 */
public class Extractor {

    /**
     * Function to get the filenamestrings of the files in the archive
     */
    public static ArrayList<String> loadImageNamesFromComic(Comic comicToExtract)
    {
        String filename = comicToExtract.getFileName();
        String path = comicToExtract.getFilePath()+ "/" + filename;

        ArrayList<String> pages= new ArrayList<String>();

        File comic = new File(path);
        try {
            Archive arch = new Archive(comic);
            List<FileHeader> fileheaders = arch.getFileHeaders();

            Pattern p = Pattern.compile("\\d\\d");

            for (int j = 0; j < fileheaders.size(); j++) {


                String pageFileIndex = fileheaders.get(j).getFileNameString()
                        .substring(fileheaders.get(j).getFileNameString().length() - 7);
                Matcher m = p.matcher(pageFileIndex);
                if (m.find())
                {
                    pages.add(fileheaders.get(j).getFileNameString());
                }
            }

        }
        catch (Exception e)
        {
            Log.e("ExtractRarTask", e.getMessage());
        }

        return pages;
    }

}
