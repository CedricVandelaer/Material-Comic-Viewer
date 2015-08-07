package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 7/08/2015.
 */
public class OtherSettingsFragment extends AbstractOtherSettingsFragment {
    @Override
    protected void addMangaModusSetting() {
        ColoredSwitchPreference mangaPreference = new ColoredSwitchPreference(getActivity());

        mangaPreference.setKey("BUY_PRO");
        mangaPreference.setSummary(getString(R.string.manga_setting_note));
        mangaPreference.setTitle(getString(R.string.manga_setting_non_pro));
        mangaPreference.setEnabled(false);

        addPreference(mangaPreference);
    }

    @Override
    protected void addUnhideSetting() {
        ColoredPreference unhideListPreference = new ColoredPreference(getActivity());

        unhideListPreference.setTitle(getString(R.string.unhide_setting_non_pro));

        unhideListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showBuyProDialog();
                return true;
            }
        });

        addPreference(unhideListPreference);
    }

    @Override
    protected void addFilenameFormatSettings() {
        ColoredPreference preference = new ColoredPreference(getActivity());
        preference.setKey("BUY_PRO");
        preference.setTitle(getString(R.string.file_format_setting_non_pro));
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
