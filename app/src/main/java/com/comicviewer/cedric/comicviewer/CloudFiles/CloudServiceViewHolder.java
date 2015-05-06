package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 6/05/2015.
 */
public class CloudServiceViewHolder extends RecyclerView.ViewHolder {

    protected TextView mTitleTextView;
    protected CardView mCardView;
    protected ImageView mLogoImageView;
    private CloudService mCloudService = null;

    public CloudServiceViewHolder(View itemView) {
        super(itemView);

        mTitleTextView = (TextView) itemView.findViewById(R.id.cloud_service_title);
        mCardView = (CardView) itemView.findViewById(R.id.card);
        mLogoImageView = (ImageView) itemView.findViewById(R.id.cloud_image_view);
    }

    public void setCloudService(CloudService cloudService)
    {
        mCloudService = cloudService;
    }

    public CloudService getCloudService()
    {
        return mCloudService;
    }
}
