package com.comicviewer.cedric.comicviewer.ComicListFiles;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
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
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cédric on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends AbstractComicAdapter {



    @Override
    protected void multiAddToCollection(final ArrayList<Comic> comics) {

        JSONArray collections = PreferenceSetter.getCollectionList(mListFragment.getActivity());
        CharSequence[] collectionNames = new CharSequence[collections.length()+1];

        for (int i=0;i<collections.length();i++)
        {
            try {
                collectionNames[i] = collections.getJSONObject(i).keys().next();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        final String newCollection = "Add new collection";
        collectionNames[collectionNames.length-1] = newCollection;

        new MaterialDialog.Builder(mListFragment.getActivity())
                .title("Choose collection")
                .items(collectionNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (charSequence.equals(newCollection)) {
                            MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                                    .title("Create new collection")
                                    .input("Collection name", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                            PreferenceSetter.createCollection(mListFragment.getActivity(), charSequence.toString());
                                            ComicActions.addComicsToCollection(mListFragment.getActivity(), charSequence.toString(), comics);
                                        }
                                    })
                                    .show();
                        } else {
                            ComicActions.addComicsToCollection(mListFragment.getActivity(), charSequence.toString(), comics);
                        }
                        mActionMode.finish();
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onAny(MaterialDialog dialog) {
                        super.onAny(dialog);
                        mActionMode.finish();
                    }
                })
                .show();


    }

    public ComicAdapter(AbstractComicListFragment context, List<Object> comics, MultiSelector multiSelector) {
        super(context, comics, multiSelector);
    }

    public ComicAdapter(AbstractComicListFragment context, MultiSelector multiSelector) {
        super(context, multiSelector);
    }

    @Override
    protected void addFolderAddToCollectionClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder vh) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null)
                    dialog.dismiss();
                vh.mSwipeLayout.close();
                showChooseCollectionDialog(vh.getFile().getAbsolutePath());
            }
        });
    }

    public void showChooseCollectionDialog(final String folderPath)
    {
        JSONArray collections = PreferenceSetter.getCollectionList(mListFragment.getActivity());
        CharSequence[] collectionNames = new CharSequence[collections.length()+1];

        for (int i=0;i<collections.length();i++)
        {
            try {
                collectionNames[i] = collections.getJSONObject(i).keys().next();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        final String newCollection = "Add new collection";
        collectionNames[collectionNames.length-1] = newCollection;

        MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                .title("Choose collection")
                .items(collectionNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (charSequence.equals(newCollection))
                        {
                            MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                                    .title("Create new collection")
                                    .input("Collection name", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                            PreferenceSetter.createCollection(mListFragment.getActivity(), charSequence.toString());
                                            ComicActions.addFolderToCollection(mListFragment.getActivity(), charSequence.toString(), folderPath);
                                        }
                                    })
                                    .show();
                        }
                        else {
                            ComicActions.addFolderToCollection(mListFragment.getActivity(), charSequence.toString(), folderPath);
                        }
                    }
                })
                .show();

    }

    @Override
    void addFolderHideClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder vh) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null)
                    dialog.dismiss();
                vh.mSwipeLayout.close();
                PreferenceSetter.addHiddenPath(mListFragment.getActivity(), vh.getFile().getAbsolutePath());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pos = mComicList.indexOf(vh.getFile());
                        mComicList.remove(vh.getFile());
                        notifyItemRemoved(pos);
                    }
                }, 300);

            }
        });
    }

    @Override
    void addFolderMarkUnreadClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder vh) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog!=null)
                    dialog.dismiss();
                vh.mSwipeLayout.close();
                new MarkFolderUnreadTask().execute(vh.getFile().getAbsolutePath());
            }
        });
    }

    private class MarkFolderUnreadTask extends AsyncTask
    {
        MaterialDialog mDialog;
        @Override
        public void onPreExecute()
        {
            mDialog = new MaterialDialog.Builder(mListFragment.getActivity())
                    .title(mListFragment.getActivity().getString(R.string.mark_unread))
                    .content(mListFragment.getActivity().getString(R.string.updating_data_notice))
                    .cancelable(false)
                    .progress(true,1,false)
                    .show();
        }

        @Override
        public Object doInBackground(Object[] params)
        {
            String folder = (String) params[0];
            ComicActions.markFolderUnread(mListFragment.getActivity(), folder);
            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
        }
    }

    private class MarkFolderReadTask extends AsyncTask
    {
        MaterialDialog mDialog;
        @Override
        public void onPreExecute()
        {
            mDialog = new MaterialDialog.Builder(mListFragment.getActivity())
                    .title(mListFragment.getActivity().getString(R.string.mark_read))
                    .content(mListFragment.getActivity().getString(R.string.updating_data_notice))
                    .cancelable(false)
                    .progress(true,1,false)
                    .show();
        }

        @Override
        public Object doInBackground(Object[] params)
        {
            String folder = (String) params[0];
            ComicActions.markFolderRead(mListFragment.getActivity(), folder);
            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
        }
    }

    @Override
    void addFolderMarkReadClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder vh) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog!=null)
                    dialog.dismiss();
                vh.mSwipeLayout.close();
                new MarkFolderReadTask().execute(vh.getFile().getAbsolutePath());
            }
        });
    }

    @Override
    protected void addAddToCollectionClickListener(final MaterialDialog dialog, View v, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (dialog!=null)
                dialog.dismiss();
            JSONArray collections = PreferenceSetter.getCollectionList(mListFragment.getActivity());
            CharSequence[] collectionNames = new CharSequence[collections.length()+1];

            for (int i=0;i<collections.length();i++)
            {
                try {
                    collectionNames[i] = collections.getJSONObject(i).keys().next();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            final String newCollection = "Add new collection";
            collectionNames[collectionNames.length-1] = newCollection;

            new MaterialDialog.Builder(mListFragment.getActivity())
                    .title("Choose collection")
                    .items(collectionNames)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            if (charSequence.equals(newCollection)) {
                                MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                                        .title("Create new collection")
                                        .input("Collection name", "", false, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                                PreferenceSetter.createCollection(mListFragment.getActivity(), charSequence.toString());
                                                ComicActions.addComicToCollection(mListFragment.getActivity(), charSequence.toString(), comic);
                                            }
                                        })
                                        .show();
                            } else {
                                ComicActions.addComicToCollection(mListFragment.getActivity(), charSequence.toString(), comic);
                            }
                        }
                    })
                    .show();
            }
        });
    }


    @Override
    void addHideClickListener(final MaterialDialog dialog, View v, final Comic comic) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                PreferenceSetter.addHiddenPath(mListFragment.getActivity(), comic.getFilePath() + "/" + comic.getFileName());
                int pos = mComicList.indexOf(comic);
                mComicList.remove(comic);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    protected void multiHideComics(ArrayList<Comic> comics) {

        ArrayList<String> hiddenPaths = new ArrayList<>();

        for (Comic comic:comics) {
            hiddenPaths.add(comic.getFilePath() + "/" + comic.getFileName());
            int pos = mComicList.indexOf(comic);
            mComicList.remove(comic);
        }

        PreferenceSetter.batchAddHiddenPath(mListFragment.getActivity(), hiddenPaths);
        notifyDataSetChanged();
        mActionMode.finish();
    }

}
