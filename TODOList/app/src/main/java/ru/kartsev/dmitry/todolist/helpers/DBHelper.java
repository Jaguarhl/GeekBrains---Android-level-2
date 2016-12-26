package ru.kartsev.dmitry.todolist.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.kartsev.dmitry.todolist.MainActivity;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "tasklist";
    public static final String TB_NAME = "tasks";
    public static final String TB_ACTIVE_COL_NAME = "active";
    public static final String TB_TITLE_COL_NAME = "title";
    public static final String TB_DESC_COL_NAME = "description";
    public static final String TB_ID_COL_NAME = "_id";
    public static final String QUERY_ID = "_id=?";
    public static final String TB_USERNAME_COL_NAME = "uname";
    public static final String TB_USERS_NAME = "users";
    public static final String TB_INCHARGE_COL_NAME = "in_charge";
    final String DROP_TABLE = "DROP TABLE IF EXISTS " + TB_NAME;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("CREATE TABLE " + TB_USERS_NAME + " (" + TB_ID_COL_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TB_USERNAME_COL_NAME + " STRING, " + TB_ACTIVE_COL_NAME + " BOOLEAN);");
            sqLiteDatabase.execSQL("CREATE TABLE " + TB_NAME +" (" + TB_ID_COL_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TB_TITLE_COL_NAME + " STRING, " + TB_DESC_COL_NAME + " STRING, " + TB_ACTIVE_COL_NAME + " BOOLEAN, " + TB_INCHARGE_COL_NAME + " INTEGER REFERENCES users (_id));");
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