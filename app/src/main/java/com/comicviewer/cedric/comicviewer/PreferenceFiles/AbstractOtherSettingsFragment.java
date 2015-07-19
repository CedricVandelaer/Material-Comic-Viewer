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
public abstract class AbstractOtherSettingsFragment extends PreferenceFragment implements BaseNavigationInterface
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBackground();

        addPreferencesFromResource(R.xml.basic_preferences);

        addUseRecentsSetting();
        addFilenameFormatSettings();
        addUnhideSetting();
        addRemoveFilePathsSetting();
        addMangaModusSetting();
        addClearComicDataSetting();
    }

    private void addClearComicDataSetting() {
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
        getPreferenceScreen().addPreference(clearDataPref);
    }

    protected abstract void addMangaModusSetting();

    private void addRemoveFilePathsSetting() {
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


        getPreferenceScreen().addPreference(removePathsPreference);
    }

    protected abstract void addUnhideSetting();

    protected abstract void addFilenameFormatSettings();

    private void addUseRecentsSetting() {
        CheckBoxPreference useRecentsPreference = new CheckBoxPreference(getActivity());
        useRecentsPreference.setKey(StorageManager.USES_RECENTS);
        useRecentsPreference.setTitle(getString(R.string.recents_screen_setting));
        useRecentsPreference.setSummary(getString(R.string.recents_screen_setting_note));
        useRecentsPreference.setDefaultValue(true);
        getPreferenceScreen().addPreference(useRecentsPreference);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackground();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    protected void setBackground()
    {
        getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.WhiteBG));
        if (Build.VERSION.SDK_INT>20)
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.Black));
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
