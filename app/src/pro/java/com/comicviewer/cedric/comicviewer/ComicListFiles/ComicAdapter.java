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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cédric on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends AbstractComicAdapter {

    public ComicAdapter(AbstractComicListFragment listFragment, List<Comic> comics, boolean dummy) {
        super(listFragment, comics, dummy);
    }

    public ComicAdapter(AbstractComicListFragment context, List<Object> comics) {
        super(context, comics);
    }

    public ComicAdapter(AbstractComicListFragment context) {
        super(context);
    }

    @Override
    void addFolderHideClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder vh) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                },300);

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


}
