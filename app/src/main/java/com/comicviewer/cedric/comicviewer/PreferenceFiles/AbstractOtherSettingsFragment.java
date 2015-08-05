package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;

/**
 * Created by Cédric on 20/07/2015.
 */
public abstract class AbstractOtherSettingsFragment extends AbstractSettingsFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addUseRecentsSetting();
        addFilenameFormatSettings();
        addUnhideSetting();
        addRemoveFilePathsSetting();
        addMangaModusSetting();
        addClearComicDataSetting();
    }

    private void addClearComicDataSetting() {
        ColoredPreference clearDataPref = new ColoredPreference(getActivity());

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
        addPreference(clearDataPref);
    }

    protected abstract void addMangaModusSetting();

    private void addRemoveFilePathsSetting() {
        ColoredPreference removePathsPreference = new ColoredPreference(getActivity());

        removePathsPreference.setTitle(getString(R.string.remove_filepaths_setting));
        removePathsPreference.setSummary(getString(R.string.path_preference_summary));

        removePathsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                ArrayList<String> filePaths = StorageManager.getFilePathsFromPreferences(getActivity());

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
                                ArrayList<String> paths = new ArrayList<String>();
                                for (int i = 0; i < charSequences.length; i++) {
                                    paths.add(charSequences[i].toString());
                                }
                                StorageManager.batchRemoveFilePaths(getActivity(), paths);

                                return false;
                            }
                        }).show();
                return true;
            }
        });


        addPreference(removePathsPreference);
    }

    protected abstract void addUnhideSetting();

    protected abstract void addFilenameFormatSettings();

    private void addUseRecentsSetting() {
        ColoredSwitchPreference useRecentsPreference = new ColoredSwitchPreference(getActivity());
        useRecentsPreference.setKey(StorageManager.USES_RECENTS);
        useRecentsPreference.setTitle(getString(R.string.recents_screen_setting));
        useRecentsPreference.setSummary(getString(R.string.recents_screen_setting_note));
        useRecentsPreference.setDefaultValue(true);
        addPreference(useRecentsPreference);
    }


}
