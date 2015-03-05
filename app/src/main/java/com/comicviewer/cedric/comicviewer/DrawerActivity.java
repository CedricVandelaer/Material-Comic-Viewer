package com.comicviewer.cedric.comicviewer;

import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by Cédric on 5/03/2015.
 */
public class DrawerActivity extends MaterialNavigationDrawer implements ComicListFragment.OnFragmentInteractionListener {

    private ArrayList<Comic> mComicList;
    @Override
    public void init(Bundle savedInstanceState) {

        MaterialSection section1 = newSection("All comics", ComicListFragment.newInstance());
        addSection(section1);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
