package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * Created by Cédric on 19/07/2015.
 */
public abstract class AbstractReadingSettingsFragment extends AbstractSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addWidthAutoFitPreference();
        addRotateSetting();
        addKeepScreenOnSetting();
        addHighQualitySetting();
        addUseVolumeKeysSetting();
        addReverseVolumeKeySetting();
        addShowToolbarSetting();
        addForcePortraitSetting();
        addPagingOnZoomSetting();
        addZoomFactorSetting();
        addScrollOnTapSetting();
        addSingleTapScrollSetting();
        addReadingBackgroundSetting();
        addPageNumberSetting();
        addPageAnimationSetting();
    }

    private void addZoomFactorSetting() {

        CharSequence[] entries = {"1.5x", "2x", "3x", "4x", "4.5x"};
        CharSequence[] values = {"1.5f", "2.0f", "3.0f", "4.0f", "4.5f"};

        ColoredListPreference zoomFactorPreference = new ColoredListPreference(getActivity());
        zoomFactorPreference.setKey(StorageManager.ZOOM_FACTOR);
        zoomFactorPreference.setTitle("Zoom factor");
        zoomFactorPreference.setDefaultValue("3.0f");
        zoomFactorPreference.setEntries(entries);
        zoomFactorPreference.setEntryValues(values);
        addPreference(zoomFactorPreference);
    }

    protected abstract void addPageAnimationSetting();

    private void addHighQualitySetting() {
        ColoredSwitchPreference highQualityPreference = new ColoredSwitchPreference(getActivity());
        highQualityPreference.setKey(StorageManager.PAGE_QUALITY_SETTING);
        highQualityPreference.setTitle("High quality pages");
        highQualityPreference.setSummary("Note: Disabling this options might increase performance");
        highQualityPreference.setDefaultValue(false);
        addPreference(highQualityPreference);
    }

    private void addPageNumberSetting() {
        ColoredListPreference pageNumberPreference = new ColoredListPreference(getActivity());
        pageNumberPreference.setKey(StorageManager.PAGE_NUMBER_SETTING);
        pageNumberPreference.setTitle(getString(R.string.page_number_setting));
        pageNumberPreference.setDefaultValue(getString(R.string.page_number_setting_1));
        pageNumberPreference.setEntries(getResources().getTextArray(R.array.Page_number_settings));
        pageNumberPreference.setEntryValues(getResources().getTextArray(R.array.Page_number_settings));
        addPreference(pageNumberPreference);
    }

    private void addReadingBackgroundSetting() {
        ColoredListPreference readingBackgroundPreference = new ColoredListPreference(getActivity());
        readingBackgroundPreference.setKey(StorageManager.READING_BACKGROUND_COLOR);
        readingBackgroundPreference.setTitle(getString(R.string.reading_background_color_setting));
        readingBackgroundPreference.setDefaultValue(getString(R.string.dynamic));
        readingBackgroundPreference.setEntries(getResources().getTextArray(R.array.Reading_background_options));
        readingBackgroundPreference.setEntryValues(getResources().getTextArray(R.array.Reading_background_options));
        addPreference(readingBackgroundPreference);
    }

    private void addScrollOnTapSetting() {
        ColoredSwitchPreference scrollOnTapPreference = new ColoredSwitchPreference(getActivity());
        scrollOnTapPreference.setKey(StorageManager.SCROLL_BY_TAP_SETTING);
        scrollOnTapPreference.setTitle("Scroll on tap");
        scrollOnTapPreference.setSummary("Note: You can go to the next or previous page by tapping left and right. You can also long press to go in the opposite direction");
        scrollOnTapPreference.setDefaultValue(false);
        addPreference(scrollOnTapPreference);
    }

    private void addSingleTapScrollSetting()
    {
        ColoredSwitchPreference singleTapScrollPreference = new ColoredSwitchPreference(getActivity());
        singleTapScrollPreference.setKey(StorageManager.SINGLE_TAP_SCROLL);
        singleTapScrollPreference.setTitle("Single tap to scroll");
        singleTapScrollPreference.setSummary("Note: You can go in the opposite direction by a long press");
        singleTapScrollPreference.setDefaultValue(false);
        addPreference(singleTapScrollPreference);
    }

    private void addPagingOnZoomSetting() {
        ColoredSwitchPreference pagingOnZoomPreference = new ColoredSwitchPreference(getActivity());
        pagingOnZoomPreference.setKey(StorageManager.SCROLL_ON_ZOOM_SETTING);
        pagingOnZoomPreference.setTitle("Allow paging when zoomed in");
        pagingOnZoomPreference.setDefaultValue(true);
        addPreference(pagingOnZoomPreference);
    }

    private void addForcePortraitSetting() {
        ColoredSwitchPreference forcePortraitPreference = new ColoredSwitchPreference(getActivity());
        forcePortraitPreference.setKey(StorageManager.FORCE_PORTRAIT_SETTING);
        forcePortraitPreference.setTitle(getString(R.string.force_portrait_setting));
        forcePortraitPreference.setDefaultValue(false);
        addPreference(forcePortraitPreference);
    }

    private void addShowToolbarSetting() {
        ColoredSwitchPreference toolbarPreference = new ColoredSwitchPreference(getActivity());
        toolbarPreference.setKey(StorageManager.TOOLBAR_OPTION);
        toolbarPreference.setTitle(getString(R.string.show_toolbar));
        toolbarPreference.setDefaultValue(false);
        addPreference(toolbarPreference);
    }

    protected abstract void addUseVolumeKeysSetting();

    protected abstract void addReverseVolumeKeySetting();

    private void addKeepScreenOnSetting() {
        ColoredSwitchPreference keepScreenOnPreference = new ColoredSwitchPreference(getActivity());
        keepScreenOnPreference.setKey(StorageManager.KEEP_SCREEN_ON);
        keepScreenOnPreference.setTitle(getString(R.string.screen_on_setting));
        keepScreenOnPreference.setDefaultValue(true);
        addPreference(keepScreenOnPreference);
    }

    private void addRotateSetting() {
        ColoredSwitchPreference rotateLandscapePreference = new ColoredSwitchPreference(getActivity());
        rotateLandscapePreference.setKey(StorageManager.ROTATE_LANDSCAPE_PAGE);
        rotateLandscapePreference.setTitle(getString(R.string.rotate_landscape_pages_setting));
        rotateLandscapePreference.setDefaultValue(false);
        addPreference(rotateLandscapePreference);
    }

    private void addWidthAutoFitPreference() {
        ColoredSwitchPreference widthAutoFitPreference = new ColoredSwitchPreference(getActivity());
        widthAutoFitPreference.setKey(StorageManager.WIDTH_AUTO_FIT_SETTING);
        widthAutoFitPreference.setTitle(getString(R.string.width_auto_fit_setting));
        widthAutoFitPreference.setDefaultValue(true);
        addPreference(widthAutoFitPreference);
    }


}
