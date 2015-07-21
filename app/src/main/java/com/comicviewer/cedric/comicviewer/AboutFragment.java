package com.comicviewer.cedric.comicviewer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bluejamesbond.text.DocumentView;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.gc.materialdesign.views.ButtonFlat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AboutFragment extends BaseFragment {

    private ButtonFlat mChangelogButton;
    private ButtonFlat mRateButton;
    private TextView mTitleTextView;
    private DocumentView mAboutText;
    private TextView mFindUsTextView;
    private TextView mThanksTextView;

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
        View v = inflater.inflate(R.layout.fragment_about,container, false);

        StorageManager.setBackgroundColorPreference(getActivity());

        ImageView logoview = (ImageView)v.findViewById(R.id.logo);
        ImageView meview = (ImageView)v.findViewById(R.id.me_drawable);
        mTitleTextView = (TextView) v.findViewById(R.id.logo_text);
        mAboutText = (DocumentView) v.findViewById(R.id.about_text);
        mFindUsTextView = (TextView) v.findViewById(R.id.find_us_text_view);
        mThanksTextView = (TextView) v.findViewById(R.id.thanks_text_view);
        mGooglePlusLogo = (ImageView) v.findViewById(R.id.google_plus_logo);
        mGooglePlusTextView = (TextView) v.findViewById(R.id.google_plus_textview);
        mFacebookLogo = (ImageView) v.findViewById(R.id.facebook_logo);
        mFacebookTextView = (TextView) v.findViewById(R.id.facebook_textview);
        mRateButton = (ButtonFlat) v.findViewById(R.id.rate_button);
        mChangelogButton = (ButtonFlat) v.findViewById(R.id.updates_button);

        if (StorageManager.getBackgroundColorPreference(getActivity()) == getResources().getColor(R.color.WhiteBG))
            setTextColor(getResources().getColor(R.color.BlueGreyVeryDark));
        else
            setTextColor(getResources().getColor(R.color.White));

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
        ImageLoader.getInstance().displayImage("drawable://"+R.drawable.logo,logoview);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.me, meview);

        return v;
    }

    private void setTextColor(int color)
    {
        mTitleTextView.setTextColor(color);
        mAboutText.getDocumentLayoutParams().setTextColor(color);
        mFindUsTextView.setTextColor(color);
        mThanksTextView.setTextColor(color);
        mGooglePlusTextView.setTextColor(color);
        mFacebookTextView.setTextColor(color);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
