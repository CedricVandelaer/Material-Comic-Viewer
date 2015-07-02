package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastComicPageFragment extends Fragment {

    protected ArrayList<Comic> mComicList;
    protected Comic mCurrentComic;

    public LastComicPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_comic_page, container, false);
    }


}
