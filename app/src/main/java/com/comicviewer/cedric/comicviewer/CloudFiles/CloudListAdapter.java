package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by CV on 6/05/2015.
 * controller class to map the data of cloud files on their respective views
 */
public class CloudListAdapter extends RecyclerView.Adapter {

    private ArrayList<CloudService> mCloudServiceList;
    private Context mContext;
    private LayoutInflater mInflater;
    private Handler mHandler;

    public CloudListAdapter(Context context)
    {
        mCloudServiceList = new ArrayList<>();
        mContext = context;
        mHandler = new Handler();

        mCloudServiceList = PreferenceSetter.getCloudServices(mContext);

        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshCloudServiceList()
    {
        mCloudServiceList = PreferenceSetter.getCloudServices(mContext);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.cloud_service_card, null);

        CloudServiceViewHolder cloudServiceViewHolder = new CloudServiceViewHolder(v);

        if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG))
            cloudServiceViewHolder.mDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.Black));

        addClickListener(cloudServiceViewHolder);
        addDeleteClickListener(cloudServiceViewHolder);

        return cloudServiceViewHolder;
    }

    private void addDeleteClickListener(final CloudServiceViewHolder cloudServiceViewHolder) {

        cloudServiceViewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = mCloudServiceList.indexOf(cloudServiceViewHolder.getCloudService());
                mCloudServiceList.remove(cloudServiceViewHolder.getCloudService());
                PreferenceSetter.removeCloudService(mContext, cloudServiceViewHolder.getCloudService().getEmail(), cloudServiceViewHolder.getCloudService().getName());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemRemoved(pos);
                    }
                });
            }
        });

    }

    private void addClickListener(final CloudServiceViewHolder cloudServiceViewHolder) {

        cloudServiceViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_1))) {
                    intent = new Intent(mContext, DropboxActivity.class);
                    intent.putExtra("CloudService", cloudServiceViewHolder.getCloudService());

                }
                else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_3)))
                {
                    intent = new Intent(mContext, OneDriveActivity.class);
                    intent.putExtra("CloudService", cloudServiceViewHolder.getCloudService());

                }
                else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_2)))
                {
                    intent = new Intent(mContext, GoogleDriveActivity.class);
                    intent.putExtra("CloudService", cloudServiceViewHolder.getCloudService());
                }

                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CloudServiceViewHolder cloudServiceViewHolder = (CloudServiceViewHolder) holder;

        cloudServiceViewHolder.setCloudService(mCloudServiceList.get(position));
        if (mCloudServiceList.get(position).getUsername()!=null)
            cloudServiceViewHolder.mTitleTextView.setText(mCloudServiceList.get(position).getUsername());
        else
            cloudServiceViewHolder.mTitleTextView.setText(mCloudServiceList.get(position).getName());

        cloudServiceViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mContext)));

        if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_1)))
        {
            ImageLoader.getInstance().displayImage("Drawable://"+R.drawable.dropbox_icon,cloudServiceViewHolder.mLogoImageView);
        }
        else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_3)))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.onedrive_icon, cloudServiceViewHolder.mLogoImageView);
        }
        else  if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_2)))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.drive_icon, cloudServiceViewHolder.mLogoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mCloudServiceList.size();
    }
}
