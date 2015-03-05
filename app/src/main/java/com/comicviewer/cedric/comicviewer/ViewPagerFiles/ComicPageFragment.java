package com.comicviewer.cedric.comicviewer.ViewPagerFiles;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.comicviewer.cedric.comicviewer.R;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    //Function to load an image, gets called after extracting the image
    public void loadImage(String filename)
    {
        mFullscreenComicView.setImageBitmap(null);
        if (filename!=null) {
            if (getActivity()!=null) {
                String imagePath = "file:" + getActivity().getFilesDir().getPath() + "/" + filename;
                Log.d("loadImage", imagePath);
                Picasso.with(getActivity()).load(imagePath).fit().centerInside().into(mFullscreenComicView);
            }
        }

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

        mFullscreenComicView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mFullscreenComicView.getScale() > 1f) {
                    ((DisplayComicActivity)getActivity()).enablePaging(false);
                } else {
                    ((DisplayComicActivity)getActivity()).enablePaging(true);
                }
                return false;
            }


        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        File file = new File(mComicArchivePath);

        if (isZipArchive(file))
            new ExtractZipTask().execute();
        else
            new ExtractRarTask().execute();
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
                        
                        File outputPage = new File(getActivity().getFilesDir(), extractedImageFile);
                        
                        if (!outputPage.exists()) {
                            FileOutputStream osPage = new FileOutputStream(outputPage);
                            arch.extractFile(fileheaders.get(j), osPage);
                        }

                        return extractedImageFile;
                    }
                }

            }
            catch (Exception e)
            {
                Log.e("ExtractRarTask", e.getMessage());
                
                // try if zip archive
                new ExtractZipTask().execute();
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

            File comic = new File(mComicArchivePath);

            String filename = null;
            InputStream is;
            ZipInputStream zis;
            try
            {
                is = new FileInputStream(comic);
                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;
                byte[] buffer = new byte[1024];
                int count;
                boolean pageFound=false;

                while ((ze = zis.getNextEntry()) != null && !pageFound)
                {
                    filename = ze.getName();

                    //Ignore directories
                    if (ze.isDirectory())
                    {
                        continue;
                    }

                    if (filename.contains("/"))
                        filename = filename.substring(filename.lastIndexOf("/")+1);

                    // get rid of special chars
                    if (filename.contains("#"))
                        filename = filename.replaceAll("#","");

                    File output = new File(getActivity().getFilesDir(), filename);

                    if (filename.equals(mImageFileName))
                    {
                        
                        if (!output.exists()) {
                            FileOutputStream fout = new FileOutputStream(output);

                            while ((count = zis.read(buffer)) != -1) {
                                fout.write(buffer, 0, count);
                            }
                            fout.close();
                        }
                        pageFound=true;

                    }
                    zis.closeEntry();
                }

                zis.close();

                return filename;

            }
            catch (Exception e)
            {
                Log.e("ExtractZipTask", e.getMessage());
            }


            return null;
        }

        @Override
        public void onPostExecute(String file)
        {
            loadImage(file);
        }

    }

    private static boolean isZipArchive(File file) {
        try {
            InputStream is = new FileInputStream(file);
            boolean isZipped = new ZipInputStream(is).getNextEntry() != null;
            return isZipped;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean isRarArchive(File filFile) {

        try {

            byte[] bytSignature = new byte[] {0x52, 0x61, 0x72, 0x21, 0x1a, 0x07, 0x00};
            FileInputStream fisFileInputStream = new FileInputStream(filFile);

            byte[] bytHeader = new byte[20];
            fisFileInputStream.read(bytHeader);

            Short shoFlags = (short) (((bytHeader[10]&0xFF)<<8) | (bytHeader[11]&0xFF));

            //Check if is an archive
            if (Arrays.equals(Arrays.copyOfRange(bytHeader, 0, 7), bytSignature)) {
                //Check if is a spanned archive
                if ((shoFlags & 0x0100) != 0) {
                    //Check if it the first part of a spanned archive
                    if ((shoFlags & 0x0001) != 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }



}
