package com.comicviewer.cedric.comicviewer;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.ComicListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class SynchronisationFragment extends Fragment {

    private ButtonRectangle mExportButton;

    public SynchronisationFragment() {
        // Required empty public constructor
    }

    public static SynchronisationFragment newInstance()
    {
        return new SynchronisationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_synchronisation, container, false);

        mExportButton = (ButtonRectangle) v.findViewById(R.id.export_button);
        mExportButton.setBackgroundColor(PreferenceSetter.getAppThemeColor(getActivity()));

        PreferenceSetter.setBackgroundColorPreference(getActivity());

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExportDataTask().execute();
            }
        });

        return v;
    }

    private class ExportDataTask extends AsyncTask
    {



        MaterialDialog mDialog;

        @Override
        protected void onPreExecute()
        {
            mDialog = new MaterialDialog.Builder(getActivity()).title("Exporting data")
                    .content("Please wait while the app exports the app data.")
                    .cancelable(false)
                    .progress(true, 1, false)
                    .show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            PreferenceSetter.exportData(getActivity(), getActivity().getExternalFilesDir(null).getAbsolutePath());

            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
            SnackBar snackBar = new SnackBar(getActivity(), "The app has finished exporting data!", null, null);
            snackBar.show();
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
