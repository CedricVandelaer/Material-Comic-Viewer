package com.comicviewer.cedric.comicviewer.PreferenceFiles;


import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import com.comicviewer.cedric.comicviewer.R;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SettingsFragment extends AbstractSettingsFragment{

    public SettingsFragment() {
        // Required empty public constructor
    }

    /*

    protected void addFabColorPreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("LayoutCategory");

        Preference accentColorPreference = new Preference(getActivity());


        accentColorPreference.setKey("BUY_PRO_FILEFORMAT");
        accentColorPreference.setTitle(getString(R.string.app_accent_color_setting_non_pro));
        accentColorPreference.setDefaultValue(getString(R.string.none));

        accentColorPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showBuyProDialog();

                return true;
            }
        });

        targetCategory.addPreference(accentColorPreference);
    }

    protected void addViewPagerAnimationPreference()
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

    @Override
    protected void addGoProPreference() {
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
    }

    protected void addUnhidePreference()
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

    protected void addMangaPreference()
    {
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("FunctionalityCategory");

        final CustomCheckBoxPreference mangaPreference = new CustomCheckBoxPreference(getActivity());

        mangaPreference.setKey(StorageManager.MANGA_SETTING);
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

    protected void addFilenameFormatSettings()
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

    protected void addAppThemeSettings()
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

    protected void disableVolumeKeyPreference()
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


*/

}
