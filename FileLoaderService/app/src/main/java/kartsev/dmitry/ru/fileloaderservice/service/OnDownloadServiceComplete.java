package kartsev.dmitry.ru.fileloaderservice.service;

import android.os.Bundle;

/**
 * Created by Jag on 14.01.2017.
 */

public interface OnDownloadServiceComplete {
    void OnDownloadServiceComplete(int resultCode, Bundle resultData);
}
