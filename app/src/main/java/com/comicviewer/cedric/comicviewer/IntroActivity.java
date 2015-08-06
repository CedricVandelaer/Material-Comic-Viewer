package com.comicviewer.cedric.comicviewer;

import android.content.Intent;
import android.os.Bundle;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Cédric on 6/08/2015.
 */
public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle bundle) {
        addSlide(AppIntroFragment.newInstance(
                "Welcome to Comic Viewer!",
                "A beautiful and easy to use comic reader",
                R.drawable.logo_intro,
                getResources().getColor(R.color.Red)));

        addSlide(AppIntroFragment.newInstance(
                "Add folders",
                "To get started, simply add a comic folder by pressing the floating action button",
                R.drawable.add_folder_intro,
                getResources().getColor(R.color.Teal)));

        addSlide(AppIntroFragment.newInstance(
                "Actions",
                "A lot of the apps functionality can be found by swiping a card to the left, this includes comics, folders etc.",
                R.drawable.actions_intro,
                getResources().getColor(R.color.Orange)));

        addSlide(AppIntroFragment.newInstance(
                "Actions",
                "To show in-comic actions, swipe from the bottom or top edge to reveal the menu button",
                R.drawable.actions_intro2,
                getResources().getColor(R.color.Blue)));

        addSlide(AppIntroFragment.newInstance(
                "Customize",
                "The app has many options so be sure to check out the settings",
                R.drawable.settings_intro,
                getResources().getColor(R.color.Indigo)));
    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(this, NewDrawerActivity.class);
        startActivity(intent);
        StorageManager.saveBooleanSetting(this, StorageManager.INTRO_WAS_SHOWN, true);
        this.finish();
    }
}
