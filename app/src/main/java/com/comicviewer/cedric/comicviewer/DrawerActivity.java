package com.comicviewer.cedric.comicviewer;

import android.app.ActivityManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.FavoritesListFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by Cédric on 5/03/2015.
 * The drawer activity*
 */
public class DrawerActivity extends MaterialNavigationDrawer implements ComicListFragment.OnFragmentInteractionListener,
AboutFragment.OnFragmentInteractionListener, FavoritesListFragment.OnFragmentInteractionListener, StatisticsFragment.OnFragmentInteractionListener
{

    MaterialSection[] mSectionsArray;

    @Override
    public void init(Bundle savedInstanceState) {

        new SimpleEula(this).show();
        new SetTaskDescriptionTask().execute();

        mSectionsArray = new MaterialSection[5];

        this.disableLearningPattern();
        this.setBackPattern(BACKPATTERN_BACK_TO_FIRST);
        this.setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));
        enableToolbarElevation();
        allowArrowAnimation();

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }

        Drawable[] layers = new Drawable[2];
        layers[0] = new ColorDrawable(PreferenceSetter.getAppThemeColor(this));
        layers[1] = getResources().getDrawable(R.drawable.comic_viewer_drawer_header_text);

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        this.setDrawerHeaderImage(layerDrawable);

        MaterialSection allComicsSection = newSection("All comics", R.drawable.book, ComicListFragment.getInstance());
        mSectionsArray[0] = allComicsSection;
        addSection(allComicsSection);

        MaterialSection favoritesSection = newSection("Favorites", R.drawable.star, FavoritesListFragment.getInstance());
        mSectionsArray[1] = favoritesSection;
        addSection(favoritesSection);

        MaterialSection statsSection = newSection("Statistics", R.drawable.stats, StatisticsFragment.newInstance());
        mSectionsArray[2] = statsSection;
        addSection(statsSection);

        MaterialSection settingsSection = newSection("Settings", R.drawable.settings, SettingsFragment.newInstance());
        mSectionsArray[3] = settingsSection;
        addBottomSection(settingsSection);
        
        MaterialSection aboutSection = newSection("About", R.drawable.about, AboutFragment.newInstance());
        mSectionsArray[4] = aboutSection;
        addBottomSection(aboutSection);

        for (MaterialSection section:mSectionsArray)
        {
            section.setSectionColor(PreferenceSetter.getAppThemeColor(this));
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        changeToolbarColor(PreferenceSetter.getAppThemeColor(this),darkenColor(PreferenceSetter.getAppThemeColor(this)));
    }

    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(DrawerActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            ActivityManager.TaskDescription tdscr = null;

            if (Build.VERSION.SDK_INT>20) {
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(getString(R.string.app_name),
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.logohighres, size),
                            PreferenceSetter.getAppThemeColor(DrawerActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tdscr != null)
                setTaskDescription(tdscr);


            return null;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
