package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.widget.DataBufferAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by CV on 4/06/2015.
 * adapter for google drive files
 */
public class GoogleDriveAdapter extends RecyclerView.Adapter{

    private ArrayList<Metadata> mFileList;
    private Context mContext;

    public GoogleDriveAdapter(Context context)
    {
        mFileList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setData(DriveApi.MetadataBufferResult data)
    {
        mFileList.clear();
        MetadataBuffer buffer = data.getMetadataBuffer();
        Iterator iterator = buffer.iterator();
        int i=0;
        while (iterator.hasNext()) {
            mFileList.add((Metadata)iterator.next());
            Log.d("GoogleDriveAdapter", mFileList.get(i).getTitle());
            i++;
        }
    }
}
