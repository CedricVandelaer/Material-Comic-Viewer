package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxSession;
import com.comicviewer.cedric.comicviewer.Model.CloudService;
import com.comicviewer.cedric.comicviewer.Model.GoogleDriveObject;
import com.comicviewer.cedric.comicviewer.Model.ObjectType;
import com.comicviewer.cedric.comicviewer.Model.OneDriveObject;
import com.comicviewer.cedric.comicviewer.NewDrawerActivity;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SplashActivity;
import com.comicviewer.cedric.comicviewer.Utilities;
import com.comicviewer.cedric.comicviewer.ViewPagerFiles.DisplayComicActivity;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class DownloadFileService extends IntentService implements LiveAuthListener{

    final static private String NOTIFICATION_KEY="ComicViewerNotifGroup";

    NotificationCompat.Builder mNotification;
    private static Random mRand = new Random();

    private static final String ACTION_DROPBOX_DOWNLOAD = "com.comicviewer.cedric.comicviewer.CloudFiles.action.DROPBOXDOWNLOAD";
    private static final String ACTION_ONEDRIVE_DOWNLOAD = "com.comicviewer.cedric.comicviewer.CloudFiles.action.ONEDRIVEDOWNLOAD";
    private static final String ACTION_GOOGLEDRIVE_DOWNLOAD = "com.comicviewer.cedric.comicviewer.CloudFiles.action.GOOGLEDRIVEDOWNLOAD";
    private static final String ACTION_BOX_DOWNLOAD = "com.comicviewer.cedric.comicviewer.CloudFiles.action.BOXDOWNLOAD";

    private BoxApiFile mBoxFileApi;
    private BoxSession mSession;

    private LiveConnectClient mLiveConnectClient = null;


    public static void startActionDownload(Context context, String fileUrl, CloudService cloudService) {

        mRand.setSeed(System.currentTimeMillis());
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.setAction(ACTION_DROPBOX_DOWNLOAD);
        intent.putExtra("FILE_URL", fileUrl);
        intent.putExtra("CLOUD_SERVICE", cloudService);
        context.startService(intent);
    }

    public static void startActionDownload(Context context, BoxItem boxItem, CloudService cloudService) {

        mRand.setSeed(System.currentTimeMillis());
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.setAction(ACTION_BOX_DOWNLOAD);
        intent.putExtra("BOX_ITEM", boxItem);
        intent.putExtra("CLOUD_SERVICE", cloudService);
        context.startService(intent);
    }

    public static void startActionDownload(Context context, OneDriveObject oneDriveObject, CloudService cloudService) {

        mRand.setSeed(System.currentTimeMillis());
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.setAction(ACTION_ONEDRIVE_DOWNLOAD);
        intent.putExtra("ONEDRIVE_OBJECT", oneDriveObject);
        intent.putExtra("CLOUD_SERVICE", cloudService);
        context.startService(intent);
    }

    public static void startActionDownload(Context context, GoogleDriveObject googleDriveObject, CloudService cloudService) {

        mRand.setSeed(System.currentTimeMillis());
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.setAction(ACTION_GOOGLEDRIVE_DOWNLOAD);
        intent.putExtra("GOOGLEDRIVE_OBJECT", googleDriveObject);
        intent.putExtra("CLOUD_SERVICE", cloudService);
        context.startService(intent);
    }

    public DownloadFileService() {
        super("DownloadFileService");
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
            else if (ACTION_ONEDRIVE_DOWNLOAD.equals(action))
            {
                final OneDriveObject oneDriveObject = (OneDriveObject) intent.getSerializableExtra("ONEDRIVE_OBJECT");
                final CloudService cloudService = (CloudService) intent.getSerializableExtra("CLOUD_SERVICE");

                handleActionDownload(oneDriveObject, cloudService);
            }
            else if (ACTION_GOOGLEDRIVE_DOWNLOAD.equals(action))
            {
                final CloudService cloudService = (CloudService) intent.getSerializableExtra("CLOUD_SERVICE");
                final GoogleDriveObject googleDriveObject = (GoogleDriveObject) intent.getSerializableExtra("GOOGLEDRIVE_OBJECT");
                handleActionDownload(googleDriveObject.getName(), googleDriveObject.getDownloadUrl(), cloudService);
            }
            else if (ACTION_BOX_DOWNLOAD.equals(action))
            {
                final CloudService cloudService = (CloudService) intent.getSerializableExtra("CLOUD_SERVICE");
                final BoxItem boxItem = (BoxItem) intent.getSerializableExtra("BOX_ITEM");
                handleActionDownload(boxItem, cloudService);
            }
        }
    }

    private void handleActionDownload(final BoxItem boxItem, final CloudService cloudService)
    {
        BoxConfig.CLIENT_ID = getString(R.string.box_client_id);
        BoxConfig.CLIENT_SECRET = getString(R.string.box_client_secret);
        BoxConfig.REDIRECT_URL = getString(R.string.box_redirect_url);
        BoxConfig.IS_LOG_ENABLED = true;

        mSession = new BoxSession(this, cloudService.getEmail());
        mSession.authenticate();
        boxFileDownload(boxItem,cloudService);
    }

    private void boxFileDownload(final BoxItem boxItem, final CloudService cloudService)
    {
        mBoxFileApi = new BoxApiFile(mSession);

        final int notificationId = mRand.nextInt();

        createStartNotification(boxItem.getName(), notificationId);

        File boxDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ComicViewer" + "/Box");
        if (!boxDir.exists())
            boxDir.mkdir();
        File output = new File(boxDir.getAbsolutePath() + "/" + boxItem.getName());
        File renamedOutput = new File(output.getAbsolutePath() + ".mcvdownload");

        if (!output.exists())
        {
            if (renamedOutput.exists())
                renamedOutput.delete();
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(renamedOutput);
            } catch (Exception e) {
                System.out.println("Outputstream exception");
                e.printStackTrace(System.out);
                setErrorNotification(boxItem.getName(), notificationId);
            }

            if (outputStream!=null) {
                try {
                    BoxDownload fileDownload = mBoxFileApi.getDownloadRequest(outputStream, boxItem.getId())
                            // Optional: Set a listener to track download progress.
                            .setProgressListener(new com.box.androidsdk.content.listeners.ProgressListener() {

                                long progress = 0;

                                @Override
                                public void onProgressChanged(long l, long l1) {
                                    if (l > progress + 1000000) {
                                        progress += l;
                                        setNotificationProgress(l, l1, boxItem.getName(), notificationId);
                                    }
                                }
                            })
                            .send();
                }
                catch (Exception e)
                {
                    System.out.println("fileDownload exception");
                    e.printStackTrace(System.out);
                }

                renamedOutput.renameTo(output);

                try {
                    outputStream.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                setEndNotification(boxItem.getName(), output.getAbsolutePath(), notificationId);
            }

        }
        else
        {
            createFileExistsNotification(boxItem.getName(),notificationId);
        }

    }

    private void handleActionDownload(final String fileName, final String url, final CloudService cloudService)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                googleDriveDownload(fileName, url, cloudService);
            }
        }).run();
    }

    private void googleDriveDownload(String fileName, String urlString, CloudService cloudService) {
        int count;

        long totalPercentage = 0;

        final int notificationId = mRand.nextInt();

        createStartNotification(fileName, notificationId);

        try {
            URL url = new URL(urlString+"&access_token="+cloudService.getToken());
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a typical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            File googleDriveDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ComicViewer"+"/Google Drive");
            if (!googleDriveDir.exists())
                googleDriveDir.mkdir();
            File output = new File(googleDriveDir.getAbsolutePath()+"/"+fileName);
            File renamedOutput = new File(output.getAbsolutePath()+".mcvdownload");

            // Output stream
            OutputStream outputStream = new FileOutputStream(renamedOutput);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                if (total>totalPercentage+1000000) {
                    totalPercentage = total;
                    setNotificationProgress(total, lenghtOfFile, fileName, notificationId);
                }


                // writing data to file
                outputStream.write(data, 0, count);
            }

            // flushing output
            outputStream.flush();

            // closing streams
            outputStream.close();
            input.close();

            //rename file
            renamedOutput.renameTo(output);

            setEndNotification(fileName, output.getAbsolutePath(), notificationId);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotification(fileName, notificationId);
        }

    }

    private void handleActionDownload(final String fileUrl, final CloudService cloudService) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cloudService.getName().equals(getString(R.string.cloud_storage_1)))
                    downloadDropboxFile(fileUrl, cloudService);
            }
        }).start();
    }

    private void handleActionDownload(final OneDriveObject oneDriveObject, final CloudService cloudService) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cloudService.getName().equals(getString(R.string.cloud_storage_3)))
                    downloadOneDriveFile(oneDriveObject, cloudService, "");
            }
        }).start();
    }

    private void downloadOneDriveFile(OneDriveObject oneDriveObject, CloudService cloudService, String parentPath)
    {
        final int notificationId = mRand.nextInt();

        if (mLiveConnectClient==null) {
            LiveAuthClient oneDriveAuth = new LiveAuthClient(this, getString(R.string.onedrive_id));
            Object userState = new Object();
            Iterable<String> scopes = Arrays.asList("wl.signin", "wl.offline_access", "wl.basic", "wl.skydrive", "wl.emails");

            oneDriveAuth.initialize(scopes, this, userState, cloudService.getToken());

            int i=0;
            while (mLiveConnectClient==null && i<20) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
        }


        if (mLiveConnectClient!=null) {

            final File oneDriveDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ComicViewer"+"/OneDrive");
            if (!oneDriveDir.exists())
                oneDriveDir.mkdir();
            final File output = new File(oneDriveDir.getAbsolutePath()+"/"+parentPath+oneDriveObject.getName());
            final File renamedOutput = new File(output.getAbsolutePath()+".mcvdownload");

            final String filePath = output.getAbsolutePath();
            final String title = output.getName();


            try {
                if (!output.exists() && !renamedOutput.exists()) {

                    if (! (oneDriveObject.getType() == ObjectType.FOLDER)) {

                        createStartNotification(title, notificationId);

                        mLiveConnectClient.downloadAsync(oneDriveObject.getId()+"/content", renamedOutput, new LiveDownloadOperationListener() {

                            private int progress = 0;

                            @Override
                            public void onDownloadCompleted(LiveDownloadOperation operation) {

                                renamedOutput.renameTo(output);

                                setEndNotification(title, filePath, notificationId);
                            }

                            @Override
                            public void onDownloadFailed(LiveOperationException exception, LiveDownloadOperation operation) {
                                setErrorNotification(title, notificationId);
                                exception.printStackTrace();
                            }

                            @Override
                            public void onDownloadProgress(int totalBytes, int bytesRemaining, LiveDownloadOperation operation) {

                                if ((totalBytes-bytesRemaining)>progress+1000000) {
                                    setNotificationProgress(totalBytes - bytesRemaining, totalBytes, title, notificationId);
                                    progress+=1000000;
                                }
                            }
                        });


                    }
                    else
                    {
                        if (!output.exists())
                        {
                            output.mkdir();

                            downloadOneDriveFolder(oneDriveObject, cloudService, parentPath);
                        }
                    }

                }
                else {

                    try
                    {
                        if (!(oneDriveObject.getType()==ObjectType.FOLDER))
                            createFileExistsNotification(title, notificationId);
                        else
                        {
                            downloadOneDriveFolder(oneDriveObject, cloudService, parentPath);
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

    private void downloadOneDriveFolder(final OneDriveObject oneDriveObject, final CloudService cloudService, final String parentPath)
    {
        if (mLiveConnectClient!=null) {
            mLiveConnectClient.getAsync(oneDriveObject.getId()+"/files", new LiveOperationListener() {

                public void onComplete(LiveOperation operation) {
                    JSONObject result = operation.getResult();
                    Log.d("Result", result.toString());
                    try {
                        JSONArray data = result.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject folder = data.getJSONObject(i);
                            OneDriveObject newFile = new OneDriveObject(folder.getString("name"), folder.getString("id"));

                            if (newFile.getType() == ObjectType.FOLDER || Utilities.checkExtension(newFile.getName())) {
                                downloadOneDriveFile(newFile, cloudService, parentPath + oneDriveObject.getName()+"/");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onError(LiveOperationException exception, LiveOperation operation) {
                    exception.printStackTrace();
                }
            });
        }
    }


    private void downloadDropboxFile(String fileUrl, CloudService cloudService)
    {

        final int notificationId = mRand.nextInt();

        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dropbox_app_key), getResources().getString(R.string.dropbox_app_secret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys, cloudService.getToken());
        DropboxAPI<AndroidAuthSession> dbApi = new DropboxAPI<AndroidAuthSession>(session);


        if (dbApi.getSession().authenticationSuccessful()) {
            String token = dbApi.getSession().finishAuthentication();
            cloudService.setToken(token);
            StorageManager.saveCloudService(DownloadFileService.this, cloudService);
        }

        if (dbApi.getSession().isLinked()) {

            File dropboxDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ComicViewer"+"/Dropbox");
            if (!dropboxDir.exists())
                dropboxDir.mkdir();
            File output = new File(dropboxDir.getAbsolutePath()+fileUrl);
            File renamedOutput = new File(output.getAbsolutePath()+".mcvdownload");

            createParentDirs(output);
            String filePath = output.getAbsolutePath();
            final String title = output.getName();


            try {
                if (!output.exists() && !renamedOutput.exists()) {

                    DropboxAPI.Entry entry = dbApi.metadata(fileUrl, 1000, null, true, null);

                    if (!entry.isDir) {
                        FileOutputStream outputStream = new FileOutputStream(renamedOutput);

                        createStartNotification(title, notificationId);

                        DropboxAPI.DropboxFileInfo info = dbApi.getFile(fileUrl, null, outputStream, new ProgressListener() {
                            @Override
                            public void onProgress(long l, long l1) {
                                setNotificationProgress(l, l1, title, notificationId);
                            }
                        });
                        Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);

                        renamedOutput.renameTo(output);

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

    private void createParentDirs(File file)
    {

        ArrayList<File> parentFolders = new ArrayList<>();
        File leafFile = file.getParentFile();

        while (leafFile.getParentFile()!=null) {
            parentFolders.add(leafFile);
            leafFile = leafFile.getParentFile();
        }

        for (int i=parentFolders.size()-1;i>=0;i--)
        {
            try
            {
                if (!parentFolders.get(i).exists())
                    parentFolders.get(i).mkdir();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void createFileExistsNotification(String title, int id)
    {
        mNotification = new NotificationCompat.Builder(this)
                .setColor(StorageManager.getAppThemeColor(this))
                .setSmallIcon(R.drawable.ic_recents)
                .setContentTitle("Material Comic Viewer")
                .setContentText(getString(R.string.error)+": "+title + " "+getString(R.string.already_exists));

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
        if (StorageManager.getBooleanSetting(this, StorageManager.USES_RECENTS, true))
        {
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NewDrawerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );

        mNotification.setContentIntent(resultPendingIntent);
        mNotification.setContentText(getString(R.string.finished_downloading) + ": " + title);
        mNotification.setProgress(0, 0, false);
        mNotification.setSmallIcon(R.drawable.ic_check);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mNotification.build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notification);
    }

    private void setNotificationProgress(long completed, long total, String title, int id)
    {
        mNotification.setProgress((int) total, (int) completed, false)
        .setContentText(getString(R.string.downloading)+": " + title);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, mNotification.build());
    }

    private void createStartNotification(String title, int id)
    {
        mNotification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_recents)
                        .setColor(StorageManager.getAppThemeColor(DownloadFileService.this))
                        .setGroup(NOTIFICATION_KEY)
                .setContentTitle("Material Comic Viewer")
                        .setContentText(getString(R.string.downloading)+": " + title);

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
                .setColor(StorageManager.getAppThemeColor(DownloadFileService.this))
                .setContentText(getString(R.string.error_while_downloading)+": " + title);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mNotification.build());


    }

    @Override
    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
        if(status == LiveStatus.CONNECTED)
        {
            mLiveConnectClient = new LiveConnectClient(session);
        }
        else
        {
            mLiveConnectClient = null;
        }
    }

    @Override
    public void onAuthError(LiveAuthException exception, Object userState) {

    }

}
