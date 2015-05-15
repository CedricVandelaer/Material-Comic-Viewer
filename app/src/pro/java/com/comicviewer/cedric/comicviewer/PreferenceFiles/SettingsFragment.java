package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;

import java.util.ArrayList;


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

        addFilenameFormatSettings();
        addRemovePathsPreference();
        addAppThemeSettings();
        addMangaPreference();

        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));

    }

    private void addFilenameFormatSettings()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        ListPreference fileFormatList = new ListPreference(getActivity());

        String[] entries = getActivity().getResources().getStringArray(R.array.File_formats);

        CharSequence[] charSequences = new CharSequence[entries.length];

        for (int i=0;i<charSequences.length;i++)
        {
            charSequences[i] = entries[i];
        }

        fileFormatList.setKey(PreferenceSetter.FILE_FORMAT_SETTING);
        fileFormatList.setSummary("Note: Extra or special characters will be removed");
        fileFormatList.setEntries(charSequences);
        fileFormatList.setEntryValues(charSequences);
        fileFormatList.setTitle("Comic file format");
        fileFormatList.setDialogTitle("Choose the file format");
        fileFormatList.setDefaultValue(getActivity().getResources().getString(R.string.file_format_1));

        targetCategory.addPreference(fileFormatList);
    }

    private void addAppThemeSettings()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("LayoutCategory");

        final ListPreference appThemeListPreference = new ListPreference(getActivity());
        appThemeListPreference.setKey(PreferenceSetter.APP_THEME_COLOR);

        CharSequence[] entries = {"Teal","Red", "Orange","Blue", "Pink", "Purple", "Deep Purple", "Green", "Yellow"};
        CharSequence[] entryValues = {""+getResources().getColor(R.color.Teal),
        ""+getResources().getColor(R.color.Red),
        ""+getResources().getColor(R.color.Orange),
        ""+getResources().getColor(R.color.Blue),
        ""+getResources().getColor(R.color.Pink),
        ""+getResources().getColor(R.color.Purple),
        ""+getResources().getColor(R.color.DeepPurple),
        ""+getResources().getColor(R.color.Green),
        ""+getResources().getColor(R.color.Yellow)};

        appThemeListPreference.setEntries(entries);
        appThemeListPreference.setEntryValues(entryValues);
        appThemeListPreference.setDefaultValue("" + PreferenceSetter.getAppThemeColor(getActivity()));

        appThemeListPreference.setTitle("App theme color");
        appThemeListPreference.setSummary("Note: app will have to restart");
        appThemeListPreference.setDialogTitle("Choose app theme color");

        appThemeListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("Warning")
                        .content("The app will have to restart to complete the operation")
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .positiveText("Accept")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                PreferenceSetter.saveAppThemeColor(getActivity(), (CharSequence) newValue);
                                appThemeListPreference.setOnPreferenceChangeListener(null);
                                getActivity().finish();
                            }
                        })
                        .show();

                return false;
            }
        });

        targetCategory.addPreference(appThemeListPreference);

    }

    private void addMangaPreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        CustomCheckBoxPreference mangaPreference = new CustomCheckBoxPreference(getActivity());


        mangaPreference.setKey(PreferenceSetter.MANGA_SETTING);
        mangaPreference.setSummary("Note: By checking this option comics will open in manga modus by default");
        mangaPreference.setTitle("Manga modus");
        mangaPreference.setDefaultValue(false);

        targetCategory.addPreference(mangaPreference);
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
