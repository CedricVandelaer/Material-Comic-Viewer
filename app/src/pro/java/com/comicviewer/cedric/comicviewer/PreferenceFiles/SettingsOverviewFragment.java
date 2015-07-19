package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 19/07/2015.
 */
public class SettingsOverviewFragment extends AbstractSettingsOverviewFragment {
    @Override
    void addProNote() {
        Preference proPreference = new Preference(getActivity());
        proPreference.setIcon(R.drawable.ic_thumb_up);
        proPreference.setSummary("Thanks for buying the pro version and supporting development!");
        getPreferenceScreen().addPreference(proPreference);
    }
}
