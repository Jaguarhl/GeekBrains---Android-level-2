package ru.dmitry.kartsev.mapgoogleapiapp.helpers;

/**
 * Created by Jag on 25.01.2017.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "gmapmarkers.db";
    public static final String TB_NAME = "markers";
    public static final String TB_ACTIVE_COL_NAME = "active";
    public static final String TB_TITLE_COL_NAME = "name";
    public static final String TB_DESC_COL_NAME = "description";
    public static final String TB_LAT_COL_NAME = "latitude";
    public static final String TB_LNG_COL_NAME = "longitude";
    public static final String TB_ID_COL_NAME = "_id";
    public static final String QUERY_ID = "_id=?";
    final String DROP_TABLE = "DROP TABLE IF EXISTS " + TB_NAME;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("CREATE TABLE " + TB_NAME + " (" + TB_ID_COL_NAME +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " + TB_TITLE_COL_NAME + " STRING, " +
                    TB_DESC_COL_NAME + " STRING, " + TB_LAT_COL_NAME + " DOUBLE, " +
                    TB_LNG_COL_NAME + " DOUBLE, " + TB_ACTIVE_COL_NAME + " BOOLEAN);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
