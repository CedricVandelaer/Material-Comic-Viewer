package com.comicviewer.cedric.comicviewer;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComicPageFragment extends Fragment {

    // The imageview for the comic
    private ImageViewTouch mFullscreenComicView;

    // The path to the .cbr file (Directory + filename)
    private String mComicArchivePath;

    // The filename of the file in the archive according to JUnrar
    // (Notice that the filename also contains folders in the archive delimited by '\' instead of '/' )
    private String mImageFileName;

    // int to keep track of the number of the page in the comic
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

    public void loadImage(String filename)
    {
        mFullscreenComicView.setImageBitmap(null);
        Picasso picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        }).build();
        String imagePath = "file:"+getActivity().getFilesDir().getPath()+"/"+filename;
        Log.d("loadImage", imagePath);
        picasso.with(getActivity()).load(imagePath).fit().into(mFullscreenComicView);
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

    private class ExtractRarTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... comicVar) {

            File comic = new File(mComicArchivePath);
            try {
                Archive arch = new Archive(comic);
                List<FileHeader> fileheaders = arch.getFileHeaders();

                for (int j = 0; j < fileheaders.size(); j++) {

                    String extractedImageFile = fileheaders.get(j).getFileNameString().substring(fileheaders.get(j).getFileNameString().lastIndexOf("\\")+1);
                    File outputPage = new File(getActivity().getFilesDir(), extractedImageFile);
                    FileOutputStream osPage = new FileOutputStream(outputPage);

                    if (fileheaders.get(j).getFileNameString().equals(mImageFileName))
                    {
                        arch.extractFile(fileheaders.get(j),osPage);
                        String imagePath = "file:"+getActivity().getFilesDir().getPath()+"/"+extractedImageFile;
                        Log.d("ExtractImage", "Extracted "+imagePath);
                        return extractedImageFile;
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
        public void onPostExecute(String file)
        {
            loadImage(file);
        }
    }

}
