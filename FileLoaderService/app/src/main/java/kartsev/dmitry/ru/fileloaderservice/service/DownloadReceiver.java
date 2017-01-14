package kartsev.dmitry.ru.fileloaderservice.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Jag on 14.01.2017.
 */

public class DownloadReceiver extends ResultReceiver {
    OnDownloadServiceComplete callback;

    public DownloadReceiver(Handler handler, OnDownloadServiceComplete callback) {
        super(handler);
        this.callback = callback;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (callback != null) {
            callback.OnDownloadServiceComplete(resultCode, resultData);
        }
    }
}
