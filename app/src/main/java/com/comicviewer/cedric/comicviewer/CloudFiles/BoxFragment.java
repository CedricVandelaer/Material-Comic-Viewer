package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.os.AsyncTask;
import android.os.Bundle;
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

import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxListItems;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxResponse;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.FragmentNavigation.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.RecyclerViewListFiles.DividerItemDecoration;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by CV on 11/07/2015.
 */
public class BoxFragment extends AbstractCloudServiceListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private CloudService mCloudService;
    private Handler mHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mErrorTextView;
    private RecyclerView mRecyclerView;
    private BoxAdapter mAdapter;
    private BoxSession mSession;
    private BoxApiFolder mBoxApiFolder;

    public BoxFragment() {
        // Required empty public constructor
    }

    public static BoxFragment newInstance(CloudService cloudService)
    {
        BoxFragment fragment = new BoxFragment();

        Bundle args = new Bundle();
        args.putSerializable("CloudService", cloudService);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.activity_box, container, false);

        mCloudService = (CloudService) getArguments().getSerializable("CloudService");

        mHandler = new Handler();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mErrorTextView = (TextView) v.findViewById(R.id.error_text_view);


        if (StorageManager.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG))
            mErrorTextView.setTextColor(getResources().getColor(R.color.Black));

        mErrorTextView.setVisibility(View.GONE);

        Log.d("CloudBrowserActivity", mCloudService.getName() + "\n"
                + mCloudService.getUsername() + "\n"
                + mCloudService.getEmail() + "\n"
                + mCloudService.getToken());

        mRecyclerView = (RecyclerView) v.findViewById(R.id.cloud_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new BoxAdapter(this, mCloudService);
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

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(true);

        BoxConfig.CLIENT_ID = getString(R.string.box_client_id);
        BoxConfig.CLIENT_SECRET = getString(R.string.box_client_secret);
        BoxConfig.REDIRECT_URL = getString(R.string.box_redirect_url);

        final BoxSession mSession = new BoxSession(getActivity(), mCloudService.getEmail());
        mSession.authenticate().addOnCompletedListener(new BoxFutureTask.OnCompletedListener<BoxSession>() {
            @Override
            public void onCompleted(BoxResponse<BoxSession> boxResponse) {
                if (boxResponse.isSuccess()) {
                    if (getNavigationManager().emptyStack())
                        getNavigationManager().reset("0");
                    mBoxApiFolder = new BoxApiFolder(mSession);
                    new GetBoxFilesTask().execute();
                }
                else {
                    boxResponse.getException().printStackTrace();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        StorageManager.setBackgroundColorPreference(getActivity());
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh()
    {
        mAdapter.clear();
        new GetBoxFilesTask().execute();
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

    private class GetBoxFilesTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            String id = (String)getNavigationManager().getValueFromStack();

            try {
                final BoxListItems boxListItems = mBoxApiFolder.getItemsRequest(id).send();

                final ArrayList<BoxItem> boxItems = new ArrayList<>();

                for (int i=0;i<boxListItems.size();i++)
                {
                    if (boxListItems.get(i).getType().equals("folder") || Utilities.checkExtension(boxListItems.get(i).getName()))
                        boxItems.add(boxListItems.get(i));
                }

                Collections.sort(boxItems, new Comparator<BoxItem>() {
                    @Override
                    public int compare(BoxItem lhs, BoxItem rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });

                for (int i=0;i<boxItems.size();i++)
                {
                    if (boxItems.get(i).getType().equals("folder")) {
                        final int finalI = i;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.addBoxObject(boxItems.get(finalI));
                            }
                        });
                    }
                }
                for (int i=0;i<boxItems.size();i++)
                {
                    if (Utilities.checkExtension(boxItems.get(i).getName())) {
                        final int finalI = i;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.addBoxObject(boxItems.get(finalI));
                            }
                        });
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (boxItems.size()<1) {
                            mErrorTextView.setVisibility(View.VISIBLE);
                            mErrorTextView.setText(getString(R.string.no_supported_files_found));
                        }
                        else
                            mErrorTextView.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mErrorTextView.setVisibility(View.VISIBLE);
                        mErrorTextView.setText(getString(R.string.error));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }

            return null;
        }
    }

}
