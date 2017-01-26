package ru.dmitry.kartsev.mapgoogleapiapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.dmitry.kartsev.mapgoogleapiapp.activity.MapActivity;
import ru.dmitry.kartsev.mapgoogleapiapp.data.MarkerItem;

/**
 * Created by Jag on 26.01.2017.
 */

public class DBWork {
    public static final String LOG_TAG = "DBWork";
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private List<MarkerItem> markersList;
    private Context mContext;

    public DBWork(Context mContext) {
        this.mContext = mContext;
        markersList = new ArrayList<>();
    }

    public List initDatabase() {
        dbHelper = new DBHelper(mContext);
        database = dbHelper.getWritableDatabase();

        // filling list data
        if(dbHelper != null & database != null) {
            try {
                Cursor cursor = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    // определеяем номера столцов по имени в выборке
                    int idIndex = cursor.getColumnIndex(DBHelper.TB_ID_COL_NAME);
                    int titleColIndex = cursor.getColumnIndex(DBHelper.TB_TITLE_COL_NAME);
                    int descriptionColIndex = cursor.getColumnIndex(DBHelper.TB_DESC_COL_NAME);
                    int markerActive = cursor.getColumnIndex(DBHelper.TB_ACTIVE_COL_NAME);
                    int markerLat = cursor.getColumnIndex(DBHelper.TB_LAT_COL_NAME);
                    int markerLng = cursor.getColumnIndex(DBHelper.TB_LNG_COL_NAME);

                    do {
                        markersList.add(new MarkerItem(cursor.getInt(idIndex), cursor.getString(titleColIndex),
                                cursor.getString(descriptionColIndex),
                                cursor.getDouble(markerLat), cursor.getDouble(markerLng),
                                cursor.getInt(markerActive) != 0));
                    } while (cursor.moveToNext());
                }

                Log.d(LOG_TAG, "count of rows = " + cursor.getCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "DBHelper or database is null");
        }
        dbHelper.close();

        return markersList;
    }

    public MarkerItem addMarker(MarkerItem item) {
        dbHelper = new DBHelper(mContext);
        database = dbHelper.getWritableDatabase();
        Cursor c = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);
        ContentValues cv = new ContentValues();
        try {
            cv.put(DBHelper.TB_TITLE_COL_NAME, item.getMarkerName());
            cv.put(DBHelper.TB_DESC_COL_NAME, item.getMarkerDescription());
            cv.put(DBHelper.TB_LAT_COL_NAME, item.getMarkerLatitude());
            cv.put(DBHelper.TB_LNG_COL_NAME, item.getMarkerLongitude());
            c.moveToLast();
            item.setMarkerIdInDB((int)database.insert(DBHelper.TB_NAME, null, cv));
            Log.d(LOG_TAG, "Added to database: " + item.getMarkerName() + " " + item.getMarkerDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbHelper.close();

        return item;
    }

    public void deleteMarker(int id) {
        dbHelper = new DBHelper(mContext);
        database = dbHelper.getWritableDatabase();
        /*ContentValues cv = new ContentValues();
        Cursor c = database.query(DBHelper.TB_NAME, new String[] {(DBHelper.TB_ID_COL_NAME)},
        String.valueOf(Integer.toString(id)),
                null, null, null, null);  */
        database.delete(DBHelper.TB_NAME, DBHelper.QUERY_ID,
                new String[]{(Integer.toString(id) )});
        dbHelper.close();
    }
}
