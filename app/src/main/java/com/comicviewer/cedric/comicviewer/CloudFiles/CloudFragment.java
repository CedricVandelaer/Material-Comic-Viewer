package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.FileDialog;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.melnykov.fab.FloatingActionButton;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class CloudFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LiveAuthListener{

    private static CloudFragment mSingleton;

    private FloatingActionButton mFab;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    private LiveAuthClient mOneDriveAuth;
    private LiveConnectClient mOneDriveClient;

    private RecyclerView mRecyclerView;
    private CloudListAdapter mAdapter;
    private Handler mHandler;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFragmentInteractionListener mListener;


    public static CloudFragment getInstance() {

        if (mSingleton == null) {
            mSingleton = new CloudFragment();
        }

        return mSingleton;
    }

    public CloudFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceSetter.setBackgroundColorPreference(getActivity());

        new AddDropboxUserInfoTask().execute();

    }

    @Override
    public void onRefresh() {
        mAdapter.refreshCloudServiceList();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
        if(status == LiveStatus.CONNECTED) {
            mOneDriveClient = new LiveConnectClient(session);

            final String token = session.getRefreshToken();


            mOneDriveClient.getAsync("me", new LiveOperationListener() {
                @Override
                public void onComplete(LiveOperation operation) {

                    JSONObject result = operation.getResult();

                    String name = "OneDrive";
                    String email = "Email";

                    try
                    {
                        name = result.getString("first_name");
                        name += " " + result.getString("last_name");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    try
                    {
                        JSONObject emails = result.getJSONObject("emails");
                        email = emails.getString("preferred");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    CloudService newOneDrive = new CloudService(getString(R.string.cloud_storage_3),
                            token,
                            name
                            , email);

                    PreferenceSetter.saveCloudService(getActivity(), newOneDrive);
                    mAdapter.refreshCloudServiceList();
                }

                @Override
                public void onError(LiveOperationException exception, LiveOperation operation) {
                    Toast.makeText(getActivity(), "An error occured retrieving OneDrive credentials...", Toast.LENGTH_LONG).show();
                }
            });


        }
        else {
            mOneDriveClient = null;
        }
    }

    @Override
    public void onAuthError(LiveAuthException exception, Object userState) {
        Toast.makeText(getActivity(), "Something went wrong signing into OneDrive", Toast.LENGTH_LONG).show();
    }


    private class AddDropboxUserInfoTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (mDBApi != null &&
                    mDBApi.getSession()!=null &&
                    mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();

                    String userName = "User";
                    String email = "Email";
                    String service = getString(R.string.cloud_storage_1);

                    try {
                        userName = mDBApi.accountInfo().displayName;
                        email = mDBApi.accountInfo().email;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    ArrayList<CloudService> savedCloudServices = PreferenceSetter.getCloudServices(getActivity());


                    for (int i=0;i<savedCloudServices.size();i++)
                    {
                        if (savedCloudServices.get(i).getEmail().equals(email) &&
                                savedCloudServices.get(i).getName().equals(service))
                        {
                            PreferenceSetter.removeCloudService(getActivity(),
                                    email, service);
                        }
                    }


                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                    CloudService cloudService = new CloudService(service, accessToken, userName, email);

                    PreferenceSetter.saveCloudService(getActivity(), cloudService);

                    mAdapter.refreshCloudServiceList();


                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }
            }
            return null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cloud, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.cloud_list_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        this.mOneDriveAuth = new LiveAuthClient(getActivity(), getActivity().getResources().getString(R.string.onedrive_id));

        mAdapter = new CloudListAdapter(getActivity());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        int vSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, outMetrics);
        int hSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(vSpace, hSpace));

        createFab(v);

        return v;
    }

    private void createFab(View v) {
        mFab = (FloatingActionButton)v.findViewById(R.id.fab);
        mFab.setColorNormal(PreferenceSetter.getAppThemeColor(getActivity()));
        mFab.setColorPressed(Utilities.darkenColor(PreferenceSetter.getAppThemeColor(getActivity())));
        mFab.setColorRipple(Utilities.lightenColor(PreferenceSetter.getAppThemeColor(getActivity())));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getActivity())
                        .title("Add cloud storage account")
                        .items(R.array.Cloud_storage_services)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text.equals(getString(R.string.cloud_storage_1)))
                                    addDropboxAccount();
                                else if (text.equals(getString(R.string.cloud_storage_3)))
                                {
                                    Iterable<String> scopes = Arrays.asList("wl.signin", "wl.offline_access", "wl.basic","wl.skydrive", "wl.emails");
                                    mOneDriveAuth.login(getActivity(), scopes, CloudFragment.this);
                                }
                            }
                        })
                        .negativeText("Cancel")
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .show();

            }
        });
    }

    private void addDropboxAccount() {
        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dropbox_app_key), getResources().getString(R.string.dropbox_app_secret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(getActivity());

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
