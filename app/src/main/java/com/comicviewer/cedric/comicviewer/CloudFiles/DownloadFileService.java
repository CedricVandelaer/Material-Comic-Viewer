package com.comicviewer.cedric.comicviewer.CloudFiles;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;

import com.comicviewer.cedric.comicviewer.Model.CloudService;


public class DownloadFileService extends IntentService {

    final static private String APP_KEY = "id9ssazcpa41gys";
    final static private String APP_SECRET = "yj0gk3nipr6ti4u";

    private static final String ACTION_DROPBOX_DOWNLOAD = "com.comicviewer.cedric.comicviewer.CloudFiles.action.DROPBOXDOWNLOAD";

    public static void startActionFoo(Context context, String fileUrl, CloudService cloudService) {
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

    private void handleActionDownload(String fileUrl, CloudService cloudService) {



    }

    private class DownloadDropboxFileTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {



            return null;
        }
    }

}
