package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.app.ActivityOptions;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.ComicActions;
import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.Info.InfoActivity;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
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
    private Handler mHandler;

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
        mHandler = new Handler();
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
        mHandler = new Handler();
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
        mHandler = new Handler();
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
            } else if(cardSize.equals(mContext.getString(R.string.card_size_setting_4))) {
                return 4;
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

        if (viewType!=3) {
            if (viewType == 0) {
                v = mInflater.inflate(R.layout.comic_card_swipe, null);
            } else if (viewType == 1) {
                v = mInflater.inflate(R.layout.small_comic_card_swipe, null);
            } else if (viewType == 4) {
                v = mInflater.inflate(R.layout.tiny_comic_card, null);
            }
            else {
                v = mInflater.inflate(R.layout.comic_card_image_bg_swipe, null);
            }

            ComicItemViewHolder comicItemViewHolder = new ComicItemViewHolder(v);

            addFavoriteClickListener(comicItemViewHolder);
            addClickListener(comicItemViewHolder);
            addMarkReadClickListener(comicItemViewHolder);
            addOptionsClickListener(comicItemViewHolder);
            addMangaClickListener(comicItemViewHolder);

            return comicItemViewHolder;
        }
        else
        {
            v = mInflater.inflate(R.layout.folder_card, null);
            FolderItemViewHolder folderItemViewHolder = new FolderItemViewHolder(v);
            addFolderClickListener(folderItemViewHolder);
            addFolderDeleteClickListener(folderItemViewHolder);
            addFolderRenameClickListener(folderItemViewHolder);
            addFolderHideClickListener(folderItemViewHolder.mHideButton, folderItemViewHolder);
            return folderItemViewHolder;
        }
    }

    private void addFolderRenameClickListener(final FolderItemViewHolder folderItemViewHolder) {

        folderItemViewHolder.mRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderItemViewHolder.mSwipeLayout.close();

                if (folderItemViewHolder.getFile().getName().equals("ComicViewer"))
                {
                    Toast.makeText(mContext, mContext.getString(R.string.folder_rename_error), Toast.LENGTH_LONG).show();
                    return;
                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                                .title(mContext.getString(R.string.rename_folder))
                                .negativeText(mContext.getString(R.string.cancel))
                                .negativeColor(PreferenceSetter.getAppThemeColor(mContext))
                                .positiveText(mContext.getString(R.string.confirm))
                                .positiveColor(PreferenceSetter.getAppThemeColor(mContext))
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .input("Folder name",
                                        folderItemViewHolder.getFile().getName(), new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                                materialDialog.dismiss();

                                                String oldPath = folderItemViewHolder.getFile().getAbsolutePath();
                                                String newPath = folderItemViewHolder.getFile().getParentFile().getAbsolutePath()
                                                        + "/" + charSequence;

                                                int pos = mComicList.indexOf(folderItemViewHolder.getFile());

                                                File newFolder = new File(newPath);

                                                if (!newFolder.exists()) {
                                                    new RenameTask().execute(oldPath, newPath, folderItemViewHolder.getFile());

                                                } else {
                                                    Toast.makeText(mContext, mContext.getString(R.string.folder_exists_notice), Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        }
                                ).show();
                    }
                }, 300);
            }
        });

    }

    private class RenameTask extends AsyncTask
    {

        MaterialDialog mDialog;

        @Override
        protected void onPreExecute()
        {
            mDialog = new MaterialDialog.Builder(mContext).title(mContext.getString(R.string.renaming_folder))
                    .content(mContext.getString(R.string.updating_folder_name_notice))
                    .cancelable(false)
                    .progress(true,1,false)
                    .show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            String oldPath = (String) params[0];
            String newPath = (String) params[1];
            File folder = (File) params[2];

            PreferenceSetter.renamePaths(mContext, oldPath, newPath);
            folder.renameTo(new File(newPath));

            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
            ComicListFragment.getInstance().refresh();
        }
    }

    private void addMangaClickListener(final ComicItemViewHolder comicItemViewHolder) {

        comicItemViewHolder.mMangaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Comic comic = comicItemViewHolder.getComic();
                PreferenceSetter.saveMangaComic(mContext, comic);
                PreferenceSetter.removeNormalComic(mContext, comic);
                comicItemViewHolder.mSwipeLayout.close();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pos = mComicList.indexOf(comic);
                        notifyItemChanged(pos);
                    }
                },300);
            }
        });
    }

    private void addFolderDeleteClickListener(final FolderItemViewHolder folderItemViewHolder) {

        folderItemViewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderItemViewHolder.mSwipeLayout.close();
                Log.d("ItemClick", "Delete " + folderItemViewHolder.getFile().getAbsolutePath());
                final String path = folderItemViewHolder.getFile().getAbsolutePath();

                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title(mContext.getString(R.string.warning))
                        .content(mContext.getString(R.string.delete_folder_notice_1)+"\n\""+folderItemViewHolder.getFile().getName()
                                +"\"\n"+ mContext.getString(R.string.delete_folder_notice_2) +" "+
                                mContext.getString(R.string.sure_prompt))
                        .positiveColor(PreferenceSetter.getAppThemeColor(mContext))
                        .positiveText(mContext.getString(R.string.accept))
                        .negativeColor(PreferenceSetter.getAppThemeColor(mContext))
                        .negativeText(mContext.getString(R.string.cancel))
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
                Log.d("ItemClick", folderItemViewHolder.getFile().getAbsolutePath());
                String path = folderItemViewHolder.getFile().getAbsolutePath();
                NavigationManager.getInstance().pushPathToFileStack(path);
                ComicListFragment.getInstance().refresh();
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

            if (cardSize.equals(mContext.getString(R.string.card_size_setting_2)) || cardSize.equals(mContext.getString(R.string.card_size_setting_4))) {
                initialiseNormalCard(comicItemViewHolder, position);
            } else if (cardSize.equals(mContext.getString(R.string.card_size_setting_1))) {
                initialiseSmallCard(comicItemViewHolder, position);
            } else {
                initialiseCardBg(comicItemViewHolder, position);
            }



            if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG)) {
                comicItemViewHolder.mMarkReadTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mOptionsTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
                comicItemViewHolder.mMangaTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            }

            comicItemViewHolder.setComic((Comic) mComicList.get(position));

            comicItemViewHolder.mMangaPicture.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mMangaPicture.setBackground(null);
            comicItemViewHolder.mMangaPicture.setImageDrawable(null);
            if (PreferenceSetter.getMangaSetting(mContext))
            {
                if (PreferenceSetter.isNormalComic(mContext, comicItemViewHolder.getComic()))
                {
                    comicItemViewHolder.mMangaPicture.setVisibility(View.VISIBLE);
                    if (((Comic) mComicList.get(position)).getColorSetting().equals(mContext.getString(R.string.card_color_setting_3))) {
                        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(circle);
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(null);
                    }
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.ic_fire, comicItemViewHolder.mMangaPicture);
                }
            }
            else
            {
                if (PreferenceSetter.isMangaComic(mContext, comicItemViewHolder.getComic()))
                {
                    comicItemViewHolder.mMangaPicture.setVisibility(View.VISIBLE);
                    if (((Comic) mComicList.get(position)).getColorSetting().equals(mContext.getString(R.string.card_color_setting_3))) {
                        Drawable circle = mContext.getResources().getDrawable(R.drawable.dark_circle);
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(circle);
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(null);
                    }
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.ic_fish, comicItemViewHolder.mMangaPicture);
                }
            }

        }
        else
        {
            FolderItemViewHolder folderItemViewHolder = (FolderItemViewHolder)itemViewHolder;
            initialiseFolderCard(folderItemViewHolder, position);
            folderItemViewHolder.setFile((File) mComicList.get(position));
        }
    }

    private void initialiseFolderCard(final FolderItemViewHolder folderItemViewHolder, int i)
    {

        File folder = (File) mComicList.get(i);
        folderItemViewHolder.mFolderTitleTextView.setText(folder.getName());
        folderItemViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(mContext)));

        if (PreferenceSetter.getBackgroundColorPreference(mContext) == mContext.getResources().getColor(R.color.WhiteBG)) {

            folderItemViewHolder.mDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            folderItemViewHolder.mRenameTextView.setTextColor(mContext.getResources().getColor(R.color.Black));
            folderItemViewHolder.mHideTextView.setTextColor(mContext.getResources().getColor(R.color.Black));

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

    private void addFolderHideClickListener(View v, final FolderItemViewHolder vh)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PreferenceSetter.addHiddenPath(mContext, vh.getFile().getAbsolutePath());
                int pos = mComicList.indexOf(vh.getFile());
                mComicList.remove(vh.getFile());
                notifyItemRemoved(pos);
            }
        });
    }

    private void addOptionsClickListener(final ComicItemViewHolder vh)
    {
        vh.mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.mSwipeLayout.close();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                                .title(mContext.getString(R.string.options))
                                .positiveText(mContext.getString(R.string.cancel))
                                .positiveColor(PreferenceSetter.getAppThemeColor(mContext))
                                .customView(R.layout.options_menu_layout, true)
                                .show();

                        FloatingActionButton deleteButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.delete_button);
                        FloatingActionButton markUnreadButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.mark_unread_button);
                        FloatingActionButton normalButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.normal_button);
                        FloatingActionButton infoButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.info_button);
                        FloatingActionButton hideButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.hide_button);
                        FloatingActionButton reloadButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.reload_button);

                        addDeleteButtonClickListener(dialog, deleteButton, vh.getComic());
                        addNormalComicClickListener(dialog, normalButton, vh.getComic());
                        addMarkUnreadClickListener(dialog, markUnreadButton, vh.getComic());
                        addInfoClickListener(dialog, infoButton, vh, vh.getComic());
                        addReloadClickListener(dialog, reloadButton, vh.getComic());
                        addHideClickListener(dialog, hideButton, vh.getComic());
                    }
                }, 300);

            }
        });
    }

    private void addHideClickListener(final MaterialDialog dialog, View v, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                PreferenceSetter.addHiddenPath(mContext, comic.getFilePath() + "/" + comic.getFileName());
                int pos = mComicList.indexOf(comic);
                mComicList.remove(comic);
                notifyItemRemoved(pos);
            }
        });
    }

    private void addReloadClickListener(final MaterialDialog dialog, View v, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                new ReloadComicTask().execute(comic);
            }
        });
    }

    private class ReloadComicTask extends AsyncTask
    {
        MaterialDialog mDialog;
        @Override
        public void onPreExecute()
        {
            mDialog = new MaterialDialog.Builder(mContext).title(mContext.getString(R.string.reloading_comic))
                    .content(mContext.getString(R.string.updating_data_notice))
                    .cancelable(false)
                    .progress(true,1,false)
                    .show();
        }

        @Override
        public Object doInBackground(Object[] params)
        {
            Comic comic = (Comic) params[0];
            Integer pos = mComicList.indexOf(comic);

            ComicLoader.loadComicSync(mContext, comic);

            return pos;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
            Integer pos = (Integer) object;
            notifyItemChanged(pos);
        }
    }

    private void addNormalComicClickListener(final MaterialDialog dialog, View v, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                PreferenceSetter.removeMangaComic(mContext, comic);
                PreferenceSetter.saveNormalComic(mContext, comic);
                int pos = mComicList.indexOf(comic);
                notifyItemChanged(pos);
            }
        });
    }

    private void addInfoClickListener(final MaterialDialog dialog, View v, final ComicItemViewHolder vh, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, InfoActivity.class);
                        intent.putExtra("Comic", comic);
                        if (Build.VERSION.SDK_INT > 20) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ComicListFragment.getInstance().getActivity(),
                                    vh.mCoverPicture, "cover");
                            mContext.startActivity(intent, options.toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }
                    }
                }, 100);

            }
        });
    }

    private void addMarkUnreadClickListener(final MaterialDialog dialog, View v, final Comic comic) {


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if (PreferenceSetter.getReadComics(mContext).containsKey((comic.getFileName()))) {


                    ComicActions.markComicUnread(mContext, comic);
                    int pos = mComicList.indexOf(comic);
                    notifyItemChanged(pos);
                }
                else
                {
                    //Do nothing, already marked as unread
                    Toast message = Toast.makeText(mContext, mContext.getString(R.string.not_started_toast), Toast.LENGTH_SHORT);
                    message.show();
                }
            }
        });

    }

    private void addDeleteButtonClickListener(final MaterialDialog dialog, final View v, final Comic comic)
    {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                String message = "";

                if (comic.getIssueNumber()!=-1) {
                    message = comic.getTitle() + " "+comic.getIssueNumber();
                }
                else
                {
                    message = comic.getTitle();
                }

                final String finalMessage = message;
                final String fileName = comic.getFileName();

                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getString(R.string.confirm_delete))
                        .setMessage(mContext.getString(R.string.sure_delete_prompt)+" "+finalMessage+"?\n" +
                                mContext.getString(R.string.will_also_remove_file)+" \""+fileName+"\".")
                        .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(comic);
                            }

                        })
                        .setNegativeButton(mContext.getString(R.string.no), null)
                        .show();
            }
        });

    }

    private void addMarkReadClickListener(final ComicItemViewHolder vh)
    {
        vh.mMarkReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Comic comic = vh.getComic();
                vh.mSwipeLayout.close(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ComicActions.markComicRead(mContext, comic);
                        int pos = mComicList.indexOf(comic);
                        notifyItemChanged(pos);
                    }
                }, 300);
            }
        });

    }

    private void addFavoriteClickListener(final ComicItemViewHolder vh)
    {
        View v = vh.mFavoriteButton;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceSetter.getFavoriteComics(mContext).contains(vh.getComic().getFileName())) {
                    PreferenceSetter.removeFavoriteComic(mContext, vh.getComic().getFileName());
                } else {
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


        }
        else
        {
            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(null);
            else
                comicItemViewHolder.mLastReadIcon.getBackground().setAlpha(0);
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

    private void initialiseNormalCard(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        final Comic comic = (Comic) mComicList.get(i);

        comicItemViewHolder.mTitle.setText(comic.getTitle());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);

        if (comic.getIssueNumber()!=-1)
            comicItemViewHolder.mIssueNumber.setText(mContext.getString(R.string.issue_number)+": "+comic.getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (comic.getYear()!=-1)
            comicItemViewHolder.mYear.setText(mContext.getString(R.string.year)+": "+comic.getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (comic.getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText(mContext.getString(R.string.pages)+": "+comic.getPageCount());
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
        if (comic.getColorSetting().equals(mContext.getString(R.string.card_color_setting_3))) {
            if (Build.VERSION.SDK_INT > 15)
                comicItemViewHolder.mFavoriteButton.setBackground(circle);
        }
        else
        {
            if (Build.VERSION.SDK_INT > 15)
                comicItemViewHolder.mFavoriteButton.setBackground(null);
        }

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
            if (comic.getColorSetting().equals(mContext.getString(R.string.card_color_setting_3))) {
                if (Build.VERSION.SDK_INT > 15)
                    comicItemViewHolder.mLastReadIcon.setBackground(circle);
            }
            else
            {
                if (Build.VERSION.SDK_INT > 15)
                    comicItemViewHolder.mLastReadIcon.setBackground(null);
            }
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
            if (Build.VERSION.SDK_INT>15)
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
            comicItemViewHolder.mIssueNumber.setText(mContext.getString(R.string.issue_number)+": "+comic.getIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (comic.getYear()!=-1)
            comicItemViewHolder.mYear.setText(mContext.getString(R.string.year)+": "+comic.getYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (comic.getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText(""+comic.getPageCount()+" "+mContext.getString(R.string.pages_no_capital));
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

        if (Build.VERSION.SDK_INT>15)
            comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (PreferenceSetter.getReadComics(mContext).containsKey((comic.getFileName())))
        {
            comicItemViewHolder.mLastReadIcon.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT>15)
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
            if (Build.VERSION.SDK_INT>15)
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
