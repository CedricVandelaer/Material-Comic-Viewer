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
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
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
public class ComicAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {

    private List<Object> mComicList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int lastPosition=-1;
    private DisplayImageOptions mImageOptions;
    private ComicAdapter mRootAdapterReference = null;

    public ComicAdapter(Context context, List<Comic> comics, boolean dummy)
    {
        setHasStableIds(true);

        ArrayList<Object> comicList = new ArrayList<>();

        for (int i=0;i<comics.size();i++)
        {
            comicList.add(comics.get(i));
        }
        mComicList=comicList;
        mContext=context;
        mImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ComicAdapter(Context context, List<Object> comics)
    {
        setHasStableIds(true);
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
        setHasStableIds(true);
        mComicList= Collections.synchronizedList(new ArrayList<Object>());
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
        if (mComicList.get(position) instanceof Comic) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String cardSize = prefs.getString("cardSize", mContext.getString(R.string.card_size_setting_2));

            if (cardSize.equals(mContext.getString(R.string.card_size_setting_2))) {
                return 0;
            } else if (cardSize.equals(mContext.getString(R.string.card_size_setting_1))) {
                return 1;
            } else {
                return 2;
            }
        }
        else
        {
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        if (viewType<3) {
            if (viewType == 0) {
                v = mInflater.inflate(R.layout.comic_card_swipe, null);
            } else if (viewType == 1) {
                v = mInflater.inflate(R.layout.small_comic_card_swipe, null);
            } else {
                v = mInflater.inflate(R.layout.comic_card_image_bg_swipe, null);
            }

            ComicItemViewHolder comicItemViewHolder = new ComicItemViewHolder(v);

            addFavoriteClickListener(comicItemViewHolder);
            addClickListener(comicItemViewHolder);
            addDeleteButtonClickListener(comicItemViewHolder);
            addMarkReadClickListener(comicItemViewHolder);
            addMarkUnreadClickListener(comicItemViewHolder);

            return comicItemViewHolder;
        }
        else
        {
            v = mInflater.inflate(R.layout.folder_card, null);
            FolderItemViewHolder folderItemViewHolder = new FolderItemViewHolder(v);
            addFolderClickListener(folderItemViewHolder);
            addFolderDeleteClickListener(folderItemViewHolder);
            return folderItemViewHolder;
        }
    }

    private void addFolderDeleteClickListener(final FolderItemViewHolder folderItemViewHolder) {

        folderItemViewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderItemViewHolder.mSwipeLayout.close();
                Log.d("ItemClick", "Delete " + folderItemViewHolder.getFile().getAbsolutePath());
                final String path = folderItemViewHolder.getFile().getAbsolutePath();

                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title("Warning")
                        .content("This will delete the folder:\n\""+folderItemViewHolder.getFile().getName()+"\"\nand all its contents. " +
                                "Are you sure you wish to continue?")
                        .positiveColor(PreferenceSetter.getAppThemeColor(mContext))
                        .positiveText("Accept")
                        .negativeColor(PreferenceSetter.getAppThemeColor(mContext))
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                removeItem(folderItemViewHolder.getFile());
                            }
                        })
                        .show();

            }
        });
    }

    private void addFolderClickListener(final FolderItemViewHolder folderItemViewHolder) {
        folderItemViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ItemClick",folderItemViewHolder.getFile().getAbsolutePath());
                String path = folderItemViewHolder.getFile().getAbsolutePath();
                ComicListFragment.getInstance().navigateToFolder(path);
            }
        });
    }

    private void addClickListener(final ComicItemViewHolder vh) {

        View v = vh.mCardView;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DisplayComicActivity.class);
                Comic clickedComic = (Comic) vh.getComic();

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
    public void onBindViewHolder(final RecyclerView.ViewHolder itemViewHolder, final int position) {

        if (mComicList.get(position) instanceof Comic) {

            ComicItemViewHolder comicItemViewHolder = (ComicItemViewHolder) itemViewHolder;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String cardSize = prefs.getString("cardSize", mContext.getString(R.string.card_size_setting_2));

            comicItemViewHolder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            comicItemViewHolder.mSwipeLayout.addSwipeListener(new SimpleSwipeListener());

            if (cardSize.equals(mContext.getString(R.string.card_size_setting_2))) {
                initialiseNormalCard(comicItemViewHolder, position);
            } else if (cardSize.equals(mContext.getString(R.string.card_size_setting_1))) {
                initialiseSmallCard(comicItemViewHolder, position);
            } else {
                initialiseCardBg(comicItemViewHolder, position);
            }

            if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG)) {
                comicItemViewHolder.mMarkUnreadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mMarkReadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            }

            comicItemViewHolder.setComic((Comic)mComicList.get(position));

        }
        else
        {
            FolderItemViewHolder folderItemViewHolder = (FolderItemViewHolder)itemViewHolder;
            initialiseFolderCard(folderItemViewHolder, position);
            folderItemViewHolder.setFile((File)mComicList.get(position));
        }
    }

    private void initialiseFolderCard(final FolderItemViewHolder folderItemViewHolder, int i)
    {

        File folder = (File) mComicList.get(i);
        folderItemViewHolder.mFolderTitleTextView.setText(folder.getName());
        folderItemViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mContext)));

        if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG)) {

            folderItemViewHolder.mDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
        }

        setAnimation(folderItemViewHolder.mCardView, i);
    }

    public long getIdForObject(Object obj)
    {
        return obj.hashCode();
    }

    public int getPositionForId(long id) {
        for (int i = 0; i < mComicList.size(); i++) {

            long currentItemId = getIdForObject(mComicList.get(i));

            if (id == currentItemId)
                return i;
        }

        return -1;
    }

    private void addMarkUnreadClickListener(final ComicItemViewHolder vh) {

        vh.mMarkUnreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getReadComics(mContext).containsKey((vh.getComic().getFileName()))) {

                    Comic comic = vh.getComic();

                    PreferenceSetter.removeReadComic(mContext, comic.getFileName());

                    int pagesRead = PreferenceSetter.getPagesReadForComic(mContext, comic.getFileName());

                    PreferenceSetter.resetSavedPagesForComic(mContext, comic.getFileName());

                    if (pagesRead > 0) {
                        PreferenceSetter.decrementNumberOfComicsStarted(mContext, 1);
                    }

                    if (pagesRead >= comic.getPageCount()) {
                        PreferenceSetter.decrementNumberOfComicsRead(mContext, 1);
                    }

                    PreferenceSetter.decrementPagesForSeries(mContext, comic.getTitle(), pagesRead);
                    vh.mSwipeLayout.close(true);
                    notifyItemChanged(vh.getPosition());
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

        vh.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = "";

                final Comic comic = vh.getComic();

                if (comic.getIssueNumber()!=-1) {
                    message = comic.getTitle() + " "+comic.getIssueNumber();
                }
                else
                {
                    message = comic.getTitle();
                }

                final String finalMessage = message;
                final String fileName = comic.getFileName();

                vh.mSwipeLayout.close(true);
                new AlertDialog.Builder(mContext)
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you wish to delete "+finalMessage+"?\n" +
                                "This will also remove the file \""+fileName+"\".")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(comic);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void addMarkReadClickListener(final ComicItemViewHolder vh)
    {

        vh.mMarkReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getReadComics(mContext).containsKey(vh.getComic().getFileName())) {

                    if (PreferenceSetter.getReadComics(mContext).get(vh.getComic().getFileName())+1>= vh.getComic().getPageCount())
                    {
                        //Do nothing, already marked as read
                        Toast message = Toast.makeText(mContext, "You have already read this comic!", Toast.LENGTH_SHORT);
                        message.show();
                    }
                    else
                    {
                        //Comic was opened but not yet fully read
                        PreferenceSetter.saveLastReadComic(mContext,vh.getComic().getFileName(),vh.getComic().getPageCount()-1);

                        int pagesRead = PreferenceSetter.getPagesReadForComic(mContext, vh.getComic().getFileName());

                        PreferenceSetter.savePagesForComic(mContext, vh.getComic().getFileName(), vh.getComic().getPageCount());

                        if (pagesRead == 0) {
                            PreferenceSetter.incrementNumberOfComicsStarted(mContext, 1);
                        }

                        if (pagesRead < vh.getComic().getPageCount()) {
                            PreferenceSetter.incrementNumberOfComicsRead(mContext, 1);
                        }

                        int extraPagesRead = vh.getComic().getPageCount() - pagesRead;
                        PreferenceSetter.incrementPagesForSeries(mContext, vh.getComic().getTitle(), extraPagesRead);

                        PreferenceSetter.saveLongestReadComic(mContext,
                                vh.getComic().getFileName(),
                                vh.getComic().getPageCount(),
                                vh.getComic().getTitle(),
                                vh.getComic().getIssueNumber());
                        vh.mSwipeLayout.close(true);
                        notifyItemChanged(vh.getPosition());
                    }
                }
                else {
                    PreferenceSetter.saveLongestReadComic(mContext,
                            vh.getComic().getFileName(),
                            vh.getComic().getPageCount(),
                            vh.getComic().getTitle(),
                            vh.getComic().getIssueNumber());

                    //Comic wasn't opened yet
                    PreferenceSetter.saveLastReadComic(mContext,vh.getComic().getFileName(),vh.getComic().getPageCount()-1);
                    PreferenceSetter.savePagesForComic(mContext, vh.getComic().getFileName(), vh.getComic().getPageCount());
                    PreferenceSetter.incrementNumberOfComicsStarted(mContext, 1);
                    PreferenceSetter.incrementNumberOfComicsRead(mContext, 1);
                    PreferenceSetter.incrementPagesForSeries(mContext, vh.getComic().getTitle(), vh.getComic().getPageCount());
                    vh.mSwipeLayout.close(true);
                    notifyItemChanged(vh.getPosition());
                }
            }
        });

    }

    private void addFavoriteClickListener(final ComicItemViewHolder vh)
    {
        View v = vh.mFavoriteButton;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getFavoriteComics(mContext).contains(vh.getComic().getFileName()))
                {
                    PreferenceSetter.removeFavoriteComic(mContext, vh.getComic().getFileName());
                }
                else
                {
                    PreferenceSetter.saveFavoriteComic(mContext, vh.getComic().getFileName());
                }
                notifyItemChanged(vh.getPosition());
            }
        });
    }

    private void initialiseCardBg(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        final Comic comic = (Comic) mComicList.get(i);

        comicItemViewHolder.mTitle.setText(comic.getTitle()+" "+comic.getIssueNumber());

        int color = comic.getComicColor();
        int transparentColor = Color.argb(235,
                Color.red(color),
                Color.green(color),
                Color.blue(color));
        comicItemViewHolder.mTitle.setBackgroundColor(transparentColor);
        comicItemViewHolder.mTitle.setTextColor(comic.getPrimaryTextColor());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);


        if (comic.getCoverImage()!=null)
        {
            if (comic.getComicColor()!=-1)
            {
                comicItemViewHolder.mCardView.setCardBackgroundColor(comic.getComicColor());
            }
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
                ImageLoader.getInstance().init(config);
            }

            ImageLoader.getInstance().displayImage(comic.getCoverImage(), comicItemViewHolder.mCoverPicture, mImageOptions);
        }
        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
        if (Build.VERSION.SDK_INT>15)
            comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getReadComics(mContext).containsKey(comic.getFileName()))
        {

            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(circle);

            if (PreferenceSetter.getReadComics(mContext).get(comic.getFileName())+1==comic.getPageCount())
            {
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.ic_check,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }

            if (PreferenceSetter.getFavoriteComics(mContext).contains(comic.getFileName()))
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
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(null);
            else
                comicItemViewHolder.mLastReadIcon.getBackground().setAlpha(0);
        }

    }

    private void initialiseNormalCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        final Comic comic = (Comic) mComicList.get(i);

        comicItemViewHolder.mTitle.setText(comic.getTitle());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);

        if (comic.getIssueNumber()!=-1)
            comicItemViewHolder.mIssueNumber.setText("Issue number: "+comic.getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (comic.getYear()!=-1)
            comicItemViewHolder.mYear.setText("Year: "+comic.getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (comic.getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText("Pages: "+comic.getPageCount());
        else
            comicItemViewHolder.mPageCount.setText("");

        setAnimation(comicItemViewHolder.mCardView, i);


        if (comic.getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(comic.getPrimaryTextColor());

            comicItemViewHolder.mIssueNumber.setTextColor(comic.getPrimaryTextColor());
            comicItemViewHolder.mPageCount.setTextColor(comic.getPrimaryTextColor());
            comicItemViewHolder.mYear.setTextColor(comic.getPrimaryTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(comic.getComicColor());
        }

        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
        comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getFavoriteComics(mContext).contains(comic.getFileName()))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
        }
        else
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
        }

        if (PreferenceSetter.getReadComics(mContext).containsKey(comic.getFileName()))
        {
            //comicItemViewHolder.mLastReadIcon.setColorFilter(mComicList.get(i).getPrimaryTextColor());
            comicItemViewHolder.mLastReadIcon.setBackground(circle);
            if (PreferenceSetter.getReadComics(mContext).get((comic.getFileName()))+1==comic.getPageCount())
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

        if (comic.getCoverImage()==null)
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.comicplaceholder, comicItemViewHolder.mCoverPicture);
        }
        else
        {
            ImageLoader.getInstance().displayImage(comic.getCoverImage(), comicItemViewHolder.mCoverPicture, mImageOptions);

        }

    }

    private void initialiseSmallCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        final Comic comic = (Comic) mComicList.get(i);

        comicItemViewHolder.mTitle.setText(comic.getTitle());
        if (comic.getIssueNumber()!= -1)
            comicItemViewHolder.mIssueNumber.setText("Issue number: "+comic.getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (comic.getYear()!=-1)
            comicItemViewHolder.mYear.setText("Year: "+comic.getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (comic.getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText(""+comic.getPageCount()+" pages");
        else
            comicItemViewHolder.mPageCount.setText("");

        setAnimation(comicItemViewHolder.mCardView,i);

        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);

        if (PreferenceSetter.getFavoriteComics(mContext).contains(comic.getFileName()))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
        }
        else
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
        }

        comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getReadComics(mContext).containsKey((comic.getFileName())))
        {
            comicItemViewHolder.mLastReadIcon.setVisibility(View.VISIBLE);
            comicItemViewHolder.mLastReadIcon.setBackground(circle);

            if (PreferenceSetter.getReadComics(mContext).get((comic.getFileName()))+1==comic.getPageCount())
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

        if (comic.getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(comic.getPrimaryTextColor());

            comicItemViewHolder.mIssueNumber.setTextColor(comic.getPrimaryTextColor());
            comicItemViewHolder.mPageCount.setTextColor(comic.getPrimaryTextColor());
            comicItemViewHolder.mYear.setTextColor(comic.getPrimaryTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(comic.getComicColor());
        }

    }

    public List<Comic> getComics()
    {
        ArrayList<Comic> comicList = new ArrayList<>();

        for (int i=0;i<mComicList.size();i++)
        {
            if (mComicList.get(i) instanceof Comic)
            {
                comicList.add((Comic)mComicList.get(i));
            }
        }
        return comicList;
    }

    public List<Object> getComicsAndFiles()
    {
        return mComicList;
    }

    public void clearList()
    {
        mComicList.clear();
        notifyDataSetChanged();
    }

    public void addObject(Object obj)
    {
        if (obj instanceof Comic) {
            int pos = mComicList.size();
            mComicList.add(pos, obj);
            notifyItemInserted(pos);
        }
        else
        {
            mComicList.add(0, obj);
            notifyItemInserted(0);
        }
    }

    public void addObjectSorted(Object obj)
    {
        if (obj instanceof Comic) {

            Comic comic = (Comic) obj;

            if (mComicList.size() == 0) {
                mComicList.add(0, comic);
                notifyItemInserted(0);

            } else {
                for (int i = mComicList.size() - 1; i >= 0; i--) {

                    if (mComicList.get(i) instanceof File) {
                        mComicList.add(i+1, comic);
                        notifyItemInserted(i+1);
                        return;
                    }

                    Comic comicInList = (Comic) mComicList.get(i);

                    if (comic.getTitle().compareToIgnoreCase(comicInList.getTitle()) > 0) {
                        mComicList.add(i + 1, comic);
                        notifyItemInserted(i + 1);
                        return;
                    } else if (comic.getTitle().compareToIgnoreCase(comicInList.getTitle()) == 0) {
                        if (comic.getIssueNumber() > comicInList.getIssueNumber()) {
                            mComicList.add(i + 1, comic);
                            notifyItemInserted(i + 1);
                            return;
                        } else if (comic.getIssueNumber() == comicInList.getIssueNumber()) {
                            mComicList.add(i + 1, comic);
                            notifyItemInserted(i + 1);
                            return;
                        } else if (i == 0) {
                            mComicList.add(i, comic);
                            notifyItemInserted(i);
                            return;
                        }
                    } else if (comic.getTitle().compareToIgnoreCase(comicInList.getTitle()) < 0) {
                        if (i == 0) {
                            mComicList.add(i, comic);
                            notifyItemInserted(i);
                            return;
                        }
                    }

                }
            }
        }
        else
        {
            int i=0;

            while (i<mComicList.size() && mComicList.get(i) instanceof File)
                i++;

            mComicList.add(i,obj);
            notifyItemInserted(i);
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

    public void removeItem(Object obj)
    {

        if (mComicList.contains(obj)) {

            if (mRootAdapterReference!=null)
            {
                mRootAdapterReference.removeItem(obj);
            }

            if (obj instanceof Comic) {

                Comic comic = (Comic) obj;

                String coverImageFileName = comic.getCoverImage();
                if (coverImageFileName != null && coverImageFileName.startsWith("file:///")) {
                    coverImageFileName = coverImageFileName.replace("file:///", "");
                }

                try {
                    if (coverImageFileName != null) {
                        File coverImageFile = new File(coverImageFileName);
                        if (coverImageFile.exists())
                            coverImageFile.delete();
                    }

                    File archiveFile = new File(comic.getFilePath() + "/" + comic.getFileName());
                    if (archiveFile.exists())
                        archiveFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PreferenceSetter.removeSavedComic(mContext, comic);
                int pos = mComicList.indexOf(comic);
                mComicList.remove(comic);
                notifyItemRemoved(pos);
            }
            else
            {
                int pos = mComicList.indexOf(obj);
                new DeleteDirectoryTask().execute(obj);
                mComicList.remove(pos);
                notifyItemRemoved(pos);

            }

        }
    }

    private class DeleteDirectoryTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {
            File directory = (File) params[0];


            Utilities.deleteDirectory(mContext, directory);

            return null;
        }
    }


    @Override
    public long getItemId(int position)
    {
        return getIdForObject(mComicList.get(position));
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
