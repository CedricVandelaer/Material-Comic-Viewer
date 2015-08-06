package com.comicviewer.cedric.comicviewer;

import android.content.Intent;
import android.os.Bundle;

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
                R.drawable.logo,
                getResources().getColor(R.color.WhiteBG)));



    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(this, NewDrawerActivity.class);
        startActivity(intent);
        this.finish();
    }
}
