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

    protected ImageView mFolderImage;
    protected TextView mFolderTitleTextView;
    protected SwipeLayout mSwipeLayout;
    protected CardView mCardView;
    protected TextView mDeleteTextView;
    protected FloatingActionButton mDeleteButton;
    protected TextView mRenameTextView;
    protected FloatingActionButton mRenameButton;
    protected FloatingActionButton mHideButton;
    protected TextView mHideTextView;

    protected File mFile = null;

    public FolderItemViewHolder(View itemView) {
        super(itemView);

        mDeleteButton = (FloatingActionButton) itemView.findViewById(R.id.delete_button);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);
        mDeleteTextView = (TextView) itemView.findViewById(R.id.delete_text);
        mFolderImage = (ImageView) itemView.findViewById(R.id.folder_image_view);
        mFolderTitleTextView = (TextView) itemView.findViewById(R.id.folder_title_text_view);
        mRenameTextView = (TextView) itemView.findViewById(R.id.rename_text);
        mRenameButton = (FloatingActionButton) itemView.findViewById(R.id.rename_button);
        mHideButton = (FloatingActionButton) itemView.findViewById(R.id.hide_button);
        mHideTextView = (TextView) itemView.findViewById(R.id.hide_text);
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
