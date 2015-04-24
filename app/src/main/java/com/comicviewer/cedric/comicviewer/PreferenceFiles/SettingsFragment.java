package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment{

    private CharSequence[] mPathListing;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance()
    {
        SettingsFragment settings = new SettingsFragment();
        return settings;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        

        ArrayList<CharSequence> paths = new ArrayList<>();
        ArrayList<CharSequence> excludedPaths = new ArrayList<>();

        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String csvList = prefs.getString("Filepaths", defaultPath);
        
        String[] items = csvList.split(",");
        for(int i=0; i < items.length; i++){
            paths.add(items[i]);
        }

        csvList = prefs.getString("Excludedpaths", null);

        //remove excluded paths
        if (csvList!=null) {
            items = csvList.split(",");
            for (int i = 0; i < items.length; i++) {
                if (paths.contains(items[i]))
                    paths.remove(items[i]);
            }
        }
        //remove duplicates
        for (int i=0;i<paths.size();i++)
        {
            for (int j=i+1;j<paths.size()-1;j++)
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

        mPathListing = paths.toArray(new CharSequence[paths.size()]);

        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");
        
        CustomMultiSelectListPreference multiListPref = createListPreference(paths,paths);
        
        targetCategory.addPreference(multiListPref);

        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));

    }

    private CustomMultiSelectListPreference createListPreference(ArrayList<CharSequence> keys, ArrayList<CharSequence> values)
    {
        CustomMultiSelectListPreference listPref = new CustomMultiSelectListPreference(getActivity(),keys,values, "Choose paths to remove");
        
        listPref.setTitle("Remove added paths");
        listPref.setSummary(getString(R.string.path_preference_summary));

        listPref.setPositiveButtonText("Remove");
        listPref.setNegativeButtonText("Cancel");
        

        return listPref;
        
    }

}
