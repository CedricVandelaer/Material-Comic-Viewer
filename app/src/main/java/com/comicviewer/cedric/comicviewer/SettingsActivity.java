package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, SettingsFragment.newInstance()).commit();

        getActionBar().setTitle("Settings");
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.TealDark));

    }

    @Override

    public void onResume()
    {
        super.onResume();
    }

}
