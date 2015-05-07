package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.R;

public class CloudBrowserActivity extends Activity {

    private CloudService mCloudService;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCloudService = (CloudService) getIntent().getSerializableExtra("CloudService");

        setContentView(R.layout.activity_cloud_browser);

        mTextView = (TextView) findViewById(R.id.info_text_view);

        mTextView.setText(mCloudService.getName()+"\n"+mCloudService.getUsername()+"\n"+mCloudService.getEmail()+"\n"+mCloudService.getToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


}
