package com.comicviewer.cedric.comicviewer.ComicListFiles;

/**
 * Created by CV on 24/06/2015.
 */

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.comicviewer.cedric.comicviewer.CollectionActions;
import com.comicviewer.cedric.comicviewer.ComicActions;
import com.comicviewer.cedric.comicviewer.ComicLoader;
import com.comicviewer.cedric.comicviewer.Info.InfoActivity;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Sorter;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by CV on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public abstract class AbstractComicAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {

    protected List<Object> mComicList;
    protected AbstractComicListFragment mListFragment;
    protected LayoutInflater mInflater;
    protected int lastPosition=-1;
    protected DisplayImageOptions mImageOptions;
    protected ComicAdapter mRootAdapterReference = null;
    protected Handler mHandler;

    protected MultiSelector mMultiSelector;
    protected ActionMode mActionMode;

    protected ComicListActionModeCallback mEditModeCallback = new ComicListActionModeCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mListFragment.getActivity().getMenuInflater().inflate(R.menu.edit_menu, menu);

            return true;
        }


        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.mark_read_menu_action:
                    multiMarkRead(getSelectedComics());
                    return true;
                case R.id.mark_unread_menu_action:
                    multiMarkUnread(getSelectedComics());
                    return true;
                case R.id.manga_menu_action:
                    multiMakeManga(getSelectedComics());
                    return true;
                case R.id.normal_menu_action:
                    multiMakeNormal(getSelectedComics());
                    return true;
                case R.id.add_collection_menu_action:
                    multiAddToCollection(getSelectedComics());
                    return true;
                case R.id.remove_collection_menu_action:
                    multiRemoveFromCollection(getSelectedComics());
                    return true;
                case R.id.hide_menu_action:
                    multiHideComics(getSelectedComics());
                    return true;
                case R.id.delete_menu_action:
                    multiDelete(getSelectedComics());
                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            List<Integer> selectedItems = mMultiSelector.getSelectedPositions();

            super.onDestroyActionMode(actionMode);

            for (Integer pos:selectedItems)
            {
                notifyItemChanged(pos);
            }

        }

    };

    protected abstract void multiHideComics(ArrayList<Comic> comics);

    protected abstract void multiAddToCollection(ArrayList<Comic> comics);

    protected void multiRemoveFromCollection(final ArrayList<Comic> comics)
    {
        ArrayList<String> collections = StorageManager.getCollectionNames(mListFragment.getActivity());
        CharSequence[] charCollectionNames = new CharSequence[collections.size()];
        for (int i=0;i<collections.size();i++)
        {
            charCollectionNames[i] = collections.get(i);
        }
        MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                .title("Add to collection")
                .negativeColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                .negativeText(mListFragment.getString(R.string.cancel))
                .items(charCollectionNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        CollectionActions.removeComicsFromCollection(mListFragment.getActivity(), charSequence.toString(), comics);
                        mActionMode.finish();
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onAny(MaterialDialog dialog) {
                        super.onAny(dialog);
                        mActionMode.finish();
                    }
                }).show();
    }


    protected ArrayList<Comic> getSelectedComics()
    {
        List<Integer> positions = mMultiSelector.getSelectedPositions();
        ArrayList<Comic> selectedComics = new ArrayList<>();
        for (Integer pos:positions)
        {
            selectedComics.add((Comic) mComicList.get(pos));
        }
        return selectedComics;
    }

    public void multiMakeManga(final ArrayList<Comic> comicsToMakeManga)
    {
        ComicActions.makeMangaComics(mListFragment.getActivity(), comicsToMakeManga);
        notifyDataSetChanged();
        mActionMode.finish();
    }

    public void multiMakeNormal(final ArrayList<Comic> comicsToMakeNormal)
    {
        ComicActions.makeNormalComics(mListFragment.getActivity(), comicsToMakeNormal);
        notifyDataSetChanged();
        mActionMode.finish();
    }

    public void multiMarkRead(final ArrayList<Comic> comicsToMark)
    {
        for (Comic comic:comicsToMark)
        {
            ComicActions.markComicRead(mListFragment.getActivity(), comic, false);
        }
        notifyDataSetChanged();
        mActionMode.finish();
    }

    public void multiMarkUnread(final ArrayList<Comic> comicsToMark)
    {
        for (Comic comic:comicsToMark) {
            ComicActions.markComicUnread(mListFragment.getActivity(),comic);
        }
        notifyDataSetChanged();
        mActionMode.finish();
    }

    public void multiDelete(final ArrayList<Comic> comicsToRemove)
    {
        new MaterialDialog.Builder(mListFragment.getActivity())
                .title(mListFragment.getString(R.string.confirm_delete))
                .content("Are you sure you want to delete "+comicsToRemove.size()+" items?")
                .positiveText(mListFragment.getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                .negativeColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                .negativeText(mListFragment.getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        removeComics(comicsToRemove);
                    }
                    @Override
                    public void onAny(MaterialDialog dialog)
                    {
                        mActionMode.finish();
                    }
                }).show();
    }

    public AbstractComicAdapter(AbstractComicListFragment context, List<Object> comics, MultiSelector multiSelector)
    {
        setHasStableIds(true);
        mComicList=comics;
        mListFragment=context;
        mMultiSelector = multiSelector;
        mEditModeCallback.setMultiSelector(multiSelector);
        mImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();
        mHandler = new Handler();
        this.mInflater = (LayoutInflater) context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public AbstractComicAdapter(AbstractComicListFragment context, MultiSelector multiSelector)
    {
        setHasStableIds(true);
        mComicList= Collections.synchronizedList(new ArrayList<Object>());
        mListFragment=context;
        mMultiSelector = multiSelector;
        mEditModeCallback.setMultiSelector(multiSelector);
        mImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mHandler = new Handler();
        this.mInflater = (LayoutInflater) context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRootAdapter(ComicAdapter comicAdapter)
    {
        mRootAdapterReference = comicAdapter;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mComicList.get(position) instanceof Comic) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mListFragment.getActivity());
            String cardSize = prefs.getString("cardSize", mListFragment.getActivity().getString(R.string.card_size_setting_2));

            if (cardSize.equals(mListFragment.getActivity().getString(R.string.card_size_setting_2))) {
                return 0;
            } else if (cardSize.equals(mListFragment.getActivity().getString(R.string.card_size_setting_1))) {
                return 1;
            } else if(cardSize.equals(mListFragment.getActivity().getString(R.string.card_size_setting_4))) {
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
                v = mInflater.inflate(R.layout.comic_card_swipe, viewGroup, false);
            } else if (viewType == 1) {
                v = mInflater.inflate(R.layout.small_comic_card_swipe, viewGroup, false);
            } else if (viewType == 4) {
                v = mInflater.inflate(R.layout.tiny_comic_card, viewGroup, false);
            }
            else {
                v = mInflater.inflate(R.layout.comic_card_image_bg_swipe, viewGroup, false);
            }

            ComicItemViewHolder comicItemViewHolder = new ComicItemViewHolder(v, mMultiSelector);

            addFavoriteClickListener(comicItemViewHolder);
            addClickListener(comicItemViewHolder);
            addOptionsClickListener(comicItemViewHolder);

            return comicItemViewHolder;
        }
        else
        {
            v = mInflater.inflate(R.layout.folder_card, viewGroup, false);
            FolderItemViewHolder folderItemViewHolder = new FolderItemViewHolder(v);
            addFolderClickListener(folderItemViewHolder);
            addFolderAddToCollectionClickListener(null, folderItemViewHolder.mAddToCollectionButton, folderItemViewHolder);
            addFolderRenameClickListener(folderItemViewHolder);
            addFolderOptionsClickListener(folderItemViewHolder);
            return folderItemViewHolder;
        }
    }



    protected abstract void addFolderAddToCollectionClickListener(MaterialDialog dialog,  View v, FolderItemViewHolder folderItemViewHolder);


    private void addFolderRenameClickListener(final FolderItemViewHolder folderItemViewHolder) {

        folderItemViewHolder.mRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderItemViewHolder.mSwipeLayout.close();

                if (folderItemViewHolder.getFile().getName().equals("ComicViewer"))
                {
                    Toast.makeText(mListFragment.getActivity(), mListFragment.getActivity().getString(R.string.folder_rename_error), Toast.LENGTH_LONG).show();
                    return;
                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                                .title(mListFragment.getActivity().getString(R.string.rename_folder))
                                .negativeText(mListFragment.getActivity().getString(R.string.cancel))
                                .negativeColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                                .positiveText(mListFragment.getActivity().getString(R.string.confirm))
                                .positiveColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
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
                                                    Toast.makeText(mListFragment.getActivity(), mListFragment.getActivity().getString(R.string.folder_exists_notice), Toast.LENGTH_LONG).show();
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
            mDialog = new MaterialDialog.Builder(mListFragment.getActivity()).title(mListFragment.getActivity().getString(R.string.renaming_folder))
                    .content(mListFragment.getActivity().getString(R.string.updating_folder_name_notice))
                    .cancelable(false)
                    .progress(true,1,false)
                    .show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            String oldPath = (String) params[0];
            String newPath = (String) params[1];
            File folder = (File) params[2];

            StorageManager.renamePaths(mListFragment.getActivity(), oldPath, newPath);
            folder.renameTo(new File(newPath));

            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
            //ComicListFragment.getInstance().refresh();
            ComicListFragment.getInstance().refresh();
        }
    }

    private void addNormalClickListener(final ComicItemViewHolder comicItemViewHolder) {

        comicItemViewHolder.mMangaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Comic comic = comicItemViewHolder.getComic();
                StorageManager.removeMangaComic(mListFragment.getActivity(), comic);
                StorageManager.saveNormalComic(mListFragment.getActivity(), comic);
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

    private void addMangaClickListener(final ComicItemViewHolder comicItemViewHolder) {

        comicItemViewHolder.mMangaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Comic comic = comicItemViewHolder.getComic();
                StorageManager.saveMangaComic(mListFragment.getActivity(), comic);
                StorageManager.removeNormalComic(mListFragment.getActivity(), comic);
                comicItemViewHolder.mSwipeLayout.close();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pos = mComicList.indexOf(comic);
                        notifyItemChanged(pos);
                    }
                }, 300);
            }
        });
    }


    private void addFolderClickListener(final FolderItemViewHolder folderItemViewHolder) {
        folderItemViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ItemClick", folderItemViewHolder.getFile().getAbsolutePath());
                String path = folderItemViewHolder.getFile().getAbsolutePath();
                mListFragment.getNavigationManager().pushToStack(path);
                mListFragment.refresh();
            }
        });
    }

    private void addClickListener(final ComicItemViewHolder vh) {

        View v = vh.mCardView;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(vh)) {
                    Intent intent = new Intent(mListFragment.getActivity(), DisplayComicActivity.class);
                    Comic clickedComic = (Comic) vh.getComic();

                    InputMethodManager imm = (InputMethodManager) mListFragment.getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Log.d("ItemClick", clickedComic.getEditedTitle());
                    intent.putExtra("Comic", clickedComic);

                    if (StorageManager.getBooleanSetting(mListFragment.getActivity(), StorageManager.USES_RECENTS, true)) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }

                    mListFragment.getActivity().startActivity(intent);
                }
                else
                {
                    vh.showSelectedLayout(vh.isActivated());

                    if (mMultiSelector.getSelectedPositions().size() < 1) {
                        mMultiSelector.setSelectable(false);
                        mActionMode.finish();
                    }
                }
            }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMultiSelector.setSelectable(true);
                mMultiSelector.setSelected(vh, true);
                vh.showSelectedLayout(vh.isActivated());
                mActionMode = mListFragment.getActivity().startActionMode(mEditModeCallback);
                return true;
            }
        });

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder itemViewHolder, final int position) {


        if (mComicList.get(position) instanceof Comic) {

            ComicItemViewHolder comicItemViewHolder = (ComicItemViewHolder) itemViewHolder;

            comicItemViewHolder.showSelectedLayout(comicItemViewHolder.isActivated());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mListFragment.getActivity());
            String cardSize = prefs.getString("cardSize", mListFragment.getActivity().getString(R.string.card_size_setting_2));

            comicItemViewHolder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            comicItemViewHolder.mSwipeLayout.addSwipeListener(new SimpleSwipeListener());

            if (cardSize.equals(mListFragment.getActivity().getString(R.string.card_size_setting_2)) || cardSize.equals(mListFragment.getActivity().getString(R.string.card_size_setting_4))) {
                initialiseNormalCard(comicItemViewHolder, position);
            } else if (cardSize.equals(mListFragment.getActivity().getString(R.string.card_size_setting_1))) {
                initialiseSmallCard(comicItemViewHolder, position);
            } else {
                initialiseCardBg(comicItemViewHolder, position);
            }


            if (StorageManager.hasWhiteBackgroundSet(mListFragment.getActivity())) {
                comicItemViewHolder.mMarkReadTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.Black));
                comicItemViewHolder.mOptionsTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.Black));
                comicItemViewHolder.mMangaTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.Black));
            }
            else
            {
                comicItemViewHolder.mMarkReadTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.White));
                comicItemViewHolder.mOptionsTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.White));
                comicItemViewHolder.mMangaTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.White));
            }

            comicItemViewHolder.setComic((Comic) mComicList.get(position));

            comicItemViewHolder.mMangaPicture.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mMangaPicture.setBackground(null);
            comicItemViewHolder.mMangaPicture.setImageDrawable(null);

            if (StorageManager.isMangaComic(mListFragment.getActivity(), comicItemViewHolder.getComic())) {
                // Change button
                comicItemViewHolder.mMangaButton.setImageResource(R.drawable.fab_fire);
                comicItemViewHolder.mMangaButton.setColorNormal(mListFragment.getResources().getColor(R.color.LightGreen));
                comicItemViewHolder.mMangaButton.setColorPressed(mListFragment.getResources().getColor(R.color.LightGreenDark));
                comicItemViewHolder.mMangaButton.setColorRipple(mListFragment.getResources().getColor(R.color.LightGreenLight));
                comicItemViewHolder.mMangaTextView.setText(mListFragment.getString(R.string.normal_comic));
                addNormalClickListener(comicItemViewHolder);
                ///////////
            }
            else
            {
                comicItemViewHolder.mMangaButton.setImageResource(R.drawable.fab_fish);
                comicItemViewHolder.mMangaButton.setColorNormal(mListFragment.getResources().getColor(R.color.Orange));
                comicItemViewHolder.mMangaButton.setColorPressed(mListFragment.getResources().getColor(R.color.OrangeDark));
                comicItemViewHolder.mMangaButton.setColorRipple(mListFragment.getResources().getColor(R.color.OrangeLight));
                comicItemViewHolder.mMangaTextView.setText(mListFragment.getString(R.string.manga_comic));
                addMangaClickListener(comicItemViewHolder);
            }
            if (StorageManager.getBooleanSetting(mListFragment.getActivity(), StorageManager.MANGA_SETTING,false))
            {
                if (StorageManager.isNormalComic(mListFragment.getActivity(), comicItemViewHolder.getComic()))
                {
                    comicItemViewHolder.mMangaPicture.setVisibility(View.VISIBLE);
                    if (((Comic) mComicList.get(position)).getColorSetting().equals(mListFragment.getActivity().getString(R.string.card_color_setting_3))
                            || cardSize.equals(mListFragment.getString(R.string.card_size_setting_3))) {
                        Drawable circle = mListFragment.getActivity().getResources().getDrawable(R.drawable.dark_circle);
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(circle);
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(null);
                    }
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.fab_fire, comicItemViewHolder.mMangaPicture);
                }
            }
            else
            {
                if (StorageManager.isMangaComic(mListFragment.getActivity(), comicItemViewHolder.getComic()))
                {
                    comicItemViewHolder.mMangaPicture.setVisibility(View.VISIBLE);
                    if (((Comic) mComicList.get(position)).getColorSetting().equals(mListFragment.getActivity().getString(R.string.card_color_setting_3))
                            || cardSize.equals(mListFragment.getString(R.string.card_size_setting_3))) {
                        Drawable circle = mListFragment.getActivity().getResources().getDrawable(R.drawable.dark_circle);
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(circle);
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT > 15)
                            comicItemViewHolder.mMangaPicture.setBackground(null);
                    }
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.fab_fish, comicItemViewHolder.mMangaPicture);
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
        folderItemViewHolder.mCardView.setCardBackgroundColor(Utilities.darkenColor(StorageManager.getAppThemeColor(mListFragment.getActivity())));

        if (StorageManager.getBackgroundColorPreference(mListFragment.getActivity()) == mListFragment.getActivity().getResources().getColor(R.color.WhiteBG)) {

            folderItemViewHolder.mAddToCollectionTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.Black));
            folderItemViewHolder.mRenameTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.Black));
            folderItemViewHolder.mOptionsTextView.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.Black));

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

    abstract void addFolderHideClickListener(MaterialDialog dialog, View v, final FolderItemViewHolder vh);
    abstract void addFolderMarkUnreadClickListener(MaterialDialog dialog, View v, final FolderItemViewHolder vh);
    abstract void addFolderMarkReadClickListener(MaterialDialog dialog, View v, final FolderItemViewHolder vh);

    private void addFolderDeleteClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder folderItemViewHolder) {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null)
                    dialog.dismiss();
                folderItemViewHolder.mSwipeLayout.close();
                Log.d("ItemClick", "Delete " + folderItemViewHolder.getFile().getAbsolutePath());
                final String path = folderItemViewHolder.getFile().getAbsolutePath();

                MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                        .title(mListFragment.getActivity().getString(R.string.warning))
                        .content(mListFragment.getActivity().getString(R.string.delete_folder_notice_1)+"\n\""+folderItemViewHolder.getFile().getName()
                                +"\"\n"+ mListFragment.getActivity().getString(R.string.delete_folder_notice_2) +" "+
                                mListFragment.getActivity().getString(R.string.sure_prompt))
                        .positiveColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                        .positiveText(mListFragment.getActivity().getString(R.string.accept))
                        .negativeColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                        .negativeText(mListFragment.getActivity().getString(R.string.cancel))
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

    private void addFolderOptionsClickListener(final FolderItemViewHolder vh) {
        vh.mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.mSwipeLayout.close();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                                .title(mListFragment.getActivity().getString(R.string.options))
                                .positiveText(mListFragment.getActivity().getString(R.string.cancel))
                                .positiveColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                                .customView(R.layout.folders_options_menu, true)
                                .show();

                        FloatingActionButton deleteButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.delete_button);
                        FloatingActionButton markUnreadButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.mark_unread_button);
                        FloatingActionButton markReadButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.mark_read_button);
                        FloatingActionButton hideButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.hide_button);

                        addFolderDeleteClickListener(dialog, deleteButton, vh);
                        addFolderMarkUnreadClickListener(dialog, markUnreadButton, vh);
                        addFolderHideClickListener(dialog, hideButton, vh);
                        addFolderMarkReadClickListener(dialog, markReadButton, vh);
                    }
                }, 300);

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
                        MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                                .title(mListFragment.getActivity().getString(R.string.options))
                                .positiveText(mListFragment.getActivity().getString(R.string.cancel))
                                .positiveColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                                .customView(R.layout.options_menu_layout, true)
                                .show();

                        FloatingActionButton deleteButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.delete_button);
                        FloatingActionButton infoButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.info_button);
                        FloatingActionButton hideButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.hide_button);
                        FloatingActionButton reloadButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.reload_button);
                        FloatingActionButton addToCollectionButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.add_collection_button);

                        FloatingActionButton removeFromCollectionButton = (FloatingActionButton) dialog.getCustomView().findViewById(R.id.remove_collection_button);
                        LinearLayout removeFromCollectionLayout = (LinearLayout) dialog.getCustomView().findViewById(R.id.remove_collection_layout);

                        addDeleteButtonClickListener(dialog, deleteButton, vh.getComic());
                        addInfoClickListener(dialog, infoButton, vh, vh.getComic());
                        addReloadClickListener(dialog, reloadButton, vh.getComic());
                        addHideClickListener(dialog, hideButton, vh.getComic());
                        addAddToCollectionClickListener(dialog, addToCollectionButton, vh.getComic());
                        addRemoveFromCollectionClickListener(dialog, removeFromCollectionButton, removeFromCollectionLayout, vh.getComic());
                    }
                }, 300);

            }
        });
    }

    protected void addRemoveFromCollectionClickListener(final MaterialDialog dialog, View button, View layout, final Comic comic)
    {
        final ArrayList<String> containingCollections = CollectionActions.getContainingCollections(mListFragment.getActivity(), comic);
        if (containingCollections.size()>0) {
            button.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null)
                        dialog.dismiss();

                    CharSequence[] collections = new CharSequence[containingCollections.size()];
                    for (int i=0;i<containingCollections.size();i++)
                    {
                        collections[i] = containingCollections.get(i);
                    }
                    new MaterialDialog.Builder(mListFragment.getActivity())
                            .title("Choose collection")
                            .items(collections)
                            .negativeColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                            .negativeText(mListFragment.getString(R.string.cancel))
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    StorageManager.removeComicFromCollection(mListFragment.getActivity(), charSequence.toString(), comic);
                                }
                            }).show();

                }
            });
        }
        else
        {
            layout.setVisibility(View.GONE);
        }
    }

    protected abstract void addAddToCollectionClickListener(final MaterialDialog dialog, View v, final Comic comic);


    abstract void addHideClickListener(final MaterialDialog dialog, View v, final Comic comic);


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
            mDialog = new MaterialDialog.Builder(mListFragment.getActivity()).title(mListFragment.getActivity().getString(R.string.reloading_comic))
                    .content(mListFragment.getActivity().getString(R.string.updating_data_notice))
                    .cancelable(false)
                    .progress(true,1,false)
                    .show();
        }

        @Override
        public Object doInBackground(Object[] params)
        {
            Comic comic = (Comic) params[0];
            Integer pos = mComicList.indexOf(comic);

            ComicLoader.loadComicSync(mListFragment.getActivity(), comic);

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
                StorageManager.removeMangaComic(mListFragment.getActivity(), comic);
                StorageManager.saveNormalComic(mListFragment.getActivity(), comic);
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
                        Intent intent = new Intent(mListFragment.getActivity(), InfoActivity.class);
                        intent.putExtra("Comic", comic);
                        if (Build.VERSION.SDK_INT > 20) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mListFragment.getActivity(),
                                    vh.mCoverPicture, "cover");
                            mListFragment.getActivity().startActivity(intent, options.toBundle());
                        } else {
                            mListFragment.getActivity().startActivity(intent);
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

                if (dialog!=null)
                    dialog.dismiss();

                if (StorageManager.getReadComics(mListFragment.getActivity()).containsKey((comic.getFileName()))) {


                    ComicActions.markComicUnread(mListFragment.getActivity(), comic);
                    int pos = mComicList.indexOf(comic);
                    notifyItemChanged(pos);
                }
                else
                {
                    //Do nothing, already marked as unread
                    Toast message = Toast.makeText(mListFragment.getActivity(), mListFragment.getActivity().getString(R.string.not_started_toast), Toast.LENGTH_SHORT);
                    message.show();
                }
            }
        });

    }

    private void addMarkUnreadClickListener(final ComicItemViewHolder vh) {


        vh.mMarkReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vh.mSwipeLayout.close(true);

                if (StorageManager.getReadComics(mListFragment.getActivity()).containsKey((vh.getComic().getFileName()))) {
                    ComicActions.markComicUnread(mListFragment.getActivity(), vh.getComic());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = mComicList.indexOf(vh.getComic());
                            notifyItemChanged(pos);
                        }
                    }, 300);
                } else {
                    //Do nothing, already marked as unread
                    Toast message = Toast.makeText(mListFragment.getActivity(), mListFragment.getActivity().getString(R.string.not_started_toast), Toast.LENGTH_SHORT);
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

                if (comic.getEditedIssueNumber()!=-1) {
                    message = comic.getEditedTitle() + " "+comic.getEditedIssueNumber();
                }
                else
                {
                    message = comic.getEditedTitle();
                }

                final String finalMessage = message;
                final String fileName = comic.getFileName();

                new AlertDialog.Builder(mListFragment.getActivity())
                        .setTitle(mListFragment.getActivity().getString(R.string.confirm_delete))
                        .setMessage(mListFragment.getActivity().getString(R.string.sure_delete_prompt)+" "+finalMessage+"?\n" +
                                mListFragment.getActivity().getString(R.string.will_also_remove_file)+" \""+fileName+"\".")
                        .setPositiveButton(mListFragment.getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(comic);
                            }

                        })
                        .setNegativeButton(mListFragment.getActivity().getString(R.string.no), null)
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
                        ComicActions.markComicRead(mListFragment.getActivity(), comic, true);
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
                if (StorageManager.getFavoriteComics(mListFragment.getActivity()).contains(vh.getComic().getFileName())) {
                    StorageManager.removeFavoriteComic(mListFragment.getActivity(), vh.getComic().getFileName());
                } else {
                    StorageManager.saveFavoriteComic(mListFragment.getActivity(), vh.getComic().getFileName());
                }
                notifyItemChanged(vh.getPosition());
            }
        });
    }

    private void initialiseCardBg(final ComicItemViewHolder comicItemViewHolder, int i)
    {
        final Comic comic = (Comic) mComicList.get(i);

        comicItemViewHolder.mTitle.setText(comic.getEditedTitle() + " " + comic.getEditedIssueNumber());

        int color = comic.getComicColor();
        int transparentColor = Color.argb(235,
                Color.red(color),
                Color.green(color),
                Color.blue(color));
        comicItemViewHolder.mTitle.setBackgroundColor(transparentColor);
        comicItemViewHolder.mTitle.setTextColor(comic.getTextColor());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);


        if (comic.getCoverImage()!=null)
        {
            if (comic.getComicColor()!=-1)
            {
                comicItemViewHolder.mCardView.setCardBackgroundColor(comic.getComicColor());
            }
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mListFragment.getActivity())
                        .build();
                ImageLoader.getInstance().init(config);
            }

            ImageLoader.getInstance().displayImage(comic.getCoverImage(), comicItemViewHolder.mCoverPicture, mImageOptions);
        }
        Drawable circle = mListFragment.getActivity().getResources().getDrawable(R.drawable.dark_circle);
        if (Build.VERSION.SDK_INT>15)
            comicItemViewHolder.mFavoriteButton.setBackground(circle);

        if (StorageManager.getReadComics(mListFragment.getActivity()).containsKey(comic.getFileName()))
        {

            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(circle);

            if (StorageManager.getReadComics(mListFragment.getActivity()).get(comic.getFileName())+1>=comic.getPageCount())
            {
                // Change button
                comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_close);
                comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Blue));
                comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.BlueDark));
                comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.BlueLight));
                comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_unread));
                addMarkUnreadClickListener(comicItemViewHolder);
                ///////////
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.fab_check,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                // Change button
                comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_check);
                comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Teal));
                comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.TealDark));
                comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.TealLight));
                comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_read));
                addMarkReadClickListener(comicItemViewHolder);
                ///////////
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }


        }
        else
        {
            // Change button
            comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_check);
            comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Teal));
            comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.TealDark));
            comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.TealLight));
            comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_read));
            addMarkReadClickListener(comicItemViewHolder);
            ///////////
            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(null);
            else
                comicItemViewHolder.mLastReadIcon.getBackground().setAlpha(0);
        }

        if (StorageManager.getFavoriteComics(mListFragment.getActivity()).contains(comic.getFileName()))
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

        comicItemViewHolder.mTitle.setText(comic.getEditedTitle());

        comicItemViewHolder.mCoverPicture.setImageBitmap(null);

        if (comic.getEditedIssueNumber()!=-1) {
            comicItemViewHolder.mIssueNumber.setVisibility(View.VISIBLE);
            comicItemViewHolder.mIssueNumber.setText(mListFragment.getActivity().getString(R.string.issue_number) + ": " + comic.getEditedIssueNumber());
        }
        else {
            comicItemViewHolder.mIssueNumber.setVisibility(View.GONE);
        }

        if (comic.getEditedYear()!=-1) {
            comicItemViewHolder.mYear.setVisibility(View.VISIBLE);
            comicItemViewHolder.mYear.setText(mListFragment.getActivity().getString(R.string.year) + ": " + comic.getEditedYear());
        }
        else {
            comicItemViewHolder.mYear.setVisibility(View.GONE);
        }

        if (comic.getPageCount()!=-1) {
            comicItemViewHolder.mPageCount.setVisibility(View.VISIBLE);
            comicItemViewHolder.mPageCount.setText(mListFragment.getActivity().getString(R.string.pages) + ": " + comic.getPageCount());
        }
        else {
            comicItemViewHolder.mPageCount.setVisibility(View.GONE);
            comicItemViewHolder.mPageCount.setText("");
        }

        setAnimation(comicItemViewHolder.mCardView, i);


        if (comic.getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(Utilities.lightenColor(comic.getTextColor()));

            comicItemViewHolder.mIssueNumber.setTextColor(comic.getTextColor());
            comicItemViewHolder.mPageCount.setTextColor(comic.getTextColor());
            comicItemViewHolder.mYear.setTextColor(comic.getTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(comic.getComicColor());
        }
        else
        {
            comicItemViewHolder.mTitle.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.White));

            comicItemViewHolder.mIssueNumber.setTextColor(Utilities.darkenColor(mListFragment.getActivity().getResources().getColor(R.color.White)));
            comicItemViewHolder.mPageCount.setTextColor(Utilities.darkenColor(mListFragment.getActivity().getResources().getColor(R.color.White)));
            comicItemViewHolder.mYear.setTextColor(Utilities.darkenColor(mListFragment.getActivity().getResources().getColor(R.color.White)));

            comicItemViewHolder.mCardView.setCardBackgroundColor(StorageManager.getAppThemeColor(mListFragment.getActivity()));
        }

        Drawable circle = mListFragment.getActivity().getResources().getDrawable(R.drawable.dark_circle);
        if (comic.getColorSetting().equals(mListFragment.getActivity().getString(R.string.card_color_setting_3))) {
            if (Build.VERSION.SDK_INT > 15)
                comicItemViewHolder.mFavoriteButton.setBackground(circle);
        }
        else
        {
            if (Build.VERSION.SDK_INT > 15)
                comicItemViewHolder.mFavoriteButton.setBackground(null);
        }

        if (StorageManager.getFavoriteComics(mListFragment.getActivity()).contains(comic.getFileName()))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
        }
        else
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
        }

        if (StorageManager.getReadComics(mListFragment.getActivity()).containsKey(comic.getFileName()))
        {
            //comicItemViewHolder.mLastReadIcon.setColorFilter(mComicList.get(i).getPrimaryTextColor());
            if (comic.getColorSetting().equals(mListFragment.getActivity().getString(R.string.card_color_setting_3))) {
                if (Build.VERSION.SDK_INT > 15)
                    comicItemViewHolder.mLastReadIcon.setBackground(circle);
            }
            else
            {
                if (Build.VERSION.SDK_INT > 15)
                    comicItemViewHolder.mLastReadIcon.setBackground(null);
            }
            if (StorageManager.getReadComics(mListFragment.getActivity()).get((comic.getFileName()))+1>=comic.getPageCount())
            {
                // Change button
                comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_close);
                comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Blue));
                comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.BlueDark));
                comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.BlueLight));
                comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_unread));
                addMarkUnreadClickListener(comicItemViewHolder);
                ///////////
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.fab_check,comicItemViewHolder.mLastReadIcon);
            }
            else
            {

                // Change button
                comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_check);
                comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Teal));
                comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.TealDark));
                comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.TealLight));
                comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_read));
                addMarkReadClickListener(comicItemViewHolder);
                ///////////
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
            }
        }
        else
        {
            // Change button
            comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_check);
            comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Teal));
            comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.TealDark));
            comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.TealLight));
            comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_read));
            addMarkReadClickListener(comicItemViewHolder);
            ///////////

            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(null);
        }

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mListFragment.getActivity()).build();
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

        comicItemViewHolder.mTitle.setText(comic.getEditedTitle());
        if (comic.getEditedIssueNumber()!= -1)
            comicItemViewHolder.mIssueNumber.setText(mListFragment.getActivity().getString(R.string.issue_number)+": "+comic.getEditedIssueNumber());
        else
            comicItemViewHolder.mIssueNumber.setText("");

        if (comic.getEditedYear()!=-1)
            comicItemViewHolder.mYear.setText(mListFragment.getActivity().getString(R.string.year)+": "+comic.getEditedYear());
        else
            comicItemViewHolder.mYear.setText("");

        if (comic.getPageCount()!=-1)
            comicItemViewHolder.mPageCount.setText(""+comic.getPageCount()+" "+mListFragment.getActivity().getString(R.string.pages_no_capital));
        else
            comicItemViewHolder.mPageCount.setText("");

        setAnimation(comicItemViewHolder.mCardView,i);

        Drawable circle = mListFragment.getActivity().getResources().getDrawable(R.drawable.dark_circle);

        if (StorageManager.getFavoriteComics(mListFragment.getActivity()).contains(comic.getFileName()))
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star, comicItemViewHolder.mFavoriteButton);
        }
        else
        {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.star_outline, comicItemViewHolder.mFavoriteButton);
        }

        if (comic.getColorSetting().equals(mListFragment.getActivity().getString(R.string.card_color_setting_3))) {
            if (Build.VERSION.SDK_INT > 15)
                comicItemViewHolder.mFavoriteButton.setBackground(circle);
        }
        else
        {
            if (Build.VERSION.SDK_INT > 15)
                comicItemViewHolder.mFavoriteButton.setBackground(null);
        }

        if (StorageManager.getReadComics(mListFragment.getActivity()).containsKey((comic.getFileName())))
        {
            comicItemViewHolder.mLastReadIcon.setVisibility(View.VISIBLE);
            if (comic.getColorSetting().equals(mListFragment.getActivity().getString(R.string.card_color_setting_3))) {
                if (Build.VERSION.SDK_INT > 15)
                    comicItemViewHolder.mLastReadIcon.setBackground(circle);
            }
            else
            {
                if (Build.VERSION.SDK_INT > 15)
                    comicItemViewHolder.mLastReadIcon.setBackground(null);
            }

            if (StorageManager.getReadComics(mListFragment.getActivity()).get((comic.getFileName()))+1>=comic.getPageCount())
            {
                // Change button
                comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_close);
                comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Blue));
                comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.BlueDark));
                comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.BlueLight));
                comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_unread));
                addMarkUnreadClickListener(comicItemViewHolder);
                ///////////
                ImageLoader.getInstance().displayImage("drawable://"+R.drawable.fab_check,comicItemViewHolder.mLastReadIcon);
            }
            else
            {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.last_read, comicItemViewHolder.mLastReadIcon);
                // Change button
                comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_check);
                comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Teal));
                comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.TealDark));
                comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.TealLight));
                comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_read));
                addMarkReadClickListener(comicItemViewHolder);
                ///////////
            }

        }
        else
        {
            // Change button
            comicItemViewHolder.mMarkReadButton.setImageResource(R.drawable.fab_check);
            comicItemViewHolder.mMarkReadButton.setColorNormal(mListFragment.getResources().getColor(R.color.Teal));
            comicItemViewHolder.mMarkReadButton.setColorPressed(mListFragment.getResources().getColor(R.color.TealDark));
            comicItemViewHolder.mMarkReadButton.setColorRipple(mListFragment.getResources().getColor(R.color.TealLight));
            comicItemViewHolder.mMarkReadTextView.setText(mListFragment.getString(R.string.mark_read));
            addMarkReadClickListener(comicItemViewHolder);
            ///////////

            comicItemViewHolder.mLastReadIcon.setVisibility(View.GONE);
            comicItemViewHolder.mLastReadIcon.setImageBitmap(null);
            if (Build.VERSION.SDK_INT>15)
                comicItemViewHolder.mLastReadIcon.setBackground(null);
        }

        if (comic.getComicColor()!=-1)
        {

            comicItemViewHolder.mTitle.setTextColor(Utilities.lightenColor(comic.getTextColor()));

            comicItemViewHolder.mIssueNumber.setTextColor(comic.getTextColor());
            comicItemViewHolder.mPageCount.setTextColor(comic.getTextColor());
            comicItemViewHolder.mYear.setTextColor(comic.getTextColor());

            comicItemViewHolder.mCardView.setCardBackgroundColor(comic.getComicColor());
        }
        else
        {
            comicItemViewHolder.mTitle.setTextColor(mListFragment.getActivity().getResources().getColor(R.color.White));

            comicItemViewHolder.mIssueNumber.setTextColor(Utilities.darkenColor(mListFragment.getActivity().getResources().getColor(R.color.White)));
            comicItemViewHolder.mPageCount.setTextColor(Utilities.darkenColor(mListFragment.getActivity().getResources().getColor(R.color.White)));
            comicItemViewHolder.mYear.setTextColor(Utilities.darkenColor(mListFragment.getActivity().getResources().getColor(R.color.White)));

            comicItemViewHolder.mCardView.setCardBackgroundColor(StorageManager.getAppThemeColor(mListFragment.getActivity()));
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
        int pos = Sorter.sortedInsert(mListFragment.getActivity(), mComicList, obj);
        if (pos!=-1)
            notifyItemInserted(pos);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mListFragment.getActivity());
        String scrollAnimPref = prefs.getString("scrollAnimation", mListFragment.getActivity().getString(R.string.scroll_animation_setting_1));

        if (scrollAnimPref.equals(mListFragment.getActivity().getString(R.string.scroll_animation_setting_1)))
        {
            //No animation
        }
        else if (scrollAnimPref.equals(mListFragment.getActivity().getString(R.string.scroll_animation_setting_2)))
        {
            YoYo.with(Techniques.BounceIn)
                    .duration(400)
                    .playOn(viewToAnimate);
        }
        else if (scrollAnimPref.equals(mListFragment.getActivity().getString(R.string.scroll_animation_setting_3)))
        {
            YoYo.with(Techniques.StandUp)
                    .duration(700)
                    .playOn(viewToAnimate);
        }
        else if (scrollAnimPref.equals(mListFragment.getActivity().getString(R.string.scroll_animation_setting_4)))
        {
            YoYo.with(Techniques.FadeIn)
                    .duration(300)
                    .playOn(viewToAnimate);
        }
        else if (scrollAnimPref.equals(mListFragment.getActivity().getString(R.string.scroll_animation_setting_5)))
        {
            YoYo.with(Techniques.Tada)
                    .duration(400)
                    .playOn(viewToAnimate);
        }

        lastPosition = position;
    }

    public void removeComics(final ArrayList<Comic> comics)
    {

        for (int i=0;i<comics.size();i++)
        {
            mComicList.remove(comics.get(i));
        }
        notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ComicActions.removeComics(mListFragment.getActivity(), comics);
            }
        }).run();
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

                ComicActions.removeComic(mListFragment.getActivity(), comic);
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


            Utilities.deleteDirectory(mListFragment.getActivity(), directory);

            return null;
        }
    }

    public void setComic(int pos, Comic comic)
    {
        if (pos<mComicList.size())
            mComicList.set(pos, comic);
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

