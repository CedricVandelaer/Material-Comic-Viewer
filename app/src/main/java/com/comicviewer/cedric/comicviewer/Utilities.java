package com.comicviewer.cedric.comicviewer;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import net.lingala.zip4j.core.ZipFile;

/**
 * Created by Cédric on 5/03/2015.
 * Utilities for extension checking or filetype checking
 */
public class Utilities {
    
    public static boolean checkExtension(String filename)
    {
        try {
            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            
            if (extension.equals("rar") || extension.equals("cbr") || extension.equals("cbz") || extension.equals("zip"))
            {
                return true;
            }
            else
                return false;
        }
        catch(Exception e)
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

    public static boolean isPicture(String filename)
    {
        String extension = "notAPicture";
        try {
            extension = filename.substring(filename.lastIndexOf(".") + 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (extension.equals("jpg") || extension.equals("jpeg")
                || extension.equals("png"))
        {
            return true;
        }
        return false;
    }

    public static boolean isZipArchive(File file) {

        try {
            ZipFile zipFile = new ZipFile(file);
            return zipFile.isValidZipFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        /*
        try {
            InputStream is = new FileInputStream(file);
            boolean isZipped = new ZipInputStream(is).getNextEntry() != null;
            return isZipped;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        */
    }
}
