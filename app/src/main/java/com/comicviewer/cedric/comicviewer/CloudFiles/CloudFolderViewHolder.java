package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;
import com.dropbox.client2.DropboxAPI;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by C�dric on 8/05/2015.
 *
 */
public class CloudFolderViewHolder extends RecyclerView.ViewHolder {

    protected TextView mFolderNameTextView;
    protected CardView mCardView;
    protected TextView mDownloadTextView;
    protected FloatingActionButton mDownloadFolderButton;

    private DropboxAPI.Entry mDropboxEntry = null;

    public CloudFolderViewHolder(View itemView) {
        super(itemView);

        mDownloadTextView = (TextView) itemView.findViewById(R.id.download_text);
        mFolderNameTextView = (TextView) itemView.findViewById(R.id.folder_title_text_view);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mDownloadFolderButton = (FloatingActionButton) itemView.findViewById(R.id.download_button);
    }

    public void setDropboxEntry(DropboxAPI.Entry entry)
    {
        mDropboxEntry = entry;
    }

    public DropboxAPI.Entry getDropboxEntry()
    {
        return mDropboxEntry;
    }
}
