package com.comicviewer.cedric.comicviewer.CloudFiles;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.HttpUtilities;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.GoogleDriveObject;
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleDriveFragment extends AbstractCloudServiceListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private CloudService mCloudService;
    private TextView mErrorTextView;

    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private GoogleDriveAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final static String DRIVE_API_SCOPE_FILES = "https://www.googleapis.com/auth/drive.readonly";
    private final static String DRIVE_API_SCOPE_METADATA = "https://www.googleapis.com/auth/drive.metadata.readonly";
    private final static String SCOPE_PROFILE_INFO = "https://www.googleapis.com/auth/userinfo.profile";

    public GoogleDriveFragment() {
        // Required empty public constructor
    }

    public static GoogleDriveFragment newInstance(CloudService cloudService)
    {
        GoogleDriveFragment fragment = new GoogleDriveFragment();

        Bundle args = new Bundle();
        args.putSerializable("CloudService", cloudService);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_google_drive, container, false);

        mErrorTextView = (TextView) v.findViewById(R.id.error_text_view);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.cloud_file_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHandler = new Handler();


        mCloudService = (CloudService) getArguments().getSerializable("CloudService");

        StorageManager.setBackgroundColorPreference(getActivity());
        if (StorageManager.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));

        if (getNavigationManager().emptyStack())
            getNavigationManager().reset(NavigationManager.ROOT);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new GoogleDriveAdapter(this, mCloudService);
        mRecyclerView.setAdapter(mAdapter);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        //in pixels
        int vSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);
        int hSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(vSpace, hSpace));


        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mErrorTextView.setVisibility(View.GONE);


        if (HttpUtilities.isConnected(getActivity()))
        {
            try {
                new GetDriveFilesTask().execute("root");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText("No internet connection...");
        }

        return v;
    }


    private void showProgressSpinner(final boolean enable)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh()
    {
        new GetDriveFilesTask().execute((String)getNavigationManager().getValueFromStack());
    }

    @Override
    public boolean onBackPressed() {
        getNavigationManager().popFromStack();
        if (getNavigationManager().emptyStack())
            return false;
        else
        {
            refresh();
            return true;
        }
    }

    private class GetDriveFilesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {
            mAdapter.clear();
            showProgressSpinner(true);
        }

        @Override
        protected String doInBackground(String... folderId) {
            try {
                String token= GoogleAuthUtil.getToken(getActivity(), mCloudService.getEmail(),
                        "oauth2:" + DRIVE_API_SCOPE_FILES + " " + DRIVE_API_SCOPE_METADATA + " " + SCOPE_PROFILE_INFO);
                mCloudService.setToken(token);
                StorageManager.saveCloudService(getActivity(), mCloudService);

                String url = "https://www.googleapis.com/drive/v2/files"+
                        "?q=%27" + folderId[0] +
                        "%27%20in%20parents%20and%20trashed=false"+"&fields=items(title,downloadUrl,id,mimeType)"+"&access_token="+token;

                return HttpUtilities.GET(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Authentication error";
            }

        }

        @Override
        protected void onPostExecute(String result)
        {
            showProgressSpinner(false);
            try
            {
                JSONObject queryResults = new JSONObject(result);
                JSONArray filesList = queryResults.getJSONArray("items");

                ArrayList<GoogleDriveObject> driveObjects = new ArrayList<>();

                for (int i=0;i<filesList.length();i++)
                {
                    JSONObject file = filesList.getJSONObject(i);
                    String downloadUrl = null;
                    try
                    {
                        downloadUrl = file.getString("downloadUrl");
                    }
                    catch (JSONException e)
                    {
                        Log.d("Google Drive","Download url is null");
                    }
                    GoogleDriveObject driveObject = new GoogleDriveObject(file.getString("title"),
                            file.getString("id"),
                            file.getString("mimeType"),
                            downloadUrl);
                    if ((driveObject.getType()== ObjectType.FILE && Utilities.checkExtension(driveObject.getName()))
                            || driveObject.getType() == ObjectType.FOLDER) {
                        driveObjects.add(driveObject);
                    }
                    else
                    {
                        Log.d("GoogleDrive", "Other file found: "+file.getString("title"));
                    }
                }

                Collections.sort(driveObjects, new Comparator<GoogleDriveObject>() {
                    @Override
                    public int compare(GoogleDriveObject lhs, GoogleDriveObject rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });

                for (int i=0;i<driveObjects.size();i++)
                {
                    if (driveObjects.get(i).getType() == ObjectType.FOLDER)
                        mAdapter.addDriveObject(driveObjects.get(i));
                }

                for (int i=0;i<driveObjects.size();i++)
                {
                    if (driveObjects.get(i).getType() == ObjectType.FILE)
                        mAdapter.addDriveObject(driveObjects.get(i));
                }

                if (driveObjects.size()<1)
                {
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(getString(R.string.no_supported_files_found));
                }
                else
                {
                    mErrorTextView.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                mAdapter.clear();
                e.printStackTrace();
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.error));
            }

        }

    }


}
