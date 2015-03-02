package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    public int getItemViewType(int position)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String cardSize = prefs.getString("cardSize",mContext.getString(R.string.card_size_setting_2));

        if (cardSize.equals(mContext.getString(R.string.card_size_setting_2))) {
            return 0;
        }
        else
        {
            return 1;
        }
    }


    @Override
    public ComicItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        
        if (viewType == 0) {
            v = mInflater.inflate(R.layout.comic_card, null);
        }
        else
        {
            v = mInflater.inflate(R.layout.small_comic_card, null);
        }

        ComicItemViewHolder vh = new ComicItemViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ComicItemViewHolder comicItemViewHolder, int i) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String cardSize = prefs.getString("cardSize",mContext.getString(R.string.card_size_setting_2));

        if (cardSize.equals(mContext.getString(R.string.card_size_setting_2))) {
            initialiseNormalCard(comicItemViewHolder,i);
        }
        else
        {
            initialiseSmallCard(comicItemViewHolder, i);
        }
        

    }
    
    private void initialiseNormalCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle());
        if (mComicList.get(i).getIssueNumber()!=-1)
            comicItemViewHolder.mIssueNumber.setText("Issue number: "+mComicList.get(i).getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (mComicList.get(i).getYear()!=-1)
            comicItemViewHolder.mYear.setText("Year: "+mComicList.get(i).getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (mComicList.get(i).getPageCount()!=-1)
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
                comicItemViewHolder.mYear.setTextColor(mContext.getResources().getColor(R.color.White));
            }
            else
            {
                comicItemViewHolder.mIssueNumber.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mPageCount.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mYear.setTextColor(mContext.getResources().getColor(R.color.Black));
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
                    .fit().centerCrop()
                    .into(comicItemViewHolder.mCoverPicture);
        }
        
    }

    private void initialiseSmallCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle());
        if (mComicList.get(i).getIssueNumber()!=-1)
            comicItemViewHolder.mIssueNumber.setText("Issue number: "+mComicList.get(i).getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (mComicList.get(i).getYear()!=-1)
            comicItemViewHolder.mYear.setText(""+mComicList.get(i).getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (mComicList.get(i).getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText(""+mComicList.get(i).getPageCount()+" pages");
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
                comicItemViewHolder.mYear.setTextColor(mContext.getResources().getColor(R.color.White));
            }
            else
            {
                comicItemViewHolder.mIssueNumber.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mPageCount.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mYear.setTextColor(mContext.getResources().getColor(R.color.Black));
            }
            comicItemViewHolder.mCardView.setCardBackgroundColor(mComicList.get(i).mCoverColor);
        }

    }

    private void setAnimation(View viewToAnimate, int position)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String scrollAnimPref = prefs.getString("scrollAnimation", mContext.getString(R.string.scroll_animation_setting_2));
        
        if (scrollAnimPref.equals(mContext.getString(R.string.scroll_animation_setting_1)))
        {
            //No animation
        }
        else if (scrollAnimPref.equals(mContext.getString(R.string.scroll_animation_setting_2)))
        {
            YoYo.with(Techniques.BounceIn)
                    .duration(400)
                    .playOn(viewToAnimate);
        }
        else if (scrollAnimPref.equals(mContext.getString(R.string.scroll_animation_setting_3)))
        {
            YoYo.with(Techniques.StandUp)
                    .duration(700)
                    .playOn(viewToAnimate);
        }
        else if (scrollAnimPref.equals(mContext.getString(R.string.scroll_animation_setting_4)))
        {
            YoYo.with(Techniques.FadeIn)
                    .duration(300)
                    .playOn(viewToAnimate);
        }
        else if (scrollAnimPref.equals(mContext.getString(R.string.scroll_animation_setting_5)))
        {
            YoYo.with(Techniques.Tada)
                    .duration(400)
                    .playOn(viewToAnimate);
        }

        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }
}
