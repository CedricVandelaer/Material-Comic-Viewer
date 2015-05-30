package com.comicviewer.cedric.comicviewer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MuzeiService extends MuzeiArtSource {

    public static final String SOURCE_NAME="Comic Viewer";
    private static final int ROTATE_TIME_MILLIS = 3 * 60 * 60 * 1000; // rotate every 3 hours

    public MuzeiService() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onUpdate(int reason) {

        List<Comic> comicList = PreferenceSetter.getSavedComics(this);


        File[] savedFolders = getFilesDir().listFiles();

        for (int j=0;j<comicList.size();j++)
        {
            boolean found = false;

            for (int i=0;i<savedFolders.length;i++)
            {
                if (Utilities.removeExtension(comicList.get(j).getFileName()).equals(savedFolders[i].getName()))
                    found = true;
            }

            if (!found)
                comicList.remove(j);

        }


        if (comicList.size()<1)
        {
            return;
        }

        Random rand = new Random(System.currentTimeMillis());

        int pos = rand.nextInt(comicList.size());

        String uriToParse = comicList.get(pos).getCoverImage();

        uriToParse = uriToParse.replace("file:///","");
        File image = new File(uriToParse);

        File muzeiFolder = new File(getFilesDir().getAbsolutePath()+"/"+"muzei");

        if (!muzeiFolder.exists())
            muzeiFolder.mkdir();

        for (int i=0;i<muzeiFolder.listFiles().length;i++)
        {
            File file = muzeiFolder.listFiles()[i];
            file.delete();
        }

        File muzeiImage = new File(muzeiFolder.getAbsolutePath() + "/" + image.getName());

        try {
            copy(image, muzeiImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Uri contentUri = FileProvider.getUriForFile(this, getString(R.string.file_provider_string), muzeiImage);

        grantUriPermission("net.nurik.roman.muzei", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        grantUriPermission("net.nurik.roman.muzei", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        publishArtwork(new Artwork.Builder()
                .title(comicList.get(pos).getTitle())
                .byline(getString(R.string.issue_number)+": " + comicList.get(pos).getIssueNumber())
                .imageUri(contentUri)
                .token(comicList.get(pos).getFileName())
                .build());

        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
