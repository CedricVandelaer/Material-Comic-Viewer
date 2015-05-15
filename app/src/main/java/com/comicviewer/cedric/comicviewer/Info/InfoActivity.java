package com.comicviewer.cedric.comicviewer.Info;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InfoActivity extends Activity {

    private Comic mComic = null;
    private TextView mTitleTextView;
    private ImageView mCoverImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if (getActionBar()!=null)
            getActionBar().hide();

        if (getIntent().getParcelableExtra("Comic")!=null)
        {
            mComic = getIntent().getParcelableExtra("Comic");
        }

        if (Build.VERSION.SDK_INT>20)
        {
            getWindow().setStatusBarColor(Utilities.darkenColor(mComic.getComicColor()));
            getWindow().setNavigationBarColor(mComic.getComicColor());
        }

        mTitleTextView = (TextView) findViewById(R.id.title_text_view);
        mCoverImageView = (ImageView) findViewById(R.id.cover_image_view);

        mTitleTextView.setText(mComic.getTitle());
        mTitleTextView.setBackgroundColor(mComic.getComicColor());
        mTitleTextView.setTextColor(mComic.getPrimaryTextColor());

        ImageLoader.getInstance().displayImage(mComic.getCoverImage(),mCoverImageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
