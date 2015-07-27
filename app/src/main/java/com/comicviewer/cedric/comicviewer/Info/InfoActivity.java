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
import android.widget.Toast;

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
                text+= "\n"+character;
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

        final CharSequence[] editOptions = {
                "Title",
                getString(R.string.issue_number),
                getString(R.string.year),
                "Description",
                "Writer",
                "Penciller",
                "Inker",
                "Colorist",
                "Letterer",
                "Editor",
                "Cover artist",
                "Story arcs",
                "Characters",
                "Additional info"
        };

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(InfoActivity.this)
                        .title("Edit")
                        .titleGravity(GravityEnum.CENTER)
                        .items(editOptions)
                        .itemsGravity(GravityEnum.CENTER)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                handleEditChoice(i);
                            }
                        })
                        .negativeColor(StorageManager.getAppThemeColor(InfoActivity.this))
                        .negativeText(getString(R.string.cancel))
                        .show();
            }
        });
    }

    private void handleEditChoice(int choice)
    {

        switch (choice)
        {
            case 0:
                handleEditTitle();
                break;
            case 1:
                handleEditIssueNumber();
                break;
            case 2:
                handleEditYear();
                break;
            case 3:
                handleEditDescription();
                break;
            case 4:
                handleEditWriter();
                break;
            case 5:
                handleEditPenciller();
                break;
            case 6:
                handleEditInker();
                break;
            case 7:
                handleEditColorist();
                break;
            case 8:
                handleEditLetter();
                break;
            case 9:
                handleEditEditor();
                break;
            case 10:
                handleEditCoverArtist();
                break;
            case 11:
                handleEditStoryArcs();
                break;
            case 12:
                handleEditCharacters();
                break;
            case 13:
                handleEditAdditionalInfo();
                break;
        }
    }

    private void handleEditStoryArcs() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit story arcs")
                .titleColor(StorageManager.getAppThemeColor(this))
                .items(new CharSequence[]{"Add story arc", "Remove story arcs"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (i==0)
                            handleAddStoryArc();
                        else if (i==1)
                            handleRemoveStoryArcs();
                    }
                })
                .positiveColor(StorageManager.getAppThemeColor(this))
                .positiveText("Done")
                .show();
    }

    private void handleEditCharacters() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit characters")
                .titleColor(StorageManager.getAppThemeColor(this))
                .items(new CharSequence[]{"Add character", "Remove characters"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (i==0)
                            handleAddCharacter();
                        else if (i==1)
                            handleRemoveCharacter();
                    }
                })
                .positiveColor(StorageManager.getAppThemeColor(this))
                .positiveText("Done")
                .show();
    }

    private void handleAddCharacter()
    {
        showInputDialog("Add character", "", "Character", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.addCharacter(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
                handleEditCharacters();
            }
        });
    }

    private void handleRemoveCharacter()
    {
        CharSequence[] items = null;
        if (mComic.getCharacters()!=null && mComic.getCharacters().size()>0) {
            items = new CharSequence[mComic.getCharacters().size()];
        }

        if (items!=null) {

            int i = 0;
            for (String character : mComic.getCharacters()) {
                items[i] = character;
                i++;
            }

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Select characters to remove")
                    .titleColor(StorageManager.getAppThemeColor(this))
                    .items(items)
                    .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {

                            for (CharSequence sequence : charSequences)
                                mComic.removeCharacter(sequence.toString());
                            setMetadata();
                            StorageManager.saveComic(InfoActivity.this, mComic);
                            StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
                            handleEditCharacters();
                            return false;
                        }
                    })
                    .negativeColor(StorageManager.getAppThemeColor(this))
                    .negativeText(getString(R.string.cancel))
                    .positiveColor(StorageManager.getAppThemeColor(InfoActivity.this))
                    .positiveText(getString(R.string.remove))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            handleEditCharacters();
                        }
                    })
                    .show();
        }
        else
        {
            Toast.makeText(this, "No characters added yet", Toast.LENGTH_LONG).show();
            handleEditCharacters();
        }
    }

    private void handleAddStoryArc()
    {
        showInputDialog("Add story arc", "", "Story arc", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.addStoryArc(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
                handleEditStoryArcs();
            }
        });
    }

    private void handleRemoveStoryArcs()
    {
        CharSequence[] items = null;

        if (mComic.getStoryArcs()!=null && mComic.getStoryArcs().size()>0)
            items = new CharSequence[mComic.getStoryArcs().size()];

        if (items!=null) {
            int i = 0;
            for (String storyArc : mComic.getStoryArcs()) {
                items[i] = storyArc;
                i++;
            }

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Select story arcs to remove")
                    .titleColor(StorageManager.getAppThemeColor(this))
                    .items(items)
                    .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {

                            for (CharSequence sequence : charSequences)
                                mComic.removeStoryArc(sequence.toString());
                            setMetadata();
                            StorageManager.saveComic(InfoActivity.this, mComic);
                            StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
                            handleEditStoryArcs();
                            return false;
                        }
                    })
                    .negativeColor(StorageManager.getAppThemeColor(this))
                    .negativeText(getString(R.string.cancel))
                    .positiveColor(StorageManager.getAppThemeColor(InfoActivity.this))
                    .positiveText(getString(R.string.remove))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            handleEditStoryArcs();
                        }
                    })
                    .show();
        }
        else
        {
            Toast.makeText(this, "No story arcs added yet", Toast.LENGTH_LONG).show();
            handleEditStoryArcs();
        }
    }

    private void handleEditAdditionalInfo() {
        showInputDialog("Edit additional info", "", "Cover artist", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setAdditionalInfo(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditCoverArtist() {
        String prefill;
        if (mComic.getCoverArtist() == null)
            prefill = "";
        else
            prefill = mComic.getCoverArtist();
        showInputDialog("Edit cover artist", prefill, "Cover artist", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setCoverArtist(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditEditor() {
        String prefill;
        if (mComic.getEditor() == null)
            prefill = "";
        else
            prefill = mComic.getEditor();
        showInputDialog("Edit editor", prefill, "Editor", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setEditor(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditLetter() {
        String prefill;
        if (mComic.getLetterer() == null)
            prefill = "";
        else
            prefill = mComic.getLetterer();
        showInputDialog("Edit letterer", prefill, "Letterer", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setLetterer(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditColorist() {
        String prefill;
        if (mComic.getColorist() == null)
            prefill = "";
        else
            prefill = mComic.getColorist();
        showInputDialog("Edit colorist", prefill, "Colorist", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setColorist(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditInker() {
        String prefill;
        if (mComic.getInker() == null)
            prefill = "";
        else
            prefill = mComic.getInker();
        showInputDialog("Edit inker", prefill, "Inker", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setInker(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditPenciller() {
        String prefill;
        if (mComic.getPenciller() == null)
            prefill = "";
        else
            prefill = mComic.getPenciller();
        showInputDialog("Edit penciller", prefill, "Penciller", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setPenciller(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditWriter() {
        String prefill;
        if (mComic.getWriter() == null)
            prefill = "";
        else
            prefill = mComic.getWriter();
        showInputDialog("Edit writer", prefill, "Writer", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setWriter(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditDescription()
    {

        showInputDialog("Edit description", "", "Description", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setDescription(charSequence.toString());
                setMetadata();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditYear() {
        showInputDialog("Edit year", ""+mComic.getEditedYear(), "Year", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                int year = -1;
                try {
                    year = Integer.parseInt(charSequence.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(InfoActivity.this, "Invalid year", Toast.LENGTH_LONG).show();
                    return;
                }
                if (year<0)
                {
                    Toast.makeText(InfoActivity.this, "Years can't be negative", Toast.LENGTH_LONG).show();
                    return;
                }
                mComic.setEditedYear(year);
                setYear();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditTitle()
    {
        showInputDialog("Edit title", mComic.getEditedTitle(), "Title", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                mComic.setEditedTitle(charSequence.toString());
                setTitleTextView();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void handleEditIssueNumber()
    {
        showInputDialog("Edit issue number", ""+mComic.getEditedIssueNumber(), "Issue number", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                int issueNumber = -1;
                try {
                    issueNumber = Integer.parseInt(charSequence.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(InfoActivity.this, "Invalid issue number", Toast.LENGTH_LONG).show();
                    return;
                }
                if (issueNumber<0)
                {
                    Toast.makeText(InfoActivity.this, "Issue numbers can't be negative", Toast.LENGTH_LONG).show();
                    return;
                }
                mComic.setEditedIssueNumber(issueNumber);
                setIssueNumber();
                StorageManager.saveComic(InfoActivity.this, mComic);
                StorageManager.saveComicToUpdate(InfoActivity.this, mComic.getFileName());
            }
        });
    }

    private void showInputDialog(String title, String prefill, String hint, MaterialDialog.InputCallback callback)
    {
        new MaterialDialog.Builder(this)
                .title(title)
                .titleColor(StorageManager.getAppThemeColor(this))
                .negativeColor(StorageManager.getAppThemeColor(this))
                .negativeText(getString(R.string.cancel))
                .positiveText(getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(this))
                .input(hint, prefill, false, callback)
                .show();
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
