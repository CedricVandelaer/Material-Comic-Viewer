package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cedric on 7/08/2015.
 */
public class LayoutSettingsFragment extends AbstractLayoutSettingsFragment {
    @Override
    protected void addFabColorPreference() {
        ColoredPreference accentColorPreference = new ColoredPreference(getActivity());


        accentColorPreference.setKey("BUY_PRO_FILEFORMAT");
        accentColorPreference.setTitle(getString(R.string.app_accent_color_setting_non_pro));
        accentColorPreference.setDefaultValue(getString(R.string.none));

        accentColorPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showBuyProDialog();

                return true;
            }
        });

        addPreference(accentColorPreference);
    }

    @Override
    protected void addAppThemeSettings() {
        ColoredPreference preference = new ColoredPreference(getActivity());

        preference.setKey("BUY_PRO");
        preference.setTitle(getString(R.string.app_theme_setting_non_pro));

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showBuyProDialog();
                return true;
            }
        });

        addPreference(preference);
    }
}
