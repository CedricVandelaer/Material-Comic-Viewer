package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.Model.GoogleDriveObject;
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.util.ArrayList;

/**
 * Created by CV on 12/06/2015.
 */
public class GoogleDriveAdapter extends RecyclerView.Adapter {

    private GoogleDriveActivity mActivity;
    private ArrayList<GoogleDriveObject> mFileList;
    private LayoutInflater mInflater;

    public GoogleDriveAdapter(GoogleDriveActivity activity)
    {
        mActivity = activity;
        mFileList = new ArrayList<>();
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mFileList.get(position).getType() == ObjectType.FOLDER)
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
            cloudFolderViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mActivity)));
            if (PreferenceSetter.getBackgroundColorPreference(mActivity) == mActivity.getResources().getColor(R.color.WhiteBG))
                cloudFolderViewHolder.mDownloadTextView.setTextColor(mActivity.getResources().getColor(R.color.Black));
            //addFolderClickListener(cloudFolderViewHolder);
            //addDownloadFolderClickListener(cloudFolderViewHolder);
            return cloudFolderViewHolder;
        }
        else {
            v = mInflater.inflate(R.layout.file_card, null);
            CloudFileViewHolder cloudFileViewHolder = new CloudFileViewHolder(v);
            if (PreferenceSetter.getBackgroundColorPreference(mActivity) == mActivity.getResources().getColor(R.color.WhiteBG))
                cloudFileViewHolder.mDownloadTextView.setTextColor(mActivity.getResources().getColor(R.color.Black));
            cloudFileViewHolder.mCardView.setCardBackgroundColor(PreferenceSetter.getAppThemeColor(mActivity));
            //addFileClickListener(cloudFileViewHolder);
            return cloudFileViewHolder;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mFileList.get(position).getType() == ObjectType.FOLDER)
        {
            CloudFolderViewHolder cloudFolderViewHolder = (CloudFolderViewHolder) holder;
            cloudFolderViewHolder.setGoogleDriveEntry(mFileList.get(position));
            cloudFolderViewHolder.mFolderNameTextView.setText(cloudFolderViewHolder.getGoogleDriveEntry().getName());
        }
        else
        {
            CloudFileViewHolder cloudFileViewHolder = (CloudFileViewHolder) holder;
            cloudFileViewHolder.setGoogleDriveEntry(mFileList.get(position));
            cloudFileViewHolder.mFileNameTextView.setText(cloudFileViewHolder.getGoogleDriveEntry().getName());
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public void clear()
    {
        mFileList.clear();
        notifyDataSetChanged();
    }

    public void addDriveObject(GoogleDriveObject object)
    {
        mFileList.add(object);
        notifyItemInserted(mFileList.size()-1);
    }
}
