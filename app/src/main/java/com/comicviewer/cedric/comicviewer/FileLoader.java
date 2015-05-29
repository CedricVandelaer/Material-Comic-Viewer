package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Cédric on 11/03/2015.
 * Class to create the list of comics without actually loading them
 */
public class FileLoader {

    public static Map<String, String> searchComicsAndFolders(Context context, String folder)
    {
        long startTime = System.currentTimeMillis();
        //the deafault path
        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ComicViewer";

        // create the default path if not exists
        File defaultPathFile = new File(defaultPath);
        if (!defaultPathFile.exists())
        {
            defaultPathFile.mkdir();
        }

        ArrayList<String> filepaths;
        if (folder.equals("root"))
        {
            filepaths = PreferenceSetter.getFilePathsFromPreferences(context);

        }
        else
        {
            filepaths = new ArrayList<>();
            filepaths.add(folder);
        }

        ArrayList<String> hiddenFiles = PreferenceSetter.getHiddenFiles(context);

        if (folder.equals("root"))
        {
            Map<String,String> map = new HashMap<>();

            for (int i=0;i<filepaths.size();i++)
            {
                File addedFolder = new File(filepaths.get(i));
                String path = new File(addedFolder.getParent()).getAbsolutePath()+"/"+addedFolder.getName();
                // don't add hidden files
                if (!hiddenFiles.contains(path)) {
                    map.put(addedFolder.getName(), new File(addedFolder.getParent()).getAbsolutePath());
                }
            }
            return map;
        }

        Map<String,String> map = findFilesAndFoldersInPaths(filepaths);


        //remove hidden files and folders
        Iterator iterator = map.keySet().iterator();
        List<String> keys = new ArrayList<String>();

        while(iterator.hasNext()) {
            keys.add((String)iterator.next());
        }
        for (int i=0;i<keys.size();i++)
        {
            String str = keys.get(i);
            String path = map.get(str)+"/"+str;
            if (hiddenFiles.contains(path))
            {
                map.remove(str);
            }
        }

        long endTime = System.currentTimeMillis();

        Log.d("File loader", "time: "+(endTime-startTime));

        return map;
    }

    public static Map<String, String> searchComics(Context context) {

        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ComicViewer";
        ArrayList<String> hiddenFiles = PreferenceSetter.getHiddenFiles(context);

        File defaultPathFile = new File(defaultPath);
        if (!defaultPathFile.exists())
        {
            defaultPathFile.mkdir();
        }

        long startTime = System.currentTimeMillis();

        ArrayList<String> filepaths = PreferenceSetter.getFilePathsFromPreferences(context);

        ArrayList<String> subFolders = searchSubFolders(filepaths);

        //remove hidden folders
        for (int i=0;i<hiddenFiles.size();i++)
        {
            if (subFolders.contains(hiddenFiles.get(i)))
            {
                subFolders.remove(hiddenFiles.get(i));
            }
        }

        Map<String,String> map = findFilesInPaths(subFolders);

        //remove hidden files and folders
        Iterator iterator = map.keySet().iterator();
        List<String> keys = new ArrayList<String>();

        while(iterator.hasNext()) {
            keys.add((String)iterator.next());
        }
        for (int i=0;i<keys.size();i++)
        {
            String str = keys.get(i);
            String path = map.get(str)+"/"+str;
            if (hiddenFiles.contains(path))
            {
                map.remove(str);
            }
        }

        long endTime = System.currentTimeMillis();

        Log.d("File loader", "time: "+(endTime-startTime));

        return map;

    }

    private static int getComicPositionInList(String filename, ArrayList<Comic> list)
    {
        for (int pos=0;pos<list.size();pos++)
        {
            if (list.get(pos).getFileName().equals(filename))
                return pos;
        }
        return -1;
    }

    private static Map findFilesAndFoldersInPaths(ArrayList<String> pathsToSearch)
    {
        // list of filenames
        ArrayList<String> files = new ArrayList<>();
        // list of directories to search from
        ArrayList<String> paths = new ArrayList<>();

        // map to map the filenames to their directories
        Map<String,String> map = new HashMap<>();

        // search for all files in all paths
        for (int i=0;i<pathsToSearch.size();i++)
        {
            String path = pathsToSearch.get(i);
            File f = new File(path);
            f.mkdirs();

            File fileList[] = f.listFiles();

            if (fileList!=null)
            {
                for (int j=0;j<fileList.length;j++)
                {
                    files.add(fileList[j].getName());
                    paths.add(path);
                }
            }
        }

        // map the filenames to their directories
        for (int i=0;i<files.size();i++) {
            map.put(files.get(i),paths.get(i));
        }

        return map;
    }

    private static Map findFilesInPaths(ArrayList<String> pathsToSearch)
    {
        // list of filenames
        ArrayList<String> files = new ArrayList<>();
        // list of directories to search from
        ArrayList<String> paths = new ArrayList<>();

        // map to map the filenames to their directories
        Map<String,String> map = new HashMap<>();

        // search for all files in all paths
        for (int i=0;i<pathsToSearch.size();i++)
        {
            String path = pathsToSearch.get(i);
            File f = new File(path);
            f.mkdirs();

            File fileList[] = f.listFiles();

            if (fileList!=null)
            {
                for (int j=0;j<fileList.length;j++)
                {

                    if (!fileList[j].isDirectory())
                    {
                        files.add(fileList[j].getName());
                        paths.add(path);
                    }
                    else if (Utilities.checkImageFolder(fileList[j]))
                    {
                        files.add(fileList[j].getName());
                        paths.add(path);
                    }
                }
            }
        }

        // map the filenames to their directories
        for (int i=0;i<files.size();i++) {
            map.put(files.get(i),paths.get(i));
        }

        return map;
    }

    public static ArrayList<String> searchSubFolders(ArrayList<String> paths)
    {
        ArrayList<String> allFoldersInPaths = new ArrayList<>();

        for (int i=0;i<paths.size();i++)
        {
            File root = new File(paths.get(i));


            if (root.isDirectory()) {

                allFoldersInPaths.add(paths.get(i));
                File[] subFiles = root.listFiles();
                ArrayList<String> subFolders = new ArrayList<>();

                for (int j = 0; j < subFiles.length; j++) {
                    subFolders.add(subFiles[j].toString());
                }
                allFoldersInPaths.addAll(searchSubFolders(subFolders));

            }

        }

        return allFoldersInPaths;
    }

}
