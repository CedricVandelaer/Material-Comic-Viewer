package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 6/08/2015.
 */
public class SettingsOverviewFragment extends AbstractSettingsOverviewFragment {
    @Override
    void addProNote() {
        ColoredPreference goProPreference = new ColoredPreference(getActivity());
        goProPreference.setTitle(getString(R.string.buy_full_version));
        goProPreference.setIcon(R.drawable.ic_duck);
        goProPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                startActivity(browse);
                return false;
            }
        });
        addPreference(goProPreference);
    }
}
