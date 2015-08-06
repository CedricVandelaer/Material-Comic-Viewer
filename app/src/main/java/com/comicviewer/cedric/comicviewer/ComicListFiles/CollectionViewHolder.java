package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;
import com.daimajia.swipe.SwipeLayout;
import com.github.clans.fab.FloatingActionButton;

/**
 * Created by CV on 21/06/2015.
 */
public class CollectionViewHolder extends RecyclerView.ViewHolder {

    protected SwipeLayout mSwipeLayout;

    protected TextView mTitleTextView;
    protected ImageView mIconImageView;
    protected CardView mCard;

    protected FloatingActionButton mDeleteButton;
    protected TextView mDeleteTextView;

    protected FloatingActionButton mRenameButton;
    protected TextView mRenameTextView;

    protected FloatingActionButton mEditFilterButton;
    protected TextView mEditFilterTextView;

    private String mCollectionName;

    public CollectionViewHolder(View itemView) {
        super(itemView);

        mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);
        mTitleTextView = (TextView) itemView.findViewById(R.id.collection_title_text_view);
        mIconImageView = (ImageView) itemView.findViewById(R.id.collection_image_view);
        mCard = (CardView) itemView.findViewById(R.id.card);
        mDeleteButton = (FloatingActionButton) itemView.findViewById(R.id.delete_button);
        mDeleteTextView = (TextView) itemView.findViewById(R.id.delete_text);
        mRenameButton = (FloatingActionButton) itemView.findViewById(R.id.rename_button);
        mRenameTextView = (TextView) itemView.findViewById(R.id.rename_text);
        mEditFilterButton = (FloatingActionButton) itemView.findViewById(R.id.edit_button);
        mEditFilterTextView = (TextView) itemView.findViewById(R.id.edit_text);

    }

    public void setCollectionName(String name)
    {
        mCollectionName = name;
    }

    public String getCollectionName()
    {
        return mCollectionName;
    }
}
