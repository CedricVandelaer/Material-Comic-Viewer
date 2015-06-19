package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxListItems;
import com.box.androidsdk.content.models.BoxSession;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.microsoft.live.LiveAuthClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.Arrays;

public class BoxActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private CloudService mCloudService;
    private Handler mHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mErrorTextView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        new SetTaskDescriptionTask().execute();

        mCloudService = (CloudService) getIntent().getSerializableExtra("CloudService");

        mHandler = new Handler();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mErrorTextView = (TextView) findViewById(R.id.error_text_view);


        if (PreferenceSetter.getBackgroundColorPreference(this)==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));

        mErrorTextView.setVisibility(View.GONE);

        getActionBar().setTitle(getString(R.string.cloud_storage_4));

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
        //mAdapter = new OneDriveAdapter(this, mCloudService);
        //mRecyclerView.setAdapter(mAdapter);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        //in pixels
        int vSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);
        int hSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(vSpace, hSpace));

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        BoxConfig.CLIENT_ID = getString(R.string.box_client_id);
        BoxConfig.CLIENT_SECRET = getString(R.string.box_client_secret);
        BoxConfig.REDIRECT_URL = getString(R.string.box_redirect_url);

        final BoxSession session = new BoxSession(this, mCloudService.getEmail());
        session.getAuthInfo().setRefreshToken(mCloudService.getToken());

        session.setSessionAuthListener(new BoxAuthentication.AuthListener() {
            @Override
            public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo boxAuthenticationInfo) {

            }

            @Override
            public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo boxAuthenticationInfo) {
                String id = boxAuthenticationInfo.getClientId();
                String name = boxAuthenticationInfo.getUser().getName();
                String token = boxAuthenticationInfo.refreshToken();
                mCloudService = new CloudService(getString(R.string.cloud_storage_4),
                        token, name, id);
                PreferenceSetter.saveCloudService(BoxActivity.this, mCloudService);

                BoxApiFolder boxApiFolder = new BoxApiFolder(session);
                try {
                    BoxListItems boxListItems = boxApiFolder.getItemsRequest("0").send();
                    for (int i=0;i<boxListItems.size();i++)
                    {
                        Log.d("Box", "File: "+boxListItems.get(i).getName());
                    }
                    mErrorTextView.setVisibility(View.GONE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(getString(R.string.error));
                }
            }

            @Override
            public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo boxAuthenticationInfo, Exception e) {
                e.printStackTrace();
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.error_while_authenticating));
            }

            @Override
            public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo boxAuthenticationInfo, Exception e) {

            }
        });
        session.authenticate();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceSetter.setBackgroundColorPreference(this);
    }

    @Override
    public void onRefresh() {

    }

    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(BoxActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            ActivityManager.TaskDescription tdscr = null;

            if (Build.VERSION.SDK_INT>20) {
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(getString(R.string.app_name),
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_recents, size),
                            PreferenceSetter.getAppThemeColor(BoxActivity.this));
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
