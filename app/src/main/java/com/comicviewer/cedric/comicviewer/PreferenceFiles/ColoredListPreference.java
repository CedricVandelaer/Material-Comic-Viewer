package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.R;

/**
 * Created by Cédric on 20/07/2015.
 */
public class ColoredListPreference extends ColoredPreference {

    CharSequence[] mEntries = null;
    CharSequence[] mEntryValues;
    CharSequence mDefaultValue = null;
    String mDialogTitle = null;
    private DefaultValueImplementation mDefaultGetter = null;

    public void setEntries(CharSequence[] entries)
    {
        mEntries = entries;
    }

    public void setEntryValues(CharSequence[] values)
    {
        mEntryValues = values;
    }

    public ColoredListPreference(Context context) {
        super(context);
        setClickListener();
    }

    public ColoredListPreference(Context context, int color) {
        super(context, color);
        setClickListener();
    }

    public void setDialogTitle(String title){mDialogTitle = title;}

    public void setDefaultValue(CharSequence value)
    {
        mDefaultValue = value;
    }

    private void setClickListener()
    {
        super.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .items(mEntries)
                        .itemsCallbackSingleChoice(getCurrentOrDefaultValue(), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                CharSequence value;
                                if (mEntryValues != null)
                                    value = mEntryValues[i];
                                else
                                    value = mEntries[i];

                                OnPreferenceChangeListener onPreferenceChangeListener = getOnPreferenceChangeListener();

                                if (onPreferenceChangeListener == null) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(getKey(), value.toString());
                                    editor.apply();
                                }
                                else
                                    onPreferenceChangeListener.onPreferenceChange(ColoredListPreference.this, value);
                                return false;
                            }
                        })
                        .widgetColor(StorageManager.getAccentColor(getContext()))
                        .negativeColor(StorageManager.getAppThemeColor(getContext()))
                        .negativeText(getContext().getString(R.string.cancel))
                        .build();
                if (mDialogTitle!=null)
                    dialog.setTitle(mDialogTitle);
                dialog.show();

                return false;
            }
        });
    }

    public void setDefaultGetter(DefaultValueImplementation impl){mDefaultGetter = impl;}

    private int getCurrentOrDefaultValue()
    {
        if (mDefaultGetter != null)
            return mDefaultGetter.getDefaultValuePos();

        int selected = -1;

        if (getKey()!=null)
        {
            CharSequence currentVal = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getKey(), null);

            if (currentVal != null) {
                if (mEntryValues == null) {
                    for (int i = 0; i < mEntries.length; i++) {
                        CharSequence string = mEntries[i];
                        if (currentVal.equals(string))
                            selected = i;
                    }
                } else {
                    for (int i = 0; i < mEntryValues.length; i++) {
                        CharSequence string = mEntryValues[i];
                        if (currentVal.equals(string))
                            selected = i;
                    }
                }
            }
        }
        if (selected == -1 && mDefaultValue != null)
        {
            if (mEntryValues == null)
            {
                for (int i =0;i<mEntries.length;i++)
                {
                    CharSequence string = mEntries[i];
                    if (mDefaultValue.equals(string))
                        selected = i;
                }
            }
            else
            {
                for (int i =0;i<mEntryValues.length;i++)
                {
                    CharSequence string = mEntryValues[i];
                    if (mDefaultValue.equals(string))
                        selected = i;
                }
            }
        }
        if (selected == -1)
            selected = 0;
        return selected;
    }

    public interface DefaultValueImplementation
    {
        abstract int getDefaultValuePos();
    }
}
