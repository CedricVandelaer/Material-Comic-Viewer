package com.comicviewer.cedric.comicviewer;

import android.graphics.Color;
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

    public static int darkenColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        color = Color.HSVToColor(hsv);
        return color;
    }

    public static int lightenColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);// value component
        color = Color.HSVToColor(hsv);
        return color;
    }

    public static String removeExtension(String filename)
    {
        if (filename.contains("."))
            return filename.substring(0, filename.lastIndexOf("."));
        return filename;
    }

    public static boolean checkExtension(String filename)
    {
        try {
            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            
            if (extension.equalsIgnoreCase("rar")
                    || extension.equalsIgnoreCase("cbr")
                    || extension.equalsIgnoreCase("cbz")
                    || extension.equalsIgnoreCase("zip"))
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

        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("png"))
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

    }
}
