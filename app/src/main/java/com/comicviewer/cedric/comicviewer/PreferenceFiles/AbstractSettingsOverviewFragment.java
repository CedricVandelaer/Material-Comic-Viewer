package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * Created by Cédric on 18/07/2015.
 */
public abstract class AbstractSettingsOverviewFragment extends PreferenceFragment implements BaseNavigationInterface{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBackground();

        addPreferencesFromResource(R.xml.basic_preferences);

        addLayoutSettings();
        addReadingSettings();
        addOtherSettings();
        addProNote();


    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackground();
    }

    abstract void addProNote();

    private void addLayoutSettings()
    {
        Preference layoutCategory = new Preference(getActivity());

        layoutCategory.setTitle(getString(R.string.layout_settings_category_title));
        layoutCategory.setIcon(R.drawable.ic_palette);
        layoutCategory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((NewDrawerActivity) getActivity()).setFragmentInSection(new LayoutSettingsFragment(), getString(R.string.layout_settings_category_title));
                return true;
            }
        });
        getPreferenceScreen().addPreference(layoutCategory);
    }

    private void addReadingSettings()
    {
        Preference readingCat = new Preference(getActivity());

        readingCat.setIcon(R.drawable.ic_read);

        readingCat.setTitle(getString(R.string.reading_settings_category_setting));
        readingCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((NewDrawerActivity) getActivity()).setFragmentInSection(new ReadingSettingsFragment(), getString(R.string.reading_settings_category_setting));
                return true;
            }
        });
        getPreferenceScreen().addPreference(readingCat);
    }

    private void addOtherSettings()
    {
        Preference otherCat = new Preference(getActivity());

        otherCat.setTitle(getString(R.string.other_settings));
        otherCat.setIcon(R.drawable.ic_settings_grey);
        otherCat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((NewDrawerActivity) getActivity()).setFragmentInSection(new OtherSettingsFragment(), getString(R.string.other_settings));
                return true;
            }
        });
        getPreferenceScreen().addPreference(otherCat);
    }

    protected void setBackground()
    {
        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.WhiteBG));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.Black));
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
