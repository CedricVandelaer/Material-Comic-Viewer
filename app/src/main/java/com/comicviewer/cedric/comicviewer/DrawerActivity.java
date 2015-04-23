package com.comicviewer.cedric.comicviewer;

import android.net.Uri;
import android.os.Bundle;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.FavoritesListFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by Cédric on 5/03/2015.
 * The drawer activity*
 */
public class DrawerActivity extends MaterialNavigationDrawer implements ComicListFragment.OnFragmentInteractionListener,
AboutFragment.OnFragmentInteractionListener, FavoritesListFragment.OnFragmentInteractionListener{

    @Override
    public void init(Bundle savedInstanceState) {
        
        this.disableLearningPattern();
        this.setBackPattern(BACKPATTERN_BACK_TO_FIRST);
        this.setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));
        enableToolbarElevation();
        allowArrowAnimation();

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }

        this.setDrawerHeaderImage(ImageLoader.getInstance().loadImageSync("drawable://"+R.drawable.tealtrianglebg));
        
        MaterialSection allComicsSection = newSection("All comics", R.drawable.book, ComicListFragment.getInstance());
        addSection(allComicsSection);

        MaterialSection favoritesSection = newSection("Favorites", R.drawable.star, FavoritesListFragment.getInstance());
        addSection(favoritesSection);

        MaterialSection settingsSection = newSection("Settings", R.drawable.settings, SettingsFragment.newInstance());
        addBottomSection(settingsSection);
        
        MaterialSection aboutSection = newSection("About", R.drawable.about, AboutFragment.newInstance());
        addBottomSection(aboutSection);

    }

    
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
