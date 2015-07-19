package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
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
    private CloudFragment mFragment;
    private Context mContext;
    private LayoutInflater mInflater;
    private Handler mHandler;

    public CloudListAdapter(CloudFragment fragment)
    {
        mCloudServiceList = new ArrayList<>();
        mFragment = fragment;
        mHandler = new Handler();
        mContext = mFragment.getActivity();
        mCloudServiceList = StorageManager.getCloudServices(mFragment.getActivity());

        this.mInflater = (LayoutInflater) mFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshCloudServiceList()
    {
        mCloudServiceList = StorageManager.getCloudServices(mFragment.getActivity());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.cloud_service_card, parent, false);

        CloudServiceViewHolder cloudServiceViewHolder = new CloudServiceViewHolder(v);

        if (StorageManager.getBackgroundColorPreference(mFragment.getActivity()) == mFragment.getActivity().getResources().getColor(R.color.WhiteBG))
            cloudServiceViewHolder.mDeleteTextView.setTextColor(mFragment.getActivity().getResources().getColor(R.color.Black));

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
                StorageManager.removeCloudService(mFragment.getActivity(), cloudServiceViewHolder.getCloudService().getEmail(), cloudServiceViewHolder.getCloudService().getName());
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

                AbstractCloudServiceListFragment fragment=null;
                String title=null;

                if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_1))) {

                    fragment = DropboxFragment.newInstance(cloudServiceViewHolder.getCloudService());
                    title = mContext.getString(R.string.dropbox);
                }
                else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_3)))
                {
                    fragment = OneDriveFragment.newInstance(cloudServiceViewHolder.getCloudService());
                    title = mContext.getString(R.string.onedrive);
                }
                else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_2)))
                {
                    fragment = GoogleDriveFragment.newInstance(cloudServiceViewHolder.getCloudService());
                    title = "Google Drive";
                }
                else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_4)))
                {
                    fragment = BoxFragment.newInstance(cloudServiceViewHolder.getCloudService());
                    title= "BOX";
                }

                mFragment.setActiveCloudServiceFragment(fragment);
                ((NewDrawerActivity)mFragment.getActivity()).setFragmentInSection(fragment, title);
                //mContext.startActivity(intent);

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

        cloudServiceViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(StorageManager.getAppThemeColor(mFragment.getActivity())));

        if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_1)))
        {
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.dropbox_icon,cloudServiceViewHolder.mLogoImageView);
        }
        else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_3)))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.onedrive_icon, cloudServiceViewHolder.mLogoImageView);
        }
        else  if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_2)))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.drive_icon, cloudServiceViewHolder.mLogoImageView);
        }
        else if (cloudServiceViewHolder.getCloudService().getName().equals(mContext.getString(R.string.cloud_storage_4)))
        {
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.box_icon,cloudServiceViewHolder.mLogoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mCloudServiceList.size();
    }
}
