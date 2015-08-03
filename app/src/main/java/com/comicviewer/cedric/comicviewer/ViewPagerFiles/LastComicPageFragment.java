package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.CollectionActions;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.github.clans.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastComicPageFragment extends Fragment {

    protected ArrayList<Comic> mComicList;
    protected Comic mCurrentComic;

    protected LinearLayout mInfoLayout;
    protected ImageView mParallaxImageView;
    protected TextView mTitleTextView;

    protected TextView mFileNameTextView;
    protected TextView mYearTextView;
    protected TextView mPagesTextView;

    protected TextView mErrorTextView;
    protected TextView mWriterTextView;
    protected TextView mPencillerTextView;
    protected TextView mInkerTextView;
    protected TextView mColoristTextView;
    protected TextView mLettererTextView;
    protected TextView mEditorTextView;
    protected TextView mCoverArtistTextView;
    protected TextView mStoryArcsTextView;
    protected TextView mCharactersTextView;

    protected FloatingActionButton mAddToCollectionFab;
    protected TextView mAddToCollectionTextView;

    public static LastComicPageFragment newInstance(Comic currentComic, ArrayList<Comic> nextComics)
    {
        LastComicPageFragment fragment = new LastComicPageFragment();

        Bundle args = new Bundle();

        args.putParcelable("Comic", currentComic);

        fragment.setArguments(args);

        return fragment;
    }

    public LastComicPageFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_last_comic_page, container, false);

        if (getArguments().getParcelable("Comic")!=null)
            mCurrentComic = getArguments().getParcelable("Comic");

        mParallaxImageView = (ImageView)v.findViewById(R.id.parallax_image_view);
        mInfoLayout = (LinearLayout) v.findViewById(R.id.info_layout);
        mTitleTextView = (TextView) v.findViewById(R.id.title_text_view);
        mFileNameTextView = (TextView) v.findViewById(R.id.filename_text_view);
        mPagesTextView = (TextView) v.findViewById(R.id.pages_text_view);
        mYearTextView = (TextView) v.findViewById(R.id.year_text_view);

        mErrorTextView = (TextView) v.findViewById(R.id.error_text_view);
        mWriterTextView = (TextView) v.findViewById(R.id.writer_text_view);
        mPencillerTextView = (TextView) v.findViewById(R.id.penciller_text_view);
        mInkerTextView = (TextView) v.findViewById(R.id.inker_text_view);
        mColoristTextView = (TextView) v.findViewById(R.id.colorist_text_view);
        mLettererTextView = (TextView) v.findViewById(R.id.letterer_text_view);
        mEditorTextView = (TextView) v.findViewById(R.id.editor_text_view);
        mCoverArtistTextView = (TextView) v.findViewById(R.id.cover_artist_text_view);
        mStoryArcsTextView = (TextView) v.findViewById(R.id.story_arcs_text_view);
        mCharactersTextView = (TextView) v.findViewById(R.id.characters_text_view);

        mAddToCollectionFab = (FloatingActionButton) v.findViewById(R.id.add_collection_button);
        mAddToCollectionTextView = (TextView) v.findViewById(R.id.add_collection_text);

        ImageLoader.getInstance().displayImage(mCurrentComic.getCoverImage(), mParallaxImageView);

        mInfoLayout.setBackgroundColor(mCurrentComic.getComicColor());
        setEssentialInfo();
        setMetadataInfo();
        setComicActions();

        return v;
    }

    private void setComicActions() {
        setAddCollectionAction();
    }

    private void setAddCollectionAction() {
        mAddToCollectionTextView.setTextColor(getResources().getColor(R.color.BlueGreyDark));
        mAddToCollectionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseCollectionsDialog();
            }
        });
    }

    private void showChooseCollectionsDialog()
    {

        ArrayList<Collection> collections = StorageManager.getCollectionList(getActivity());
        CharSequence[] collectionNames = new CharSequence[collections.size()+1];

        for (int i=0;i<collections.size();i++)
        {
            collectionNames[i] = collections.get(i).getName();
        }

        final String newCollection = "Add new collection";
        collectionNames[collectionNames.length-1] = newCollection;

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Choose collection")
                .items(collectionNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (charSequence.equals(newCollection)) {
                            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                    .title("Create new collection")
                                    .input("Collection name", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                            StorageManager.createCollection(getActivity(), charSequence.toString());
                                            CollectionActions.addComicToCollection(getActivity(), charSequence.toString(), mCurrentComic);
                                        }
                                    })
                                    .show();
                        } else {
                            CollectionActions.addComicToCollection(getActivity(), charSequence.toString(), mCurrentComic);
                        }
                    }
                })
                .show();


    }

    private void setMetadataInfo() {
        setErrorTextView();
        setWriterTextView();
        setPencillerTextView();
        setInkerTextView();
        setColoristTextView();
        setLettererTextView();
        setEditorTextView();
        setCoverArtistTextView();
        setStoryArcsTextView();
        setCharactersTextView();
    }

    private void setCharactersTextView() {
        if (mCurrentComic.getCharacters()!=null && mCurrentComic.getCharacters().size()>0)
        {
            String text = "Characters:";

            for (String character:mCurrentComic.getCharacters())
                text+="\n"+character;

            mCharactersTextView.setText(text);
        }
        else
            mCharactersTextView.setVisibility(View.GONE);
    }

    private void setStoryArcsTextView() {
        if (mCurrentComic.getStoryArcs()!=null && mCurrentComic.getStoryArcs().size()>0)
        {
            String text = "Story arcs:";

            for (String storyArc:mCurrentComic.getStoryArcs())
                text+="\n"+storyArc;

            mStoryArcsTextView.setText(text);
        }
        else
            mStoryArcsTextView.setVisibility(View.GONE);
    }

    private void setCoverArtistTextView() {
        if (mCurrentComic.getCoverArtist()!=null)
            mCoverArtistTextView.setText("Cover artist: "+mCurrentComic.getCoverArtist());
        else
            mCoverArtistTextView.setVisibility(View.GONE);
    }

    private void setEditorTextView() {
        if (mCurrentComic.getEditor()!=null)
            mEditorTextView.setText("Editor: "+mCurrentComic.getEditor());
        else
            mEditorTextView.setVisibility(View.GONE);
    }

    private void setLettererTextView() {
        if (mCurrentComic.getLetterer()!=null)
            mLettererTextView.setText("Letterer: "+mCurrentComic.getLetterer());
        else
            mLettererTextView.setVisibility(View.GONE);
    }

    private void setColoristTextView() {
        if (mCurrentComic.getColorist()!=null)
            mColoristTextView.setText("Colorist: "+mCurrentComic.getColorist());
        else
            mColoristTextView.setVisibility(View.GONE);
    }

    private void setInkerTextView() {
        if (mCurrentComic.getInker()!=null)
            mInkerTextView.setText("Inker: "+mCurrentComic.getInker());
        else
            mInkerTextView.setVisibility(View.GONE);
    }

    private void setPencillerTextView() {
        if (mCurrentComic.getPenciller()!=null)
            mPencillerTextView.setText("Penciller: "+mCurrentComic.getPenciller());
        else
            mPencillerTextView.setVisibility(View.GONE);
    }

    private void setWriterTextView() {
        if (mCurrentComic.getWriter()!=null)
            mWriterTextView.setText("Writer: "+mCurrentComic.getWriter());
        else
            mWriterTextView.setVisibility(View.GONE);
    }

    private void setErrorTextView() {
        if (!mCurrentComic.hasCreatorInfo())
            mErrorTextView.setVisibility(View.VISIBLE);
        else
            mErrorTextView.setVisibility(View.GONE);
    }

    private void setEssentialInfo() {

        setTitleInfo();
        setFileNameInfo();
        setYearInfo();
        setPagesInfo();
    }

    private void setTitleInfo()
    {
        String title = mCurrentComic.getEditedTitle();

        if (mCurrentComic.getEditedIssueNumber()!=-1)
            title+=" "+mCurrentComic.getEditedIssueNumber();

        mTitleTextView.setText(title);
    }

    private void setYearInfo()
    {
        if (mCurrentComic.getEditedYear()!=-1)
            mYearTextView.setText("Year:\n"+mCurrentComic.getEditedYear());
        else
            mYearTextView.setVisibility(View.GONE);
    }

    private void setPagesInfo()
    {
        if (mCurrentComic.getPageCount()!=-1)
            mPagesTextView.setText("Pages:\n" + mCurrentComic.getPageCount());
        else
            mPagesTextView.setVisibility(View.GONE);
    }

    private void setFileNameInfo()
    {
        mFileNameTextView.setText("Filename:\n"+mCurrentComic.getFileName());
    }

}
