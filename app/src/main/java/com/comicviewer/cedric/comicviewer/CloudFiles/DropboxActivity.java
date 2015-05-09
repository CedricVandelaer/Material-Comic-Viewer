package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DropboxActivity extends Activity {

    private CloudService mCloudService;

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DropboxAdapter mAdapter;
    private Handler mHandler;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    final static private String APP_KEY = "id9ssazcpa41gys";
    final static private String APP_SECRET = "yj0gk3nipr6ti4u";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dropbox);

        new SetTaskDescriptionTask().execute();

        mCloudService = (CloudService) getIntent().getSerializableExtra("CloudService");

        mHandler = new Handler();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mErrorTextView = (TextView) findViewById(R.id.error_text_view);


        if (PreferenceSetter.getBackgroundColorPreference(this)==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));

        mErrorTextView.setVisibility(View.GONE);

        getActionBar().setTitle("Dropbox");

        getActionBar().setBackgroundDrawable(new ColorDrawable(PreferenceSetter.getAppThemeColor(this)));

        if (Build.VERSION.SDK_INT>20)
            getWindow().setStatusBarColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(this)));

        NavigationManager.getInstance().resetCloudStack();

        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mRecyclerView = (RecyclerView) findViewById(R.id.cloud_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DropboxAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        //in pixels
        int vSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, outMetrics);
        int hSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, outMetrics);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(vSpace, hSpace));

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, mCloudService.getToken());
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        if (mDBApi.getSession().authenticationSuccessful()) {
            String token = mDBApi.getSession().finishAuthentication();
            mCloudService.setToken(token);
            PreferenceSetter.saveCloudService(this, mCloudService);
        }

        if (mDBApi.getSession().isLinked()) {
            new RetrieveFilesTask().execute();
        }
        else
        {
            mDBApi.getSession().startOAuth2Authentication(this);
        }

    }

    public void refresh()
    {
        mAdapter.clear();
        new RetrieveFilesTask().execute();
    }

    @Override
    public void onBackPressed()
    {
        NavigationManager.getInstance().popFromCloudStack();
        if (NavigationManager.getInstance().cloudStackEmpty())
            finish();
        else
            refresh();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceSetter.setBackgroundColorPreference(this);

        if (!mDBApi.getSession().authenticationSuccessful()) {
            new AddDropboxUserInfoTask().execute();
        }
    }

    private class RetrieveFilesTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });

            DropboxAPI.Entry existingEntry = null;

            try {
                existingEntry = mDBApi.metadata(NavigationManager.getInstance().getPathFromCloudStack(), 1000, null, true, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (existingEntry!=null) {
                Log.d("RetrieveFileTask", existingEntry.path);

                List<DropboxAPI.Entry> entryList = existingEntry.contents;

                Collections.sort(entryList, new Comparator<DropboxAPI.Entry>() {
                    @Override
                    public int compare(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
                        return lhs.fileName().compareToIgnoreCase(rhs.fileName());
                    }
                });

                for (int i=0;i<entryList.size();i++) {

                    if (entryList.get(i).isDir)
                        mAdapter.addDropBoxEntry(entryList.get(i));
                }

                for (int i=0;i<entryList.size();i++) {

                    if (Utilities.checkExtension(entryList.get(i).fileName()))
                        mAdapter.addDropBoxEntry(entryList.get(i));
                }


            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mAdapter.getItemCount()==0)
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mErrorTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
            else
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mErrorTextView.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    private class AddDropboxUserInfoTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (mDBApi != null &&
                    mDBApi.getSession()!=null &&
                    mDBApi.getSession().authenticationSuccessful() &&
                    mDBApi.getSession().isLinked()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();

                    String userName = "User";
                    String email = "Email";
                    String service = "Dropbox";

                    try {
                        userName = mDBApi.accountInfo().displayName;
                        email = mDBApi.accountInfo().email;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                    CloudService cloudService = new CloudService(service, accessToken, userName, email);

                    PreferenceSetter.removeCloudService(DropboxActivity.this, cloudService.getEmail(), cloudService.getName());

                    PreferenceSetter.saveCloudService(DropboxActivity.this, cloudService);


                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }
            }
            return null;
        }
    }

    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(DropboxActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            ActivityManager.TaskDescription tdscr = null;

            if (Build.VERSION.SDK_INT>20) {
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(getString(R.string.app_name),
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_recents, size),
                            PreferenceSetter.getAppThemeColor(DropboxActivity.this));
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
