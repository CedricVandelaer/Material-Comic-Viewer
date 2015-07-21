package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 20/07/2015.
 */
public class ReadingSettingsFragment extends AbstractReadingSettingsFragment {
    @Override
    protected void addPageAnimationSetting() {

        ColoredListPreference viewPagerAnimationPreference = new ColoredListPreference(getActivity());

        viewPagerAnimationPreference.setKey(StorageManager.VIEWPAGER_ANIMATION_SETTING);
        viewPagerAnimationPreference.setTitle(getString(R.string.page_flip_animation_setting));
        viewPagerAnimationPreference.setEntries(getResources().getTextArray(R.array.Viewpager_animations));
        viewPagerAnimationPreference.setEntryValues(getResources().getTextArray(R.array.Viewpager_animations));
        viewPagerAnimationPreference.setDefaultValue(getString(R.string.none));

        addPreference(viewPagerAnimationPreference);
    }

    @Override
    protected void addUseVolumeKeysSetting() {

        ColoredSwitchPreference volumeKeyPreference = new ColoredSwitchPreference(getActivity());
        volumeKeyPreference.setKey(StorageManager.VOLUME_KEY_OPTION);
        volumeKeyPreference.setTitle(getString(R.string.volume_keys_setting));
        volumeKeyPreference.setDefaultValue(false);
        addPreference(volumeKeyPreference);
    }
}
