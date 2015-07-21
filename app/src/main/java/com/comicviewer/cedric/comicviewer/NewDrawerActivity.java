package com.comicviewer.cedric.comicviewer;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.CloudFiles.CloudFragment;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseNavigationInterface;
import com.comicviewer.cedric.comicviewer.ComicListFiles.CollectionsFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.CurrentlyReadingFragment;
import com.comicviewer.cedric.comicviewer.ComicListFiles.FavoritesListFragment;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.DrawerNavigator;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsOverviewFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Cédric on 15/07/2015.
 */
public class NewDrawerActivity extends AppCompatActivity {

    private Drawer mDrawer;
    private Toolbar mToolbar;
    private DrawerNavigator mDrawerNavigator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(StorageManager.getAppThemeColor(this));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.White));
        mToolbar.setTitle(getString(R.string.all_comics));
        setSupportActionBar(mToolbar);

        new SimpleEula(this).show();
        startSetTaskDescription();

        int selectedItem = 0;

        mDrawerNavigator = DrawerNavigator.getInstance();
        /*
        if (savedInstanceState!=null)
        {
            mDrawerSectionNavigation = NavigationManager.fromJSON(savedInstanceState.getString("sectionNavigation"));
            mDrawerTitleNavigation = NavigationManager.fromJSON(savedInstanceState.getString("sectionTitles"));
            selectedItem = (int) mDrawerSectionNavigation.getValueFromStack();
        }
        else {
            mDrawerTitleNavigation = new NavigationManager();
            mDrawerSectionNavigation = new NavigationManager();
        }
        */
        if (!DrawerNavigator.getInstance().getSectionNavigator().emptyStack())
            selectedItem = (int) DrawerNavigator.getInstance().getSectionNavigator().getValueFromStack();
        else
            selectedItem = 0;

        StorageManager.setBackgroundColorPreference(this);

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }

        if (Build.VERSION.SDK_INT>20)
            getWindow().setStatusBarColor(Utilities.darkenColor(StorageManager.getAppThemeColor(this)));

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withHeader(createDrawerHeaderImage())
                .addDrawerItems(
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.all_comics)
                                .withIcon(R.drawable.book)
                                .withIconTintingEnabled(true),
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.currently_reading)
                                .withIcon(R.drawable.last_read)
                                .withIconTintingEnabled(true),
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.favorites)
                                .withIcon(R.drawable.star)
                                .withIconTintingEnabled(true),
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.collections)
                                .withIcon(R.drawable.castle)
                                .withIconTintingEnabled(true),
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.cloud_storage)
                                .withIcon(R.drawable.cloud)
                                .withIconTintingEnabled(true),
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.statistics)
                                .withIcon(R.drawable.stats)
                                .withIconTintingEnabled(true),
                        new ColoredPrimaryDrawerItem()
                                .withName(R.string.synchronization)
                                .withIcon(R.drawable.sync)
                                .withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                        new ColoredSecondaryDrawerItem()
                                .withName(R.string.settings)
                                .withIcon(R.drawable.settings)
                                .withIconTintingEnabled(true),
                        new ColoredSecondaryDrawerItem()
                                .withName(R.string.about)
                                .withIcon(R.drawable.about)
                                .withIconTintingEnabled(true)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            setFragment(getFragmentForPosition(position), getString(((Nameable) drawerItem).getNameRes()), position);
                        }

                        return false;
                    }
                })
                .withFireOnInitialOnClick(false)
                .withSelectedItem(selectedItem)
                .withSavedInstance(savedInstanceState)
                .build();
        setDrawerColor();

        /*
        if (mDrawerSectionNavigation.emptyStack())
            setFragment(ComicListFragment.getInstance(), getString(R.string.all_comics), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle((String)mDrawerTitleNavigation.getValueFromStack());
            */

        if (DrawerNavigator.getInstance().getSectionNavigator().emptyStack())
            setFragment(ComicListFragment.getInstance(), getString(R.string.all_comics), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle((String) DrawerNavigator.getInstance().getTitleNavigator().getValueFromStack());

    }

    @Override
    public void onBackPressed()
    {
        if (mDrawer.isDrawerOpen())
            mDrawer.closeDrawer();
        else {
            BaseNavigationInterface f = (BaseNavigationInterface) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (!f.onBackPressed()) {
                //mDrawerTitleNavigation.popFromStack();
                //mDrawerSectionNavigation.popFromStack();
                DrawerNavigator.getInstance().getTitleNavigator().popFromStack();
                DrawerNavigator.getInstance().getSectionNavigator().popFromStack();
                if (DrawerNavigator.getInstance().getSectionNavigator().emptyStack()) {
                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("Confirm exit")
                            .content("Do you want to exit Comic Viewer?")
                            .positiveText(getString(R.string.confirm))
                            .positiveColor(StorageManager.getAppThemeColor(this))
                            .negativeText(getString(R.string.cancel))
                            .negativeColor(StorageManager.getAppThemeColor(this))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    finish();
                                }
                            }).show();
                }
                else {
                    //mDrawer.setSelection((Integer) mDrawerSectionNavigation.getValueFromStack(), false);
                    mDrawer.setSelection((Integer) DrawerNavigator.getInstance().getSectionNavigator().getValueFromStack(), false);
                    getSupportFragmentManager().popBackStack();
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle((String)DrawerNavigator.getInstance().getTitleNavigator().getValueFromStack());
                        //getSupportActionBar().setTitle((String)mDrawerTitleNavigation.getValueFromStack());
                }
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle state)
    {
        super.onRestoreInstanceState(state);

        /*
        if (state!=null && state.getString("sectionNavigation")!=null && state.getString("sectionTitles")!=null)
        {
            mDrawerSectionNavigation = NavigationManager.fromJSON(state.getString("sectionNavigation"));
            mDrawerTitleNavigation = NavigationManager.fromJSON(state.getString("sectionTitles"));
        }
        else {
            mDrawerTitleNavigation = new NavigationManager();
            mDrawerSectionNavigation = new NavigationManager();
        }
        */
    }

    public void setFragmentInSection(Fragment fragment, String title)
    {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        //mDrawerTitleNavigation.pushToStack(title);
        //mDrawerSectionNavigation.pushToStack(mDrawer.getCurrentSelection());
        DrawerNavigator.getInstance().getTitleNavigator().pushToStack(title);
        DrawerNavigator.getInstance().getSectionNavigator().pushToStack(mDrawer.getCurrentSelection());
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private Fragment getFragmentForPosition(int position)
    {
        Fragment f;
        switch (position) {
            case 0:
                f = ComicListFragment.getInstance();
                break;
            case 1:
                f = CurrentlyReadingFragment.getInstance();
                break;
            case 2:
                f = FavoritesListFragment.getInstance();
                break;
            case 3:
                f = CollectionsFragment.getInstance();
                break;
            case 4:
                f = CloudFragment.getInstance();
                break;
            case 5:
                f = StatisticsFragment.newInstance();
                break;
            case 6:
                f = SynchronisationFragment.newInstance();
                break;
            case 8:
                f = new SettingsOverviewFragment();
                break;
            case 9:
                f = AboutFragment.newInstance();
                break;
            default:
                f = ComicListFragment.getInstance();
                break;
        }
        return f;
    }

    public void setFragment(Fragment fragment, String title, int section)
    {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        //mDrawerTitleNavigation.pushToStack(title);
        DrawerNavigator.getInstance().getTitleNavigator().pushToStack(title);
        int previousSection = 0;
        if (!DrawerNavigator.getInstance().getSectionNavigator().emptyStack())
            previousSection = (int) DrawerNavigator.getInstance().getSectionNavigator().getValueFromStack();
        DrawerNavigator.getInstance().getSectionNavigator().pushToStack(section);
        /*
        if (!mDrawerSectionNavigation.emptyStack())
            previousSection = (int) mDrawerSectionNavigation.getValueFromStack();
        mDrawerSectionNavigation.pushToStack(section);
        */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (StorageManager.getBooleanSetting(this, StorageManager.SECTION_ANIMATION, true)) {
            if (section>=previousSection)
                transaction = transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);
            else
                transaction = transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);
        }
        else {
            transaction = transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        }
        transaction.replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mDrawer.saveInstanceState(outState);
        //outState.putString("sectionNavigation", mDrawerSectionNavigation.toJSON());
        //outState.putString("sectionTitles", mDrawerTitleNavigation.toJSON());
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

    public void setDrawerColor()
    {
        int color = StorageManager.getBackgroundColorPreference(this);
        int appthemecolor = StorageManager.getAppThemeColor(this);
        int iconColor;
        if (color == getResources().getColor(R.color.WhiteBG))
            iconColor = getResources().getColor(R.color.BlueGreyVeryDark);
        else
            iconColor = getResources().getColor(R.color.White);

        mDrawer.getSlider().setBackgroundColor(color);

        List<IDrawerItem> drawerItems = mDrawer.getDrawerItems();

        for (int i=0;i<drawerItems.size();i++)
        {
            IDrawerItem drawerItem = drawerItems.get(i);
            if (drawerItem instanceof ColoredPrimaryDrawerItem)
            {
                ((ColoredPrimaryDrawerItem)drawerItem).setBackgroundColor(color);
                ((ColoredPrimaryDrawerItem)drawerItem).withIconColor(iconColor);
                ((ColoredPrimaryDrawerItem)drawerItem).withTextColor(iconColor);
                ((ColoredPrimaryDrawerItem)drawerItem).withSelectedColor(appthemecolor);
                ((ColoredPrimaryDrawerItem)drawerItem).withSelectedTextColor(appthemecolor);
                ((ColoredPrimaryDrawerItem)drawerItem).withSelectedIconColor(appthemecolor);
            }
            else if (drawerItem instanceof ColoredSecondaryDrawerItem)
            {
                ((ColoredSecondaryDrawerItem)drawerItem).setBackgroundColor(color);
                ((ColoredSecondaryDrawerItem)drawerItem).withIconColor(iconColor);
                ((ColoredSecondaryDrawerItem)drawerItem).withTextColor(iconColor);
                ((ColoredSecondaryDrawerItem)drawerItem).withSelectedColor(appthemecolor);
                ((ColoredSecondaryDrawerItem)drawerItem).withSelectedTextColor(appthemecolor);
                ((ColoredSecondaryDrawerItem)drawerItem).withSelectedIconColor(appthemecolor);
            }
            mDrawer.setItem(drawerItem, i);
        }
        /*
        if (!mDrawerSectionNavigation.emptyStack())
            mDrawer.setSelection((int)mDrawerSectionNavigation.getValueFromStack(), false);
        else
            mDrawer.setSelection(0, false);
            */
        if (!DrawerNavigator.getInstance().getSectionNavigator().emptyStack())
            mDrawer.setSelection((int)DrawerNavigator.getInstance().getSectionNavigator().getValueFromStack(), false);
        else
            mDrawer.setSelection(0, false);
    }

    public void updateDrawerHeader()
    {
        mDrawer.removeHeader();
        mDrawer.setHeader(createDrawerHeaderImage());
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
    public void onDestroy()
    {
        cleanFiles();
        super.onDestroy();
    }

    public Toolbar getToolbar()
    {
        return mToolbar;
    }

    private ImageView createDrawerHeaderImage()
    {
        Drawable[] layers = new Drawable[2];
        layers[0] = new ColorDrawable(StorageManager.getAppThemeColor(this));
        layers[1] = getResources().getDrawable(R.drawable.comic_viewer_drawer_header_text);
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(layerDrawable);

        return imageView;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public void startSetTaskDescription()
    {
        new SetTaskDescriptionTask().execute();
    }

    private class SetTaskDescriptionTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(NewDrawerActivity.this).build();
                ImageLoader.getInstance().init(config);
            }

            ActivityManager.TaskDescription tdscr = null;

            if (Build.VERSION.SDK_INT>20) {
                try {
                    ImageSize size = new ImageSize(64, 64);
                    tdscr = new ActivityManager.TaskDescription(getString(R.string.app_name),
                            ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_recents, size),
                            StorageManager.getAppThemeColor(NewDrawerActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tdscr != null)
                setTaskDescription(tdscr);


            return null;
        }
    }
}
