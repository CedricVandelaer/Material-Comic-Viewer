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

        outRect.bottom = mVerticalSpace;


        outRect.right = mHorizontalSpace;
        outRect.left = mHorizontalSpace;
        /*
        if ((parent.getChildPosition(view) % mColumnCount) == 0) {
            outRect.left = mHorizontalSpace;
        } else {
            outRect.left = mHorizontalSpace / 2;
        }

        if (((parent.getChildPosition(view)+1) % mColumnCount) == 0) {
            outRect.right = mHorizontalSpace;
        }
        else
        {
            outRect.right = mHorizontalSpace/2;
        }
        */

        // Add top margin only for the first item to avoid double space between items

        if (parent.getChildPosition(view) < mColumnCount) {
            outRect.top = mVerticalSpace;
        }

    }

}