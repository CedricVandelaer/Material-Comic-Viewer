package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;

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

    public ComicItemViewHolder(View itemView) {
        super(itemView);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        String cardSize = prefs.getString("cardSize", itemView.getContext().getString(R.string.card_size_setting_2));
        
        if (cardSize.equals(itemView.getContext().getString(R.string.card_size_setting_2)))
            initialiseNormalCard(itemView);
        else if (cardSize.equals(itemView.getContext().getString(R.string.card_size_setting_1)))
            initialiseSmallCard(itemView);
        else
            initialiseCardComicBg(itemView);
    }
    
    private void initialiseCardComicBg(View itemView)
    {
        mCoverPicture = (ImageView) itemView.findViewById(R.id.card_bg_image);
        mCardView = (CardView) itemView.findViewById(R.id.card_bg);
        mTitle = (TextView) itemView.findViewById(R.id.replacement_title);
    }
    
    private void initialiseNormalCard(View itemView)
    {
        mIssueNumber = (TextView) itemView.findViewById(R.id.issue_number);
        mCoverPicture = (ImageView) itemView.findViewById(R.id.cover);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mPageCount = (TextView) itemView.findViewById(R.id.page_count);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mYear = (TextView) itemView.findViewById(R.id.year);
        
    }

    private void initialiseSmallCard(View itemView)
    {
        mIssueNumber = (TextView) itemView.findViewById(R.id.small_issue_number);
        mTitle = (TextView) itemView.findViewById(R.id.small_title);
        mPageCount = (TextView) itemView.findViewById(R.id.small_page_count);
        mCardView = (CardView) itemView.findViewById(R.id.small_card);
        mYear = (TextView) itemView.findViewById(R.id.small_year);

    }


}
