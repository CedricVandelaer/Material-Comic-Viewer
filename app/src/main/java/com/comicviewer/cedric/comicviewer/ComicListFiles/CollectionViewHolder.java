package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by CV on 21/06/2015.
 */
public class CollectionViewHolder extends RecyclerView.ViewHolder {

    protected TextView mTitleTextView;
    protected ImageView mIconImageView;
    protected CardView mCard;

    protected FloatingActionButton mDeleteButton;
    protected TextView mDeleteTextView;

    private String mCollectionName;

    public CollectionViewHolder(View itemView) {
        super(itemView);

        mTitleTextView = (TextView) itemView.findViewById(R.id.collection_title_text_view);
        mIconImageView = (ImageView) itemView.findViewById(R.id.collection_image_view);
        mCard = (CardView) itemView.findViewById(R.id.card);
        mDeleteButton = (FloatingActionButton) itemView.findViewById(R.id.delete_button);
        mDeleteTextView = (TextView) itemView.findViewById(R.id.delete_text);
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
