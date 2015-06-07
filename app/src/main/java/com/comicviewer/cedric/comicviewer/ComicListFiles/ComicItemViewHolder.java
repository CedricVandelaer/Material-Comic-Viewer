package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;
import com.daimajia.swipe.SwipeLayout;
import com.melnykov.fab.FloatingActionButton;

import org.w3c.dom.Text;


/**
 * Created by Cédric on 23/01/2015.
 * The viewholder for the comiclist
 */
public class ComicItemViewHolder extends RecyclerView.ViewHolder{

    protected ImageView mCoverPicture;
    protected TextView mTitle;
    protected CardView mCardView;
    protected TextView mIssueNumber;
    protected TextView mPageCount;
    protected TextView mYear;
    protected ImageView mLastReadIcon;
    protected ImageView mFavoriteButton;
    protected ImageView mMangaPicture;
    protected SwipeLayout mSwipeLayout;
    protected FloatingActionButton mMarkReadButton;
    protected FloatingActionButton mOptionsButton;
    protected FloatingActionButton mMangaButton;

    protected TextView mMarkReadTextView;
    protected TextView mOptionsTextView;
    protected TextView mMangaTextView;

    protected LinearLayout mMarkReadButtonLayout;
    protected LinearLayout mOptionsLayout;
    protected LinearLayout mMangaLayout;

    protected Comic mComic = null;

    public void setComic(Comic comic)
    {
        mComic = comic;
    }

    public Comic getComic()
    {
        return mComic;
    }

    public ComicItemViewHolder(View itemView) {
        super(itemView);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        String cardSize = prefs.getString("cardSize", itemView.getContext().getString(R.string.card_size_setting_2));
        
        if (cardSize.equals(itemView.getContext().getString(R.string.card_size_setting_2)) || cardSize.equals(itemView.getContext().getString(R.string.card_size_setting_4)))
            initialiseNormalCard(itemView);
        else if (cardSize.equals(itemView.getContext().getString(R.string.card_size_setting_1)))
            initialiseSmallCard(itemView);
        else
            initialiseCardComicBg(itemView);

        mMangaPicture = (ImageView) itemView.findViewById(R.id.manga_indicator);

        mMangaButton = (FloatingActionButton) itemView.findViewById(R.id.manga_button);
        mMangaLayout = (LinearLayout) itemView.findViewById(R.id.manga_layout);
        mMangaTextView = (TextView) itemView.findViewById(R.id.manga_text);

        initSwipeLayout(itemView);

    }

    private void initSwipeLayout(View itemView) {
        mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);

        mMarkReadButtonLayout = (LinearLayout) itemView.findViewById(R.id.mark_read_layout);
        mOptionsLayout = (LinearLayout) itemView.findViewById(R.id.more_layout);

        mMarkReadButton = (FloatingActionButton) itemView.findViewById(R.id.mark_read_button);
        mOptionsButton = (FloatingActionButton) itemView.findViewById(R.id.more_button);

        mMarkReadTextView = (TextView) itemView.findViewById(R.id.mark_read_text);
        mOptionsTextView = (TextView) itemView.findViewById(R.id.more_text);

    }

    private void initialiseCardComicBg(View itemView)
    {
        mCoverPicture = (ImageView) itemView.findViewById(R.id.card_bg_image);
        mCardView = (CardView) itemView.findViewById(R.id.card_bg);
        mTitle = (TextView) itemView.findViewById(R.id.replacement_title);
        mLastReadIcon = (ImageView) itemView.findViewById(R.id.large_read_indicator);
        mFavoriteButton = (ImageView) itemView.findViewById(R.id.large_favorite_button);
    }
    
    private void initialiseNormalCard(View itemView)
    {
        mIssueNumber = (TextView) itemView.findViewById(R.id.issue_number);
        mCoverPicture = (ImageView) itemView.findViewById(R.id.cover);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mPageCount = (TextView) itemView.findViewById(R.id.page_count);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mYear = (TextView) itemView.findViewById(R.id.year);
        mLastReadIcon = (ImageView) itemView.findViewById(R.id.last_read_indicator);
        mFavoriteButton = (ImageView) itemView.findViewById(R.id.normal_favorite_button);
    }

    private void initialiseSmallCard(View itemView)
    {
        mIssueNumber = (TextView) itemView.findViewById(R.id.small_issue_number);
        mTitle = (TextView) itemView.findViewById(R.id.small_title);
        mPageCount = (TextView) itemView.findViewById(R.id.small_page_count);
        mCardView = (CardView) itemView.findViewById(R.id.small_card);
        mYear = (TextView) itemView.findViewById(R.id.small_year);
        mLastReadIcon = (ImageView) itemView.findViewById(R.id.small_read_indicator);
        mFavoriteButton = (ImageView) itemView.findViewById(R.id.small_favorite_button);

    }


}
