package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

import org.json.JSONException;

/**
 * Created by CV on 22/06/2015.
 */
public class CollectionsAdapter extends RecyclerView.Adapter{

    private Context mContext;

    private LayoutInflater mInflater;

    public CollectionsAdapter(Context context)
    {
        mContext = context;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mInflater.inflate(R.layout.collection_card,parent,false);
        CollectionViewHolder vh = new CollectionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CollectionViewHolder vh = (CollectionViewHolder) holder;

        vh.mCard.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mContext)));

        try {
            vh.mTitleTextView.setText(PreferenceSetter.getCollectionList(mContext).getJSONObject(position).keys().next());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            vh.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return PreferenceSetter.getCollectionList(mContext).length();
    }
}
