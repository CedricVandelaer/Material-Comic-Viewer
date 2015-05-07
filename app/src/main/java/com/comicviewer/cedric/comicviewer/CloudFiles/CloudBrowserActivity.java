package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.util.Stack;

public class CloudBrowserActivity extends Activity {

    private CloudService mCloudService;
    private Stack<String> mNavigationStack;

    private RecyclerView mRecyclerView;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    final static private String APP_KEY = "id9ssazcpa41gys";
    final static private String APP_SECRET = "yj0gk3nipr6ti4u";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceSetter.setBackgroundColorPreference(this);

        mCloudService = (CloudService) getIntent().getSerializableExtra("CloudService");

        mNavigationStack = new Stack<>();
        mNavigationStack.push("/");

        setContentView(R.layout.activity_cloud_browser);

        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mRecyclerView = (RecyclerView) findViewById(R.id.cloud_file_list);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, mCloudService.getToken());
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

    }

    private class RetrieveFilesTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                DropboxAPI.Entry existingEntry = mDBApi.metadata(mNavigationStack.peek(), 1000, null, false, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


}
