package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.content.DialogInterface;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.CollectionActions;
import com.comicviewer.cedric.comicviewer.Model.Collection;
import com.comicviewer.cedric.comicviewer.PageFlipPageTransformer;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;

/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends AbstractDisplayComicActivity {

    @Override
    protected void initializeAd() {

    }

    protected void showAd()
    {

    }

    protected void setPagerAnimation()
    {
        if (StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.accordion)))
        {
            mPager.setPageTransformer(true, new AccordionTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.cube)))
        {
            mPager.setPageTransformer(true, new CubeOutTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.zoom_out)))
        {
            mPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.foreground_to_background)))
        {
            mPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.stack)))
        {
            mPager.setPageTransformer(true, new StackTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.page_flip)))
        {
            mPager.setPageTransformer(true, new PageFlipPageTransformer());
        }
        else
        {
            mPager.setPageTransformer(true, new DefaultTransformer());
        }
    }

    @Override
    void showChooseCollectionsDialog() {
        ArrayList<Collection> collections = StorageManager.getCollectionList(this);
        CharSequence[] collectionNames = new CharSequence[collections.size()+1];

        for (int i=0;i<collections.size();i++)
        {
            collectionNames[i] = collections.get(i).getName();
        }

        final String newCollection = "Add new collection";
        collectionNames[collectionNames.length-1] = newCollection;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Choose collection")
                .titleColor(getResources().getColor(R.color.Black))
                .itemColor(getResources().getColor(R.color.GreyDark))
                .backgroundColor(getResources().getColor(R.color.White))
                .items(collectionNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (charSequence.equals(newCollection)) {
                            MaterialDialog dialog = new MaterialDialog.Builder(DisplayComicActivity.this)
                                    .title("Create new collection")
                                    .titleColor(getResources().getColor(R.color.Black))
                                    .backgroundColor(getResources().getColor(R.color.White))
                                    .input("Collection name", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                            StorageManager.createCollection(DisplayComicActivity.this, charSequence.toString());
                                            CollectionActions.addComicToCollection(DisplayComicActivity.this, charSequence.toString(), mCurrentComic);
                                        }
                                    })
                                    .positiveText(getString(R.string.confirm))
                                    .positiveColor(StorageManager.getAppThemeColor(DisplayComicActivity.this))
                                    .negativeColor(StorageManager.getAppThemeColor(DisplayComicActivity.this))
                                    .negativeText(getString(R.string.cancel))
                                    .dismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            setSystemVisibilitySettings();
                                        }
                                    })
                                    .show();
                        } else {
                            CollectionActions.addComicToCollection(DisplayComicActivity.this, charSequence.toString(), mCurrentComic);
                        }
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setSystemVisibilitySettings();
                    }
                })
                .show();
    }


}
