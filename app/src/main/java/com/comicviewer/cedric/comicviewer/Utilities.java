package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lingala.zip4j.core.ZipFile;

/**
 * Created by Cédric on 5/03/2015.
 * Utilities for extension checking or filetype checking
 */
public class Utilities{

    public static ArrayList<String> charSequenceArrayToStringList(CharSequence[] array)
    {
        ArrayList<String> list = new ArrayList<>();
        for (int i=0;i<array.length;i++)
            list.add(array[i].toString());
        return list;
    }

    public static List<String> removeStringListDoubles(List<String> list)
    {
        Set<String> stringSet = new HashSet<>();

        for (int i=0;i<list.size();i++)
        {
            stringSet.add(list.get(i));
        }

        return getStringsFromSet(stringSet);
    }

    public static CharSequence[] stringListToCharSequenceArray(List<String> list)
    {
        if (list.size()==0)
            return new CharSequence[]{};

        CharSequence[] array = new CharSequence[list.size()];

        for (int i=0;i<list.size();i++)
        {
            array[i] = list.get(i);
        }

        return array;
    }

    public static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    public static ArrayList<String> getStringsFromSet(Set<String> set)
    {
        ArrayList<String> stringsList = new ArrayList<>();
        for (String key:set)
        {
            stringsList.add(key);
        }

        return stringsList;
    }

    public static int getPixelValue(Activity activity, int dp)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, outMetrics);
    }

    public static int darkenColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        color = Color.HSVToColor(hsv);
        return color;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

    public static void deleteComic(Context context, Comic comic)
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

    public static boolean deleteDirectory(Context context, File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(context, files[i]);
                    }
                    else {
                        if (Utilities.checkExtension(files[i].getName())
                                && (Utilities.isZipArchive(files[i]) || Utilities.isRarArchive(files[i])))
                        {
                            boolean found = false;

                            List<Comic> savedComics = StorageManager.getSavedComics(context);

                            for (int j=0;j<savedComics.size() && !found;j++)
                            {
                                String savedPath = savedComics.get(j).getFilePath()+"/"+savedComics.get(j).getFileName();
                                if (savedPath.equals(files[i].getAbsolutePath()))
                                {
                                    found = true;
                                    deleteComic(context, savedComics.get(j));
                                }
                                else
                                {
                                    files[i].delete();
                                }

                            }
                        }
                        else {
                            files[i].delete();
                        }
                    }
                }
            }
        }
        return(directory.delete());
    }

    public static boolean checkImageFolder(File folder)
    {
        if (!folder.isDirectory())
            return false;
        if (!folder.exists())
            return false;
        File files[] = folder.listFiles();

        if (files == null || files.length<1)
            return false;

        for (int i=0;i<files.length;i++)
        {
            if (files[i].isDirectory())
                return false;
            try {
                if (!(isJPEG(files[i]) || isPNG(files[i])))
                    return false;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static Boolean isJPEG(File file) throws Exception {
        DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        try {
            if (ins.readInt() == 0xffd8ffe0) {
                return true;
            } else {
                return false;

            }
        } finally {
            ins.close();
        }
    }

    public static boolean isPNG(File file) throws IOException {
        // create input stream
        int numRead;
        byte[] signature = new byte[8];
        byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
        InputStream is = null;

        try {
            is = new FileInputStream(file);

            // if first 8 bytes are PNG then return PNG reader
            numRead = is.read(signature);

            if (numRead == -1)
                throw new IOException("Trying to read from 0 byte stream");

        } finally {
            if (is != null)
                is.close();
        }

        if (numRead == 8 && Arrays.equals(signature, pngIdBytes)) {
            return true;
        }

        return false;
    }

    public static String removeFirstDigits(String text)
    {
        try {
            int i = 0;

            text = text.trim();
            while (Character.isDigit(text.charAt(i)))
                i++;
            return text.substring(i, text.length());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return text;
        }
    }

    public static String removeFirstText(String text)
    {
        try {
            int i = 0;
            text = text.trim();
            while (!Character.isDigit(text.charAt(i)))
                i++;
            return text.substring(i, text.length());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return text;
        }
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
