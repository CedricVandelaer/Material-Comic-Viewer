package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.box.androidsdk.content.models.BoxItem;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.GoogleDriveObject;
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.util.ArrayList;

/**
 * Created by CV on 19/06/2015.
 */
public class BoxAdapter extends RecyclerView.Adapter {

    private BoxActivity mActivity;
    private ArrayList<BoxItem> mBoxItemList;
    private LayoutInflater mInflater;
    private CloudService mCloudService;

    public BoxAdapter(BoxActivity activity, CloudService cloudService)
    {
        mActivity = activity;
        mCloudService = cloudService;
        mBoxItemList = new ArrayList<>();
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mBoxItemList.get(position).getType().equals("folder"))
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType==0) {
            v = mInflater.inflate(R.layout.cloud_folder_card, parent, false);
            CloudFolderViewHolder cloudFolderViewHolder = new CloudFolderViewHolder(v);
            cloudFolderViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mActivity)));
            if (PreferenceSetter.getBackgroundColorPreference(mActivity) == mActivity.getResources().getColor(R.color.WhiteBG))
                cloudFolderViewHolder.mDownloadTextView.setTextColor(mActivity.getResources().getColor(R.color.Black));
            //addFolderClickListener(cloudFolderViewHolder);
            //addDownloadFolderClickListener(cloudFolderViewHolder);
            return cloudFolderViewHolder;
        }
        else {
            v = mInflater.inflate(R.layout.file_card, parent, false);
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
        if (mBoxItemList.get(position).getType().equals("folder"))
        {
            CloudFolderViewHolder cloudFolderViewHolder = (CloudFolderViewHolder) holder;
            cloudFolderViewHolder.setBoxEntry(mBoxItemList.get(position));
            cloudFolderViewHolder.mFolderNameTextView.setText(cloudFolderViewHolder.getBoxEntry().getName());
        }
        else
        {
            CloudFileViewHolder cloudFileViewHolder = (CloudFileViewHolder) holder;
            cloudFileViewHolder.setBoxEntry(mBoxItemList.get(position));
            cloudFileViewHolder.mFileNameTextView.setText(cloudFileViewHolder.getBoxEntry().getName());
        }
    }

    @Override
    public int getItemCount() {
        return mBoxItemList.size();
    }
    public void clear()
    {
        mBoxItemList.clear();
        notifyDataSetChanged();
    }

    public void addBoxObject(BoxItem object)
    {
        mBoxItemList.add(object);
        notifyItemInserted(mBoxItemList.size()-1);
    }

}
