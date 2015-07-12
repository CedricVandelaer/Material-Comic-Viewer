package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.comicviewer.cedric.comicviewer.PageFlipPageTransformer;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;

/**
 * The activity to display a fullscreen comic
 */
public class DisplayComicActivity extends AbstractDisplayComicActivity {

    @Override
    protected void initializeAd() {

    }

    protected void showAd()
    {

    }

    protected void setPagerAnimation()
    {
        if (StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.accordion)))
        {
            mPager.setPageTransformer(true, new AccordionTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.cube)))
        {
            mPager.setPageTransformer(true, new CubeOutTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.zoom_out)))
        {
            mPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.foreground_to_background)))
        {
            mPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.stack)))
        {
            mPager.setPageTransformer(true, new StackTransformer());
        }
        else if(StorageManager.getPageFlipAnimationSetting(this).equals(getString(R.string.page_flip)))
        {
            mPager.setPageTransformer(true, new PageFlipPageTransformer());
        }
        else
        {
            mPager.setPageTransformer(true, new DefaultTransformer());
        }
    }


}
