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
import com.comicviewer.cedric.comicviewer.DrawerActivity;
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
        addBackgroundChangeListener();
        addViewPagerAnimationPreference();

        PreferenceCategory functionCategory = (PreferenceCategory) findPreference("FunctionalityCategory");
        Preference goProPreference = new Preference(getActivity());
        goProPreference.setTitle(getString(R.string.buy_full_version));
        goProPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                startActivity(browse);
                return false;
            }
        });
        functionCategory.addPreference(goProPreference);

        if (PreferenceSetter.getBackgroundColorPreference(getActivity())!= getResources().getColor(R.color.WhiteBG))
        {
            PreferenceSetter.setBackgroundColorPreference(getActivity());
        }
        else
        {
            getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
            if (Build.VERSION.SDK_INT>20)
                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.BlueGrey));
        }

    }

    private void addBackgroundChangeListener()
    {
        Preference backgroundPref = (Preference) findPreference("backgroundColor");
        backgroundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String color = (String) newValue;

                PreferenceSetter.setBackgroundColorPreference(getActivity(), color);
                if (PreferenceSetter.getBackgroundColorPreference(getActivity())!= getResources().getColor(R.color.WhiteBG)) {
                    ((DrawerActivity) getActivity()).setDrawerBackgroundColor(PreferenceSetter.getBackgroundColorPreference(getActivity()));
                    PreferenceSetter.setBackgroundColorPreference(getActivity());
                }
                else
                    ((DrawerActivity)getActivity()).setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));

                return true;
            }
        });

    }

    private void addViewPagerAnimationPreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("ReadCategory");

        Preference viewPagerAnimationPreference = new Preference(getActivity());


        viewPagerAnimationPreference.setKey("BUY_PRO_FILEFORMAT");
        viewPagerAnimationPreference.setTitle(getString(R.string.page_flip_animation_setting_non_pro));
        viewPagerAnimationPreference.setDefaultValue(getString(R.string.none));

        viewPagerAnimationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showBuyProDialog();

                return true;
            }
        });

        targetCategory.addPreference(viewPagerAnimationPreference);
    }

    private void addUnhidePreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        Preference unhideListPreference = new Preference(getActivity());

        unhideListPreference.setTitle(getString(R.string.unhide_setting_non_pro));

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
        mangaPreference.setSummary(getString(R.string.manga_setting_note));
        mangaPreference.setTitle(getString(R.string.manga_setting_non_pro));
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
        preference.setTitle(getString(R.string.file_format_setting_non_pro));

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
        preference.setTitle(getString(R.string.app_theme_setting_non_pro));

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
        volumePreference.setTitle(getString(R.string.volume_key_setting_non_pro));
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

        removePathsPreference.setTitle(getString(R.string.remove_filepaths_setting));
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
                        .title(getString(R.string.remove_filepaths))
                        .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .positiveText(getString(R.string.remove))
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .negativeText(getString(R.string.cancel))
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
                .title(getString(R.string.notice))
                .content(getString(R.string.pro_version_notice))
                .negativeText(getString(R.string.cancel))
                .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                .positiveText(getString(R.string.go_to_play_store))
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
