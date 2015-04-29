package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment{

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


        ArrayList<String> filePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());
        ArrayList<String> excludedFilePaths = PreferenceSetter.getExcludedPaths(getActivity());

        for (int i=0;i<filePaths.size();i++)
        {
            if (excludedFilePaths.contains(filePaths.get(i)))
                filePaths.remove(i);
        }
        if (!filePaths.contains(defaultPath))
            filePaths.add(defaultPath);


        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");
        
        CustomMultiSelectListPreference multiListPref = createListPreference(filePaths,filePaths);
        
        targetCategory.addPreference(multiListPref);

        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));

    }

    private CustomMultiSelectListPreference createListPreference(ArrayList<String> keys, ArrayList<String> values)
    {
        CustomMultiSelectListPreference listPref = new CustomMultiSelectListPreference(getActivity(),keys,values, "Choose paths to remove");
        
        listPref.setTitle("Remove added paths");
        listPref.setSummary(getString(R.string.path_preference_summary));

        listPref.setPositiveButtonText("Remove");
        listPref.setNegativeButtonText("Cancel");
        

        return listPref;
        
    }

}
