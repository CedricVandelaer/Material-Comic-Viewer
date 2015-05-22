package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link android.app.Fragment} subclass.
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


        addRemovePathsPreference();
        addUnhidePreference();
        addAppThemeSettings();
        addFileFormatSettings();
        addMangaPreference();
        disableVolumeKeyPreference();


        PreferenceCategory functionCategory = (PreferenceCategory) findPreference("FunctionalityCategory");
        Preference goProPreference = new Preference(getActivity());
        goProPreference.setTitle("Buy full version");
        goProPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                startActivity(browse);
                return false;
            }
        });
        functionCategory.addPreference(goProPreference);

        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));

    }

    private void addUnhidePreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        Preference unhideListPreference = new Preference(getActivity());

        unhideListPreference.setTitle("Unhide comics and folders (PRO)");

        unhideListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showBuyProDialog();
                return true;
            }
        });

        targetCategory.addPreference(unhideListPreference);
    }

    private void addMangaPreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        final CustomCheckBoxPreference mangaPreference = new CustomCheckBoxPreference(getActivity());

        mangaPreference.setKey(PreferenceSetter.MANGA_SETTING);
        mangaPreference.setSummary("Note: By checking this option comics will open in manga modus by default");
        mangaPreference.setTitle("Manga modus (PRO)");
        mangaPreference.setDefaultValue(false);

        mangaPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showBuyProDialog();
                mangaPreference.setChecked(!mangaPreference.isChecked());
                return true;
            }
        });

        targetCategory.addPreference(mangaPreference);
    }

    private void addFileFormatSettings()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        Preference preference = new Preference(getActivity());

        preference.setKey("BUY_PRO_FILEFORMAT");
        preference.setTitle("Comic file format (PRO)");

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showBuyProDialog();

                return true;
            }
        });

        targetCategory.addPreference(preference);

    }

    private void addAppThemeSettings()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("LayoutCategory");

        Preference preference = new Preference(getActivity());

        preference.setKey("BUY_PRO");
        preference.setTitle("App theme color (PRO)");

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showBuyProDialog();
                return true;
            }
        });

        targetCategory.addPreference(preference);

    }

    private void disableVolumeKeyPreference()
    {
        final CustomCheckBoxPreference volumePreference = (CustomCheckBoxPreference) findPreference("volumeKeysOption");
        volumePreference.setTitle("Use volume keys to turn pages (PRO)");
        volumePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showBuyProDialog();
                volumePreference.setChecked(!volumePreference.isChecked());
                return true;
            }
        });

    }

    private void addRemovePathsPreference()
    {

        Preference removePathsPreference = new Preference(getActivity());

        removePathsPreference.setTitle("Remove added filepaths");
        removePathsPreference.setSummary(getString(R.string.path_preference_summary));


        removePathsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
                ArrayList<String> filePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());

                if (!filePaths.contains(defaultPath))
                    filePaths.add(defaultPath);

                CharSequence[] charSequences = new CharSequence[filePaths.size()];

                for (int i = 0; i < charSequences.length; i++) {
                    charSequences[i] = filePaths.get(i);
                }


                new MaterialDialog.Builder(getActivity())
                        .title("Remove filepaths")
                        .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .positiveText("Remove")
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .negativeText("Cancel")
                        .items(charSequences)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                                materialDialog.dismiss();
                                for (int i = 0; i < charSequences.length; i++) {
                                    if (!charSequences.toString().equals(defaultPath))
                                        PreferenceSetter.removeFilePath(getActivity(), charSequences[i].toString());
                                }

                                return false;
                            }
                        }).show();
                return true;
            }
        });


        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        targetCategory.addPreference(removePathsPreference);
    }

    public void showBuyProDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Notice")
                .content("This feature requires the pro version of the app.")
                .negativeText("Cancel")
                .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                .positiveText("Go to play store")
                .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro") );
                        startActivity(browse);
                    }
                }).show();

    }

}
