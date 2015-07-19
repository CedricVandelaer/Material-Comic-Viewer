package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.os.Bundle;
import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * Created by Cédric on 18/07/2015.
 */
public class AbstractSettingsOverviewFragment extends PreferenceFragment implements BaseNavigationInterface{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.basic_preferences);

        addLayoutSettings();
        addReadingSettings();
        addOtherSettings();
        addThanksNote();
    }

    private void addLayoutSettings()
    {
        Preference layoutCategory = new Preference(getActivity());

        layoutCategory.setTitle(getString(R.string.layout_settings_category_title));
        layoutCategory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        getPreferenceScreen().addPreference(layoutCategory);
    }

    private void addReadingSettings()
    {
        Preference readingCat = new Preference(getActivity());

        readingCat.setTitle(getString(R.string.reading_settings_category_setting));
        readingCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        getPreferenceScreen().addPreference(readingCat);
    }

    private void addOtherSettings()
    {
        Preference otherCat = new Preference(getActivity());

        otherCat.setTitle(getString(R.string.other_settings));
        otherCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        getPreferenceScreen().addPreference(otherCat);
    }

    private void addThanksNote()
    {
        Preference addThanksCat = new Preference(getActivity());

        addThanksCat.setTitle("Thanks for buying the pro version!");
        addThanksCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        getPreferenceScreen().addPreference(addThanksCat);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
