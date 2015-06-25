package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;
import com.daimajia.swipe.SwipeLayout;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;

/**
 * Created by Cédric on 2/05/2015.
 * Viewholder to show folders
 */
public class FolderItemViewHolder extends RecyclerView.ViewHolder {

    protected CardView mCardView;
    protected ImageView mFolderImage;
    protected TextView mFolderTitleTextView;

    protected SwipeLayout mSwipeLayout;

    protected TextView mAddToCollectionTextView;
    protected FloatingActionButton mAddToCollectionButton;

    protected TextView mRenameTextView;
    protected FloatingActionButton mRenameButton;

    protected FloatingActionButton mOptionsButton;
    protected TextView mOptionsTextView;

    protected File mFile = null;

    public FolderItemViewHolder(View itemView) {
        super(itemView);

        mAddToCollectionButton = (FloatingActionButton) itemView.findViewById(R.id.add_collection_button);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);
        mAddToCollectionTextView = (TextView) itemView.findViewById(R.id.add_collection_text);
        mFolderImage = (ImageView) itemView.findViewById(R.id.folder_image_view);
        mFolderTitleTextView = (TextView) itemView.findViewById(R.id.folder_title_text_view);
        mRenameTextView = (TextView) itemView.findViewById(R.id.rename_text);
        mRenameButton = (FloatingActionButton) itemView.findViewById(R.id.rename_button);
        mOptionsButton = (FloatingActionButton) itemView.findViewById(R.id.more_button);
        mOptionsTextView = (TextView) itemView.findViewById(R.id.more_text);
    }

    public void setFile(File file)
    {
        mFile = file;
    }

    public File getFile()
    {
        return mFile;
    }
}
