package com.comicviewer.cedric.comicviewer;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.TouchImageView;

/**
 * Created by CV on 27/06/2015.
 */
public class PageFlipPageTransformer extends ABaseTransformer {


    @Override
    protected void onTransform(View page, float position) {


        if (position<0) {
            page.setPivotY(page.getHeight() / 2);
            page.setPivotX(page.getWidth() / 2);
            page.setScaleX(1 + (position / 5));
            float angleInRadians = (float)(Math.abs(position)*Math.PI);
            float depthScale = ((float) Math.sin(angleInRadians) / 7);
            page.setScaleY(1 + depthScale);
            page.setTranslationX((position / 32) * page.getWidth());
            page.setRotationY(depthScale*-15);
        }
        else
        {
            TouchImageView imageView = (TouchImageView) page.findViewById(R.id.fullscreen_comic);
            imageView.setColorFilter(Color.argb((int)(position*192),0,0,0));
        }
    }

}
