package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
    private int lastPosition=-1;

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String cardColor = prefs.getString("cardColor",mContext.getString(R.string.card_color_setting_1));
        
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle());
        if (mComicList.get(i).getIssueNumber()!=-1)
            comicItemViewHolder.mIssueNumber.setText("Issue number: "+mComicList.get(i).getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (mComicList.get(i).getPageCount()!=0)
            comicItemViewHolder.mPageCount.setText("Pages: "+mComicList.get(i).getPageCount());
        else
            comicItemViewHolder.mPageCount.setText("");
        
        setAnimation(comicItemViewHolder.mCardView,i);
        

        if (mComicList.get(i).mCoverColor!=-1) 
        {
            if (mComicList.get(i).mCoverColor==mContext.getResources().getColor(R.color.Black)
                    || mComicList.get(i).mCoverColor==mContext.getResources().getColor(R.color.BlueGrey))
            {
                comicItemViewHolder.mIssueNumber.setTextColor(mContext.getResources().getColor(R.color.White));
                comicItemViewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.White));
                comicItemViewHolder.mPageCount.setTextColor(mContext.getResources().getColor(R.color.White));
            }
            else
            {
                comicItemViewHolder.mIssueNumber.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mPageCount.setTextColor(mContext.getResources().getColor(R.color.Black));
            }
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

    private void setAnimation(View viewToAnimate, int position)
    {
        YoYo.with(Techniques.BounceIn)
                .duration(400)
                .playOn(viewToAnimate);

        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }
}
