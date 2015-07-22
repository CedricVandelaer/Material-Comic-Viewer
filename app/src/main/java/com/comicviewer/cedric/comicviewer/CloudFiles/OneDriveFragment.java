package com.comicviewer.cedric.comicviewer.CloudFiles;


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
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.Model.OneDriveObject;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneDriveFragment extends AbstractCloudServiceListFragment implements LiveAuthListener, SwipeRefreshLayout.OnRefreshListener{

    private CloudService mCloudService;

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OneDriveAdapter mAdapter;
    private Handler mHandler;

    private LiveAuthClient mOneDriveAuth;
    private LiveConnectClient mOneDriveClient;

    public OneDriveFragment() {
        // Required empty public constructor
    }

    public static OneDriveFragment newInstance(CloudService cloudService)
    {
        OneDriveFragment fragment = new OneDriveFragment();

        Bundle args = new Bundle();
        args.putSerializable("CloudService", cloudService);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_onedrive, container, false);

        mCloudService = (CloudService) getArguments().getSerializable("CloudService");

        mHandler = new Handler();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mErrorTextView = (TextView) v.findViewById(R.id.error_text_view);

        if (StorageManager.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));

        mErrorTextView.setVisibility(View.GONE);

        if (getNavigationManager().emptyStack())
            getNavigationManager().reset("me/skydrive/files");

        mRecyclerView = (RecyclerView) v.findViewById(R.id.cloud_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OneDriveAdapter(this, mCloudService);
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

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mOneDriveAuth = new LiveAuthClient(getActivity(), getString(R.string.onedrive_id));
        Object userState = new Object();
        Iterable<String> scopes = Arrays.asList("wl.signin", "wl.offline_access", "wl.basic", "wl.skydrive", "wl.emails");

        mOneDriveAuth.initialize(scopes, this, userState, mCloudService.getToken());

        return v;
    }

    public void refresh()
    {
        mAdapter.clear();
        readFolder();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        StorageManager.setBackgroundColorPreference(getActivity());

        if (mOneDriveClient!=null)
        {
            mAdapter.clear();
            readFolder();
        }
        else
        {
            mErrorTextView.setText(getString(R.string.error_while_authenticating));
        }
    }

    public void readFolder() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mOneDriveClient.getAsync((String)getNavigationManager().getValueFromStack(), new LiveOperationListener() {
            public void onComplete(LiveOperation operation) {
                JSONObject result = operation.getResult();
                Log.d("Result", result.toString());
                try
                {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    JSONArray data = result.getJSONArray("data");

                    boolean fileFound = false;

                    for (int i=0;i<data.length();i++)
                    {
                        JSONObject folder = data.getJSONObject(i);
                        OneDriveObject newFile = new OneDriveObject(folder.getString("name"), folder.getString("id"));

                        if (newFile.getType()== ObjectType.FOLDER || Utilities.checkExtension(newFile.getName())) {
                            mAdapter.addOneDriveEntry(newFile);
                            fileFound = true;
                        }
                    }

                    if (!fileFound)
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
                    e.printStackTrace();
                }
            }

            public void onError(LiveOperationException exception, LiveOperation operation) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {

        if(status == LiveStatus.CONNECTED)
        {
            mOneDriveClient = new LiveConnectClient(session);
            final String token = session.getRefreshToken();
            CloudService cloudService = new CloudService(mCloudService.getName(), token, mCloudService.getUsername(), mCloudService.getEmail());
            StorageManager.saveCloudService(getActivity(), cloudService);
            readFolder();
        }
        else
        {
            mOneDriveClient = null;
        }
    }

    @Override
    public void onAuthError(LiveAuthException exception, Object userState) {

    }


    @Override
    public void onRefresh() {
        mAdapter.clear();
        readFolder();
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
}
