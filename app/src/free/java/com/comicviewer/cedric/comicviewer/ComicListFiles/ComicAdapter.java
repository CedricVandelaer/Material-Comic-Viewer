package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.comicviewer.cedric.comicviewer.ComicActions;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CV on 23/01/2015.
 * Class to show a comic in the comiclist
 */
public class ComicAdapter extends AbstractComicAdapter {

    public ComicAdapter(AbstractComicListFragment context, List<Object> comics, MultiSelector multiSelector) {
        super(context, comics, multiSelector);
    }

    public ComicAdapter(AbstractComicListFragment context, MultiSelector multiSelector) {
        super(context, multiSelector);
    }

    @Override
    protected void multiHideComics(ArrayList<Comic> comics) {
        showGoProDialog();
        mActionMode.finish();
    }

    @Override
    protected void multiAddToCollection(final ArrayList<Comic> comics) {
        JSONArray collections = StorageManager.getCollectionList(mListFragment.getActivity());

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

        final String noNewCollection = "Only 2 collections allowed in free version";
        final String newCollection = "Add new collection";
        if (collections.length()<2)
            collectionNames[collectionNames.length-1] = newCollection;
        else
            collectionNames[collectionNames.length-1] = noNewCollection;

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
                                            ComicActions.addComicsToCollection(mListFragment.getActivity(), charSequence.toString(), comics);
                                        }
                                    })
                                    .show();
                        } else {
                            if (!charSequence.toString().equals(noNewCollection))
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
        JSONArray collections = StorageManager.getCollectionList(mListFragment.getActivity());
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

        final String noNewCollection = "Only 2 collections allowed in free version";
        final String newCollection = "Add new collection";
        if (collections.length()<2)
            collectionNames[collectionNames.length-1] = newCollection;
        else
            collectionNames[collectionNames.length-1] = noNewCollection;

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
                                            ComicActions.addFolderToCollection(mListFragment.getActivity(), charSequence.toString(), folderPath);
                                        }
                                    })
                                    .show();
                        }
                        else {
                            if (!charSequence.toString().equals(noNewCollection))
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
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGoProDialog();
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
                if (dialog != null)
                    dialog.dismiss();
                vh.mSwipeLayout.close();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGoProDialog();
                    }
                }, 300);
            }
        });
    }

    @Override
    void addFolderMarkReadClickListener(final MaterialDialog dialog, View v, final FolderItemViewHolder vh) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null)
                    dialog.dismiss();
                vh.mSwipeLayout.close();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGoProDialog();
                    }
                },300);
            }
        });
    }

    @Override
    protected void addAddToCollectionClickListener(final MaterialDialog dialog, View v, final Comic comic) {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null)
                    dialog.dismiss();
                JSONArray collections = StorageManager.getCollectionList(mListFragment.getActivity());
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

                final String noNewCollection = "Only 2 collections allowed in free version";
                final String newCollection = "Add new collection";
                if (collections.length()<2)
                    collectionNames[collectionNames.length-1] = newCollection;
                else
                    collectionNames[collectionNames.length-1] = noNewCollection;

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
                                                    ComicActions.addComicToCollection(mListFragment.getActivity(), charSequence.toString(), comic);
                                                }
                                            })
                                            .show();
                                } else {
                                    if (!charSequence.toString().equals(noNewCollection))
                                    ComicActions.addComicToCollection(mListFragment.getActivity(), charSequence.toString(), comic);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    protected void addFolderHideClickListener(View v, final FolderItemViewHolder vh)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGoProDialog();
            }
        });
    }


    protected void addHideClickListener(final MaterialDialog dialog, View v, final Comic comic)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                showGoProDialog();
            }
        });
    }

    public void showGoProDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mListFragment.getActivity())
                .title(mListFragment.getString(R.string.notice))
                .content(mListFragment.getString(R.string.pro_version_notice))
                .positiveText(mListFragment.getString(R.string.buy_full_version))
                .positiveColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                .negativeText(mListFragment.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mListFragment.getActivity()))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.comicviewer.cedric.comicviewer.pro"));
                        mListFragment.getActivity().startActivity(browse);
                    }
                }).show();
    }

}
