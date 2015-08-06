package com.comicviewer.cedric.comicviewer;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.gc.materialdesign.views.ButtonFlat;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class SynchronisationFragment extends BaseFragment {

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
        mDeviceExportCardView.setCardBackgroundColor(Utilities.darkenColor(StorageManager.getAppThemeColor(getActivity())));

        StorageManager.setBackgroundColorPreference(getActivity());

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAskDirectoryExportDialog();
            }
        });

        mImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAskFileImportDialog();
            }
        });

        return v;
    }

    private void showAskFileImportDialog()
    {
        final File path = new File(Environment.getExternalStorageDirectory().getPath());

        FileDialog dialog = new FileDialog(getActivity(), path);
        dialog.setSelectDirectoryOption(false);
        dialog.addFileListener(new FileDialog.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                Log.d(getClass().getName(), "Selected file: " + file.toString());
                new MaterialDialog.Builder(getActivity()).title(getString(R.string.import_data))
                        .content("The data of \"" + file.getName() + "\" will be imported. This will override current app data.\nDo you want to continue?")
                        .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                        .positiveText(getString(R.string.confirm))
                        .negativeColor(StorageManager.getAppThemeColor(getActivity()))
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

    private void showAskDirectoryExportDialog()
    {
        final File path = new File(Environment.getExternalStorageDirectory().getPath());
        FileDialog dialog = new FileDialog(getActivity(), path);
        dialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
            public void directorySelected(final File directory) {
                Log.d(getClass().getName(), "Selected directory: " + directory.toString());
                showAskFileNameDialog(directory);
            }
        });
        dialog.setSelectDirectoryOption(true);
        dialog.showDialog();
    }

    private void showAskFileNameDialog(final File directory)
    {
        new MaterialDialog.Builder(getActivity()).title(getString(R.string.import_data))
                .title("Enter filename")
                .input("Filename", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        showConfirmExportDialog(directory, charSequence.toString());
                    }
                })
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .positiveText(getString(R.string.confirm))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .negativeText(getString(R.string.cancel))
                .show();
    }

    private void showConfirmExportDialog(final File directory, final String filename)
    {
        String tempFileName = filename;
        if (!tempFileName.endsWith(".cvexport"))
            tempFileName+=".cvexport";

        new MaterialDialog.Builder(getActivity()).title(getString(R.string.export_data))
                .content("The data will be exported to the folder \n\"" + directory.toString()+"/"+tempFileName + "\"\nDo you want to continue?")
                .positiveColor(StorageManager.getAppThemeColor(getActivity()))
                .positiveText(getString(R.string.confirm))
                .negativeColor(StorageManager.getAppThemeColor(getActivity()))
                .negativeText(getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        new ExportDataTask().execute(directory.toString(), filename);
                    }
                })
                .show();
    }

    @Override
    public boolean onBackPressed() {
        return false;
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

            File jsonFile = (File) params[0];
            Boolean succes = Exporter.importData(getActivity(), jsonFile);

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
            String filename = (String) params[1];
            Exporter.exportData(getActivity(), filename, path);

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
