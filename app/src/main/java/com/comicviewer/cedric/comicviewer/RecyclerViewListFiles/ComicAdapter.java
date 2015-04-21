package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cédric on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends RecyclerView.Adapter<ComicItemViewHolder>{

    private List<Comic> mComicList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int lastPosition=-1;
    private DisplayImageOptions mImageOptions;

    public ComicAdapter(Context context, List<Comic> comics)
    {
        mComicList=comics;
        mContext=context;
        mImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ComicAdapter(Context context)
    {
        mComicList= Collections.synchronizedList(new ArrayList<Comic>());
        mContext=context;
        mImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

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
        else if (cardSize.equals(mContext.getString(R.string.card_size_setting_1)))
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }


    @Override
    public ComicItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        if (viewType == 0) {
            v = mInflater.inflate(R.layout.comic_card, null);
        }
        else if (viewType == 1)
        {
            v = mInflater.inflate(R.layout.small_comic_card, null);
        }
        else
        {
            v = mInflater.inflate(R.layout.comic_card_image_bg, null);
        }

        ComicItemViewHolder vh = new ComicItemViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ComicItemViewHolder comicItemViewHolder, int position) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String cardSize = prefs.getString("cardSize",mContext.getString(R.string.card_size_setting_2));

        if (cardSize.equals(mContext.getString(R.string.card_size_setting_2))) {
            initialiseNormalCard(comicItemViewHolder,position);
        }
        else if (cardSize.equals(mContext.getString(R.string.card_size_setting_1)))
        {
            initialiseSmallCard(comicItemViewHolder, position);
        }
        else
        {
            initialiseCardBg(comicItemViewHolder, position);
        }

    }

    private void initialiseCardBg(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle()+" "+mComicList.get(i).getIssueNumber());

        int color = mComicList.get(i).getComicColor();
        int transparentColor = Color.argb(235,
                Color.red(color),
                Color.green(color),
                Color.blue(color));
        comicItemViewHolder.mTitle.setBackgroundColor(transparentColor);
        comicItemViewHolder.mTitle.setTextColor(mComicList.get(i).getPrimaryTextColor());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);

        if (mComicList.get(i).getCoverImage()!=null)
        {
            if (mComicList.get(i).getComicColor()!=-1)
            {
                comicItemViewHolder.mCardView.setCardBackgroundColor(mComicList.get(i).getComicColor());
            }
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
                ImageLoader.getInstance().init(config);
            }



            ImageLoader.getInstance().displayImage(mComicList.get(i).getCoverImage(), comicItemViewHolder.mCoverPicture, mImageOptions);
        }

        if (PreferenceSetter.getReadComics(mContext).containsKey(mComicList.get(i).getFileName()))
        {
            comicItemViewHolder.mLastReadIcon.setColorFilter(mComicList.get(i).getPrimaryTextColor());
            if (Build.VERSION.SDK_INT>20) {
                Drawable circle = mContext.getDrawable(R.drawable.dark_circle);
                circle.setAlpha(255);
                circle.setTintMode(PorterDuff.Mode.SRC);
                circle.setTint(mComicList.get(i).getComicColor());
                comicItemViewHolder.mLastReadIcon.setBackground(circle);
            }
            else
            {
                Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
                circle.setAlpha(255);
                comicItemViewHolder.mLastReadIcon.setBackground(circle);
            }


            if (PreferenceSetter.getReadComics(mContext).get(mComicList.get(i).getFileName())+1==mComicList.get(i).getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check_black_48dp,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }
        }
        else
        {
            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            comicItemViewHolder.mLastReadIcon.setBackground(null);
        }

    }

    private void initialiseNormalCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);

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


        if (mComicList.get(i).getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(mComicList.get(i).getPrimaryTextColor());

            comicItemViewHolder.mIssueNumber.setTextColor(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mPageCount.setTextColor(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mYear.setTextColor(mComicList.get(i).getPrimaryTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(mComicList.get(i).getComicColor());
        }

        if (PreferenceSetter.getReadComics(mContext).containsKey((mComicList.get(i).getFileName())))
        {
            comicItemViewHolder.mLastReadIcon.setColorFilter(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mLastReadIcon.setBackground(mContext.getResources().getDrawable(R.drawable.dark_circle));
            if (PreferenceSetter.getReadComics(mContext).get((mComicList.get(i).getFileName()))+1==mComicList.get(i).getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check_black_48dp,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }

        }
        else
        {
            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            comicItemViewHolder.mLastReadIcon.setBackground(null);
        }

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
            ImageLoader.getInstance().init(config);
        }

        if (mComicList.get(i).getCoverImage()==null)
        {
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.comicplaceholder,comicItemViewHolder.mCoverPicture);
        }
        else
        {
            ImageLoader.getInstance().displayImage(mComicList.get(i).getCoverImage(),comicItemViewHolder.mCoverPicture, mImageOptions);

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
            comicItemViewHolder.mYear.setText("Year: "+mComicList.get(i).getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (mComicList.get(i).getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText(""+mComicList.get(i).getPageCount()+" pages");
        else
            comicItemViewHolder.mPageCount.setText("");

        setAnimation(comicItemViewHolder.mCardView,i);

        if (PreferenceSetter.getReadComics(mContext).containsKey((mComicList.get(i).getFileName())))
        {
            comicItemViewHolder.mLastReadIcon.setColorFilter(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mLastReadIcon.setBackground(mContext.getResources().getDrawable(R.drawable.dark_circle));

            if (PreferenceSetter.getReadComics(mContext).get((mComicList.get(i).getFileName()))+1==mComicList.get(i).getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check_black_48dp,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }

        }
        else
        {
            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            comicItemViewHolder.mLastReadIcon.setBackground(null);
        }

        if (mComicList.get(i).getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(mComicList.get(i).getPrimaryTextColor());

            comicItemViewHolder.mIssueNumber.setTextColor(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mPageCount.setTextColor(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mYear.setTextColor(mComicList.get(i).getPrimaryTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(mComicList.get(i).getComicColor());
        }

    }

    public List<Comic> getComics()
    {
        return mComicList;
    }

    public void clearComicList()
    {
        mComicList.clear();
        notifyDataSetChanged();
    }

    public void addComic(Comic comic)
    {
        if (mComicList.size()==0)
        {
            mComicList.add(0, comic);
            notifyItemInserted(0);

        }
        else
        {
            for (int i=mComicList.size()-1;i>=0;i--)
            {
                if (comic.getTitle().compareToIgnoreCase(mComicList.get(i).getTitle()) > 0)
                {
                    mComicList.add(i + 1, comic);
                    notifyItemInserted(i + 1);
                    return;
                }
                else if (comic.getTitle().compareToIgnoreCase(mComicList.get(i).getTitle()) == 0)
                {
                    if (comic.getIssueNumber() > mComicList.get(i).getIssueNumber())
                    {
                        mComicList.add(i + 1, comic);
                        notifyItemInserted(i+1);
                        return;
                    }
                    else if(comic.getIssueNumber() == mComicList.get(i).getIssueNumber())
                    {
                        mComicList.add(i+1, comic);
                        notifyItemInserted(i+1);
                        return;
                    }
                    else if (i==0)
                    {
                        mComicList.add(i, comic);
                        notifyItemInserted(i);
                        return;
                    }
                }
                else if (comic.getTitle().compareToIgnoreCase(mComicList.get(i).getTitle()) < 0)
                {
                    if (i == 0) {
                        mComicList.add(i, comic);
                        notifyItemInserted(i);
                        return;
                    }
                }

            }
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
