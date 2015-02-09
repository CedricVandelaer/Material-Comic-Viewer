package com.comicviewer.cedric.comicviewer;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
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

    public static SettingsFragment newInstance(ArrayList<CharSequence> paths)
    {
        SettingsFragment settings = new SettingsFragment();
        Bundle params = new Bundle();
        params.putCharSequenceArrayList("PathList",paths);
        settings.setArguments(params);
        return settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Bundle args = getArguments();

        ArrayList<CharSequence> paths = args.getCharSequenceArrayList("PathList");

        mPathListing = paths.toArray(new CharSequence[paths.size()]);

        createListPreference();

    }

    private void createListPreference()
    {
        MultiSelectListPreference pathPreference = (MultiSelectListPreference) findPreference("pathList");

        pathPreference.setEntries(mPathListing);
        pathPreference.setEntryValues(mPathListing);

        pathPreference.setPositiveButtonText("Remove");
        pathPreference.setNegativeButtonText("Cancel");
    }

}
