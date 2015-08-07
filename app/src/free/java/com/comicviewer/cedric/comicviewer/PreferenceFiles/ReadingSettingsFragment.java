package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.preference.Preference;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 7/08/2015.
 */
public class ReadingSettingsFragment extends AbstractReadingSettingsFragment {
    @Override
    protected void addPageAnimationSetting() {
        ColoredPreference viewPagerAnimationPreference = new ColoredPreference(getActivity());


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

        addPreference(viewPagerAnimationPreference);
    }

    @Override
    protected void addUseVolumeKeysSetting() {
        ColoredSwitchPreference volumePreference = new ColoredSwitchPreference(getActivity());
        volumePreference.setTitle(getString(R.string.volume_key_setting_non_pro));
        volumePreference.setEnabled(false);
        addPreference(volumePreference);
    }

    @Override
    protected void addReverseVolumeKeySetting() {

    }
}
