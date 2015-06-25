package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

import org.json.JSONException;

import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by CV on 22/06/2015.
 */
public class CollectionsAdapter extends RecyclerView.Adapter{

    private CollectionsFragment mCollectionsFragment;

    private LayoutInflater mInflater;

    public CollectionsAdapter(CollectionsFragment fragment)
    {
        mCollectionsFragment = fragment;

        mInflater = LayoutInflater.from(fragment.getActivity());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mInflater.inflate(R.layout.collection_card,parent,false);
        CollectionViewHolder vh = new CollectionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CollectionViewHolder vh = (CollectionViewHolder) holder;

        try {
            vh.setCollectionName(PreferenceSetter.getCollectionList(mCollectionsFragment.getActivity()).getJSONObject(position).keys().next());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            vh.setCollectionName("Unknown");
            vh.itemView.setVisibility(View.GONE);
        }


        vh.mCard.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mCollectionsFragment.getActivity())));

        vh.mTitleTextView.setText(vh.getCollectionName());

        vh.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().pushToCollectionStack(vh.getCollectionName());
                mCollectionsFragment.refresh();
            }
        });


    }

    @Override
    public int getItemCount() {
        return PreferenceSetter.getCollectionList(mCollectionsFragment.getActivity()).length();
    }
}
