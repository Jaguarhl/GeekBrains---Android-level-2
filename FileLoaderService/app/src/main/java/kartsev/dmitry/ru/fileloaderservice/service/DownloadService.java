package kartsev.dmitry.ru.fileloaderservice.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends IntentService {

    /**
     * Field for store a name of parameter
     */
    public static final String PARAM_NAME = "PARAM_NAME";
    public static final String LOG_TAG = "SRVC";
    public static final String FILE_LOADED = "File loaded";
    public static final String SERVICE_NAME = "Loading File from Web by Service";
    public static final String RECEIVER = "receiver";
    public static final String PROGRESS = "progress";
    private final String FILE_NAME = "File to Download";
    public static final int UPDATE_PROGRESS = 8344;

    /* Private field for store a string */
    private String mString = null;

    public DownloadService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    /**
     * Called by the system every time a client explicitly starts the service
     * by calling {@link android.content.Context#startService}, providing the
     * arguments it supplied and a unique integer token representing the start
     * request.  Do not call this method directly.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		/* Invoke a parent method and return value */
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /* Get data from intent */
        if (intent != null) {
            String urlToDownload = intent.getStringExtra(PARAM_NAME);
            Log.d(LOG_TAG, "Starting to download file " + urlToDownload);

            loadFileTask(urlToDownload, intent);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    void loadFileTask(final String urlToDownload, final Intent intent) {
        new Thread(new Runnable() {
            public void run() {
                if (urlToDownload.length() > 0) {
                    ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(RECEIVER);

                    try {
                        URL url = new URL(urlToDownload);
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        int fileLength = connection.getContentLength();
                        if(fileLength == -1) {
                            fileLength = 1024;
                        }

                        InputStream input = new BufferedInputStream(connection.getInputStream());
                        OutputStream output = new FileOutputStream(new
                                File(getFilesDir() + File.separator + FILE_NAME));

                        byte data[] = new byte[1024];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            total += count;

                            Bundle resultData = new Bundle();
                            resultData.putInt(PROGRESS, (int) total * 100 / fileLength);
                            receiver.send(UPDATE_PROGRESS, resultData);
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    stopSelf();
                    Log.d(LOG_TAG, "Task completed");
                }
            }
        }).start();
    }
}
