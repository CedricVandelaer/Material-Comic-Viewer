package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Cédric on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends RecyclerView.Adapter<ComicItemViewHolder>{

    private ArrayList<Comic> mComicList;
    private Context mContext;
    private LayoutInflater mInflater;

    public ComicAdapter(Context context, ArrayList<Comic> comics)
    {
        mComicList=comics;
        mContext=context;

        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ComicItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = mInflater.inflate(R.layout.comic_card, null);

        ComicItemViewHolder vh = new ComicItemViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ComicItemViewHolder comicItemViewHolder, int i) {

        final int index = i;
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle());
        if (mComicList.get(i).getIssueNumber()!=-1)
            comicItemViewHolder.mIssueNumber.setText("Issue number: "+mComicList.get(i).getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        comicItemViewHolder.mPageCount.setText("Pages: "+mComicList.get(i).getPageCount());

        if (mComicList.get(i).mCoverColor!=-1) {
            comicItemViewHolder.mCardView.setCardBackgroundColor(mComicList.get(i).mCoverColor);
        }


        if (mComicList.get(i).getCoverImage()==null)
        {
            Picasso.with(mContext)
                    .load(R.drawable.comicplaceholder)
                    .fit().centerCrop()
                    .into(comicItemViewHolder.mCoverPicture);
        }
        else
        {

            Picasso.with(mContext)
                    .load(mComicList.get(i).getCoverImage())
                    .placeholder(R.drawable.comicplaceholder)
                    .fit().centerInside()
                    .into(comicItemViewHolder.mCoverPicture);
        }

    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }
}
