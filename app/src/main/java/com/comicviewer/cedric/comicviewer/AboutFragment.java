package com.comicviewer.cedric.comicviewer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AboutFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_about,container, false);
        
        ImageView logoview = (ImageView)v.findViewById(R.id.logo);
        ImageView meview = (ImageView)v.findViewById(R.id.me_drawable);

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getActivity());
        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage("drawable://"+R.drawable.logo_highres,logoview);
        ImageLoader.getInstance().displayImage("drawable://"+R.drawable.me,meview);
        
        /*
        Picasso.with(getActivity())
                .load(R.drawable.logo_highres)
                .into(logoview);
        
        Picasso.with(getActivity())
                .load(R.drawable.me)
                .into(meview);
                */
        
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
