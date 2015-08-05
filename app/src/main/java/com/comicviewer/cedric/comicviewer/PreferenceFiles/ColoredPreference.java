package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cédric on 19/07/2015.
 */
public class ColoredPreference extends Preference {

    private int mTitleColor = -1;
    private int mIconColor = -1;
    private int mSummaryColor = -1;
    private TextView mTitleTextView;
    private TextView mSummaryTextView;
    private ImageView mIconImageView;
    private String mTitle = null;
    private String mSummary;
    private Drawable mIcon;

    public ColoredPreference(Context context) {
        super(context);
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public void setSummary(String summary)
    {
         mSummary = summary;
    }

    public void setIcon(int iconRes)
    {
        mIcon = ContextCompat.getDrawable(getContext(), iconRes);
    }

    public ColoredPreference(Context context, int color) {
        super(context);

        mTitleColor = color;
        mSummaryColor = color;
        mIconColor = color;
    }

    public void setTitleColor(int color)
    {
        mTitleColor = color;
    }

    public void setSummaryColor(int color) {
        mSummaryColor = color;
    }


    public void setIconColor(int color)
    {
        mIconColor = color;
    }

    protected void updateColors()
    {
        if (mTitleTextView!=null)
            mTitleTextView.setTextColor(mTitleColor);
        if (mSummaryTextView!=null)
            mSummaryTextView.setTextColor(mSummaryColor);
        if (mIconImageView!=null)
            mIconImageView.setColorFilter(mIconColor);
    }

    @Override
    protected View onCreateView( ViewGroup parent )
    {
        super.onCreateView(parent);
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = layoutInflater.inflate(R.layout.colored_preference, parent, false);

        initialiseTextViews(v);

        updateColors();

        return v;
    }

    protected void initialiseTextViews(View v)
    {
        mIconImageView = (ImageView) v.findViewById(R.id.icon);
        mTitleTextView = (TextView) v.findViewById(R.id.title);
        mSummaryTextView = (TextView) v.findViewById(R.id.summary);

        if (mTitle != null)
            mTitleTextView.setText(mTitle);
        else
            mTitleTextView.setVisibility(View.GONE);

        if (mSummary != null)
            mSummaryTextView.setText(mSummary);
        else
            mSummaryTextView.setVisibility(View.GONE);

        if (mIcon != null)
            mIconImageView.setImageDrawable(mIcon);
        else
            mIconImageView.setVisibility(View.GONE);
    }

}
