package com.comicviewer.cedric.comicviewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

/**
 * Created by Cédric on 19/07/2015.
 */
public class ColoredSecondaryDrawerItem extends SecondaryDrawerItem{

    private int mBackgroundRes = -1;
    private int mBackgroundColor = 0;


    public ColoredSecondaryDrawerItem withBackgroundRes(int backgroundRes) {
        this.mBackgroundRes = backgroundRes;
        return this;
    }

    public ColoredSecondaryDrawerItem withBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        return this;
    }

    public void setBackgroundColor(int color){
        mBackgroundColor = color;
    }

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        //use the logic of our PrimaryDrawerItem
        convertView = super.convertView(inflater, convertView, parent);

        if (mBackgroundColor != 0) {
            convertView.setBackgroundColor(mBackgroundColor);
        } else if (mBackgroundRes != -1) {
            convertView.setBackgroundResource(mBackgroundRes);
        }

        return convertView;
    }

}
