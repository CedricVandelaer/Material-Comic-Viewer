package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.Model.OneDriveObject;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CV on 8/05/2015.
 * class to map files to their respective views
 */
public class OneDriveAdapter extends RecyclerView.Adapter {

    private AbstractCloudServiceListFragment mFragment;
    private Context mContext;
    private CloudService mCloudService;
    private Handler mHandler;
    private LayoutInflater mInflater;
    private List<OneDriveObject> mFileList;

    public OneDriveAdapter(AbstractCloudServiceListFragment fragment, CloudService cloudService)
    {
        mFileList = new ArrayList<>();
        mCloudService = cloudService;
        mHandler = new Handler();
        mFragment = fragment;
        mContext = mFragment.getActivity();
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            v = mInflater.inflate(R.layout.cloud_folder_card, parent, false);
            CloudFolderViewHolder cloudFolderViewHolder = new CloudFolderViewHolder(v);
            cloudFolderViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mContext)));
            if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG))
                cloudFolderViewHolder.mDownloadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            addFolderClickListener(cloudFolderViewHolder);
            addDownloadFolderClickListener(cloudFolderViewHolder);
            return cloudFolderViewHolder;
        }
        else {
            v = mInflater.inflate(R.layout.file_card, parent, false);
            CloudFileViewHolder cloudFileViewHolder = new CloudFileViewHolder(v);
            if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG))
                cloudFileViewHolder.mDownloadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            cloudFileViewHolder.mCardView.setCardBackgroundColor(PreferenceSetter.getAppThemeColor(mContext));
            addFileClickListener(cloudFileViewHolder);
            return cloudFileViewHolder;
        }


    }

    private void addDownloadFolderClickListener(final CloudFolderViewHolder cloudFolderViewHolder)
    {
        cloudFolderViewHolder.mDownloadFolderButton.setVisibility(View.GONE);
        cloudFolderViewHolder.mDownloadTextView.setVisibility(View.GONE);
        cloudFolderViewHolder.mSwipeLayout.setLeftSwipeEnabled(false);
        cloudFolderViewHolder.mSwipeLayout.setRightSwipeEnabled(false);
        /*
        cloudFolderViewHolder.mDownloadFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OneDriveObject entry = cloudFolderViewHolder.getOneDriveEnty();

                MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                        .title("Download file")
                        .content("Do you wish to download the folder \""+entry.getName()+"\"?")
                        .positiveColor(PreferenceSetter.getAppThemeColor(mActivity))
                        .positiveText("Confirm")
                        .negativeColor(PreferenceSetter.getAppThemeColor(mActivity))
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Toast.makeText(mActivity,"Download started...",Toast.LENGTH_SHORT).show();
                                DownloadFileService.startActionDownload(mActivity, entry, mCloudService);
                            }
                        }).show();
            }
        });
        */
    }

    private void addFileClickListener(final CloudFileViewHolder cloudFileViewHolder) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OneDriveObject entry = cloudFileViewHolder.getOneDriveEnty();

                MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
                        .title(mContext.getString(R.string.download_file))
                        .content(mContext.getString(R.string.download_request)+" \""+entry.getName()+"\"?")
                        .positiveColor(PreferenceSetter.getAppThemeColor(mContext))
                        .positiveText(mContext.getString(R.string.confirm))
                        .negativeColor(PreferenceSetter.getAppThemeColor(mContext))
                        .negativeText(mContext.getString(R.string.cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Toast.makeText(mContext, mContext.getString(R.string.download_started), Toast.LENGTH_SHORT).show();
                                DownloadFileService.startActionDownload(mContext, entry, mCloudService);
                            }
                        }).show();
            }
        };

        cloudFileViewHolder.mCardView.setOnClickListener(clickListener);
        cloudFileViewHolder.mDownloadButton.setOnClickListener(clickListener);

    }

    private void addFolderClickListener(final CloudFolderViewHolder cloudFolderViewHolder) {
        cloudFolderViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.getNavigationManager().pushPathToCloudStack(cloudFolderViewHolder.getOneDriveEnty().getId()+"/files");
                mFragment.refresh();
            }
        });
    }

    public void clear()
    {
        mFileList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mFileList.get(position).getType() == ObjectType.FOLDER)
        {
            CloudFolderViewHolder cloudFolderViewHolder = (CloudFolderViewHolder) holder;
            cloudFolderViewHolder.setOneDriveEntry(mFileList.get(position));
            cloudFolderViewHolder.mFolderNameTextView.setText(cloudFolderViewHolder.getOneDriveEnty().getName());
        }
        else
        {
            CloudFileViewHolder cloudFileViewHolder = (CloudFileViewHolder) holder;
            cloudFileViewHolder.setOneDriveEntry(mFileList.get(position));
            cloudFileViewHolder.mFileNameTextView.setText(cloudFileViewHolder.getOneDriveEnty().getName());
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public void addOneDriveEntry(final OneDriveObject entry)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFileList.add(entry);
                int pos = mFileList.indexOf(entry);
                notifyItemInserted(pos);
            }
        });
    }
}
