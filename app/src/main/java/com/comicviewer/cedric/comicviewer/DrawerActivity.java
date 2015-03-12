package com.comicviewer.cedric.comicviewer;

import android.net.Uri;
import android.os.Bundle;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.Model.ComicCollection;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicAdapter;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragmentRefactor;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by Cédric on 5/03/2015.
 * The drawer activity*
 */
public class DrawerActivity extends MaterialNavigationDrawer implements ComicListFragment.OnFragmentInteractionListener,
AboutFragment.OnFragmentInteractionListener{

    /*
    private ComicCollection mAllComics;
    private ArrayList<String> mFilePaths;
    private ArrayList<String> mExcludedPaths;
    */
    
    @Override
    public void init(Bundle savedInstanceState) {
        
        this.disableLearningPattern();
        this.setBackPattern(BACKPATTERN_BACK_TO_FIRST);
        this.setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));

        //initialiseComics();
        
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }
        
        this.setDrawerHeaderImage(ImageLoader.getInstance().loadImageSync("drawable://"+R.drawable.tealtrainglebg));
        
        MaterialSection allComicsSection = newSection("All comics", ComicListFragment.newInstance());
        addSection(allComicsSection);

        MaterialSection settingsSection = newSection("Settings", SettingsFragment.newInstance());    
        addBottomSection(settingsSection);
        
        MaterialSection aboutSection = newSection("About", AboutFragment.newInstance());
        addBottomSection(aboutSection);

    }

    /*
    private void initialiseComics() {
        mExcludedPaths = PreferenceSetter.getExcludedPaths(this);
        mFilePaths = PreferenceSetter.getFilePathsFromPreferences(this);

        mAllComics = new ComicCollection("All comics",FileLoader.searchComics(mFilePaths, mExcludedPaths));
    }

    public void removeOldComics(ArrayList<Comic> comicList)
    {
        FileLoader.removeOldComics(mFilePaths, mExcludedPaths, comicList);
        
    }
    */
    
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
