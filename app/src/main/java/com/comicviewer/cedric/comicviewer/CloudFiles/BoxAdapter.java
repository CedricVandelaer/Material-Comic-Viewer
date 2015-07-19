package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.box.androidsdk.content.models.BoxItem;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.util.ArrayList;

/**
 * Created by CV on 19/06/2015.
 */
public class BoxAdapter extends RecyclerView.Adapter {

    private AbstractCloudServiceListFragment mFragment;
    private Context mContext;
    private ArrayList<BoxItem> mBoxItemList;
    private LayoutInflater mInflater;
    private CloudService mCloudService;

    public BoxAdapter(AbstractCloudServiceListFragment fragment, CloudService cloudService)
    {
        mFragment = fragment;
        mContext = fragment.getActivity();
        mCloudService = cloudService;
        mBoxItemList = new ArrayList<>();
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            cloudFolderViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(StorageManager.getAppThemeColor(mContext)));
            if (StorageManager.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG))
                cloudFolderViewHolder.mDownloadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            addFolderClickListener(cloudFolderViewHolder);
            addDownloadFolderClickListener(cloudFolderViewHolder);
            return cloudFolderViewHolder;
        }
        else {
            v = mInflater.inflate(R.layout.file_card, parent, false);
            CloudFileViewHolder cloudFileViewHolder = new CloudFileViewHolder(v);
            if (StorageManager.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG))
                cloudFileViewHolder.mDownloadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            cloudFileViewHolder.mCardView.setCardBackgroundColor(StorageManager.getAppThemeColor(mContext));
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
    }

    public void addFolderClickListener(final CloudFolderViewHolder cloudFolderViewHolder)
    {
        cloudFolderViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.getNavigationManager().pushToStack(cloudFolderViewHolder.getBoxEntry().getId());
                mFragment.refresh();
            }
        });
    }

    private void addFileClickListener(final CloudFileViewHolder cloudFileViewHolder) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BoxItem entry = cloudFileViewHolder.getBoxEntry();

                MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
                        .title(mContext.getString(R.string.download_file))
                        .content(mContext.getString(R.string.download_request)+" \""+entry.getName()+"\"?")
                        .positiveColor(StorageManager.getAppThemeColor(mContext))
                        .positiveText(mContext.getString(R.string.confirm))
                        .negativeColor(StorageManager.getAppThemeColor(mContext))
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
