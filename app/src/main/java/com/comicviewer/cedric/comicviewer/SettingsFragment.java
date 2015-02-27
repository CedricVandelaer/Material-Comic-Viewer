package com.comicviewer.cedric.comicviewer;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String csvList = prefs.getString("Filepaths", defaultPath);
        
        String[] items = csvList.split(",");
        for(int i=0; i < items.length; i++){
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

        mPathListing = paths.toArray(new CharSequence[paths.size()]);

        createListPreference();

    }

    private void createListPreference()
    {
        MultiSelectListPreference pathPreference = (MultiSelectListPreference) findPreference("pathList");
        
        pathPreference.setSummary(getString(R.string.path_preference_summary));

        pathPreference.setEntries(mPathListing);
        pathPreference.setEntryValues(mPathListing);

        pathPreference.setPositiveButtonText("Remove");
        pathPreference.setNegativeButtonText("Cancel");
        
        pathPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("OnPreferenceChange", (String)preference.getTitle());
                return false;
            }
        });
    }

}
