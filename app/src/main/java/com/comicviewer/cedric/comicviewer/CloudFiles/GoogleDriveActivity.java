package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.HttpUtilities;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * Created by CV on 7/06/2015.
 * pick a google drive comic
 */
public class GoogleDriveActivity extends Activity{

    private CloudService mCloudService;
    private TextView mErrorTextView;

    private final static String DRIVE_API_SCOPE_FILES = "https://www.googleapis.com/auth/drive.readonly";
    private final static String DRIVE_API_SCOPE_METADATA = "https://www.googleapis.com/auth/drive.metadata.readonly";
    private final static String SCOPE_PROFILE_INFO = "https://www.googleapis.com/auth/userinfo.profile";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_drive);

        mErrorTextView = (TextView) findViewById(R.id.error_text_view);

        new SetTaskDescriptionTask().execute();

        mCloudService = (CloudService) getIntent().getSerializableExtra("CloudService");

        getActionBar().setTitle(getString(R.string.cloud_storage_2));
        getActionBar().setBackgroundDrawable(new ColorDrawable(PreferenceSetter.getAppThemeColor(this)));
        if (Build.VERSION.SDK_INT>20)
            getWindow().setStatusBarColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(this)));
        PreferenceSetter.setBackgroundColorPreference(this);

        NavigationManager.getInstance().resetCloudStack();

        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mErrorTextView.setVisibility(View.GONE);


        if (HttpUtilities.isConnected(this))
        {
            try {
                new GetDriveFilesTask().execute("https://www.googleapis.com/drive/v2/files"+
                "?q=%22%27root%27%20in%20parents%20and%20trashed%20=%20false%22");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText("No internet connection...");
        }
    }


    private class GetDriveFilesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String token= GoogleAuthUtil.getToken(GoogleDriveActivity.this, mCloudService.getEmail(),
                        "oauth2:" + DRIVE_API_SCOPE_FILES + " " + DRIVE_API_SCOPE_METADATA + " " + SCOPE_PROFILE_INFO);
                mCloudService.setToken(token);
                PreferenceSetter.saveCloudService(GoogleDriveActivity.this, mCloudService);
                return HttpUtilities.GET(urls[0]+"&access_token="+token);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Authentication error";
            }

        }

        @Override
        protected void onPostExecute(String result)
        {
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(result);
        }

    }

    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(GoogleDriveActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            ActivityManager.TaskDescription tdscr = null;

            if (Build.VERSION.SDK_INT>20) {
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(getString(R.string.app_name),
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_recents, size),
                            PreferenceSetter.getAppThemeColor(GoogleDriveActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tdscr != null)
                setTaskDescription(tdscr);


            return null;
        }
    }

}
