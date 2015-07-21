package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SplashActivity;
import com.comicviewer.cedric.comicviewer.Utilities;

/**
 * Created by Cédric on 19/07/2015.
 */
public class LayoutSettingsFragment extends AbstractLayoutSettingsFragment {
    @Override
    protected void addFabColorPreference() {
        final ColoredListPreference accentColorListPreference = new ColoredListPreference(getActivity());
        accentColorListPreference.setKey(StorageManager.ACCENT_COLOR);

        CharSequence[] entries = {
                getString(R.string.app_theme_setting),
                getString(R.string.teal),
                getString(R.string.red),
                getString(R.string.orange),
                getString(R.string.blue),
                getString(R.string.pink),
                getString(R.string.purple),
                getString(R.string.deep_purple),
                getString(R.string.green),
                getString(R.string.yellow),
                getString(R.string.gold),
                getString(R.string.indigo)};
        CharSequence[] entryValues = {
                ""+ StorageManager.getAppThemeColor(getActivity()),
                ""+getResources().getColor(R.color.Teal),
                ""+getResources().getColor(R.color.Red),
                ""+getResources().getColor(R.color.Orange),
                ""+getResources().getColor(R.color.Blue),
                ""+getResources().getColor(R.color.Pink),
                ""+getResources().getColor(R.color.Purple),
                ""+getResources().getColor(R.color.DeepPurple),
                ""+getResources().getColor(R.color.Green),
                ""+getResources().getColor(R.color.Yellow),
                ""+getResources().getColor(R.color.Gold),
                ""+getResources().getColor(R.color.Indigo)};

        accentColorListPreference.setEntries(entries);
        accentColorListPreference.setEntryValues(entryValues);
        accentColorListPreference.setDefaultValue("" + StorageManager.getAppThemeColor(getActivity()));

        accentColorListPreference.setTitle(getString(R.string.app_accent_color_setting));
        accentColorListPreference.setDialogTitle(getString(R.string.choose_accent_color));

        accentColorListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {

                StorageManager.saveAppAccentColor(getActivity(), (CharSequence) newValue);
                setPreferenceColors();
                return false;
            }
        });

        addPreference(accentColorListPreference);
    }

    @Override
    protected void addAppThemeSettings() {

        final ColoredListPreference appThemeListPreference = new ColoredListPreference(getActivity());
        appThemeListPreference.setKey(StorageManager.APP_THEME_COLOR);

        CharSequence[] entries = {
                getString(R.string.teal),
                getString(R.string.red),
                getString(R.string.orange),
                getString(R.string.blue),
                getString(R.string.pink),
                getString(R.string.purple),
                getString(R.string.deep_purple),
                getString(R.string.green),
                getString(R.string.yellow),
                getString(R.string.gold),
                getString(R.string.indigo)};
        CharSequence[] entryValues = {""+getResources().getColor(R.color.Teal),
                ""+getResources().getColor(R.color.Red),
                ""+getResources().getColor(R.color.Orange),
                ""+getResources().getColor(R.color.Blue),
                ""+getResources().getColor(R.color.Pink),
                ""+getResources().getColor(R.color.Purple),
                ""+getResources().getColor(R.color.DeepPurple),
                ""+getResources().getColor(R.color.Green),
                ""+getResources().getColor(R.color.Yellow),
                ""+getResources().getColor(R.color.Gold),
                ""+getResources().getColor(R.color.Indigo)};

        appThemeListPreference.setEntries(entries);
        appThemeListPreference.setEntryValues(entryValues);
        appThemeListPreference.setDefaultValue("" + StorageManager.getAppThemeColor(getActivity()));

        appThemeListPreference.setTitle(getString(R.string.app_theme_setting));
        appThemeListPreference.setDialogTitle(getString(R.string.choose_app_color));

        appThemeListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {

                StorageManager.saveAppThemeColor(getActivity(), (CharSequence) newValue);

                if (Build.VERSION.SDK_INT>20)
                    getActivity().getWindow().setStatusBarColor(Utilities.darkenColor(StorageManager.getAppThemeColor(getActivity())));
                ((NewDrawerActivity)getActivity()).getToolbar().setBackgroundColor(StorageManager.getAppThemeColor(getActivity()));
                ((NewDrawerActivity)getActivity()).updateDrawerHeader();
                ((NewDrawerActivity)getActivity()).setDrawerColor();
                ((NewDrawerActivity)getActivity()).startSetTaskDescription();

                return true;

            }
        });

        addPreference(appThemeListPreference);
    }
}
