package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.content.DialogInterface;
import android.content.Intent;
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
import com.comicviewer.cedric.comicviewer.Utilities;
import com.github.clans.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AbstractLastComicPageFragment extends Fragment {

    private LayoutInflater mInflater;

    protected Comic mCurrentComic;
    protected ArrayList<Comic> mComicList;

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

    protected FloatingActionButton mFavoriteFab;
    protected TextView mFavoriteTextView;

    protected FloatingActionButton mExitFab;
    protected TextView mExitTextView;

    protected TextView mNextComicsErrorTextView;
    protected LinearLayout mNextComicLayout;


    public AbstractLastComicPageFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_last_comic_page, container, false);

        mInflater = inflater;

        if (getArguments().getParcelable("Comic")!=null)
            mCurrentComic = getArguments().getParcelable("Comic");

        if (getArguments().getParcelableArrayList("NextComics")!=null)
            mComicList = getArguments().getParcelableArrayList("NextComics");

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

        mFavoriteFab = (FloatingActionButton) v.findViewById(R.id.favorite_button);
        mFavoriteTextView = (TextView) v.findViewById(R.id.favorite_text);

        mExitFab = (FloatingActionButton) v.findViewById(R.id.exit_button);
        mExitTextView = (TextView) v.findViewById(R.id.exit_text);

        mNextComicLayout = (LinearLayout) v.findViewById(R.id.next_comics_layout);
        mNextComicsErrorTextView = (TextView) v.findViewById(R.id.no_comics_text_view);

        ImageLoader.getInstance().displayImage(mCurrentComic.getCoverImage(), mParallaxImageView);

        mInfoLayout.setBackgroundColor(mCurrentComic.getComicColor());
        setEssentialInfo();
        setMetadataInfo();
        setComicActions();
        setNextComicViews();

        return v;
    }

    private void setNextComicViews() {

        if (mComicList == null || mComicList.size()==0) {
            mNextComicsErrorTextView.setVisibility(View.VISIBLE);
            return;
        }
        for (int i=0;i<3 && i<mComicList.size();i++)
        {
            LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.simple_comic_layout, mNextComicLayout, false);
            TextView title = (TextView) layout.findViewById(R.id.simple_comic_text_view);
            if (mComicList.get(i).getEditedIssueNumber()!=-1)
                title.setText(mComicList.get(i).getEditedTitle()+" "+mComicList.get(i).getEditedIssueNumber());
            else
                title.setText(mComicList.get(i).getEditedTitle());
            title.setTextColor(Utilities.lightenColor(mComicList.get(i).getTextColor()));
            ImageView image = (ImageView) layout.findViewById(R.id.simple_comic_image_view);
            ImageLoader.getInstance().displayImage(mComicList.get(i).getCoverImage(), image);
            if (mComicList.get(i).getComicColor()!=-1)
                layout.setBackgroundColor(mComicList.get(i).getComicColor());
            else
                layout.setBackgroundColor(StorageManager.getAppThemeColor(getActivity()));
            final int finalI = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayComicActivity.class);

                    intent.putExtra("Comic", mComicList.get(finalI));
                    ArrayList<Comic> newNextComics = new ArrayList<Comic>();
                    for (int j=finalI+1;j<mComicList.size();j++)
                    {
                        newNextComics.add(mComicList.get(j));
                    }
                    intent.putExtra("NextComics", newNextComics);
                    if (StorageManager.getBooleanSetting(getActivity(), StorageManager.USES_RECENTS, true)) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }

                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
            mNextComicLayout.addView(layout);
        }
    }

    private void setComicActions() {
        setAddCollectionAction();
        setFavoriteAction();
        setExitAction();
    }

    private void setExitAction() {
        mExitTextView.setTextColor(getResources().getColor(R.color.BlueGreyDark));
        mExitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void setFavoriteAction() {

        mFavoriteTextView.setTextColor(getResources().getColor(R.color.BlueGreyDark));

        if (StorageManager.getFavoriteComics(getActivity()).contains(mCurrentComic.getFileName()))
        {
            mFavoriteTextView.setText("Unfavorite");
            mFavoriteFab.setImageResource(R.drawable.fab_star);
            mFavoriteFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageManager.removeFavoriteComic(getActivity(), mCurrentComic.getFileName());
                    setFavoriteAction();
                }
            });
        }
        else
        {
            mFavoriteTextView.setText("Favorite");
            mFavoriteFab.setImageResource(R.drawable.fab_star_outline);
            mFavoriteFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageManager.saveFavoriteComic(getActivity(), mCurrentComic.getFileName());
                    setFavoriteAction();
                }
            });
        }



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

    abstract void showChooseCollectionsDialog();

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
