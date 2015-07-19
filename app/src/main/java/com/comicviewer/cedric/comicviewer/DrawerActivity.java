package com.comicviewer.cedric.comicviewer;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.CloudFiles.AbstractCloudServiceListFragment;
import com.comicviewer.cedric.comicviewer.CloudFiles.CloudFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.AbstractComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.CollectionsFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.CollectionsListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.CurrentlyReadingFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.FavoritesListFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.Map;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by Cédric on 5/03/2015.
 * The drawer activity*
 */

public class DrawerActivity extends MaterialNavigationDrawer
{
    @Override
    public void init(Bundle savedInstanceState) {

    }
/*
    MaterialSection[] mSectionsArray;
    private NavigationManager mNavigationManager;


    @Override
    public void init(Bundle savedInstanceState) {

        mNavigationManager = NavigationManager.getInstance();

        new SimpleEula(this).show();
        new SetTaskDescriptionTask().execute();

        mSectionsArray = new MaterialSection[9];

        this.disableLearningPattern();
        this.setBackPattern(BACKPATTERN_CUSTOM);
        if (StorageManager.getBackgroundColorPreference(this)!=getResources().getColor(R.color.WhiteBG))
            this.setDrawerBackgroundColor(StorageManager.getBackgroundColorPreference(this));
        else
            this.setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));

        if (StorageManager.getBooleanSetting(this,StorageManager.MULTI_PANE, true))
            this.addMultiPaneSupport();

        enableToolbarElevation();
        if (StorageManager.getBooleanSetting(this, StorageManager.DRAWER_ARROW_ANIMATION, false))
            allowArrowAnimation();

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }

        setDrawerHeaderImage();

        ComicListFragment.getInstance().setRetainInstance(true);
        MaterialSection allComicsSection = newSection(getString(R.string.all_comics), R.drawable.book, ComicListFragment.getInstance());
        mSectionsArray[0] = allComicsSection;

        MaterialSection currentlyReadingSection = newSection(getString(R.string.currently_reading), R.drawable.last_read, CurrentlyReadingFragment.getInstance());
        mSectionsArray[1] = currentlyReadingSection;

        MaterialSection favoritesSection = newSection(getString(R.string.favorites), R.drawable.star, FavoritesListFragment.getInstance());
        mSectionsArray[2] = favoritesSection;

        MaterialSection collectionsSection = newSection(getString(R.string.collections), R.drawable.castle, CollectionsFragment.getInstance());
        mSectionsArray[3] = collectionsSection;

        CloudFragment.getInstance().setRetainInstance(true);
        MaterialSection cloudSection = newSection(getString(R.string.cloud_storage), R.drawable.cloud, CloudFragment.getInstance());
        mSectionsArray[4] = cloudSection;

        MaterialSection statsSection = newSection(getString(R.string.statistics), R.drawable.stats, StatisticsFragment.newInstance());
        mSectionsArray[5] = statsSection;

        MaterialSection syncSection = newSection(getString(R.string.synchronization), R.drawable.sync, SynchronisationFragment.newInstance());
        mSectionsArray[6] = syncSection;

        MaterialSection settingsSection = newSection(getString(R.string.settings), R.drawable.settings, new SettingsFragment());
        mSectionsArray[7] = settingsSection;

        MaterialSection aboutSection = newSection(getString(R.string.about), R.drawable.about, AboutFragment.newInstance());
        mSectionsArray[8] = aboutSection;

        mNavigationManager.pushToSectionStack(mSectionsArray[0]);
        for (final MaterialSection section:mSectionsArray)
        {
            section.setSectionColor(StorageManager.getAppThemeColor(this), StorageManager.getAppThemeColor(this));

            section.setOnClickListener(new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection materialSection) {
                    for (MaterialSection sec : mSectionsArray) {
                        sec.unSelect();
                    }
                    mNavigationManager.pushToSectionStack(materialSection);
                    setSection(materialSection);
                    changeToolbarColor(materialSection);
                    setFragment(getFragment(materialSection), materialSection.getTitle());
                }
            });
        }

        addSection(allComicsSection);
        addSection(currentlyReadingSection);
        addSection(favoritesSection);
        addSection(collectionsSection);
        addSection(cloudSection);
        addSection(statsSection);
        addSection(syncSection);
        addBottomSection(settingsSection);
        addBottomSection(aboutSection);

    }

    private Fragment getFragment(MaterialSection materialSection)
    {
        if (materialSection.getTitle().equals(getString(R.string.all_comics)))
            return ComicListFragment.getInstance();
        else if (materialSection.getTitle().equals(getString(R.string.currently_reading)))
            return CurrentlyReadingFragment.getInstance();
        else if (materialSection.getTitle().equals(getString(R.string.favorites)))
            return FavoritesListFragment.getInstance();
        else if (materialSection.getTitle().equals(getString(R.string.cloud_storage)))
            return CloudFragment.getInstance();
        else if (materialSection.getTitle().equals(getString(R.string.statistics)))
            return StatisticsFragment.newInstance();
        else if (materialSection.getTitle().equals(getString(R.string.settings)))
            return new SettingsFragment();
        else if (materialSection.getTitle().equals(getString(R.string.about)))
            return AboutFragment.newInstance();
        else if (materialSection.getTitle().equals(getString(R.string.synchronization)))
            return SynchronisationFragment.newInstance();
        else if (materialSection.getTitle().equals(getString(R.string.collections)))
            return CollectionsFragment.getInstance();
        else
            return ComicListFragment.getInstance();
    }


    @Override
    public void onStop()
    {
        super.onStop();
        cleanFiles();
    }

    public void cleanFiles()
    {
        File[] savedFolders = getFilesDir().listFiles();
        Map<String, String> allFiles = FileLoader.searchComics(this);
        String defaultPath = getFilesDir().getAbsolutePath();

        for (File folder:savedFolders)
        {
            boolean found = false;

            for (String filename:allFiles.keySet())
            {
                String comicFolder = defaultPath+"/"+filename;

                comicFolder = Utilities.removeExtension(comicFolder);

                File foundFolder = new File(comicFolder);

                if (foundFolder.getAbsolutePath().equals(folder.getAbsolutePath()))
                    found = true;
            }

            if (!found && !(folder.getName().equals("muzei")))
            {
                Log.d("OnStop", "Folder to delete: " + folder.getAbsolutePath());
                Utilities.deleteDirectory(this, folder);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (!NavigationManager.getInstance().getPathFromCollectionStack().equals(NavigationManager.ROOT))
        {
            NavigationManager.getInstance().popFromCollectionStack();
            if (NavigationManager.getInstance().collectionStackEmpty())
            {
                setPreviousSection();
            }
            else
                CollectionsListFragment.getInstance().refresh();
        }
        else if (getCurrentSection().getTargetFragment() instanceof CloudFragment)
        {
            if (CloudFragment.getInstance().getActiveCloudServiceFragment()!=null)
            {
                AbstractCloudServiceListFragment fragment = CloudFragment.getInstance().getActiveCloudServiceFragment();
                fragment.getNavigationManager().popFromCloudStack();
                if (fragment.getNavigationManager().cloudStackEmpty()) {
                    CloudFragment.getInstance().setActiveCloudServiceFragment(null);
                    setFragment(CloudFragment.getInstance(), getString(R.string.cloud_storage));
                }
                else
                {
                    CloudFragment.getInstance().getActiveCloudServiceFragment().refresh();
                }
            }
            else
            {
                setPreviousSection();
            }
        }
        else if (getCurrentSection().getTargetFragment() instanceof AbstractComicListFragment)
        {
            AbstractComicListFragment fragment = (AbstractComicListFragment) getCurrentSection().getTargetFragment();
            NavigationManager.getInstance().popFromStack(fragment);

            if (!NavigationManager.getInstance().stackEmpty(fragment))
            {
                fragment.refresh();
            }
            else
            {
                setPreviousSection();
            }
        }
        else
        {
            setPreviousSection();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        changeToolbarColor(StorageManager.getAppThemeColor(this),StorageManager.getAppThemeColor(this));
        if (!NavigationManager.getInstance().sectionStackEmpty())
        {
            setFragment(getFragment(NavigationManager.getInstance().getSectionFromSectionStack()),
                    NavigationManager.getInstance().getSectionFromSectionStack().getTitle());
        }
    }

    private void setPreviousSection()
    {
        NavigationManager.getInstance().popFromSectionStack();
        if (NavigationManager.getInstance().sectionStackEmpty())
        {
            finish();
        }
        else
        {
            for (MaterialSection sec:mSectionsArray)
            {
                sec.unSelect();
            }
            setFragment(getFragment(NavigationManager.getInstance().getSectionFromSectionStack()),
                    NavigationManager.getInstance().getSectionFromSectionStack().getTitle());
            setSection(NavigationManager.getInstance().getSectionFromSectionStack());
        }
    }

    private void setDrawerHeaderImage()
    {
        Drawable[] layers = new Drawable[2];
        layers[0] = new ColorDrawable(StorageManager.getAppThemeColor(this));
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
                            StorageManager.getAppThemeColor(DrawerActivity.this));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CloudFragment.REQUEST_CODE_PICK_ACCOUNT
                || requestCode == CloudFragment.REQUEST_CODE_RECOVER_FROM_AUTH_ERROR
                || requestCode == CloudFragment.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) {
            CloudFragment.getInstance().onActivityResult(requestCode,resultCode,data);
        }
    }

*/
}
