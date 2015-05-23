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
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.Model.OneDriveObject;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OneDriveActivity extends Activity implements LiveAuthListener{

    private CloudService mCloudService;

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OneDriveAdapter mAdapter;
    private Handler mHandler;

    private LiveAuthClient mOneDriveAuth;
    private LiveConnectClient mOneDriveClient;

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

        getActionBar().setTitle("Microsoft OneDrive");

        getActionBar().setBackgroundDrawable(new ColorDrawable(PreferenceSetter.getAppThemeColor(this)));

        if (Build.VERSION.SDK_INT>20)
            getWindow().setStatusBarColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(this)));

        NavigationManager.getInstance().resetCloudStackWithString("me/skydrive/files");

        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mRecyclerView = (RecyclerView) findViewById(R.id.cloud_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new OneDriveAdapter(this, mCloudService);
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

        mSwipeRefreshLayout.setRefreshing(true);

        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dropbox_app_key), getResources().getString(R.string.dropbox_app_secret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys, mCloudService.getToken());

        mOneDriveAuth = new LiveAuthClient(this, getString(R.string.onedrive_id));
        Object userState = new Object();
        Iterable<String> scopes = Arrays.asList("wl.signin", "wl.offline_access", "wl.basic", "wl.skydrive", "wl.emails");

        mOneDriveAuth.initialize(scopes, this, userState, mCloudService.getToken());

    }

    public void refresh()
    {
        mAdapter.clear();
        readFolder();
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

        if (mOneDriveClient!=null)
        {
            readFolder();
        }
        else
        {
            mErrorTextView.setText("An error occured during authentication...");
        }
    }

    public void readFolder() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mOneDriveClient.getAsync(NavigationManager.getInstance().getPathFromCloudStack(), new LiveOperationListener() {
            public void onComplete(LiveOperation operation) {
                JSONObject result = operation.getResult();
                Log.d("Result", result.toString());
                try
                {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    JSONArray data = result.getJSONArray("data");

                    boolean fileFound = false;

                    for (int i=0;i<data.length();i++)
                    {
                        JSONObject folder = data.getJSONObject(i);
                        OneDriveObject newFile = new OneDriveObject(folder.getString("name"), folder.getString("id"));

                        if (newFile.getType()== ObjectType.FOLDER || Utilities.checkExtension(newFile.getName())) {
                            mAdapter.addOneDriveEntry(newFile);
                            fileFound = true;
                        }
                    }

                    if (!fileFound)
                    {
                        mErrorTextView.setVisibility(View.VISIBLE);
                        mErrorTextView.setText("There were no compatible files found...");
                    }
                    else
                    {
                        mErrorTextView.setVisibility(View.GONE);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            public void onError(LiveOperationException exception, LiveOperation operation) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {

        if(status == LiveStatus.CONNECTED)
        {
            mOneDriveClient = new LiveConnectClient(session);
            final String token = session.getRefreshToken();
            CloudService cloudService = new CloudService(mCloudService.getName(), token, mCloudService.getUsername(), mCloudService.getEmail());
            PreferenceSetter.saveCloudService(OneDriveActivity.this, cloudService);
            readFolder();
        }
        else
        {
            mOneDriveClient = null;
        }
    }

    @Override
    public void onAuthError(LiveAuthException exception, Object userState) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }



    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(OneDriveActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            ActivityManager.TaskDescription tdscr = null;

            if (Build.VERSION.SDK_INT>20) {
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(getString(R.string.app_name),
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_recents, size),
                            PreferenceSetter.getAppThemeColor(OneDriveActivity.this));
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
