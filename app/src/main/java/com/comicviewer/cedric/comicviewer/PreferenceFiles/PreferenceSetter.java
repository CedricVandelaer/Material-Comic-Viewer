package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cédric on 8/02/2015.
 * Helper class for some preferences*
 */
public class PreferenceSetter {


    public static String getCardAppearanceSetting(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString("cardSize", context.getString(R.string.card_size_setting_2));
    }

    public static void saveComicList(Context context, List<Comic> comicList)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();

        int i=0;

        while (prefs.getString("Comic "+i, null)!=null)
        {
            sharedPreferencesEditor.remove("Comic "+i);
            i++;
        }

        for (i=0;i<comicList.size();i++)
        {
            String serializedComic = comicList.get(i).serialize();
            sharedPreferencesEditor.putString("Comic "+i, serializedComic);
        }

        sharedPreferencesEditor.apply();

    }

    public static ArrayList<Comic> getSavedComics(Context context)
    {
        ArrayList<Comic> comicList = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int i=0;

        while (prefs.getString("Comic "+i, null)!=null)
        {
            comicList.add(Comic.create(prefs.getString("Comic "+i,null)));
            i++;
        }

        return comicList;
    }

    public static void saveLastReadComic(Context context, String comicName, int pageNumber)
    {
        Map<String, Integer> lastReadMap = getReadComics(context);

        lastReadMap.put(comicName, pageNumber);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        StringBuilder csvList = new StringBuilder();

        for (String key:lastReadMap.keySet())
        {
            csvList.append(key+":"+lastReadMap.get(key)+",");
        }

        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString("lastReadComicList", csvList.toString());

        sharedPreferencesEditor.apply();

    }

    public static Map<String, Integer> getReadComics(Context context)
    {
        Map<String, Integer> lastReadMap = new HashMap<String, Integer>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //List of format "comicname:comicpage,comicname:comicpage,..."
        String lastReadComics = prefs.getString("lastReadComicList", "");

        String[] lastReadPairs = lastReadComics.split(",");

        for (String pair:lastReadPairs)
        {
            if (!pair.isEmpty()) {
                int splitPosition = pair.lastIndexOf(":");
                lastReadMap.put(pair.substring(0, splitPosition), Integer.parseInt(pair.substring(splitPosition + 1)));
            }
        }

        return lastReadMap;
    }


    public static void setBackgroundColorPreference(Activity activity)
    {
        View layout = activity.getWindow().getDecorView().getRootView();
        
        int color = getBackgroundColorPreference(activity);
        
        
        layout.setBackgroundColor(color);
        if (color!= activity.getResources().getColor(R.color.WhiteBG))
            activity.getWindow().setNavigationBarColor(getBackgroundColorPreference(activity));
        else
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.Black));
        
    }

    public static int getBackgroundColorPreference(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String bgcolor = prefs.getString("backgroundColor", context.getString(R.string.backgroundcolor_setting2));
        int color;
        
        if (bgcolor.equals(context.getString(R.string.backgroundcolor_setting1)))
        {
            color = context.getResources().getColor(R.color.BlueGrey);
        }
        else if (bgcolor.equals(context.getString(R.string.backgroundcolor_setting2)))
        {
            color = context.getResources().getColor(R.color.Black);
        }
        else if(bgcolor.equals(context.getString(R.string.backgroundcolor_setting4)))
        {
            color = context.getResources().getColor(R.color.Brown);
        }
        else if(bgcolor.equals(context.getString(R.string.backgroundcolor_setting5)))
        {
            color = context.getResources().getColor(R.color.Grey);
        }
        else
        {
            color = context.getResources().getColor(R.color.WhiteBG);
        }
        
        return color;
    }

    public static ArrayList<String> getFilePathsFromPreferences(Context context) {
        ArrayList<String> paths = new ArrayList<>();

        
        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString("Filepaths", defaultPath);
        
        String[] items = csvList.split(",");
        for(int i=0; i < items.length; i++){
            paths.add(items[i]);
        }
        if (paths.size()<1)
            paths.add(defaultPath);
        //remove duplicates
        for (int i=0;i<paths.size()-1;i++)
        {
            for (int j=i+1;j<paths.size();j++)
            {
                if (i!=j)
                {
                    if (paths.get(i).equals(paths.get(j)))
                    {
                        paths.remove(j);
                    }
                }
            }
        }
        return paths;
    }

    public static ArrayList getExcludedPaths(Context context)
    {
        ArrayList<String> paths = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvList = prefs.getString("Excludedpaths", null);

        if (csvList!=null) {
            String[] items = csvList.split(",");
            for (int i = 0; i < items.length; i++) {
                paths.add(items[i]);
            }
            //remove duplicates
            for (int i = 0; i < paths.size() - 1; i++) {
                for (int j = i + 1; j < paths.size(); j++) {
                    if (i != j) {
                        if (paths.get(i).equals(paths.get(j))) {
                            paths.remove(j);
                        }
                    }
                }
            } 
        }
        return paths;
        
    }

    
    public static void saveFilePaths(Activity activity, ArrayList<String> filepaths, ArrayList<String> excludedpaths)
    {
        StringBuilder csvList = new StringBuilder();
        for(int i=0;i<filepaths.size();i++){
            csvList.append(filepaths.get(i));
            csvList.append(",");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString("Filepaths", csvList.toString());

        sharedPreferencesEditor.apply();

        csvList = new StringBuilder();
        for(int i=0;i<excludedpaths.size();i++){
            csvList.append(excludedpaths.get(i));
            csvList.append(",");
        }

        sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString("Excludedpaths", csvList.toString());

        sharedPreferencesEditor.apply();
    }
}
