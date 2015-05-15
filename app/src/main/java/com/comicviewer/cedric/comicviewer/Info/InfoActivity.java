package com.comicviewer.cedric.comicviewer.Info;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class InfoActivity extends Activity {

    private Comic mComic = null;
    private TextView mTitleTextView;
    private ImageView mCoverImageView;
    private TextView mIssueNumberTextView;
    private TextView mYearTextView;
    private TextView mFilenameTextView;
    private TextView mFileSizeTextView;


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
        mIssueNumberTextView = (TextView) findViewById(R.id.issue_number_text_view);
        mYearTextView = (TextView) findViewById(R.id.year_text_view);
        mFilenameTextView = (TextView) findViewById(R.id.filename_text_view);
        mFileSizeTextView = (TextView) findViewById(R.id.filesize_text_view);

        mTitleTextView.setText(mComic.getTitle());
        mTitleTextView.setBackgroundColor(mComic.getComicColor());
        mTitleTextView.setTextColor(mComic.getPrimaryTextColor());

        if (mComic.getCoverImage()!=null)
            ImageLoader.getInstance().displayImage(mComic.getCoverImage(),mCoverImageView);

        if (mComic.getIssueNumber()!=-1)
        {
            mIssueNumberTextView.setText("Issue number: "+mComic.getIssueNumber());
        }
        else
        {
            mIssueNumberTextView.setVisibility(View.GONE);
        }

        if (mComic.getYear()!=-1)
        {
            mYearTextView.setText("Year: "+mComic.getYear());
        }
        else
        {
            mYearTextView.setVisibility(View.GONE);
        }

        File archiveFile = new File(mComic.getFilePath()+"/"+mComic.getFileName());

        mFilenameTextView.setText("Filename:\n"+archiveFile.getName());
        mFileSizeTextView.setText("File size: "+archiveFile.length()/(1024*1024)+" mb");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
