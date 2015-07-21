package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.comicviewer.cedric.comicviewer.R;
import com.kyleduo.switchbutton.Configuration;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Created by Cédric on 21/07/2015.
 */
public class ColoredSwitchPreference extends ColoredPreference {

    private SwitchButton mSwitch;
    private boolean mEnabled;

    public ColoredSwitchPreference(Context context) {
        super(context);

        mEnabled = false;
    }

    public ColoredSwitchPreference(Context context, int color) {
        super(context, color);

        mEnabled = false;
    }

    public void setDefaultValue(boolean value)
    {
        mEnabled = value;
    }

    @Override
    protected View onCreateView( ViewGroup parent )
    {
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = layoutInflater.inflate(R.layout.colored_switch_preference, parent, false);

        initialiseTextViews(v);

        mSwitch = (SwitchButton) v.findViewById(R.id.switchView);

        initialiseSwitch();

        updateColors();

        return v;
    }

    public void updateColors()
    {
        super.updateColors();

        if (mSwitch!=null) {
            Configuration config = Configuration.getDefault(getContext().getResources().getDisplayMetrics().density);
            config.setOffColor(getContext().getResources().getColor(R.color.GreyLight));
            config.setOnColor(StorageManager.getAccentColor(getContext()));
            mSwitch.setConfiguration(config);
        }
    }

    public void setChecked(boolean checked)
    {
        mSwitch.setChecked(checked, false);
    }

    private void initialiseSwitch()
    {
        if (getKey()!=null)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean initialValue = prefs.getBoolean(getKey(), mEnabled);
            mSwitch.setChecked(initialValue);
        }
        else
        {
            mSwitch.setChecked(mEnabled, false);
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OnPreferenceChangeListener onPreferenceChangeListener = getOnPreferenceChangeListener();

                if (onPreferenceChangeListener!=null)
                {
                    onPreferenceChangeListener.onPreferenceChange(ColoredSwitchPreference.this, isChecked);
                }
                else if (getKey()!=null)
                {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(getKey(), isChecked);
                    editor.apply();
                }
            }
        });
        updateColors();
    }
}
