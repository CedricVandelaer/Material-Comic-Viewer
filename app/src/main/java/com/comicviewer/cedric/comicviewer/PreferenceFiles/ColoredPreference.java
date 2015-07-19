package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by Cédric on 19/07/2015.
 */
public class ColoredPreference extends Preference {

    public ColoredPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ColoredPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColoredPreference(Context context) {
        super(context);

    }
}
