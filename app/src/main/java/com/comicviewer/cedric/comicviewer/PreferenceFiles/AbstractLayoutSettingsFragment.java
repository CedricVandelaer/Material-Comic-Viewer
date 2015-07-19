package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SplashActivity;
import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * Created by Cédric on 19/07/2015.
 */
public abstract class AbstractLayoutSettingsFragment extends PreferenceFragment implements BaseNavigationInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBackground();

        addPreferencesFromResource(R.xml.basic_preferences);
        addBackgroundPreference();
        addCardAppearancePreference();
        addCardColorPreference();
        addScrollAnimationPreference();
        addAppThemeSettings();
        addFabColorPreference();
        addSectionAnimationPreference();
        addMultiPanePreference();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackground();
    }

    private void addSectionAnimationPreference() {
        final CheckBoxPreference sectionAnimationPref = new CheckBoxPreference(getActivity());

        sectionAnimationPref.setKey(StorageManager.SECTION_ANIMATION);
        sectionAnimationPref.setTitle("Enable section animations");
        sectionAnimationPref.setDefaultValue(true);

        sectionAnimationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(getString(R.string.warning))
                        .content(getString(R.string.restart_dialog))
                        .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                        .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                        .positiveText(getString(R.string.accept))
                        .negativeText(getString(R.string.cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                StorageManager.saveBooleanSetting(getActivity(), StorageManager.SECTION_ANIMATION,(boolean)newValue);
                                sectionAnimationPref.setOnPreferenceChangeListener(null);
                                Intent intent = new Intent(getActivity(), SplashActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .show();

                return false;
            }
        });

        getPreferenceScreen().addPreference(sectionAnimationPref);
    }

    protected void addScrollAnimationPreference() {
        ListPreference scrollAnimationPref = new ListPreference(getActivity());
        scrollAnimationPref.setKey(StorageManager.SCROLL_ANIMATION);
        scrollAnimationPref.setTitle(getString(R.string.scroll_animation_setting));
        scrollAnimationPref.setSummary(getString(R.string.scroll_animation_setting_note));
        scrollAnimationPref.setDefaultValue(getString(R.string.scroll_animation_setting_1));
        scrollAnimationPref.setEntries(getResources().getTextArray(R.array.Scroll_animations));
        scrollAnimationPref.setEntryValues(getResources().getTextArray(R.array.Scroll_animations_values));
        getPreferenceScreen().addPreference(scrollAnimationPref);
    }

    protected void addCardColorPreference() {
        ListPreference cardColorPref = new ListPreference(getActivity());
        cardColorPref.setKey(StorageManager.CARD_COLOR);
        cardColorPref.setTitle(getString(R.string.card_color_setting));
        cardColorPref.setDefaultValue(getString(R.string.card_color_setting_1));
        cardColorPref.setEntries(getResources().getTextArray(R.array.Card_colors));
        cardColorPref.setEntryValues(getResources().getTextArray(R.array.Card_colors_values));
        getPreferenceScreen().addPreference(cardColorPref);
    }

    protected void addCardAppearancePreference() {
        ListPreference cardSizePref = new ListPreference(getActivity());
        cardSizePref.setKey(StorageManager.CARD_SIZE);
        cardSizePref.setTitle(getString(R.string.card_appearance_setting));
        cardSizePref.setDefaultValue(getString(R.string.card_size_setting_2));
        cardSizePref.setEntries(getResources().getTextArray(R.array.Card_sizes));
        cardSizePref.setEntryValues(getResources().getTextArray(R.array.Card_sizes_values));
        getPreferenceScreen().addPreference(cardSizePref);
    }

    protected void addMultiPanePreference() {
        final CheckBoxPreference multiPane = new CheckBoxPreference(getActivity());

        multiPane.setKey(StorageManager.MULTI_PANE);
        multiPane.setTitle("Enable multi-pane layout on tablets");
        multiPane.setDefaultValue(true);

        getPreferenceScreen().addPreference(multiPane);
    }

    protected void addBackgroundPreference()
    {
        ListPreference backgroundPref = new ListPreference(getActivity());
        backgroundPref.setKey(StorageManager.BACKGROUND_COLOR);
        backgroundPref.setTitle(getString(R.string.background_color_setting));
        backgroundPref.setDefaultValue(getString(R.string.backgroundcolor_setting3));
        backgroundPref.setEntries(getResources().getTextArray(R.array.Background_colors));
        backgroundPref.setEntryValues(getResources().getTextArray(R.array.Background_colors_values));
        backgroundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String color = (String) newValue;
                StorageManager.setBackgroundColorPreference(getActivity(), color);
                ((NewDrawerActivity)getActivity()).setDrawerColor();
                return true;
            }
        });
        getPreferenceScreen().addPreference(backgroundPref);
    }

    protected abstract void addFabColorPreference();

    protected abstract void addAppThemeSettings();


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
