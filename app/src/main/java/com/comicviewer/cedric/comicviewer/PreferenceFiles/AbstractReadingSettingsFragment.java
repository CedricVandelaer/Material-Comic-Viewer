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
public abstract class AbstractReadingSettingsFragment extends PreferenceFragment implements BaseNavigationInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBackground();

        addPreferencesFromResource(R.xml.basic_preferences);

        addWidthAutoFitPreference();
        addRotateSetting();
        addKeepScreenOnSetting();
        addHighQualitySetting();
        addUseVolumeKeysSetting();
        addShowToolbarSetting();
        addForcePortraitSetting();
        addPagingOnZoomSetting();
        addScrollOnTapSetting();
        addReadingBackgroundSetting();
        addPageNumberSetting();
        addPageAnimationSetting();
    }

    protected abstract void addPageAnimationSetting();

    private void addHighQualitySetting() {
        CheckBoxPreference highQualityPreference = new CheckBoxPreference(getActivity());
        highQualityPreference.setKey(StorageManager.PAGE_QUALITY_SETTING);
        highQualityPreference.setTitle("High quality pages");
        highQualityPreference.setSummary("Note: Disabling this options might increase performance");
        highQualityPreference.setDefaultValue(false);
        getPreferenceScreen().addPreference(highQualityPreference);
    }

    private void addPageNumberSetting() {
        ListPreference pageNumberPreference = new ListPreference(getActivity());
        pageNumberPreference.setKey(StorageManager.PAGE_NUMBER_SETTING);
        pageNumberPreference.setTitle(getString(R.string.page_number_setting));
        pageNumberPreference.setDefaultValue(getString(R.string.page_number_setting_1));
        pageNumberPreference.setEntries(getResources().getTextArray(R.array.Page_number_settings));
        pageNumberPreference.setEntryValues(getResources().getTextArray(R.array.Page_number_settings));
        getPreferenceScreen().addPreference(pageNumberPreference);
    }

    private void addReadingBackgroundSetting() {
        ListPreference readingBackgroundPreference = new ListPreference(getActivity());
        readingBackgroundPreference.setKey(StorageManager.READING_BACKGROUND_COLOR);
        readingBackgroundPreference.setTitle(getString(R.string.reading_background_color_setting));
        readingBackgroundPreference.setDefaultValue(getString(R.string.dynamic));
        readingBackgroundPreference.setEntries(getResources().getTextArray(R.array.Reading_background_options));
        readingBackgroundPreference.setEntryValues(getResources().getTextArray(R.array.Reading_background_options));
        getPreferenceScreen().addPreference(readingBackgroundPreference);
    }

    private void addScrollOnTapSetting() {
        CheckBoxPreference scrollOnTapPreference = new CheckBoxPreference(getActivity());
        scrollOnTapPreference.setKey(StorageManager.SCROLL_BY_TAP_SETTING);
        scrollOnTapPreference.setTitle("Scroll on tap");
        scrollOnTapPreference.setSummary("Note: You can go to the next or previous page by tapping left and right. You can also long press to go in the opposite direction");
        scrollOnTapPreference.setDefaultValue(false);
        getPreferenceScreen().addPreference(scrollOnTapPreference);
    }

    private void addPagingOnZoomSetting() {
        CheckBoxPreference pagingOnZoomPreference = new CheckBoxPreference(getActivity());
        pagingOnZoomPreference.setKey(StorageManager.SCROLL_ON_ZOOM_SETTING);
        pagingOnZoomPreference.setTitle("Allow paging when zoomed in");
        pagingOnZoomPreference.setDefaultValue(true);
        getPreferenceScreen().addPreference(pagingOnZoomPreference);
    }

    private void addForcePortraitSetting() {
        CheckBoxPreference forcePortraitPreference = new CheckBoxPreference(getActivity());
        forcePortraitPreference.setKey(StorageManager.FORCE_PORTRAIT_SETTING);
        forcePortraitPreference.setTitle(getString(R.string.force_portrait_setting));
        forcePortraitPreference.setDefaultValue(false);
        getPreferenceScreen().addPreference(forcePortraitPreference);
    }

    private void addShowToolbarSetting() {
        CheckBoxPreference toolbarPreference = new CheckBoxPreference(getActivity());
        toolbarPreference.setKey(StorageManager.TOOLBAR_OPTION);
        toolbarPreference.setTitle(getString(R.string.show_toolbar));
        toolbarPreference.setDefaultValue(false);
        getPreferenceScreen().addPreference(toolbarPreference);
    }

    protected abstract void addUseVolumeKeysSetting();

    private void addKeepScreenOnSetting() {
        CheckBoxPreference keepScreenOnPreference = new CheckBoxPreference(getActivity());
        keepScreenOnPreference.setKey(StorageManager.KEEP_SCREEN_ON);
        keepScreenOnPreference.setTitle(getString(R.string.screen_on_setting));
        keepScreenOnPreference.setDefaultValue(true);
        getPreferenceScreen().addPreference(keepScreenOnPreference);
    }

    private void addRotateSetting() {
        CheckBoxPreference rotateLandscapePreference = new CheckBoxPreference(getActivity());
        rotateLandscapePreference.setKey(StorageManager.ROTATE_LANDSCAPE_PAGE);
        rotateLandscapePreference.setTitle(getString(R.string.rotate_landscape_pages_setting));
        rotateLandscapePreference.setDefaultValue(false);
        getPreferenceScreen().addPreference(rotateLandscapePreference);
    }

    private void addWidthAutoFitPreference() {
        CheckBoxPreference widthAutoFitPreference = new CheckBoxPreference(getActivity());
        widthAutoFitPreference.setKey(StorageManager.WIDTH_AUTO_FIT_SETTING);
        widthAutoFitPreference.setTitle(getString(R.string.width_auto_fit_setting));
        widthAutoFitPreference.setDefaultValue(true);
        getPreferenceScreen().addPreference(widthAutoFitPreference);
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

    private void setBackground()
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
