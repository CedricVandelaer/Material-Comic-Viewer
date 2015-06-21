package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by CV on 21/06/2015.
 */
public class CollectionViewHolder extends RecyclerView.ViewHolder {

    protected TextView mTitleTextView;
    protected ImageView mIconImageView;

    public CollectionViewHolder(View itemView) {
        super(itemView);

        mTitleTextView = (TextView) itemView.findViewById(R.id.collection_title_text_view);
        mIconImageView = (ImageView) itemView.findViewById(R.id.collection_image_view);

    }
}
