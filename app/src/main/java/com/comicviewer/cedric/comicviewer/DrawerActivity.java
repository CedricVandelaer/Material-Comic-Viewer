package com.comicviewer.cedric.comicviewer;

import android.net.Uri;
import android.os.Bundle;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.SettingsFragment;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.ComicListFragment;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by Cédric on 5/03/2015.
 * The drawer activity*
 */
public class DrawerActivity extends MaterialNavigationDrawer implements ComicListFragment.OnFragmentInteractionListener,
AboutFragment.OnFragmentInteractionListener{

    private ArrayList<Comic> mComicList;
    @Override
    public void init(Bundle savedInstanceState) {

        this.setDrawerBackgroundColor(getResources().getColor(R.color.BlueGrey));
        this.setDrawerHeaderImage(R.drawable.tealtrainglebg);
        
        MaterialSection allComicsSection = newSection("All comics", ComicListFragment.newInstance());
        addSection(allComicsSection);

        MaterialSection settingsSection = newSection("Settings", SettingsFragment.newInstance());    
        addBottomSection(settingsSection);
        //settingsSection.setIcon(getDrawable(R.drawable.ic_settings_white_48dp));
        
        MaterialSection aboutSection = newSection("About", AboutFragment.newInstance());
        addBottomSection(aboutSection);
        //aboutSection.setIcon(getDrawable(R.drawable.ic_help_white_48dp));
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
