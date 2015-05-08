package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.R;
import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cédric on 8/05/2015.
 */
public class DropboxAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private Handler mHandler;
    private LayoutInflater mInflater;
    private List<DropboxAPI.Entry> mFileList;

    public DropboxAdapter(Context context)
    {
        mFileList = new ArrayList<>();
        mHandler = new Handler();
        mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mFileList.get(position).isDir)
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        if (viewType==0) {
            v = mInflater.inflate(R.layout.cloud_folder_card, null);
            CloudFolderViewHolder cloudFolderViewHolder = new CloudFolderViewHolder(v);
            return cloudFolderViewHolder;
        }
        else {
            v = mInflater.inflate(R.layout.file_card, null);
            CloudFileViewHolder cloudFileViewHolder = new CloudFileViewHolder(v);
            return cloudFileViewHolder;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mFileList.get(position).isDir)
        {
            CloudFolderViewHolder cloudFolderViewHolder = (CloudFolderViewHolder) holder;
            cloudFolderViewHolder.setDropboxEntry(mFileList.get(position));
            cloudFolderViewHolder.mFolderNameTextView.setText(cloudFolderViewHolder.getDropboxEntry().fileName());
        }
        else
        {
            CloudFileViewHolder cloudFileViewHolder = (CloudFileViewHolder) holder;
            cloudFileViewHolder.setDropboxEntry(mFileList.get(position));
            cloudFileViewHolder.mFileNameTextView.setText(cloudFileViewHolder.getDropboxEntry().fileName());
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public void addDropBoxEntry(final DropboxAPI.Entry entry)
    {
        final int pos = mFileList.size();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFileList.add(pos, entry);
                notifyItemInserted(pos);
            }
        });
    }
}
