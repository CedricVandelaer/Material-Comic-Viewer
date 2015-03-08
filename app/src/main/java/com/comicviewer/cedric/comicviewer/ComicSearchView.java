package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.widget.SearchView;

/**
 * Created by Cédric on 7/03/2015.
 */
public class ComicSearchView extends SearchView {
    
    Context mContext;
    
    public ComicSearchView(Context context) {
        super(context);
        mContext = context;
    }

}
