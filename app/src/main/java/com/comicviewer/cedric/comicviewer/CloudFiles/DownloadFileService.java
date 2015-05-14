package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;


public class DownloadFileService extends IntentService {

    final static private String APP_KEY = "id9ssazcpa41gys";
    final static private String APP_SECRET = "yj0gk3nipr6ti4u";
    final static private String NOTIFICATION_KEY="ComicViewerNotifGroup";

    NotificationCompat.Builder mNotification;
    private static Random mRand = new Random();

    private static final String ACTION_DROPBOX_DOWNLOAD = "com.comicviewer.cedric.comicviewer.CloudFiles.action.DROPBOXDOWNLOAD";

    public static void startActionDownload(Context context, String fileUrl, CloudService cloudService) {

        mRand.setSeed(System.currentTimeMillis());
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.setAction(ACTION_DROPBOX_DOWNLOAD);
        intent.putExtra("FILE_URL", fileUrl);
        intent.putExtra("CLOUD_SERVICE", cloudService);
        context.startService(intent);
    }

    public DownloadFileService() {
        super("DownloadDropboxFileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DROPBOX_DOWNLOAD.equals(action)) {
                final String file_url = intent.getStringExtra("FILE_URL");
                final CloudService cloudService = (CloudService) intent.getSerializableExtra("CLOUD_SERVICE");
                handleActionDownload(file_url, cloudService);
            }
        }
    }

    private void handleActionDownload(final String fileUrl, final CloudService cloudService) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadDropboxFile(fileUrl, cloudService);
            }
        }).start();

    }


    private void downloadDropboxFile(String fileUrl, CloudService cloudService)
    {

        final int notificationId = mRand.nextInt();

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, cloudService.getToken());
        DropboxAPI<AndroidAuthSession> dbApi = new DropboxAPI<AndroidAuthSession>(session);


        if (dbApi.getSession().authenticationSuccessful()) {
            String token = dbApi.getSession().finishAuthentication();
            cloudService.setToken(token);
            PreferenceSetter.saveCloudService(DownloadFileService.this, cloudService);
        }

        if (dbApi.getSession().isLinked()) {

            File dropboxDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Dropbox");
            if (!dropboxDir.exists())
                dropboxDir.mkdir();
            File output = new File(dropboxDir.getAbsolutePath()+fileUrl);
            File renamedOutput = new File(output.getAbsolutePath()+".mcvdownload");

            String filePath = output.getAbsolutePath();
            final String title = output.getName();


            try {
                if (!output.exists() && !renamedOutput.exists()) {

                    DropboxAPI.Entry entry = dbApi.metadata(fileUrl, 1000, null, true, null);

                    if (!entry.isDir) {
                        FileOutputStream outputStream = new FileOutputStream(renamedOutput);

                        createStartNotification(filePath, title, notificationId);

                        DropboxAPI.DropboxFileInfo info = dbApi.getFile(fileUrl, null, outputStream, new ProgressListener() {
                            @Override
                            public void onProgress(long l, long l1) {
                                setNotificationProgress(l, l1, title, notificationId);
                            }
                        });
                        Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);

                        renamedOutput.renameTo(output);

                        ArrayList<String> filepaths = PreferenceSetter.getFilePathsFromPreferences(DownloadFileService.this);
                        ArrayList<String> excludedpaths = PreferenceSetter.getExcludedPaths(DownloadFileService.this);


                        if (!filepaths.contains(dropboxDir.getAbsolutePath())) {
                            filepaths.add(dropboxDir.getAbsolutePath());
                            PreferenceSetter.saveFilePaths(DownloadFileService.this, filepaths);
                        }

                        if (excludedpaths.contains(dropboxDir.getAbsolutePath())) {
                            excludedpaths.remove(dropboxDir.getAbsolutePath());
                            PreferenceSetter.saveExcludedFilePaths(DownloadFileService.this, excludedpaths);
                        }

                        setEndNotification(title, filePath, notificationId);
                    }
                    else
                    {
                        if (!output.exists())
                        {
                            output.mkdir();
                            for (int i=0;i<entry.contents.size();i++)
                            {
                                if (Utilities.checkExtension(entry.contents.get(i).fileName()) || entry.contents.get(i).isDir)
                                    handleActionDownload(entry.contents.get(i).path,cloudService);
                            }
                        }
                    }

                }
                else {

                    ArrayList<String> filepaths = PreferenceSetter.getFilePathsFromPreferences(DownloadFileService.this);
                    ArrayList<String> excludedpaths = PreferenceSetter.getExcludedPaths(DownloadFileService.this);

                    if (!filepaths.contains(dropboxDir.getAbsolutePath())) {
                        filepaths.add(dropboxDir.getAbsolutePath());
                        PreferenceSetter.saveFilePaths(DownloadFileService.this, filepaths);
                    }

                    if (excludedpaths.contains(dropboxDir.getAbsolutePath())) {
                        excludedpaths.remove(dropboxDir.getAbsolutePath());
                        PreferenceSetter.saveExcludedFilePaths(DownloadFileService.this, excludedpaths);
                    }

                    DropboxAPI.Entry entry;

                    try
                    {
                        entry = dbApi.metadata(fileUrl, 1000, null, true, null);
                        if (!entry.isDir)
                            createFileExistsNotification(title, notificationId);
                        else
                        {
                            for (int i=0;i<entry.contents.size();i++)
                            {
                                if (Utilities.checkExtension(entry.contents.get(i).fileName()) || entry.contents.get(i).isDir)
                                    handleActionDownload(entry.contents.get(i).path,cloudService);
                            }
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                setErrorNotification(title, notificationId);
            }
        }

    }

    private void createFileExistsNotification(String title, int id)
    {
        mNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_recents)
                .setContentTitle("Material Comic Viewer")
                .setContentText(title + " already exists");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mNotification.build());
    }

    private void setEndNotification(String title, String filePath, int id) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, DisplayComicActivity.class);

        Uri uri = new Uri.Builder().path(filePath).build();

        resultIntent.setData(uri);
        resultIntent.setAction(Intent.ACTION_VIEW);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DrawerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mNotification.setContentIntent(resultPendingIntent);
        mNotification.setContentText("Finished downloading " + title);
        mNotification.setProgress(0,0,false);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mNotification.build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notification);
    }

    private void setNotificationProgress(long completed, long total, String title, int id)
    {
        mNotification.setProgress((int) total, (int) completed, false)
        .setContentText("Downloading " + title);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, mNotification.build());
    }

    private void createStartNotification(String filePath, String title, int id)
    {
        mNotification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_recents)
                        .setColor(PreferenceSetter.getAppThemeColor(DownloadFileService.this))
                        .setGroup(NOTIFICATION_KEY)
                .setContentTitle("Material Comic Viewer")
                        .setContentText("The file " + title + " has started downloading");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mNotification.build());


    }

    private void setErrorNotification(String title, int id)
    {
        mNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_recents)
                .setContentTitle("Material Comic Viewer")
                .setColor(PreferenceSetter.getAppThemeColor(DownloadFileService.this))
                .setContentText("An error occured downloading " + title);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mNotification.build());


    }

}
