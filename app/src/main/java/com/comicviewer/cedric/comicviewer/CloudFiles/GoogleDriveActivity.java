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

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class GoogleDriveActivity extends Activity {

    private TextView mErrorTextView;
    private CloudService mCloudService;
    private Handler mHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private GoogleDriveAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive);

        new SetTaskDescriptionTask().execute();

        mErrorTextView = (TextView) findViewById(R.id.error_text_view);

        mCloudService = (CloudService) getIntent().getSerializableExtra("CloudService");

        mHandler = new Handler();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        //mErrorTextView.setVisibility(View.GONE);
        mErrorTextView.setText("Service: " + mCloudService.getName() + "\ntoken: " + mCloudService.getToken() + "\nname: " + mCloudService.getUsername() + "\nemail: " + mCloudService.getEmail());

        PreferenceSetter.setBackgroundColorPreference(this);
        if (PreferenceSetter.getBackgroundColorPreference(this)==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));


        getActionBar().setTitle(getString(R.string.cloud_storage_2));

        getActionBar().setBackgroundDrawable(new ColorDrawable(PreferenceSetter.getAppThemeColor(this)));

        if (Build.VERSION.SDK_INT>20)
            getWindow().setStatusBarColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(this)));

        NavigationManager.getInstance().resetCloudStack();

        mRecyclerView = (RecyclerView) findViewById(R.id.cloud_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GoogleDriveAdapter();
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
