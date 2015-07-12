package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;

/**
 * Created by CV on 28/06/2015.
 */
public abstract class AbstractSettingsFragment extends PreferenceFragment {

    public AbstractSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        addFilenameFormatSettings();
        addUnhidePreference();
        addRemovePathsPreference();
        addAppThemeSettings();
        addFabColorPreference();
        addMangaPreference();
        addBackgroundChangeListener();
        addViewPagerAnimationPreference();
        addClearSavedComicDataPreference();
        disableVolumeKeyPreference();
        addGoProPreference();

        if (StorageManager.getBackgroundColorPreference(getActivity())!= getResources().getColor(R.color.WhiteBG))
        {
            StorageManager.setBackgroundColorPreference(getActivity());
        }
        else
        {
            getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
            if (Build.VERSION.SDK_INT>20)
                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));
        }
    }

    protected abstract void disableVolumeKeyPreference();

    private void addClearSavedComicDataPreference() {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");
        Preference clearDataPref = new Preference(getActivity());

        clearDataPref.setKey("clearData");
        clearDataPref.setTitle("Clear comic data");
        clearDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                StorageManager.saveComicList(getActivity(), new ArrayList<Comic>());
                Toast.makeText(getActivity(), "Data cleared!", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        targetCategory.addPreference(clearDataPref);
    }

    abstract protected void addViewPagerAnimationPreference();


    protected void addBackgroundChangeListener()
    {
        Preference backgroundPref = (Preference) findPreference("backgroundColor");
        backgroundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String color = (String) newValue;

                StorageManager.setBackgroundColorPreference(getActivity(), color);
                if (StorageManager.getBackgroundColorPreference(getActivity()) != getResources().getColor(R.color.WhiteBG)) {
                    ((DrawerActivity) getActivity()).setDrawerBackgroundColor(StorageManager.getBackgroundColorPreference(getActivity()));
                    StorageManager.setBackgroundColorPreference(getActivity());
                } else
                    ((DrawerActivity) getActivity()).setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));

                return true;
            }
        });

    }

    abstract protected void addGoProPreference();

    abstract protected void addFilenameFormatSettings();

    abstract protected void addFabColorPreference();

    abstract protected void addAppThemeSettings();

    abstract protected void addMangaPreference();

    abstract protected void addUnhidePreference();

    protected void addRemovePathsPreference()
    {
        Preference removePathsPreference = new Preference(getActivity());

        removePathsPreference.setTitle(getString(R.string.remove_filepaths_setting));
        removePathsPreference.setSummary(getString(R.string.path_preference_summary));


        removePathsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
                ArrayList<String> filePaths = StorageManager.getFilePathsFromPreferences(getActivity());

                if (!filePaths.contains(defaultPath))
                    filePaths.add(defaultPath);

                CharSequence[] charSequences = new CharSequence[filePaths.size()];

                for (int i = 0; i < charSequences.length; i++) {
                    charSequences[i] = filePaths.get(i);
                }


                new MaterialDialog.Builder(getActivity())
                        .title(getString(R.string.remove_filepaths))
                        .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                        .positiveText(getString(R.string.remove))
                        .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                        .negativeText(getString(R.string.cancel))
                        .items(charSequences)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                                materialDialog.dismiss();
                                for (int i = 0; i < charSequences.length; i++) {
                                    if (!charSequences.toString().equals(defaultPath))
                                        StorageManager.removeFilePath(getActivity(), charSequences[i].toString());
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
                .title(getString(R.string.notice))
                .content(getString(R.string.pro_version_notice))
                .negativeText(getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .positiveText(getString(R.string.go_to_play_store))
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
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
