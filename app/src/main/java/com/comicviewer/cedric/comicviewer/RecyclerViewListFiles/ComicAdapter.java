package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cédric on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends RecyclerSwipeAdapter<ComicItemViewHolder> {

    private List<Comic> mComicList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int lastPosition=-1;
    private DisplayImageOptions mImageOptions;
    private ComicAdapter mRootAdapterReference = null;

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

    public void setRootAdapter(ComicAdapter comicAdapter)
    {
        mRootAdapterReference = comicAdapter;
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
            v = mInflater.inflate(R.layout.comic_card_swipe, null);
        }
        else if (viewType == 1)
        {
            v = mInflater.inflate(R.layout.small_comic_card_swipe, null);
        }
        else
        {
            v = mInflater.inflate(R.layout.comic_card_image_bg_swipe, null);
        }

        ComicItemViewHolder vh = new ComicItemViewHolder(v);

        return vh;
    }

    private void addClickListener(ComicItemViewHolder vh) {

        View v = vh.mCardView;
        final int pos = vh.getPosition();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DisplayComicActivity.class);
                Comic clickedComic = mComicList.get(pos);

                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Log.d("ItemClick", clickedComic.getTitle());
                intent.putExtra("Comic", clickedComic);

                if (PreferenceSetter.usesRecents(mContext))
                {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public void onBindViewHolder(final ComicItemViewHolder comicItemViewHolder, final int position) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String cardSize = prefs.getString("cardSize",mContext.getString(R.string.card_size_setting_2));

        comicItemViewHolder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        comicItemViewHolder.mSwipeLayout.addSwipeListener(new SimpleSwipeListener());

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

        if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG))
        {
            comicItemViewHolder.mMarkUnreadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            comicItemViewHolder.mMarkReadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            comicItemViewHolder.mDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
        }

        addFavoriteClickListener(comicItemViewHolder);
        addClickListener(comicItemViewHolder);
        addDeleteButtonClickListener(comicItemViewHolder);
        addMarkReadClickListener(comicItemViewHolder);
        addMarkUnreadClickListener(comicItemViewHolder);
    }

    private void addMarkUnreadClickListener(ComicItemViewHolder vh) {
        final int position = vh.getPosition();

        vh.mMarkUnreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getReadComics(mContext).containsKey(mComicList.get(position).getFileName())) {

                    PreferenceSetter.removeReadComic(mContext, mComicList.get(position).getFileName());

                    int pagesRead = PreferenceSetter.getPagesReadForComic(mContext, mComicList.get(position).getFileName());

                    PreferenceSetter.resetSavedPagesForComic(mContext, mComicList.get(position).getFileName());

                    if (pagesRead > 0) {
                        PreferenceSetter.decrementNumberOfComicsStarted(mContext, 1);
                    }

                    if (pagesRead >= mComicList.get(position).getPageCount()) {
                        PreferenceSetter.decrementNumberOfComicsRead(mContext, 1);
                    }

                    PreferenceSetter.decrementPagesForSeries(mContext, mComicList.get(position).getTitle(), pagesRead);

                    notifyItemChanged(position);
                }
                else
                {
                    //Do nothing, already marked as unread
                    Toast message = Toast.makeText(mContext, "You haven't started this comic yet!", Toast.LENGTH_SHORT);
                    message.show();
                }
            }
        });
    }

    private void addDeleteButtonClickListener(final ComicItemViewHolder vh)
    {
        final int position = vh.getPosition();
        String message ="";
        if (mComicList.get(position).getIssueNumber()!=-1) {
            message = mComicList.get(position).getTitle() + " "+mComicList.get(position).getIssueNumber();
        }
        else
        {
            message = mComicList.get(position).getTitle();
        }

        final String finalMessage = message;
        final String fileName = mComicList.get(position).getFileName();

        vh.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you wish to delete "+finalMessage+"?\n" +
                                "This will also remove the file \""+fileName+"\".")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(mComicList.get(position));
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void addMarkReadClickListener(ComicItemViewHolder vh)
    {
        final int position = vh.getPosition();

        vh.mMarkReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getReadComics(mContext).containsKey(mComicList.get(position).getFileName())) {

                    if (PreferenceSetter.getReadComics(mContext).get(mComicList.get(position).getFileName())+1>= mComicList.get(position).getPageCount())
                    {
                        //Do nothing, already marked as read
                        Toast message = Toast.makeText(mContext, "You have already read this comic!", Toast.LENGTH_SHORT);
                        message.show();
                    }
                    else
                    {
                        //Comic was opened but not yet fully read
                        PreferenceSetter.saveLastReadComic(mContext,mComicList.get(position).getFileName(),mComicList.get(position).getPageCount()-1);

                        int pagesRead = PreferenceSetter.getPagesReadForComic(mContext, mComicList.get(position).getFileName());

                        PreferenceSetter.savePagesForComic(mContext, mComicList.get(position).getFileName(), mComicList.get(position).getPageCount());

                        if (pagesRead == 0) {
                            PreferenceSetter.incrementNumberOfComicsStarted(mContext, 1);
                        }

                        if (pagesRead < mComicList.get(position).getPageCount()) {
                            PreferenceSetter.incrementNumberOfComicsRead(mContext, 1);
                        }

                        int extraPagesRead = mComicList.get(position).getPageCount() - pagesRead;
                        PreferenceSetter.incrementPagesForSeries(mContext, mComicList.get(position).getTitle(), extraPagesRead);

                        PreferenceSetter.saveLongestReadComic(mContext,
                                mComicList.get(position).getFileName(),
                                mComicList.get(position).getPageCount(),
                                mComicList.get(position).getTitle(),
                                mComicList.get(position).getIssueNumber());

                        notifyItemChanged(position);
                    }
                }
                else {
                    PreferenceSetter.saveLongestReadComic(mContext,
                            mComicList.get(position).getFileName(),
                            mComicList.get(position).getPageCount(),
                            mComicList.get(position).getTitle(),
                            mComicList.get(position).getIssueNumber());

                    //Comic wasn't opened yet
                    PreferenceSetter.saveLastReadComic(mContext,mComicList.get(position).getFileName(),mComicList.get(position).getPageCount()-1);
                    PreferenceSetter.savePagesForComic(mContext, mComicList.get(position).getFileName(), mComicList.get(position).getPageCount());
                    PreferenceSetter.incrementNumberOfComicsStarted(mContext, 1);
                    PreferenceSetter.incrementNumberOfComicsRead(mContext, 1);
                    PreferenceSetter.incrementPagesForSeries(mContext, mComicList.get(position).getTitle(), mComicList.get(position).getPageCount());
                    notifyItemChanged(position);
                }
            }
        });

    }

    private void addFavoriteClickListener(ComicItemViewHolder vh)
    {
        View v = vh.mFavoriteButton;
        final int position = vh.getPosition();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getFavoriteComics(mContext).contains(mComicList.get(position).getFileName()))
                {
                    PreferenceSetter.removeFavoriteComic(mContext, mComicList.get(position).getFileName());
                }
                else
                {
                    PreferenceSetter.saveFavoriteComic(mContext, mComicList.get(position).getFileName());
                }
                notifyItemChanged(position);
            }
        });
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
        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
        comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getReadComics(mContext).containsKey(mComicList.get(i).getFileName()))
        {

            comicItemViewHolder.mLastReadIcon.setBackground(circle);

            if (PreferenceSetter.getReadComics(mContext).get(mComicList.get(i).getFileName())+1==mComicList.get(i).getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }

            if (PreferenceSetter.getFavoriteComics(mContext).contains(mComicList.get(i).getFileName()))
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
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

        setAnimation(comicItemViewHolder.mCardView, i);


        if (mComicList.get(i).getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(mComicList.get(i).getPrimaryTextColor());

            comicItemViewHolder.mIssueNumber.setTextColor(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mPageCount.setTextColor(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mYear.setTextColor(mComicList.get(i).getPrimaryTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(mComicList.get(i).getComicColor());
        }

        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
        comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getFavoriteComics(mContext).contains(mComicList.get(i).getFileName()))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
        }
        else
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
        }

        if (PreferenceSetter.getReadComics(mContext).containsKey((mComicList.get(i).getFileName())))
        {
            //comicItemViewHolder.mLastReadIcon.setColorFilter(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mLastReadIcon.setBackground(circle);
            if (PreferenceSetter.getReadComics(mContext).get((mComicList.get(i).getFileName()))+1==mComicList.get(i).getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check,comicItemViewHolder.mLastReadIcon);
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
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.comicplaceholder, comicItemViewHolder.mCoverPicture);
        }
        else
        {
            ImageLoader.getInstance().displayImage(mComicList.get(i).getCoverImage(), comicItemViewHolder.mCoverPicture, mImageOptions);

        }

    }

    private void initialiseSmallCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        comicItemViewHolder.mTitle.setText(mComicList.get(i).getTitle());
        if (mComicList.get(i).getIssueNumber()!= -1)
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

        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);

        if (PreferenceSetter.getFavoriteComics(mContext).contains(mComicList.get(i).getFileName()))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
        }
        else
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
        }

        comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getReadComics(mContext).containsKey((mComicList.get(i).getFileName())))
        {
            comicItemViewHolder.mLastReadIcon.setVisibility(View.VISIBLE);
            comicItemViewHolder.mLastReadIcon.setBackground(circle);

            if (PreferenceSetter.getReadComics(mContext).get((mComicList.get(i).getFileName()))+1==mComicList.get(i).getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }

        }
        else
        {
            comicItemViewHolder.mLastReadIcon.setVisibility(View.GONE);
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
        int pos = mComicList.size();
        mComicList.add(pos, comic);
        notifyItemInserted(pos);
    }

    public void addComicSorted(Comic comic)
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
        String scrollAnimPref = prefs.getString("scrollAnimation", mContext.getString(R.string.scroll_animation_setting_1));

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

    public void removeItem(Comic comic)
    {
        if (mComicList.contains(comic)) {

            if (mRootAdapterReference!=null)
            {
                mRootAdapterReference.removeItem(comic);
            }

            String coverImageFileName = comic.getCoverImage();
            if (coverImageFileName!=null && coverImageFileName.startsWith("file:///"))
            {
                coverImageFileName = coverImageFileName.replace("file:///","");
            }

            try
            {
                if (coverImageFileName!=null) {
                    File coverImageFile = new File(coverImageFileName);
                    if (coverImageFile.exists())
                        coverImageFile.delete();
                }

                File archiveFile = new File(comic.getFilePath()+"/"+comic.getFileName());
                if (archiveFile.exists())
                    archiveFile.delete();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            PreferenceSetter.removeSavedComic(mContext, comic);
            int pos = mComicList.indexOf(comic);
            mComicList.remove(comic);
            notifyItemRemoved(pos);
        }
    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe_layout;
    }
}
