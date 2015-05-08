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
 * Created by Cédric on 6/05/2015.
 *
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

        addClickListener(cloudServiceViewHolder);

        return cloudServiceViewHolder;
    }

    private void addClickListener(final CloudServiceViewHolder cloudServiceViewHolder) {

        cloudServiceViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (cloudServiceViewHolder.getCloudService().getName().equals("Dropbox")) {
                    intent = new Intent(mContext, DropboxActivity.class);
                    intent.putExtra("CloudService", cloudServiceViewHolder.getCloudService());

                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CloudServiceViewHolder cloudServiceViewHolder = (CloudServiceViewHolder) holder;

        cloudServiceViewHolder.setCloudService(mCloudServiceList.get(position));
        cloudServiceViewHolder.mTitleTextView.setText(mCloudServiceList.get(position).getUsername());

        cloudServiceViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mContext)));

        if (mCloudServiceList.get(position).equals("Dropbox"))
        {
            ImageLoader.getInstance().displayImage("Drawable://"+R.drawable.dropbox_icon,cloudServiceViewHolder.mLogoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mCloudServiceList.size();
    }
}
