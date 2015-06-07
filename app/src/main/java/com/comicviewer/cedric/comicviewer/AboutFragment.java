package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
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
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.gc.materialdesign.views.ButtonFlat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AboutFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ButtonFlat mChangelogButton;
    private TextView mTitleTextView;

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

        mTitleTextView.setText(getActivity().getResources().getString(R.string.app_name) + " v" + getPackageInfo().versionName);

        mChangelogButton = (ButtonFlat) v.findViewById(R.id.updates_button);

        mChangelogButton.setBackgroundColor(PreferenceSetter.getAppThemeColor(getActivity()));

        mChangelogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .content(getString(R.string.updates))
                        .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
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

        if (PreferenceSetter.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG)) {
            getActivity().getWindow().getDecorView().setBackgroundColor(getActivity().getResources().getColor(R.color.BlueGrey));
            if (Build.VERSION.SDK_INT > 20)
                getActivity().getWindow().setNavigationBarColor(getActivity().getResources().getColor(R.color.BlueGrey));
        }
        else
        {
            PreferenceSetter.setBackgroundColorPreference(getActivity());
        }

        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
