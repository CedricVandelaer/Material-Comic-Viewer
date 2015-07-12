package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ComicPageFragment extends Fragment {

    // The imageview for the comic
    private TouchImageView mFullscreenComicView;

    // The path to the .cbr file (Directory + filename)
    private String mComicArchivePath;

    // the name of the archive file of the comic, used to store the image files in a separate folder name to avoid collisions
    private String mFolderName;

    // The filename of the file in the archive according to JUnrar
    // (Notice that the filename also contains folders in the archive delimited by '\' instead of '/' )
    private String mImageFileName;

    // int to keep track of the number of the page in the comic
    private int mPageNumber;
    
    // The spinner to show when an image is loading
    //ProgressBarCircularIndeterminate mSpinner;

    private Handler mHandler;

    private Bitmap mBitmap;
    private boolean mIsRotated=false;

    public static ComicPageFragment newInstance(String comicPath, String pageFileName, int page)
    {
        ComicPageFragment fragment = new ComicPageFragment();
        Bundle args = new Bundle();
        args.putString("ComicArchive", comicPath);
        args.putString("Page", pageFileName);
        args.putInt("PageNumber", page);
        fragment.setArguments(args);
        return fragment;
    }

    public ComicPageFragment() {
        // Required empty public constructor

    }

    //Function to load an image, gets called after extracting the image
    public void loadImage(String filename)
    {
        
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
            ImageLoader.getInstance().init(config);
        }
        mFullscreenComicView.setImageBitmap(null);
        if (getActivity()!=null)
            mFullscreenComicView.setAllowScrollOnZoom(StorageManager.getBooleanSetting(getActivity(), StorageManager.SCROLL_ON_ZOOM_SETTING, true));

        if (filename!=null) {
            if (getActivity()!=null) {

                String imagePath;
                File archive = new File(mComicArchivePath);

                if (!archive.isDirectory())
                    imagePath = "file:///" + getActivity().getFilesDir().getPath()+"/" + mFolderName + "/" + filename;
                else
                    imagePath = "file:///" + mComicArchivePath + "/" + filename;

                Log.d("loadImage", imagePath);

                SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        zoomImageView();
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        //if (mSpinner != null)
                            //mSpinner.setVisibility(View.VISIBLE);
                        mFullscreenComicView.setVisibility(View.GONE);
                        mFullscreenComicView.setZoom(1.0f);

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        //if (mSpinner != null)
                            //mSpinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mFullscreenComicView.setVisibility(View.VISIBLE);
                        mFullscreenComicView.setZoom(1.0f);
                        //if (mSpinner != null)
                            //mSpinner.setVisibility(View.GONE);

                        mBitmap = loadedImage;

                        if (getActivity()!=null) {
                            ((AbstractDisplayComicActivity) getActivity()).setPagerTopPageColor(mPageNumber, loadedImage.getPixel(0, 0));
                            ((AbstractDisplayComicActivity) getActivity()).setPagerBottomPageColor(mPageNumber, loadedImage.getPixel(0, loadedImage.getHeight()-1));
                        }

                        zoomImageView();
                    }
                };

                if (getActivity()!=null && StorageManager.getBooleanSetting(getActivity(), StorageManager.PAGE_QUALITY_SETTING, false)) {
                    DisplayImageOptions highResOpts = new DisplayImageOptions.Builder()
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.NONE)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    ImageLoader.getInstance().displayImage(imagePath, mFullscreenComicView, highResOpts, listener);
                }
                else {
                    DisplayImageOptions lowResOpts = new DisplayImageOptions.Builder()
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    ImageLoader.getInstance().displayImage(imagePath, mFullscreenComicView, lowResOpts, listener);
                }

            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.comic_displaypage_fragment,container, false);

        Bundle args = getArguments();

        mHandler = new Handler();
        //mSpinner = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.spinner);
        mComicArchivePath = args.getString("ComicArchive");
        if (mComicArchivePath.contains("/"))
            mFolderName = Utilities.removeExtension(mComicArchivePath.substring(mComicArchivePath.lastIndexOf("/")+1));
        else
            mFolderName = Utilities.removeExtension(mComicArchivePath);
        mImageFileName = args.getString("Page");
        mPageNumber = args.getInt("PageNumber");
        mFullscreenComicView = (TouchImageView) rootView.findViewById(R.id.fullscreen_comic);

        if (getActivity()!= null && StorageManager.getBooleanSetting(getActivity(), StorageManager.SCROLL_BY_TAP_SETTING, false)) {
            final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    float x = e.getX();
                    if (getActivity() != null) {
                        float totalWidth = getActivity().getWindow().getDecorView().getWidth();
                        if (x > (totalWidth / 3 * 2))
                            ((AbstractDisplayComicActivity) getActivity()).goToRightPage();
                        else if (x < (totalWidth / 3))
                            ((AbstractDisplayComicActivity) getActivity()).goToLeftPage();
                    }
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    float x = e.getX();
                    if (getActivity() != null) {
                        float totalWidth = getActivity().getWindow().getDecorView().getWidth();
                        if (x > (totalWidth / 3 * 2))
                            ((AbstractDisplayComicActivity) getActivity()).goToLeftPage();
                        else if (x < (totalWidth / 3))
                            ((AbstractDisplayComicActivity) getActivity()).goToRightPage();
                    }
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

            mFullscreenComicView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        File file = new File(mComicArchivePath);

        if (file.isDirectory())
            loadImage(mImageFileName);
        else if (Utilities.isZipArchive(file))
            new ExtractZipTask().execute();
        else
            new ExtractRarTask().execute();
    }


    @Override
    public void onConfigurationChanged(Configuration config)
    {
        zoomImageView();
        super.onConfigurationChanged(config);
    }


    public void zoomImageView()
    {
        if (getActivity()!=null) {

            if (StorageManager.getBooleanSetting(getActivity(), StorageManager.WIDTH_AUTO_FIT_SETTING, true)
                    && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity()==null)
                            return;
                        if (StorageManager.getBooleanSetting(getActivity(), StorageManager.ROTATE_LANDSCAPE_PAGE, false) && mIsRotated)
                        {
                            mFullscreenComicView.setImageBitmap(Utilities.rotateBitmap(mBitmap, 0));
                            mIsRotated = false;
                        }

                        mFullscreenComicView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mFullscreenComicView.setScrollPosition(0.5f, 0.0f);
                    }
                }, 100);



            }
            else if(StorageManager.getBooleanSetting(getActivity(), StorageManager.ROTATE_LANDSCAPE_PAGE, false))
            {
                if (mFullscreenComicView.getDrawable().getIntrinsicWidth()>mFullscreenComicView.getDrawable().getIntrinsicHeight()
                        && !mIsRotated)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity()==null)
                                return;
                            mFullscreenComicView.setImageBitmap(Utilities.rotateBitmap(mBitmap,90));
                            mIsRotated = true;

                            if (StorageManager.getBooleanSetting(getActivity(), StorageManager.WIDTH_AUTO_FIT_SETTING, true))
                            {
                                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                                {
                                    mFullscreenComicView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    mFullscreenComicView.setScrollPosition(0.5f, 0.0f);
                                }
                                else
                                {
                                    mFullscreenComicView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                }
                            }
                            else
                            {
                                mFullscreenComicView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }

                        }
                    }, 100);
                }
                else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    if (mIsRotated)
                    {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity()==null)
                                    return;
                                mFullscreenComicView.setImageBitmap(Utilities.rotateBitmap(mBitmap,0));
                                mIsRotated = false;
                            }
                        }, 100);
                    }
                }
                else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFullscreenComicView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                    }, 100);
                }
            }
            else {
                if (mIsRotated)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity()==null)
                                return;
                            if (StorageManager.getBooleanSetting(getActivity(), StorageManager.ROTATE_LANDSCAPE_PAGE, false) && mIsRotated) {
                                mFullscreenComicView.setImageBitmap(Utilities.rotateBitmap(mBitmap, 0));
                                mIsRotated = false;
                            }

                            mFullscreenComicView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                    }, 100);
                }
                mFullscreenComicView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

        }
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

                    if (fileheaders.get(j).getFileNameString().equals(mImageFileName))
                    {
                        // get rid of special chars
                        if (extractedImageFile.contains("#"))
                            extractedImageFile = extractedImageFile.replaceAll("#","");

                        if (getActivity()==null)
                            throw new NullPointerException("Activity is null while loading page");
                        File directory = new File(getActivity().getFilesDir().getPath()+"/"+mFolderName);
                        directory.mkdir();
                        File outputPage = new File(directory.getPath(), extractedImageFile);

                        if (!outputPage.exists()) {
                            FileOutputStream osPage = new FileOutputStream(outputPage);
                            arch.extractFile(fileheaders.get(j), osPage);
                        }

                        Log.d("Extract rar",extractedImageFile);
                        return extractedImageFile;
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String file)
        {
            loadImage(file);
        }

    }


    private class ExtractZipTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... comicVar) {

            try {
                ZipFile zipFile = new ZipFile(mComicArchivePath);

                List<net.lingala.zip4j.model.FileHeader> fileHeaders = zipFile.getFileHeaders();

                for (int i=0;i<fileHeaders.size();i++)
                {
                    String extractedImageFile;
                    if (fileHeaders.get(i).getFileName().contains("\\"))
                    {
                        extractedImageFile = fileHeaders.get(i).getFileName().substring(fileHeaders.get(i).getFileName().lastIndexOf("\\")+1);
                    }
                    else
                    {
                        extractedImageFile = fileHeaders.get(i).getFileName();
                    }

                    if (extractedImageFile.contains("/"))
                        extractedImageFile = extractedImageFile.substring(extractedImageFile.lastIndexOf("/")+1);


                    Log.d("ExtractZip",extractedImageFile);
                    Log.d("mImageFileName", mImageFileName);

                    if (extractedImageFile.equals(mImageFileName))
                    {
                        // get rid of special chars
                        if (extractedImageFile.contains("#"))
                            extractedImageFile = extractedImageFile.replaceAll("#","");

                        if (getActivity()==null)
                            throw new NullPointerException("Activity is null while loading page");

                        File directory = new File(getActivity().getFilesDir().getPath()+"/"+mFolderName);
                        directory.mkdir();
                        File outputPage = new File( directory.getPath(), fileHeaders.get(i).getFileName());

                        if (!outputPage.exists()) {
                            //FileOutputStream osPage = new FileOutputStream(outputPage);
                            String extractPath = directory.getPath();
                            Log.d("Extractpath",directory.getPath());
                            zipFile.extractFile(fileHeaders.get(i), directory.getPath());
                        }

                        return fileHeaders.get(i).getFileName();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        public void onPostExecute(String file)
        {
            loadImage(file);
        }

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (mBitmap!=null)
            mBitmap.recycle();
    }

}
