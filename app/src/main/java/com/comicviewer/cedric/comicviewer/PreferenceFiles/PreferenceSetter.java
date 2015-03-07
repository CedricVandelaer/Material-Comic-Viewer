package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;

import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;

/**
 * Created by Cédric on 8/02/2015.
 * Helper class for some preferences*
 */
public class PreferenceSetter {

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

    public static int getBackgroundColorPreference(Activity activity)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String bgcolor = prefs.getString("backgroundColor", activity.getString(R.string.backgroundcolor_setting2));
        int color;
        
        if (bgcolor.equals(activity.getString(R.string.backgroundcolor_setting1)))
        {
            color = activity.getResources().getColor(R.color.BlueGrey);
        }
        else if (bgcolor.equals(activity.getString(R.string.backgroundcolor_setting2)))
        {
            color = activity.getResources().getColor(R.color.Black);
        }
        else if(bgcolor.equals(activity.getString(R.string.backgroundcolor_setting4)))
        {
            color = activity.getResources().getColor(R.color.Brown);
        }
        else if(bgcolor.equals(activity.getString(R.string.backgroundcolor_setting5)))
        {
            color = activity.getResources().getColor(R.color.Grey);
        }
        else
        {
            color = activity.getResources().getColor(R.color.WhiteBG);
        }
        
        return color;
    }

    public static ArrayList<String> getFilePathsFromPreferences(Activity activity) {
        ArrayList<String> paths = new ArrayList<>();

        
        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
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

    public static ArrayList getExcludedPaths(Activity activity)
    {
        ArrayList<String> paths = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
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
