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

        MultiSelectListPreference unhideListPreference = new MultiSelectListPreference(getActivity());

        ArrayList<String> hiddenPaths = PreferenceSetter.getHiddenFiles(getActivity());

        CharSequence[] entries = new CharSequence[hiddenPaths.size()];

        for (int i=0;i<entries.length;i++)
        {
            entries[i] = hiddenPaths.get(i);
        }

        unhideListPreference.setKey(PreferenceSetter.UNHIDE_LIST);
        unhideListPreference.setTitle("Unhide comics and folders");
        unhideListPreference.setEntries(entries);
        unhideListPreference.setEntryValues(entries);
        unhideListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                List<CharSequence> selected = Arrays.asList((CharSequence[]) newValue);

                for (int i=0;i<selected.size();i++)
                {
                    PreferenceSetter.removeHiddenPath(getActivity(), selected.get(i).toString());
                }

                return true;
            }
        });

        targetCategory.addPreference(unhideListPreference);
    }

    private void addMangaPreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        CustomCheckBoxPreference mangaPreference = new CustomCheckBoxPreference(getActivity());



        mangaPreference.setKey(PreferenceSetter.MANGA_SETTING);
        mangaPreference.setSummary("Note: By checking this option comics will open in manga modus by default");
        mangaPreference.setTitle("Manga modus (PRO)");
        mangaPreference.setDefaultValue(false);

        mangaPreference.setEnabled(false);

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
                                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                                startActivity(browse);
                            }
                        }).show();

                return false;
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

                return false;
            }
        });

        targetCategory.addPreference(preference);

    }

    private void addRemovePathsPreference()
    {
        ArrayList<CharSequence> paths = new ArrayList<>();
        ArrayList<CharSequence> excludedPaths = new ArrayList<>();

        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";


        ArrayList<String> filePaths = PreferenceSetter.getFilePathsFromPreferences(getActivity());
        ArrayList<String> excludedFilePaths = PreferenceSetter.getExcludedPaths(getActivity());

        for (int i=0;i<filePaths.size();i++)
        {
            if (excludedFilePaths.contains(filePaths.get(i)))
                filePaths.remove(i);
        }
        if (!filePaths.contains(defaultPath))
            filePaths.add(defaultPath);


        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        CustomMultiSelectListPreference multiListPref = createListPreference(filePaths,filePaths);

        targetCategory.addPreference(multiListPref);
    }

    private CustomMultiSelectListPreference createListPreference(ArrayList<String> keys, ArrayList<String> values)
    {
        CustomMultiSelectListPreference listPref = new CustomMultiSelectListPreference(getActivity(),keys,values, "Choose paths to remove");

        listPref.setTitle("Remove added paths");
        listPref.setSummary(getString(R.string.path_preference_summary));

        listPref.setPositiveButtonText("Remove");
        listPref.setNegativeButtonText("Cancel");


        return listPref;

    }

}
