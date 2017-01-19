package kartsev.dmitry.ru.simplewidgetapp;

import java.text.DecimalFormat;
import java.util.Arrays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import kartsev.dmitry.ru.simplewidgetapp.location.GPSTracker;

public class MyWidget extends AppWidgetProvider {

    final String LOG_TAG = "WdgtLOG";
    private Context mContext;
    private AppWidgetManager widgetManager = null;
    private int[] widgetIds = null;
    GPSTracker gps = null;
    private static final String UPDATE_ALL_WIDGETS = "update_all_widgets";
    /*StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();*/

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        mContext = context;
        try {
            /*
            GPSTracker gps = new GPSTracker(context);
             */
            Intent intent = new Intent(context, MyWidget.class);
            intent.setAction(UPDATE_ALL_WIDGETS);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10000, pIntent);
            Log.d(LOG_TAG, "onEnabled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        widgetManager = appWidgetManager;
        widgetIds = appWidgetIds.clone();

        for (int id : appWidgetIds){
            updateWidget(context, appWidgetManager, id);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget);
        if(gps == null) {
            gps = new GPSTracker(context);
        }
        if(gps.canGetLocation()) {
            try {
                v.setTextViewText(R.id.status, context.getResources().getString(R.string.gps_on));
                DecimalFormat decimalFormat = new DecimalFormat("###.###");
                v.setTextViewText(R.id.longitute, context.getResources().getString(R.string.latitude) + " " + decimalFormat.format(gps.getLatitude()));
                v.setTextViewText(R.id.latitude, context.getResources().getString(R.string.longitude) + " " + decimalFormat.format(gps.getLongitude()));
                v.setTextViewText(R.id.place, gps.getLocationAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            v.setTextViewText(R.id.status, context.getResources().getString(R.string.gps_off));
            v.setTextViewText(R.id.longitute, context.getResources().getString(R.string.none));
            v.setTextViewText(R.id.latitude, context.getResources().getString(R.string.none));
        }

        appWidgetManager.updateAppWidget(widgetId, v);
        Log.d(LOG_TAG, "onUpdate " + widgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        gps.stopUsingGPS();
        Log.d(LOG_TAG, "onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(
                    context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateWidget(context, appWidgetManager, appWidgetID);
            }
            Log.d(LOG_TAG, "Received");
        }
    }

}