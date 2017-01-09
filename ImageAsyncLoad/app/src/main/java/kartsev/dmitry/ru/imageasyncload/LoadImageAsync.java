package kartsev.dmitry.ru.imageasyncload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

class LoadImageAsync extends AsyncTask<String, Integer, Bitmap> {
    OnAsyncTaskComplete callback;
    Context context;

    public LoadImageAsync(OnAsyncTaskComplete callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = "";
        if(strings.length > 0 ){
            url = strings[0];
        }

        InputStream input = null;

        try {
            URL urlConn = new URL(url);
            input = urlConn.openStream();
            Log.d("TSK", "Started to load image");
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(input);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        callback.onAsyncTaskComplete(bitmap);

    }
}