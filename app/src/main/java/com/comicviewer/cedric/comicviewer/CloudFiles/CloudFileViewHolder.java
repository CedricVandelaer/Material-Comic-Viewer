package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;
import com.dropbox.client2.DropboxAPI;

/**
 * Created by Cédric on 8/05/2015.
 */
public class CloudFileViewHolder extends RecyclerView.ViewHolder {

    protected TextView mFileNameTextView;
    protected TextView mDeleteTextView;
    protected CardView mCardView;
    private DropboxAPI.Entry mDropboxEntry = null;

    public CloudFileViewHolder(View itemView) {
        super(itemView);

        mFileNameTextView = (TextView) itemView.findViewById(R.id.file_title_text_view);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mDeleteTextView = (TextView) itemView.findViewById(R.id.delete_text);
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
