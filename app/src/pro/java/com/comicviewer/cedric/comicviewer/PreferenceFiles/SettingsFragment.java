package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.View;

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


        addRemovePathsPreference();
        addAppThemeSettings();

        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));

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
        appThemeListPreference.setDefaultValue(""+PreferenceSetter.getAppThemeColor(getActivity()));

        appThemeListPreference.setTitle("App theme color");
        appThemeListPreference.setSummary("Note: app will have to restart");
        appThemeListPreference.setDialogTitle("Choose app theme color");

        appThemeListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PreferenceSetter.saveAppThemeColor(getActivity(), (CharSequence)newValue);

                final Dialog dialog = new Dialog(getActivity(),"Warning","The app will have to restart to complete the operation");
                dialog.show();

                ButtonFlat cancelButton = dialog.getButtonCancel();
                cancelButton.setBackgroundColor(PreferenceSetter.getAppThemeColor(getActivity()));

                ButtonFlat acceptButton = dialog.getButtonAccept();
                acceptButton.setBackgroundColor(PreferenceSetter.getAppThemeColor(getActivity()));

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appThemeListPreference.setOnPreferenceChangeListener(null);
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });

                return false;
            }
        });

        targetCategory.addPreference(appThemeListPreference);

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
