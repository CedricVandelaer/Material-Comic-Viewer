package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

/**
 * Created by CV on 22/06/2015.
 */
public class CollectionsAdapter extends RecyclerView.Adapter{

    private AbstractCollectionsFragment mCollectionsFragment;

    private LayoutInflater mInflater;

    public CollectionsAdapter(AbstractCollectionsFragment fragment)
    {
        mCollectionsFragment = fragment;

        mInflater = LayoutInflater.from(fragment.getActivity());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mInflater.inflate(R.layout.collection_card,parent,false);
        CollectionViewHolder vh = new CollectionViewHolder(v);

        addDeleteClickListener(vh.mDeleteButton, vh);
        addRenameClickListener(vh.mRenameButton, vh);

        return vh;
    }

    private void addRenameClickListener(View v, final CollectionViewHolder vh) {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.mSwipeLayout.close();
                MaterialDialog dialog = new MaterialDialog.Builder(mCollectionsFragment.getActivity())
                        .title(mCollectionsFragment.getString(R.string.rename))
                        .input("Name", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                StorageManager.renameCollection(mCollectionsFragment.getActivity(), vh.getCollectionName(), charSequence.toString());
                                mCollectionsFragment.refresh();
                            }
                        })
                        .positiveColor(StorageManager.getAppThemeColor(mCollectionsFragment.getActivity()))
                        .positiveText(mCollectionsFragment.getString(R.string.confirm))
                        .negativeColor(StorageManager.getAppThemeColor(mCollectionsFragment.getActivity()))
                        .negativeText(mCollectionsFragment.getString(R.string.cancel))
                        .show();
            }
        });
    }

    public void addDeleteClickListener(View v, final CollectionViewHolder vh)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.mSwipeLayout.close();
                MaterialDialog dialog = new MaterialDialog.Builder(mCollectionsFragment.getActivity())
                        .title(mCollectionsFragment.getString(R.string.confirm_delete))
                        .content("This will delete the collection \""+vh.getCollectionName()+"\".\n"+
                                "Are you sure you wish to continue?")
                        .positiveColor(StorageManager.getAppThemeColor(mCollectionsFragment.getActivity()))
                        .positiveText(mCollectionsFragment.getString(R.string.confirm))
                        .negativeColor(StorageManager.getAppThemeColor(mCollectionsFragment.getActivity()))
                        .negativeText(mCollectionsFragment.getString(R.string.cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                StorageManager.removeCollection(mCollectionsFragment.getActivity(), vh.getCollectionName());
                                mCollectionsFragment.refresh();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CollectionViewHolder vh = (CollectionViewHolder) holder;

        vh.setCollectionName(StorageManager.getCollectionList(mCollectionsFragment.getActivity()).get(position).getName());


        vh.mCard.setCardBackgroundColor(Utilities.darkenColor(StorageManager.getAppThemeColor(mCollectionsFragment.getActivity())));

        vh.mTitleTextView.setText(vh.getCollectionName());

        vh.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCollectionsFragment.getNavigationManager().pushToStack(vh.getCollectionName());
                mCollectionsFragment.refresh();
            }
        });

        if (StorageManager.getBackgroundColorPreference(mCollectionsFragment.getActivity())== mCollectionsFragment.getResources().getColor(R.color.WhiteBG)) {
            vh.mDeleteTextView.setTextColor(mCollectionsFragment.getResources().getColor(R.color.Black));
            vh.mRenameTextView.setTextColor(mCollectionsFragment.getResources().getColor(R.color.Black));
        }

    }

    @Override
    public int getItemCount() {
        return StorageManager.getCollectionList(mCollectionsFragment.getActivity()).size();
    }
}
