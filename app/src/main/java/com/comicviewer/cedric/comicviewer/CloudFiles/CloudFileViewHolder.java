package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.box.androidsdk.content.models.BoxItem;
import com.comicviewer.cedric.comicviewer.Model.GoogleDriveObject;
import com.comicviewer.cedric.comicviewer.Model.OneDriveObject;
import com.comicviewer.cedric.comicviewer.R;
import com.dropbox.client2.DropboxAPI;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by CV on 8/05/2015.
 * class to represent a file in the cloud
 */
public class CloudFileViewHolder extends RecyclerView.ViewHolder {

    protected TextView mFileNameTextView;
    protected TextView mDownloadTextView;
    protected CardView mCardView;
    protected FloatingActionButton mDownloadButton;

    private DropboxAPI.Entry mDropboxEntry = null;

    private OneDriveObject mOneDriveEntry = null;

    private GoogleDriveObject mGoogleDriveEntry = null;

    private BoxItem mBoxEntry = null;

    public CloudFileViewHolder(View itemView) {
        super(itemView);

        mFileNameTextView = (TextView) itemView.findViewById(R.id.file_title_text_view);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mDownloadTextView = (TextView) itemView.findViewById(R.id.download_text);
        mDownloadButton = (FloatingActionButton) itemView.findViewById(R.id.download_button);
    }

    public void setDropboxEntry(DropboxAPI.Entry entry)
    {
        mDropboxEntry = entry;
    }

    public DropboxAPI.Entry getDropboxEntry()
    {
        return mDropboxEntry;
    }

    public void setOneDriveEntry(OneDriveObject entry)
    {
        mOneDriveEntry = entry;
    }

    public OneDriveObject getOneDriveEnty()
    {
        return mOneDriveEntry;
    }

    public void setGoogleDriveEntry(GoogleDriveObject driveObject)
    {
        mGoogleDriveEntry = driveObject;
    }

    public GoogleDriveObject getGoogleDriveEntry()
    {
        return mGoogleDriveEntry;
    }

    public void setBoxEntry(BoxItem boxEntry)
    {
        mBoxEntry = boxEntry;
    }

    public BoxItem getBoxEntry()
    {
        return mBoxEntry;
    }
}
