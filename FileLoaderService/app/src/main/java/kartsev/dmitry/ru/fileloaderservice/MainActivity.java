package kartsev.dmitry.ru.fileloaderservice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import kartsev.dmitry.ru.fileloaderservice.service.DownloadReceiver;
import kartsev.dmitry.ru.fileloaderservice.service.OnDownloadServiceComplete;
import kartsev.dmitry.ru.fileloaderservice.service.DownloadService;

/**
 * Any activity in this application.
 * TODO: put description of this activity.
 * */
public class MainActivity extends AppCompatActivity implements OnClickListener, OnDownloadServiceComplete {

    public static final String RECEIVER = "receiver";
    public static final String LOG_TAG = "DBG";
    public static final String PROGRESS = "progress";

    private final int DIALOG_EXIT = 1;

    private Button btnStart = null;
    private Button btnStop = null;
    private TextView tvState = null;
    private ProgressBar progressBar = null;

    private void initUI() {
        btnStart = (Button) this.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        tvState = (TextView) this.findViewById(R.id.tvState);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        this.initUI();
    }

    @Override
    public void onClick(View v) {
        if (btnStart.equals(v)) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            tvState.setText(R.string.loading);
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DownloadService.PARAM_NAME,
                    getResources().getString(R.string.file_url));
            DownloadReceiver downloadReceiver = new DownloadReceiver(new Handler(),
                    MainActivity.this);
            intent.putExtra(RECEIVER, downloadReceiver);
            this.startService(intent);

            return;
        }

        if (btnStop.equals(v)) {
            Intent intent = new Intent(this, DownloadService.class);
            this.stopService(intent);

            return;

        }

    }

    @Override
    public void OnDownloadServiceComplete(int resultCode, Bundle resultData) {
        if (resultCode == DownloadService.UPDATE_PROGRESS){
            Log.d(LOG_TAG, "Progress " + resultData.getInt(PROGRESS));
            int progress = resultData.getInt(PROGRESS);
            progressBar.setProgress(progress);
            if (progress == 100){
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                tvState.setText("");
                createDialog(DIALOG_EXIT);
            }
        }
    }

    private void createDialog(int id) {
        AlertDialog alertDialog = null;
        if (id == DIALOG_EXIT) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.download_over);
            adb.setNeutralButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            adb.setCancelable(false);
            alertDialog = adb.create();
            alertDialog.show();
        }
    }
}