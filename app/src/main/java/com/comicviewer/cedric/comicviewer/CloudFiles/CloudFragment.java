package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
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
import com.comicviewer.cedric.comicviewer.HttpUtilities;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class CloudFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LiveAuthListener{

    private static CloudFragment mSingleton;

    private FloatingActionButton mFab;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    private LiveAuthClient mOneDriveAuth;
    private LiveConnectClient mOneDriveClient;

    //private GoogleApiClient mGoogleApiClient;

    private RecyclerView mRecyclerView;
    private CloudListAdapter mAdapter;
    private Handler mHandler;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFragmentInteractionListener mListener;

    public static final int RESOLVE_CONNECTION_REQUEST_CODE = 8;
    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    public static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    public static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR  = 1002;


    private final static String DRIVE_API_SCOPE_FILES = "https://www.googleapis.com/auth/drive.readonly";
    private final static String DRIVE_API_SCOPE_METADATA = "https://www.googleapis.com/auth/drive.metadata.readonly";
    private final static String SCOPE_PROFILE_INFO = "https://www.googleapis.com/auth/userinfo.profile";


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

        /*
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addApi(Plus.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
                    */
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

    /*
    @Override
    public void onConnected(Bundle bundle) {


        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String service = getString(R.string.cloud_storage_2);

        CloudService cloudService = new CloudService(
                service,
                "No token",
                accountName,
                accountName
        );

        PreferenceSetter.saveCloudService(getActivity(), cloudService);
        PreferenceSetter.saveLastUsedGoogleAccount(getActivity(), accountName);

        mAdapter.refreshCloudServiceList();
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
                e.printStackTrace();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(), 0).show();
        }

    }
    */
    @Override
    public void onStop()
    {
        super.onStop();
        /*
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
            */
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
                                else if (text.equals(getString(R.string.cloud_storage_3))) {
                                    Iterable<String> scopes = Arrays.asList("wl.signin", "wl.offline_access", "wl.basic", "wl.skydrive", "wl.emails");
                                    mOneDriveAuth.login(getActivity(), scopes, CloudFragment.this);
                                } else if (text.equals(getString(R.string.cloud_storage_2))) {
                                    //new ConnectGoogleAccountTask().execute();
                                    pickUserAccount();
                                }
                            }
                        })
                        .negativeText("Cancel")
                        .negativeColor(PreferenceSetter.getAppThemeColor(getActivity()))
                        .show();

            }
        });
    }


    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /*
    private class ConnectGoogleAccountTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            if (mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
            else
            {
                mGoogleApiClient.connect();
            }

            return null;
        }
    }
    */
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
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        /*
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
        */

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                String scope = "oauth2:" + DRIVE_API_SCOPE_FILES + " " + DRIVE_API_SCOPE_METADATA + " "+ SCOPE_PROFILE_INFO;
                // With the account name acquired, go get the auth token
                new GetGoogleTokenTask(getActivity(), email, scope).execute();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == Activity.RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            pickUserAccount();
        }
    }

    private class GetGoogleUserNameTask extends AsyncTask<Void, Void, String> {

        private String mToken;
        private String mEmail;

        public GetGoogleUserNameTask(String token, String email)
        {
            mToken = token;
            mEmail = email;
        }

        @Override
        protected String doInBackground(Void... aVoid) {

            return HttpUtilities.GET("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="+mToken);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONObject json = new JSONObject(result);
                String name = json.getString("name");

                CloudService driveCloudService = new CloudService(getString(R.string.cloud_storage_2),
                        mToken,
                        name,
                        mEmail);

                PreferenceSetter.saveCloudService(getActivity(),driveCloudService);
                mAdapter.refreshCloudServiceList();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public class GetGoogleTokenTask extends AsyncTask{
        Activity mActivity;
        String mScope;
        String mEmail;

        GetGoogleTokenTask(Activity activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
        }

        @Override
        protected Void doInBackground(Object... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    // **Insert the good stuff here.**
                    // Use the token to access the user's Google data.
                    Log.d("Google Drive", "Token: " + token);

                    new GetGoogleUserNameTask(token, mEmail).execute();

                }
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.

            }
            return null;
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may occur.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (Exception e) {
                handleException(e);
            }
            return null;
        }

    }


    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            getActivity(),
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
                else
                {
                    e.printStackTrace();
                }
            }
        });
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
