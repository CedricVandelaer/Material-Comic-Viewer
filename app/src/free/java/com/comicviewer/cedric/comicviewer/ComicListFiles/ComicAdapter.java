package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;

import java.util.List;

/**
 * Created by CV on 23/01/2015.
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

    protected void addFolderHideClickListener(View v, final FolderItemViewHolder vh)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                PreferenceSetter.addHiddenPath(mContext, vh.getFile().getAbsolutePath());
                int pos = mComicList.indexOf(vh.getFile());
                mComicList.remove(vh.getFile());
                notifyItemRemoved(pos);
                */
                MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                        .title(mListFragment.getString(R.string.notice))
                        .content(mListFragment.getString(R.string.pro_version_notice))
                        .positiveText(mListFragment.getString(R.string.buy_full_version))
                        .positiveColor(PreferenceSetter.getAppThemeColor(mListFragment.getActivity()))
                        .negativeText(mListFragment.getString(R.string.cancel))
                        .negativeColor(PreferenceSetter.getAppThemeColor(mListFragment.getActivity()))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                                mListFragment.getActivity().startActivity(browse);
                            }
                        }).show();
            }
        });
    }


    protected void addHideClickListener(final MaterialDialog dialog, View v, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                /*
                PreferenceSetter.addHiddenPath(mContext, comic.getFilePath() + "/" + comic.getFileName());
                int pos = mComicList.indexOf(comic);
                mComicList.remove(comic);
                notifyItemRemoved(pos);
                */
                MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                        .title(mListFragment.getString(R.string.notice))
                        .content(mListFragment.getString(R.string.pro_version_notice))
                        .positiveText(mListFragment.getString(R.string.buy_full_version))
                        .positiveColor(PreferenceSetter.getAppThemeColor(mListFragment.getActivity()))
                        .negativeText(mListFragment.getString(R.string.cancel))
                        .negativeColor(PreferenceSetter.getAppThemeColor(mListFragment.getActivity()))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                                mListFragment.getActivity().startActivity(browse);
                            }
                        }).show();
            }
        });
    }

}
