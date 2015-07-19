package com.comicviewer.cedric.comicviewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comicviewer.cedric.comicviewer.FragmentNavigation.BaseFragment;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarDeterminate;

import java.util.List;
import java.util.Map;

public class StatisticsFragment extends BaseFragment {

    private TextView mComicsStartedTextView;
    private TextView mComicsReadTextView;
    private TextView mCompletedPercentageTextView;
    private TextView mPagesReadTextView;
    private TextView mComicsAddedTextView;
    private TextView mFavoriteSeries;
    private TextView mLongestReadTitleTextView;
    private TextView mLongestReadPagesTextView;
    private ButtonFlat mResetStatsButton;

    private TextView mComicsStartedTitleTextView;
    private TextView mComicsReadTitleTextView;
    private TextView mCompletedPercentageTitleTextView;
    private TextView mPagesReadTitleTextView;
    private TextView mComicsAddedTitleTextView;
    private TextView mFavoriteSeriesTitle;
    private TextView mLongestReadTitleTitleTextView;
    private TextView mLongestReadPagesTitleTextView;

    private ProgressBarDeterminate mProgressBar;

    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);

        mComicsStartedTextView = (TextView) v.findViewById(R.id.total_comics_started_textview);
        mComicsReadTextView = (TextView) v.findViewById(R.id.total_comics_read_textview);
        mCompletedPercentageTextView = (TextView) v.findViewById(R.id.finished_comics_percentage_textview);

        mPagesReadTextView = (TextView) v.findViewById(R.id.pages_read_textview);

        mComicsAddedTextView = (TextView) v.findViewById(R.id.added_comics_textview);
        mFavoriteSeries = (TextView) v.findViewById(R.id.favorite_series_textview);

        mLongestReadTitleTextView = (TextView) v.findViewById(R.id.longest_read_title_textview);
        mLongestReadPagesTextView = (TextView) v.findViewById(R.id.longest_number_pages_textview);

        mComicsStartedTitleTextView = (TextView) v.findViewById(R.id.total_comics_started_title_textview);
        mComicsReadTitleTextView = (TextView) v.findViewById(R.id.total_comics_read_title_textview);
        mCompletedPercentageTitleTextView = (TextView) v.findViewById(R.id.finished_comics_percentage_title_textview);

        mPagesReadTitleTextView = (TextView) v.findViewById(R.id.pages_read_title_textview);

        mComicsAddedTitleTextView = (TextView) v.findViewById(R.id.added_comics_title_textview);
        mFavoriteSeriesTitle = (TextView) v.findViewById(R.id.favorite_series_title_textview);

        mLongestReadTitleTitleTextView = (TextView) v.findViewById(R.id.longest_read_title_title_textview);
        mLongestReadPagesTitleTextView = (TextView) v.findViewById(R.id.longest_number_pages_title_textview);

        mProgressBar = (ProgressBarDeterminate) v.findViewById(R.id.progress_bar);

        StorageManager.setBackgroundColorPreference(getActivity());

        if (StorageManager.getBackgroundColorPreference(getActivity())==getResources().getColor(R.color.WhiteBG))
            setTitleTextViewTextColors(getResources().getColor(R.color.Black));

        setTextViewTextColors(StorageManager.getAppThemeColor(getActivity()));

        updateTextViews();

        mResetStatsButton = (ButtonFlat) v.findViewById(R.id.reset_statistics_button);

        mResetStatsButton.setBackgroundColor(StorageManager.getAppThemeColor(getActivity()));

        mResetStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResetStatsTask().execute();
            }
        });

        return v;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private class ResetStatsTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {
            StorageManager.resetStats(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Object object)
        {
            updateTextViews();
        }
    }

    private void updateTextViews() {
        mProgressBar.setBackgroundColor(StorageManager.getAppThemeColor(getActivity()));

        int comicsStarted = StorageManager.getNumberOfComicsStarted(getActivity());
        int comicsRead = StorageManager.getNumberOfComicsRead(getActivity());
        mComicsStartedTextView.setText("" + comicsStarted);
        mComicsReadTextView.setText("" + comicsRead);

        if (comicsStarted == 0)
        {
            mCompletedPercentageTextView.setText("100%");
            mProgressBar.setProgress(100);
        }
        else
        {
            int completedPercentage = (int)(100.0f *((float)(comicsRead)/(float)(comicsStarted)));
            mCompletedPercentageTextView.setText(""+completedPercentage+"%");
            mProgressBar.setProgress(completedPercentage);
        }



        Map<String, Integer> map = StorageManager.getPagesReadMap(getActivity());

        int pagesRead = 0;

        for (String key:map.keySet())
        {
            pagesRead += map.get(key);
        }

        mPagesReadTextView.setText(""+pagesRead);

        List<String> comicsAdded = StorageManager.getComicsAdded(getActivity());

        mComicsAddedTextView.setText(""+comicsAdded.size());

        Map<String, Integer> seriesReadMap = StorageManager.getSeriesPagesReadMap(getActivity());

        int seriesPages = -1;
        String seriesName = "";

        for (String key:seriesReadMap.keySet())
        {
            int pagesForSeries = seriesReadMap.get(key);

            if ( pagesForSeries > seriesPages)
            {
                seriesPages = pagesForSeries;
                seriesName = key;
            }
        }

        if (seriesPages>-1)
        {
            mFavoriteSeries.setText(seriesName);
        }
        else
        {
            mFavoriteSeries.setText(getString(R.string.none_yet));
        }

        mLongestReadTitleTextView.setText(StorageManager.getLongestReadComicTitle(getActivity()));
        mLongestReadPagesTextView.setText(""+ StorageManager.getLongestReadComicPages(getActivity()));

    }

    public void setTextViewTextColors(int color)
    {
        mComicsStartedTextView.setTextColor(color);
        mComicsReadTextView.setTextColor(color);
        mCompletedPercentageTextView.setTextColor(color);
        mPagesReadTextView.setTextColor(color);
        mComicsAddedTextView.setTextColor(color);
        mFavoriteSeries.setTextColor(color);
        mLongestReadTitleTextView.setTextColor(color);
        mLongestReadPagesTextView.setTextColor(color);


    }

    public void setTitleTextViewTextColors(int color)
    {
        mComicsStartedTitleTextView.setTextColor(color);
        mComicsReadTitleTextView.setTextColor(color);
        mCompletedPercentageTitleTextView.setTextColor(color);
        mPagesReadTitleTextView.setTextColor(color);
        mComicsAddedTitleTextView.setTextColor(color);
        mFavoriteSeriesTitle.setTextColor(color);
        mLongestReadTitleTitleTextView.setTextColor(color);
        mLongestReadPagesTitleTextView.setTextColor(color);
    }

}
