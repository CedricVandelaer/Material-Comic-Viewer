package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Cédric on 29/04/2015.
 */
public class CustomPreferenceCategory extends PreferenceCategory {

    public CustomPreferenceCategory(Context context) {
        super(context);
    }
    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        // It's just a TextView!
        TextView categoryTitle =  (TextView)super.onCreateView(parent);
        categoryTitle.setTextColor(StorageManager.getAppThemeColor(getContext()));

        return categoryTitle;
    }
}