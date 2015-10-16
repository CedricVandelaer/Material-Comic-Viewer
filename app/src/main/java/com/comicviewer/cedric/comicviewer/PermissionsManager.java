package com.comicviewer.cedric.comicviewer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

/**
 * Created by Cédric on 16/10/2015.
 */
public class PermissionsManager {

    public static final int REQUEST_FILE_PERMISSIONS = 4;
    public static final int REQUEST_ACCOUNT_PERMISSIONS = 5;

    public static void showFilePermissionDialog(final Activity activity)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("Warning")
                .content("Material Comic Viewer requires the read/write external storage permission in order to find the comics on your device.")
                .positiveColor(StorageManager.getAppThemeColor(activity))
                .positiveText(activity.getString(R.string.accept))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onAny(MaterialDialog dialog) {
                        super.onAny(dialog);
                        requestFilePermissions(activity);
                    }
                })
                .show();
    }

    public static void checkFilePermissions(Activity activity)
    {
        if (Build.VERSION.SDK_INT>22) {
            int readCheck = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (readCheck != PackageManager.PERMISSION_GRANTED || writeCheck != PackageManager.PERMISSION_GRANTED) {
                showFilePermissionDialog(activity);
            }
        }
    }

    public static void requestFilePermissions(Activity activity)
    {
        if (Build.VERSION.SDK_INT>22) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_FILE_PERMISSIONS);
        }
    }

    public static void checkAccountPermissions(Activity activity)
    {
        if (Build.VERSION.SDK_INT>22) {
            int accountCheck = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.GET_ACCOUNTS);

            if (accountCheck != PackageManager.PERMISSION_GRANTED) {
                requestAccountPermissions(activity);
            }
        }
    }

    public static void requestAccountPermissions(Activity activity)
    {
        if (Build.VERSION.SDK_INT>22) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    REQUEST_ACCOUNT_PERMISSIONS);
        }
    }
}
