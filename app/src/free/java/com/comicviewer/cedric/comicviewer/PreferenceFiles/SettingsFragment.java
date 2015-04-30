package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.content.Intent;
import android.net.Uri;
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

        PreferenceCategory functionCategory = (PreferenceCategory) findPreference("FunctionalityCategory");
        Preference goProPreference = new Preference(getActivity());
        goProPreference.setTitle("Buy full version");
        goProPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro") );
                startActivity( browse );
                return false;
            }
        });
        functionCategory.addPreference(goProPreference);

        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));

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
                final Dialog dialog = new Dialog(getActivity(), "Notice", "This feature requires the pro version of the app. ");
                dialog.show();

                dialog.getButtonAccept().setBackgroundColor(PreferenceSetter.getAppThemeColor(getActivity()));
                dialog.getButtonAccept().setText("Go to play store");
                dialog.getButtonAccept().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro") );
                        startActivity( browse );
                    }
                });


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
