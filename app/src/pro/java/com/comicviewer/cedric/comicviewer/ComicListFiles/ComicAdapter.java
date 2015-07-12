package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.AsyncTask;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.comicviewer.cedric.comicviewer.CollectionActions;
import com.comicviewer.cedric.comicviewer.ComicActions;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cédric on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends AbstractComicAdapter {

    @Override
    protected void multiAddToCollection(final ArrayList<Comic> comics) {

        ArrayList<Collection> collections = StorageManager.getCollectionList(mListFragment.getActivity());
        CharSequence[] collectionNames = new CharSequence[collections.size()+1];

        for (int i=0;i<collections.size();i++)
        {
            collectionNames[i] = collections.get(i).getName();
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
                                            StorageManager.createCollection(mListFragment.getActivity(), charSequence.toString());
                                            CollectionActions.addComicsToCollection(mListFragment.getActivity(), charSequence.toString(), comics);
                                        }
                                    })
                                    .show();
                        } else {
                            CollectionActions.addComicsToCollection(mListFragment.getActivity(), charSequence.toString(), comics);
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
        ArrayList<Collection> collections = StorageManager.getCollectionList(mListFragment.getActivity());
        CharSequence[] collectionNames = new CharSequence[collections.size()+1];

        for (int i=0;i<collections.size();i++)
        {
            collectionNames[i] = collections.get(i).getName();
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
                                            StorageManager.createCollection(mListFragment.getActivity(), charSequence.toString());
                                            CollectionActions.addFolderToCollection(mListFragment.getActivity(), charSequence.toString(), folderPath);
                                        }
                                    })
                                    .show();
                        }
                        else {
                            CollectionActions.addFolderToCollection(mListFragment.getActivity(), charSequence.toString(), folderPath);
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
                StorageManager.addHiddenPath(mListFragment.getActivity(), vh.getFile().getAbsolutePath());
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
            ArrayList<Collection> collections = StorageManager.getCollectionList(mListFragment.getActivity());
            CharSequence[] collectionNames = new CharSequence[collections.size()+1];

            for (int i=0;i<collections.size();i++)
            {
                collectionNames[i] = collections.get(i).getName();
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
                                                StorageManager.createCollection(mListFragment.getActivity(), charSequence.toString());
                                                CollectionActions.addComicToCollection(mListFragment.getActivity(), charSequence.toString(), comic);
                                            }
                                        })
                                        .show();
                            } else {
                                CollectionActions.addComicToCollection(mListFragment.getActivity(), charSequence.toString(), comic);
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
                StorageManager.addHiddenPath(mListFragment.getActivity(), comic.getFilePath() + "/" + comic.getFileName());
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

        StorageManager.batchAddHiddenPath(mListFragment.getActivity(), hiddenPaths);
        notifyDataSetChanged();
        mActionMode.finish();
    }

}
