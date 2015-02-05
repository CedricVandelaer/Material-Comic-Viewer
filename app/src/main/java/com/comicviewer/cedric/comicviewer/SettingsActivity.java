package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.TealDark));

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setPreferences();
    }

    private void setPreferences() {
        View layout = getWindow().getDecorView().getRootView();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bgcolor = prefs.getString("backgroundColor", getString(R.string.backgroundcolor_setting2));

        if (bgcolor.equals(getString(R.string.backgroundcolor_setting1)))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.BlueGrey));
        }
        else if (bgcolor.equals(getString(R.string.backgroundcolor_setting2)))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.Black));
        }
        else
        {
            layout.setBackgroundColor(getResources().getColor(R.color.WhiteBG));
        }
    }

}
