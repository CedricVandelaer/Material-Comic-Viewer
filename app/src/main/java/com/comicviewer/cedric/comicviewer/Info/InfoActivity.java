package com.comicviewer.cedric.comicviewer.Info;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.gc.materialdesign.views.ButtonFlat;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class InfoActivity extends Activity {

    private Comic mComic = null;
    private TextView mTitleTextView;
    private ImageView mCoverImageView;
    private TextView mIssueNumberTextView;
    private TextView mYearTextView;
    private TextView mFilenameTextView;
    private TextView mFileSizeTextView;
    private TextView mPagesTextView;

    private TextView mDescriptionTextView;
    private TextView mWriterTextView;
    private TextView mPencillerTextView;
    private TextView mInkerTextView;
    private TextView mColoristTextView;
    private TextView mLettererTextView;
    private TextView mEditorTextView;
    private TextView mCoverArtistTextView;
    private TextView mStoryArcsTextView;
    private TextView mCharactersTextView;
    private TextView mAdditionalInfoTextView;

    private ButtonFlat mEditButton;
    private RelativeLayout mEditButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if (getActionBar()!=null)
            getActionBar().hide();

        StorageManager.setBackgroundColorPreference(this);

        if (getIntent().getParcelableExtra("Comic")!=null)
        {
            mComic = getIntent().getParcelableExtra("Comic");
        }

        if (Build.VERSION.SDK_INT>20)
        {
            getWindow().setStatusBarColor(Utilities.darkenColor(mComic.getComicColor()));
            getWindow().setNavigationBarColor(mComic.getComicColor());
        }

        initIDs();

        setTitleTextView();
        setComicCover();
        setIssueNumber();
        setYear();
        setPageCount();
        setFileInfo();
        setMetadata();
        initEditButton();

        if (StorageManager.hasWhiteBackgroundSet(this))
            setTextViewTextColors(getResources().getColor(R.color.BlueGreyDark));
        else
            setTextViewTextColors(getResources().getColor(R.color.White));
    }

    private void setMetadata() {
        if (mComic.getDescription()!=null) {
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText("Description: " + mComic.getDescription());
        }
        else
        {
            mDescriptionTextView.setVisibility(View.GONE);
        }
        if (mComic.getWriter()!=null) {
            mWriterTextView.setVisibility(View.VISIBLE);
            mWriterTextView.setText("Writer: " + mComic.getWriter());
        }
        else {
            mWriterTextView.setVisibility(View.GONE);
        }

        if (mComic.getPenciller()!=null) {
            mPencillerTextView.setVisibility(View.VISIBLE);
            mPencillerTextView.setText("Penciller: " + mComic.getPenciller());
        }
        else
        {
            mPencillerTextView.setVisibility(View.GONE);
        }
        if (mComic.getInker()!=null) {
            mInkerTextView.setVisibility(View.VISIBLE);
            mInkerTextView.setText("Inker: " + mComic.getInker());
        }
        else
        {
            mInkerTextView.setVisibility(View.GONE);
        }
        if (mComic.getColorist()!=null) {
            mColoristTextView.setVisibility(View.VISIBLE);
            mColoristTextView.setText("Colorist: " + mComic.getColorist());
        }
        else
        {
            mColoristTextView.setVisibility(View.GONE);
        }

        if (mComic.getLetterer()!=null) {
            mLettererTextView.setVisibility(View.VISIBLE);
            mLettererTextView.setText("Letterer: " + mComic.getLetterer());
        }
        else
        {
            mLettererTextView.setVisibility(View.GONE);
        }
        if (mComic.getEditor()!=null) {
            mEditorTextView.setVisibility(View.VISIBLE);
            mEditorTextView.setText("Editor: " + mComic.getEditor());
        }
        else
        {
            mEditorTextView.setVisibility(View.GONE);
        }
        if (mComic.getCoverArtist()!=null) {
            mCoverArtistTextView.setVisibility(View.VISIBLE);
            mCoverArtistTextView.setText("Cover artist: " + mComic.getCoverArtist());
        }
        else
        {
            mCoverArtistTextView.setVisibility(View.GONE);
        }
        if (mComic.getStoryArcs()!=null)
        {
            mStoryArcsTextView.setVisibility(View.VISIBLE);
            String text = "Story arcs: ";
            for (String arc:mComic.getStoryArcs())
                text+= arc;
            mStoryArcsTextView.setText(text);
        }
        else
        {
            mStoryArcsTextView.setVisibility(View.GONE);
        }
        if (mComic.getCharacters()!=null)
        {
            mCharactersTextView.setVisibility(View.VISIBLE);
            String text = "Characters: ";
            for (String character:mComic.getCharacters())
                text+= character;
            mCharactersTextView.setText(text);
        }
        else
        {
            mCharactersTextView.setVisibility(View.GONE);
        }
        if (mComic.getAdditionalInfo()!=null) {
            mAdditionalInfoTextView.setVisibility(View.VISIBLE);
            mAdditionalInfoTextView.setText("Additional info: " + mComic.getAdditionalInfo());
        }
        else
        {
            mAdditionalInfoTextView.setVisibility(View.GONE);
        }
    }

    private void initEditButton() {

        mEditButtonLayout.setBackgroundColor(mComic.getComicColor());
        mEditButton.setBackgroundColor(mComic.getTextColor());
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence[] editOptions = {mComic.getEditedTitle(), ""+mComic.getEditedIssueNumber(), ""+mComic.getEditedYear()};

                if (mComic.getEditedIssueNumber()==-1)
                    editOptions[1] = getString(R.string.issue_number);
                if (mComic.getEditedYear() == -1)
                    editOptions[2] = getString(R.string.year);

                new MaterialDialog.Builder(InfoActivity.this)
                        .title("Edit")
                        .titleGravity(GravityEnum.CENTER)
                        .items(editOptions)
                        .itemsGravity(GravityEnum.CENTER)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                            }
                        })
                        .negativeColor(StorageManager.getAppThemeColor(InfoActivity.this))
                        .negativeText(getString(R.string.cancel))
                        .show();
            }
        });
    }

    private void setFileInfo()
    {
        File archiveFile = new File(mComic.getFilePath()+"/"+mComic.getFileName());

        mFilenameTextView.setText(getString(R.string.filename)+":\n"+archiveFile.getName());
        mFileSizeTextView.setText(getString(R.string.file_size)+": "+archiveFile.length()/(1024*1024)+" mb");
    }

    private void setPageCount()
    {
        if (mComic.getPageCount()>0)
        {
            mPagesTextView.setText(getString(R.string.pages)+": "+mComic.getPageCount());
        }
        else
        {
            mPagesTextView.setVisibility(View.GONE);
        }
    }

    private void setYear()
    {
        if (mComic.getYear()!=-1)
        {
            mYearTextView.setText(getString(R.string.year)+": "+mComic.getEditedYear());
        }
        else
        {
            mYearTextView.setVisibility(View.GONE);
        }
    }

    private void setIssueNumber()
    {
        if (mComic.getIssueNumber()!=-1)
        {
            mIssueNumberTextView.setText(getString(R.string.issue_number)+": "+mComic.getEditedIssueNumber());
        }
        else
        {
            mIssueNumberTextView.setVisibility(View.GONE);
        }
    }

    private void setTitleTextView()
    {
        mTitleTextView.setText(mComic.getEditedTitle());
        mTitleTextView.setBackgroundColor(mComic.getComicColor());
        mTitleTextView.setTextColor(mComic.getTextColor());
    }

    private void setComicCover()
    {
        if (mComic.getCoverImage()!=null)
            ImageLoader.getInstance().displayImage(mComic.getCoverImage(),mCoverImageView);
    }

    private void initIDs()
    {
        mEditButton = (ButtonFlat) findViewById(R.id.edit_button);
        mTitleTextView = (TextView) findViewById(R.id.title_text_view);
        mCoverImageView = (ImageView) findViewById(R.id.cover_image_view);
        mIssueNumberTextView = (TextView) findViewById(R.id.issue_number_text_view);
        mYearTextView = (TextView) findViewById(R.id.year_text_view);
        mFilenameTextView = (TextView) findViewById(R.id.filename_text_view);
        mFileSizeTextView = (TextView) findViewById(R.id.filesize_text_view);
        mPagesTextView = (TextView) findViewById(R.id.pages_text_view);
        mEditButtonLayout = (RelativeLayout) findViewById(R.id.button_background_layout);

        mDescriptionTextView = (TextView) findViewById(R.id.description_text_view);
        mWriterTextView = (TextView) findViewById(R.id.writer_text_view);
        mPencillerTextView = (TextView) findViewById(R.id.penciller_text_view);
        mInkerTextView = (TextView) findViewById(R.id.inker_text_view);
        mColoristTextView = (TextView) findViewById(R.id.colorist_text_view);
        mLettererTextView = (TextView) findViewById(R.id.letterer_text_view);
        mEditorTextView = (TextView) findViewById(R.id.editor_text_view);
        mCoverArtistTextView = (TextView) findViewById(R.id.cover_artist_text_view);
        mStoryArcsTextView = (TextView) findViewById(R.id.story_arcs_text_view);
        mCharactersTextView = (TextView) findViewById(R.id.characters_text_view);
        mAdditionalInfoTextView = (TextView) findViewById(R.id.additional_info_text_view);
    }

    public void setTextViewTextColors(int color)
    {
        mIssueNumberTextView.setTextColor(color);
        mYearTextView.setTextColor(color);
        mFilenameTextView.setTextColor(color);
        mFileSizeTextView.setTextColor(color);
        mPagesTextView.setTextColor(color);

        mDescriptionTextView.setTextColor(color);
        mWriterTextView.setTextColor(color);
        mPencillerTextView.setTextColor(color);
        mInkerTextView.setTextColor(color);
        mColoristTextView.setTextColor(color);
        mLettererTextView.setTextColor(color);
        mEditorTextView.setTextColor(color);
        mCoverArtistTextView.setTextColor(color);
        mStoryArcsTextView.setTextColor(color);
        mCharactersTextView.setTextColor(color);
        mAdditionalInfoTextView.setTextColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
