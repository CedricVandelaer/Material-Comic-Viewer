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
public abstract class AbstractLayoutSettingsFragment extends AbstractSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addBackgroundPreference();
        addCardAppearancePreference();
        addCardColorPreference();
        addScrollAnimationPreference();
        addAppThemeSettings();
        addFabColorPreference();
        addSectionAnimationPreference();
        addMultiPanePreference();

    }

    private void addSectionAnimationPreference() {
        final ColoredSwitchPreference sectionAnimationPref = new ColoredSwitchPreference(getActivity());

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
                                StorageManager.saveBooleanSetting(getActivity(), StorageManager.SECTION_ANIMATION, (boolean) newValue);
                                sectionAnimationPref.setChecked((boolean) newValue);
                                sectionAnimationPref.setOnPreferenceChangeListener(null);
                                Intent intent = new Intent(getActivity(), SplashActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                sectionAnimationPref.setChecked(!(boolean)newValue);
                            }

                        })
                        .show();
                return false;
            }
        });

        addPreference(sectionAnimationPref);
    }

    protected void addScrollAnimationPreference() {
        ColoredListPreference scrollAnimationPref = new ColoredListPreference(getActivity());
        scrollAnimationPref.setKey(StorageManager.SCROLL_ANIMATION);
        scrollAnimationPref.setTitle(getString(R.string.scroll_animation_setting));
        scrollAnimationPref.setSummary(getString(R.string.scroll_animation_setting_note));
        scrollAnimationPref.setDefaultValue(getString(R.string.scroll_animation_setting_1));
        scrollAnimationPref.setEntries(getResources().getTextArray(R.array.Scroll_animations));
        scrollAnimationPref.setEntryValues(getResources().getTextArray(R.array.Scroll_animations_values));
        addPreference(scrollAnimationPref);
    }

    protected void addCardColorPreference() {
        ColoredListPreference cardColorPref = new ColoredListPreference(getActivity());
        cardColorPref.setKey(StorageManager.CARD_COLOR);
        cardColorPref.setTitle(getString(R.string.card_color_setting));
        cardColorPref.setDefaultValue(getString(R.string.card_color_setting_1));
        cardColorPref.setEntries(getResources().getTextArray(R.array.Card_colors));
        cardColorPref.setEntryValues(getResources().getTextArray(R.array.Card_colors_values));
        addPreference(cardColorPref);
    }

    protected void addCardAppearancePreference() {
        ColoredListPreference cardSizePref = new ColoredListPreference(getActivity());
        cardSizePref.setKey(StorageManager.CARD_SIZE);
        cardSizePref.setTitle(getString(R.string.card_appearance_setting));
        cardSizePref.setDefaultValue(getString(R.string.card_size_setting_2));
        cardSizePref.setEntries(getResources().getTextArray(R.array.Card_sizes));
        cardSizePref.setEntryValues(getResources().getTextArray(R.array.Card_sizes_values));
        addPreference(cardSizePref);
    }

    protected void addMultiPanePreference() {
        final ColoredSwitchPreference multiPane = new ColoredSwitchPreference(getActivity());

        multiPane.setKey(StorageManager.MULTI_PANE);
        multiPane.setTitle("Enable multi-pane layout on tablets");
        multiPane.setDefaultValue(true);

        addPreference(multiPane);
    }

    protected void addBackgroundPreference()
    {
        ColoredListPreference backgroundPref = new ColoredListPreference(getActivity());
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
                setBackground();
                ((NewDrawerActivity)getActivity()).setDrawerColor();
                return true;
            }
        });
        addPreference(backgroundPref);
    }

    protected abstract void addFabColorPreference();

    protected abstract void addAppThemeSettings();


}
