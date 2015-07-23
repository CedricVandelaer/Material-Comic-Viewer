package com.comicviewer.cedric.comicviewer.RecyclerViewListFiles;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

/**
 * Created by Cédric on 23/07/2015.
 */
public class SlideRightAnimator extends BaseItemAnimator {

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(holder.itemView.getRootView().getWidth())
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder)).start();
    }
}
