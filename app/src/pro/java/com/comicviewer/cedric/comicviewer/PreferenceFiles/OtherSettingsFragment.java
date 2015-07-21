package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;

/**
 * Created by Cédric on 20/07/2015.
 */
public class OtherSettingsFragment extends AbstractOtherSettingsFragment {
    @Override
    protected void addMangaModusSetting() {

        ColoredSwitchPreference mangaPreference = new ColoredSwitchPreference(getActivity());

        mangaPreference.setKey(StorageManager.MANGA_SETTING);
        mangaPreference.setSummary(getString(R.string.manga_setting_note));
        mangaPreference.setTitle(getString(R.string.manga_setting));
        mangaPreference.setDefaultValue(false);

        addPreference(mangaPreference);
    }

    @Override
    protected void addUnhideSetting() {

        ColoredPreference unhideListPreference = new ColoredPreference(getActivity());
        unhideListPreference.setTitle(getString(R.string.unhide_setting));

        unhideListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                ArrayList<String> hiddenPaths = StorageManager.getHiddenFiles(getActivity());

                CharSequence[] charSequences = new CharSequence[hiddenPaths.size()];

                for (int i = 0; i < charSequences.length; i++) {
                    charSequences[i] = hiddenPaths.get(i);
                }

                new MaterialDialog.Builder(getActivity())
                        .title(getString(R.string.unhide_files))
                        .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                        .positiveText(getString(R.string.unhide))
                        .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                        .negativeText(getString(R.string.cancel))
                        .items(charSequences)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                                materialDialog.dismiss();
                                for (int i = 0; i < charSequences.length; i++) {
                                    StorageManager.removeHiddenPath(getActivity(), charSequences[i].toString());
                                }

                                return false;
                            }
                        }).show();
                return true;
            }
        });

        addPreference(unhideListPreference);
    }

    @Override
    protected void addFilenameFormatSettings() {

        ColoredListPreference fileFormatList = new ColoredListPreference(getActivity());

        String[] entries = getActivity().getResources().getStringArray(R.array.File_formats);

        CharSequence[] charSequences = new CharSequence[entries.length];

        for (int i=0;i<charSequences.length;i++)
        {
            charSequences[i] = entries[i];
        }

        fileFormatList.setKey(StorageManager.FILE_FORMAT_SETTING);
        fileFormatList.setSummary(getString(R.string.extra_chars_removed_note));
        fileFormatList.setEntries(charSequences);
        fileFormatList.setEntryValues(charSequences);
        fileFormatList.setTitle(getString(R.string.file_format_setting));
        fileFormatList.setDialogTitle(getString(R.string.choose_file_format));
        fileFormatList.setDefaultValue(getActivity().getResources().getString(R.string.file_format_1));

        addPreference(fileFormatList);
    }
}
