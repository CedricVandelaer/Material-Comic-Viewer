package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;

/**
 * Created by Cédric on 18/07/2015.
 */
public abstract class AbstractSettingsOverviewFragment extends AbstractSettingsFragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addLayoutSettings();
        addReadingSettings();
        addOtherSettings();
        addProNote();
    }

    abstract void addProNote();

    private void addLayoutSettings()
    {
        ColoredPreference layoutCategory = new ColoredPreference(getActivity(), getResources().getColor(R.color.Black));

        layoutCategory.setTitle(getString(R.string.layout_settings_category_title));
        layoutCategory.setIcon(R.drawable.ic_palette);
        layoutCategory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((NewDrawerActivity) getActivity()).setFragmentInSection(new LayoutSettingsFragment(), getString(R.string.layout_settings_category_title));
                return true;
            }
        });
        addPreference(layoutCategory);
    }

    private void addReadingSettings()
    {
        ColoredPreference readingCat = new ColoredPreference(getActivity());

        readingCat.setIcon(R.drawable.ic_read);

        readingCat.setTitle(getString(R.string.reading_settings_category_setting));
        readingCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((NewDrawerActivity) getActivity()).setFragmentInSection(new ReadingSettingsFragment(), getString(R.string.reading_settings_category_setting));
                return true;
            }
        });
        addPreference(readingCat);
    }

    private void addOtherSettings()
    {
        ColoredPreference otherCat = new ColoredPreference(getActivity());

        otherCat.setTitle(getString(R.string.other_settings));
        otherCat.setIcon(R.drawable.ic_settings_grey);
        otherCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((NewDrawerActivity) getActivity()).setFragmentInSection(new OtherSettingsFragment(), getString(R.string.other_settings));
                return true;
            }
        });
        addPreference(otherCat);
    }

}
