package com.comicviewer.cedric.comicviewer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.gc.materialdesign.views.ButtonFlat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AboutFragment extends Fragment {

    private ButtonFlat mChangelogButton;
    private ButtonFlat mRateButton;
    private TextView mTitleTextView;

    private ImageView mGooglePlusLogo;
    private TextView mGooglePlusTextView;
    private ImageView mFacebookLogo;
    private TextView mFacebookTextView;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_about,container, false);
        
        ImageView logoview = (ImageView)v.findViewById(R.id.logo);
        ImageView meview = (ImageView)v.findViewById(R.id.me_drawable);
        mTitleTextView = (TextView) v.findViewById(R.id.logo_text);
        mGooglePlusLogo = (ImageView) v.findViewById(R.id.google_plus_logo);
        mGooglePlusTextView = (TextView) v.findViewById(R.id.google_plus_textview);
        mFacebookLogo = (ImageView) v.findViewById(R.id.facebook_logo);
        mFacebookTextView = (TextView) v.findViewById(R.id.facebook_textview);
        mRateButton = (ButtonFlat) v.findViewById(R.id.rate_button);
        mChangelogButton = (ButtonFlat) v.findViewById(R.id.updates_button);



        View.OnClickListener googlePlusClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.google_plus_community_url)));
                startActivity(i);
            }
        };

        View.OnClickListener facebookClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.facebook_page_url)));
                startActivity(i);
            }
        };

        mGooglePlusLogo.setOnClickListener(googlePlusClickListener);
        mGooglePlusTextView.setOnClickListener(googlePlusClickListener);
        mFacebookLogo.setOnClickListener(facebookClickListener);
        mFacebookTextView.setOnClickListener(facebookClickListener);

        mTitleTextView.setText(getActivity().getResources().getString(R.string.app_name) + " v" + getPackageInfo().versionName);

        mRateButton.setBackgroundColor(StorageManager.getAppThemeColor(getActivity()));
        mChangelogButton.setBackgroundColor(StorageManager.getAppThemeColor(getActivity()));

        mRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
            }
        });

        mChangelogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .content(getString(R.string.updates))
                        .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                        .positiveText(getString(R.string.accept))
                        .title(getString(R.string.changelog))
                        .show();
            }
        });

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getActivity());
            ImageLoader.getInstance().init(config);
        }
        ImageLoader.getInstance().displayImage("drawable://"+R.drawable.logohighres,logoview);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.me, meview);

        if (StorageManager.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG)) {
            getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
            if (Build.VERSION.SDK_INT > 20)
                getActivity().getWindow().setNavigationBarColor(getActivity().getResources().getColor(R.color.BlueGrey));
        }
        else
        {
            StorageManager.setBackgroundColorPreference(getActivity());
        }

        return v;
    }

}
