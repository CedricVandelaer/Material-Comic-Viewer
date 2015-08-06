package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Cédric on 26/04/2015.
 */
public class SimpleEula {

    private String EULA_PREFIX = "eula_";
    private Activity mActivity;

    public SimpleEula(Activity context) {
        mActivity = context;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public void show() {
        PackageInfo versionInfo = getPackageInfo();

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if(hasBeenShown == false){

            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;

            //Includes the updates as well so users know what changed.
            String message = mActivity.getString(R.string.updates) + "\n\n" + mActivity.getString(R.string.eula);

            new MaterialDialog.Builder(mActivity)
                    .title(title)
                    .content(message)
                    .positiveText(mActivity.getString(R.string.accept))
                    .positiveColor(StorageManager.getAppThemeColor(mActivity))
                    .negativeText(mActivity.getString(R.string.decline))
                    .negativeColor(StorageManager.getAppThemeColor(mActivity))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(eulaKey, true);
                            editor.apply();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            mActivity.finish();
                        }
                    })
                    .cancelable(false)
                    .show();

        }
    }

}