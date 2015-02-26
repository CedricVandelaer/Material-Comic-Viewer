package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Cédric on 8/02/2015.
 */
public class PreferenceSetter {

    public void setBackgroundColorPreference(Activity activity)
    {
        View layout = activity.getWindow().getDecorView().getRootView();
        String cardColorSetting=null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        cardColorSetting = prefs.getString("cardColor", activity.getString(R.string.card_color_setting_1));
        String bgcolor = prefs.getString("backgroundColor", activity.getString(R.string.backgroundcolor_setting2));

        if (bgcolor.equals(activity.getString(R.string.backgroundcolor_setting1)))
        {
            layout.setBackgroundColor(activity.getResources().getColor(R.color.BlueGrey));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.BlueGrey));
        }
        else if (bgcolor.equals(activity.getString(R.string.backgroundcolor_setting2)))
        {
            layout.setBackgroundColor(activity.getResources().getColor(R.color.Black));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.Black));
        }
        else if(bgcolor.equals(activity.getString(R.string.backgroundcolor_setting4)))
        {
            layout.setBackgroundColor(activity.getResources().getColor(R.color.Brown));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.Brown));
        }
        else if(bgcolor.equals(activity.getString(R.string.backgroundcolor_setting5)))
        {
            layout.setBackgroundColor(activity.getResources().getColor(R.color.Grey));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.Grey));
        }
        else
        {
            layout.setBackgroundColor(activity.getResources().getColor(R.color.WhiteBG));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.Black));
        }
    }

    public ArrayList<String> getFilePathsFromPreferences(Activity activity) {
        ArrayList<String> paths = new ArrayList<>();

        Log.d("PreferenceSetter", "getFilePathsFromPreferences called");
        
        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String csvList = prefs.getString("Filepaths", defaultPath);
        Log.d("PreferenceSetter", "csvList: "+csvList);
        String[] items = csvList.split(",");
        for(int i=0; i < items.length; i++){
            Log.d("PreferenceSetter","Get paths from preferences: "+i+":"+items[i]);
            paths.add(items[i]);
        }
        //remove duplicates
        for (int i=0;i<paths.size();i++)
        {
            for (int j=0;j<paths.size();j++)
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

    public void saveFilePaths(Activity activity, ArrayList<String> filepaths)
    {

        Log.d("PreferenceSetter", "saveFilePaths called");

        StringBuilder csvList = new StringBuilder();
        for(int i=0;i<filepaths.size();i++){
            csvList.append(filepaths.get(i));
            csvList.append(",");
            Log.d("PreferenceSetter","save paths: "+filepaths.get(i));
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
        sharedPreferencesEditor.putString("Filepaths", csvList.toString());

        sharedPreferencesEditor.apply();
    }
}
