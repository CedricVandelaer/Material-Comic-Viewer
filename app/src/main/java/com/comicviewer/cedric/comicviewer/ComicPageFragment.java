package com.comicviewer.cedric.comicviewer;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComicPageFragment extends Fragment {

    private ImageViewTouch mFullscreenComicView;
    private String mComicArchivePath;
    private String mImageFileName;
    private int mPageNumber;


    public static final ComicPageFragment newInstance(String comicPath, String pageFileName, int page)
    {
        ComicPageFragment fragment = new ComicPageFragment();
        Bundle args = new Bundle();
        args.putString("ComicArchive", comicPath);
        args.putString("Page", pageFileName);
        args.putInt("PageNumber",page);
        fragment.setArguments(args);
        return fragment;
    }

    public ComicPageFragment() {
        // Required empty public constructor

    }

    public void loadImage()
    {
        String imagePath = "file:"+getActivity().getFilesDir().getPath()+"/"+mImageFileName;
        Picasso.with(getActivity()).load(imagePath).fit().into(mFullscreenComicView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.comic_displaypage_fragment,container, false);

        Bundle args = getArguments();

        mComicArchivePath = args.getString("ComicArchive");
        mImageFileName = args.getString("Page");
        mPageNumber = args.getInt("PageNumber");
        mFullscreenComicView = (ImageViewTouch) rootView.findViewById(R.id.fullscreen_comic);

        new ExtractRarTask().execute();

        return rootView;
    }

    private class ExtractRarTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... comicVar) {

            File comic = new File(mComicArchivePath);
            try {
                Archive arch = new Archive(comic);
                List<FileHeader> fileheaders = arch.getFileHeaders();

                for (int j = 0; j < fileheaders.size(); j++) {

                    File outputPage = new File(getActivity().getFilesDir(), mImageFileName);
                    FileOutputStream osPage = new FileOutputStream(outputPage);

                    if (fileheaders.get(j).getFileNameString().equals(mImageFileName))
                    {
                        arch.extractFile(fileheaders.get(j),osPage);
                        String imagePath = "file:"+getActivity().getFilesDir().getPath()+"/"+mImageFileName;
                        Log.d("ExtractImage", "Extracted "+imagePath);
                        //Picasso.with(getActivity()).load(imagePath).fetch();
                    }
                }

            }
            catch (Exception e)
            {
                Log.e("ExtractRarTask", e.getMessage());
            }

            return null;
        }

        @Override
        public void onPostExecute(Void aVoid)
        {
            loadImage();
        }
    }

}
