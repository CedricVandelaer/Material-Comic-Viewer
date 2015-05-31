package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CV on 8/05/2015.
 * Class to manage the mapping of cloud files on their views
 */
public class DropboxAdapter extends RecyclerView.Adapter {

    private DropboxActivity mActivity;
    private CloudService mCloudService;
    private Handler mHandler;
    private LayoutInflater mInflater;
    private List<DropboxAPI.Entry> mFileList;

    public DropboxAdapter(DropboxActivity activity, CloudService cloudService)
    {
        mFileList = new ArrayList<>();
        mCloudService = cloudService;
        mHandler = new Handler();
        mActivity = activity;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            cloudFolderViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mActivity)));
            if (PreferenceSetter.getBackgroundColorPreference(mActivity) == mActivity.getResources().getColor(R.color.WhiteBG))
                cloudFolderViewHolder.mDownloadTextView.setTextColor(mActivity.getResources().getColor(R.color.Black));
            addFolderClickListener(cloudFolderViewHolder);
            addDownloadFolderClickListener(cloudFolderViewHolder);
            return cloudFolderViewHolder;
        }
        else {
            v = mInflater.inflate(R.layout.file_card, null);
            CloudFileViewHolder cloudFileViewHolder = new CloudFileViewHolder(v);
            if (PreferenceSetter.getBackgroundColorPreference(mActivity) == mActivity.getResources().getColor(R.color.WhiteBG))
                cloudFileViewHolder.mDownloadTextView.setTextColor(mActivity.getResources().getColor(R.color.Black));
            cloudFileViewHolder.mCardView.setCardBackgroundColor(PreferenceSetter.getAppThemeColor(mActivity));
            addFileClickListener(cloudFileViewHolder);
            return cloudFileViewHolder;
        }


    }

    private void addDownloadFolderClickListener(final CloudFolderViewHolder cloudFolderViewHolder)
    {
        cloudFolderViewHolder.mDownloadFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DropboxAPI.Entry entry = cloudFolderViewHolder.getDropboxEntry();

                MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                        .title(mActivity.getString(R.string.download_folder))
                        .content(mActivity.getString(R.string.download_folder_request)+" \""+entry.fileName()+"\"?")
                        .positiveColor(PreferenceSetter.getAppThemeColor(mActivity))
                        .positiveText(mActivity.getString(R.string.confirm))
                        .negativeColor(PreferenceSetter.getAppThemeColor(mActivity))
                        .negativeText(mActivity.getString(R.string.cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Toast.makeText(mActivity, mActivity.getString(R.string.download_started), Toast.LENGTH_SHORT).show();
                                DownloadFileService.startActionDownload(mActivity, entry.path, mCloudService);
                            }
                        }).show();
            }
        });

    }

    private void addFileClickListener(final CloudFileViewHolder cloudFileViewHolder) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DropboxAPI.Entry entry = cloudFileViewHolder.getDropboxEntry();

                MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                        .title(mActivity.getString(R.string.download_file))
                        .content(mActivity.getString(R.string.download_request)+" \""+entry.fileName()+"\"?")
                        .positiveColor(PreferenceSetter.getAppThemeColor(mActivity))
                        .positiveText(mActivity.getString(R.string.confirm))
                        .negativeColor(PreferenceSetter.getAppThemeColor(mActivity))
                        .negativeText(mActivity.getString(R.string.cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Toast.makeText(mActivity,mActivity.getString(R.string.download_started),Toast.LENGTH_SHORT).show();
                                DownloadFileService.startActionDownload(mActivity, entry.path, mCloudService);
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
                NavigationManager.getInstance().pushPathToCloudStack(cloudFolderViewHolder.getDropboxEntry().path);
                mActivity.refresh();
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
