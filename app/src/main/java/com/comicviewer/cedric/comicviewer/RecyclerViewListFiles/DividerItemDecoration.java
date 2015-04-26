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
    private int mColumnCount = 1;


    public DividerItemDecoration(int vspace, int hspace) {
        this.mVerticalSpace = vspace;
        this.mHorizontalSpace = hspace;
    }

    public DividerItemDecoration(int vspace, int hspace, int columns) {
        this.mVerticalSpace = vspace;
        this.mHorizontalSpace = hspace;
        mColumnCount = columns;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mHorizontalSpace;
        outRect.right = mHorizontalSpace;
        outRect.bottom = mVerticalSpace;

        // Add top margin only for the first item to avoid double space between items

        if (parent.getChildPosition(view) < mColumnCount) {
            outRect.top = mVerticalSpace;
        }
    }

}