package com.comicviewer.cedric.comicviewer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

    public ComicItemViewHolder(View itemView) {
        super(itemView);

        mIssueNumber = (TextView) itemView.findViewById(R.id.issue_number);
        mCoverPicture = (ImageView) itemView.findViewById(R.id.cover);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mPageCount = (TextView) itemView.findViewById(R.id.page_count);
        mCardView = (CardView) itemView.findViewById(R.id.card);

    }


}
