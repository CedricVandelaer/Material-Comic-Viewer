package com.comicviewer.cedric.comicviewer;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.CloudFiles.CloudFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.FavoritesListFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.Stack;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by Cédric on 5/03/2015.
 * The drawer activity*
 */
public class DrawerActivity extends MaterialNavigationDrawer implements ComicListFragment.OnFragmentInteractionListener,
AboutFragment.OnFragmentInteractionListener, FavoritesListFragment.OnFragmentInteractionListener, StatisticsFragment.OnFragmentInteractionListener,
        CloudFragment.OnFragmentInteractionListener
{

    MaterialSection[] mSectionsArray;
    private Stack<MaterialSection> mSectionNavigationStack;


    @Override
    public void init(Bundle savedInstanceState) {

        mSectionNavigationStack = new Stack<>();

        new SimpleEula(this).show();
        new SetTaskDescriptionTask().execute();

        mSectionsArray = new MaterialSection[6];

        this.disableLearningPattern();
        this.setBackPattern(BACKPATTERN_CUSTOM);
        this.setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));
        enableToolbarElevation();
        allowArrowAnimation();

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }

        setDrawerHeaderImage();

        ComicListFragment.getInstance().setRetainInstance(true);
        MaterialSection allComicsSection = newSection("All comics", R.drawable.book, ComicListFragment.getInstance());
        mSectionsArray[0] = allComicsSection;
        addSection(allComicsSection);

        MaterialSection favoritesSection = newSection("Favorites", R.drawable.star, FavoritesListFragment.getInstance());
        mSectionsArray[1] = favoritesSection;
        addSection(favoritesSection);

        MaterialSection cloudSection = newSection("Cloud storage", R.drawable.cloud, CloudFragment.newInstance());
        mSectionsArray[2] = cloudSection;
        addSection(cloudSection);

        MaterialSection statsSection = newSection("Statistics", R.drawable.stats, StatisticsFragment.newInstance());
        mSectionsArray[3] = statsSection;
        addSection(statsSection);

        MaterialSection settingsSection = newSection("Settings", R.drawable.settings, SettingsFragment.newInstance());
        mSectionsArray[4] = settingsSection;
        addBottomSection(settingsSection);
        
        MaterialSection aboutSection = newSection("About", R.drawable.about, AboutFragment.newInstance());
        mSectionsArray[5] = aboutSection;
        addBottomSection(aboutSection);

        mSectionNavigationStack.push(mSectionsArray[0]);
        for (final MaterialSection section:mSectionsArray)
        {
            section.setSectionColor(PreferenceSetter.getAppThemeColor(this));

            section.setOnClickListener(new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection materialSection) {
                    mSectionNavigationStack.push(materialSection);
                    setSection(materialSection);
                    setFragment(getFragment(materialSection),materialSection.getTitle());
                }
            });
        }

    }

    private Fragment getFragment(MaterialSection materialSection)
    {
        if (materialSection.getTitle().equals("All comics"))
            return ComicListFragment.getInstance();
        else if (materialSection.getTitle().equals("Favorites"))
            return FavoritesListFragment.getInstance();
        else if (materialSection.getTitle().equals("Cloud storage"))
            return CloudFragment.newInstance();
        else if (materialSection.getTitle().equals("Statistics"))
            return StatisticsFragment.newInstance();
        else if (materialSection.getTitle().equals("Settings"))
            return SettingsFragment.newInstance();
        else if (materialSection.getTitle().equals("About"))
            return AboutFragment.newInstance();
        else
            return ComicListFragment.getInstance();
    }

    @Override
    public void onBackPressed()
    {
        if (getCurrentSection() == mSectionsArray[0])
        {
            if (!ComicListFragment.getInstance().NavigationStack.isEmpty())
                ComicListFragment.getInstance().NavigationStack.pop();
            if (!ComicListFragment.getInstance().NavigationStack.isEmpty())
            {
                ComicListFragment.getInstance().refresh();
            }
            else
            {
                if (!mSectionNavigationStack.isEmpty())
                    mSectionNavigationStack.pop();
                if (mSectionNavigationStack.isEmpty())
                {
                    finish();
                }
                else
                {
                    setFragment(getFragment(mSectionNavigationStack.peek()), mSectionNavigationStack.peek().getTitle());
                    setSection(mSectionNavigationStack.peek());
                }
            }
        }
        else
        {
            if (!mSectionNavigationStack.isEmpty())
                mSectionNavigationStack.pop();
            if (mSectionNavigationStack.isEmpty())
            {
                finish();
            }
            else
            {
                setFragment(getFragment(mSectionNavigationStack.peek()), mSectionNavigationStack.peek().getTitle());
                setSection(mSectionNavigationStack.peek());
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        changeToolbarColor(PreferenceSetter.getAppThemeColor(this),darkenColor(PreferenceSetter.getAppThemeColor(this)));
    }

    private void setDrawerHeaderImage()
    {
        Drawable[] layers = new Drawable[2];
        layers[0] = new ColorDrawable(PreferenceSetter.getAppThemeColor(this));
        layers[1] = getResources().getDrawable(R.drawable.comic_viewer_drawer_header_text);
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        this.setDrawerHeaderImage(layerDrawable);
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
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_recents, size),
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
