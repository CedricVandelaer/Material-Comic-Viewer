package com.comicviewer.cedric.comicviewer.ViewPagerFiles;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.CollectionActions;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;

/**
 * Created by Cédric on 7/08/2015.
 */
public class LastComicPageFragment extends AbstractLastComicPageFragment {

    public static AbstractLastComicPageFragment newInstance(Comic currentComic, ArrayList<Comic> nextComics)
    {
        LastComicPageFragment fragment = new LastComicPageFragment();

        Bundle args = new Bundle();

        args.putParcelable("Comic", currentComic);
        args.putParcelableArrayList("NextComics", nextComics);

        fragment.setArguments(args);

        return fragment;
    }

    public void showChooseCollectionsDialog()
    {

        ArrayList<Collection> collections = StorageManager.getCollectionList(getActivity());
        CharSequence[] collectionNames;

        final String newCollection = "Add new collection";

        if (collections.size()<2) {
            collectionNames = new CharSequence[collections.size() + 1];
            collectionNames[collectionNames.length-1] = newCollection;
        }
        else
            collectionNames = new CharSequence[collections.size()];

        for (int i=0;i<collections.size();i++)
        {
            collectionNames[i] = collections.get(i).getName();
        }

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Choose collection")
                .titleColor(getResources().getColor(R.color.Black))
                .itemColor(getResources().getColor(R.color.GreyDark))
                .backgroundColor(getResources().getColor(R.color.White))
                .items(collectionNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (charSequence.equals(newCollection)) {
                            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                    .title("Create new collection")
                                    .titleColor(getResources().getColor(R.color.Black))
                                    .backgroundColor(getResources().getColor(R.color.White))
                                    .input("Collection name", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                            StorageManager.createCollection(getActivity(), charSequence.toString());
                                            CollectionActions.addComicToCollection(getActivity(), charSequence.toString(), mCurrentComic);
                                        }
                                    })
                                    .positiveText(getString(R.string.confirm))
                                    .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                                    .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                                    .negativeText(getString(R.string.cancel))
                                    .dismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            ((AbstractDisplayComicActivity) getActivity()).setSystemVisibilitySettings();
                                        }
                                    })
                                    .show();
                        } else {
                            CollectionActions.addComicToCollection(getActivity(), charSequence.toString(), mCurrentComic);
                            Toast.makeText(getActivity(), "Comic added to "+charSequence.toString()+"!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((AbstractDisplayComicActivity)getActivity()).setSystemVisibilitySettings();
                    }
                })
                .show();
    }
}
