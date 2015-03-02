package com.comicviewer.cedric.comicviewer;

import android.util.Log;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by Cédric on 8/02/2015.
 */
public class Extractor {

    public static ArrayList<String> loadImageNamesFromComic(Comic comicToExtract)
    {
        String filename = comicToExtract.getFileName();

        String path = comicToExtract.getFilePath()+"/"+filename;
        
        File file = new File(path);

        if (isZipArchive(file))
        {
            return loadImageNamesFromComicZip(comicToExtract);
        }
        else
        {
            return loadImageNamesFromComicRar(comicToExtract);
        }
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

        if (pages.size()==0)
            return loadImageNamesFromComicZip(comicToExtract);
        
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

        try
        {
            ZipFile zip = new ZipFile(path + "/" + archivefilename);
            ZipEntry ze;

            Pattern p = Pattern.compile("\\d\\d");

            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements())
            {
                ze = entries.nextElement();
                filename = ze.getName();

                String coverFileIndex = filename.substring(filename.length() - 7);
                
                if (filename.contains("/"))
                    filename = filename.substring(filename.lastIndexOf("/")+1);

                //Ignore directories
                if (ze.isDirectory())
                {
                    continue;
                }

                Matcher m = p.matcher(coverFileIndex);
                if (m.find())
                {
                    pages.add(filename);
                    Log.d("Extractor", "added filename: "+filename);
                }
            }

        }
        catch (Exception e)
        {
            Log.e("ExtractZipTask", e.getMessage());
        }

        Collections.sort(pages);
        return pages;
    }

    private static boolean isZipArchive(File file) {
        try {
            InputStream is = new FileInputStream(file);
            boolean isZipped = new ZipInputStream(is).getNextEntry() != null;
            return isZipped;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean isRarArchive(File filFile) {

        try {

            byte[] bytSignature = new byte[] {0x52, 0x61, 0x72, 0x21, 0x1a, 0x07, 0x00};
            FileInputStream fisFileInputStream = new FileInputStream(filFile);

            byte[] bytHeader = new byte[20];
            fisFileInputStream.read(bytHeader);

            Short shoFlags = (short) (((bytHeader[10]&0xFF)<<8) | (bytHeader[11]&0xFF));

            //Check if is an archive
            if (Arrays.equals(Arrays.copyOfRange(bytHeader, 0, 7), bytSignature)) {
                //Check if is a spanned archive
                if ((shoFlags & 0x0100) != 0) {
                    //Check if it the first part of a spanned archive
                    if ((shoFlags & 0x0001) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

}
