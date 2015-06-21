package com.comicviewer.cedric.comicviewer;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.ComicListFiles.ComicListFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SynchronisationFragment extends Fragment {

    private ButtonFlat mExportButton;
    private ButtonFlat mImportButton;
    private CardView mDeviceExportCardView;
    private TextView mDeviceTitleTextView;

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

        mExportButton = (ButtonFlat) v.findViewById(R.id.export_button);
        mImportButton = (ButtonFlat) v.findViewById(R.id.import_button);
        mDeviceExportCardView = (CardView) v.findViewById(R.id.export_device_card);
        mDeviceTitleTextView = (TextView) v.findViewById(R.id.device_title_text_view);
        mDeviceExportCardView.setCardBackgroundColor(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(getActivity())));

        PreferenceSetter.setBackgroundColorPreference(getActivity());

        final File path = new File(Environment.getExternalStorageDirectory().getPath());

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileDialog dialog = new FileDialog(getActivity(), path);
                dialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(final File directory) {
                        Log.d(getClass().getName(), "Selected directory: " + directory.toString());
                        new MaterialDialog.Builder(getActivity()).title(getString(R.string.export_data))
                                .content("The data will be exported to the folder \n\"" + directory.toString() + "\"\nDo you want to continue?")
                                .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                                .positiveText(getString(R.string.confirm))
                                .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                                .negativeText(getString(R.string.cancel))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        new ExportDataTask().execute(directory.toString());
                                    }
                                })
                                .show();
                    }
                });
                dialog.setSelectDirectoryOption(true);
                dialog.showDialog();
            }
        });

        mImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDialog dialog = new FileDialog(getActivity(), path);
                dialog.setSelectDirectoryOption(false);
                dialog.addFileListener(new FileDialog.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        Log.d(getClass().getName(), "Selected file: " + file.toString());
                        new MaterialDialog.Builder(getActivity()).title(getString(R.string.import_data))
                                .content("The data of \"" + file.getName() + "\" will be imported.\nDo you want to continue?")
                                .positiveColor(PreferenceSetter.getAppThemeColor(getActivity()))
                                .positiveText(getString(R.string.confirm))
                                .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                                .negativeText(getString(R.string.cancel))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        new ImportDataTask().execute(file);
                                    }
                                })
                                .show();
                    }
                });
                dialog.showDialog();
            }
        });

        return v;
    }

    private class ImportDataTask extends AsyncTask
    {
        MaterialDialog mDialog;

        @Override
        protected void onPreExecute()
        {
            mDialog = new MaterialDialog.Builder(getActivity()).title("Importing data")
                    .content("Please wait while the app imports the app data.")
                    .cancelable(false)
                    .progress(true, 1, false)
                    .show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            File xmlfile = (File) params[0];
            Boolean succes = PreferenceSetter.importData(getActivity(), xmlfile);

            return succes;
        }

        @Override
        public void onPostExecute(Object object)
        {
            Boolean succes = (Boolean) object;
            if (mDialog!=null)
                mDialog.dismiss();
            Toast toast;
            if (succes)
                toast = Toast.makeText(getActivity(), "The app has finished importing data!", Toast.LENGTH_SHORT);
            else
                toast = Toast.makeText(getActivity(), "Something went wrong while importing...", Toast.LENGTH_LONG);

            toast.show();
        }
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

            String path = (String) params[0];
            PreferenceSetter.exportData(getActivity(), path);

            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mDialog!=null)
                mDialog.dismiss();
            Toast toast = Toast.makeText(getActivity(), "The app has finished exporting data!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
