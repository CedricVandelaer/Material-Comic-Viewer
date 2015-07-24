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

import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DropboxFragment extends AbstractCloudServiceListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private CloudService mCloudService;

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DropboxAdapter mAdapter;
    private Handler mHandler;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    public DropboxFragment() {
        // Required empty public constructor
    }

    public static DropboxFragment newInstance(CloudService cloudService)
    {
        DropboxFragment fragment = new DropboxFragment();

        Bundle args = new Bundle();
        args.putSerializable("CloudService", cloudService);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_dropbox, container, false);

        mCloudService = (CloudService) getArguments().getSerializable("CloudService");

        mHandler = new Handler();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mErrorTextView = (TextView) v.findViewById(R.id.error_text_view);


        if (StorageManager.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));

        mErrorTextView.setVisibility(View.GONE);

        if (getNavigationManager().emptyStack())
            getNavigationManager().reset("/");

        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mRecyclerView = (RecyclerView) v.findViewById(R.id.cloud_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DropboxAdapter(this, mCloudService);
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

        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dropbox_app_key), getResources().getString(R.string.dropbox_app_secret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys, mCloudService.getToken());
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        if (mDBApi.getSession().authenticationSuccessful()) {
            String token = mDBApi.getSession().finishAuthentication();
        }

        if (mDBApi.getSession().isLinked()) {
            new RetrieveFilesTask().execute();
        }
        else
        {
            mDBApi.getSession().startOAuth2Authentication(getActivity());
        }

        return v;
    }



    public void refresh()
    {
        mAdapter.clear();
        new RetrieveFilesTask().execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        StorageManager.setBackgroundColorPreference(getActivity());

        if (!mDBApi.getSession().authenticationSuccessful()) {
            new AddDropboxUserInfoTask().execute();
        }
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public boolean onBackPressed() {
        if (getNavigationManager().hasOneElementOrLess())
            return false;
        else
        {
            getNavigationManager().popFromStack();
            refresh();
            return true;
        }
    }

    private class RetrieveFilesTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });

            DropboxAPI.Entry existingEntry = null;

            try {
                existingEntry = mDBApi.metadata((String)getNavigationManager().getValueFromStack(), 1000, null, true, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (existingEntry!=null) {
                Log.d("RetrieveFileTask", existingEntry.path);

                List<DropboxAPI.Entry> entryList = existingEntry.contents;

                Collections.sort(entryList, new Comparator<DropboxAPI.Entry>() {
                    @Override
                    public int compare(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
                        return lhs.fileName().compareToIgnoreCase(rhs.fileName());
                    }
                });

                for (int i=0;i<entryList.size();i++) {

                    if (entryList.get(i).isDir)
                        mAdapter.addDropBoxEntry(entryList.get(i));
                }

                for (int i=0;i<entryList.size();i++) {

                    if (Utilities.checkExtension(entryList.get(i).fileName()))
                        mAdapter.addDropBoxEntry(entryList.get(i));
                }


            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            return null;
        }

        @Override
        public void onPostExecute(Object object)
        {
            if (mAdapter.getItemCount()==0)
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mErrorTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
            else
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mErrorTextView.setVisibility(View.GONE);
                    }
                });
            }
        }
    }


    private class AddDropboxUserInfoTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (mDBApi != null &&
                    mDBApi.getSession()!=null &&
                    mDBApi.getSession().authenticationSuccessful() &&
                    mDBApi.getSession().isLinked()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();

                    String userName = "User";
                    String email = "Email";
                    String service = "Dropbox";

                    try {
                        userName = mDBApi.accountInfo().displayName;
                        email = mDBApi.accountInfo().email;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                    CloudService cloudService = new CloudService(service, accessToken, userName, email);

                    StorageManager.removeCloudService(getActivity(), cloudService.getEmail(), cloudService.getName());

                    StorageManager.saveCloudService(getActivity(), cloudService);


                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }
            }
            return null;
        }
    }

}




