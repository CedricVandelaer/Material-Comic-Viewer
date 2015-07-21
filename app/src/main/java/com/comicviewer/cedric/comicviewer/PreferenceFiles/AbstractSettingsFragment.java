package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;

/**
 * Created by Cédric on 20/07/2015.
 */
public class AbstractSettingsFragment extends PreferenceFragment implements BaseNavigationInterface {

    private ArrayList<Preference> mPreferences;
    private int mBackgroundColor;


    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        addPreferencesFromResource(R.xml.basic_preferences);
        mPreferences = new ArrayList<>();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackground();
    }

    protected void setBackground()
    {
        mBackgroundColor = StorageManager.getBackgroundColorPreference(getActivity());
        getActivity().getWindow().getDecorView().setBackgroundColor(mBackgroundColor);
        if (Build.VERSION.SDK_INT>20) {
            if (mBackgroundColor == getResources().getColor(R.color.WhiteBG))
                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.Black));
            else
                getActivity().getWindow().setNavigationBarColor(mBackgroundColor);
        }
        setPreferenceColors();
    }

    protected void addPreference(Preference preference)
    {
        mPreferences.add(preference);
        getPreferenceScreen().addPreference(preference);
    }

    protected void setPreferenceColors()
    {
        int titleColor = getResources().getColor(R.color.White);
        int summaryColor = getResources().getColor(R.color.GreyLight);
        if (mBackgroundColor == getResources().getColor(R.color.WhiteBG)) {
            titleColor = getResources().getColor(R.color.BlueGreyVeryDark);
            summaryColor = getResources().getColor(R.color.BlueGreyDark);
        }
        for (Preference preference:mPreferences) {

            if (preference instanceof ColoredPreference) {
                ((ColoredPreference) preference).setTitleColor(titleColor);
                ((ColoredPreference) preference).setSummaryColor(summaryColor);
                ((ColoredPreference) preference).setIconColor(titleColor);
                ((ColoredPreference) preference).updateColors();
            }
        }
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
                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                        startActivity(browse);
                    }
                }).show();

    }
}
