package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Cédric on 24/01/2015.
 * Class to show a gap between the recyclerviewitems
 */


public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private int mVerticalSpace;
    private int mHorizontalSpace;


    public DividerItemDecoration(int vspace, int hspace) {
        this.mVerticalSpace = vspace;
        this.mHorizontalSpace = hspace;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mHorizontalSpace;
        outRect.right = mHorizontalSpace;
        outRect.bottom = mVerticalSpace;

        // Add top margin only for the first item to avoid double space between items
        if(parent.getChildPosition(view) == 0)
            outRect.top = mVerticalSpace;
    }
}