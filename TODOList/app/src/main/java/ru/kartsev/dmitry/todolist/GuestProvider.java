package ru.kartsev.dmitry.todolist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import ru.kartsev.dmitry.todolist.MainActivity;
import ru.kartsev.dmitry.todolist.helpers.DBHelper;

public class GuestProvider extends ContentProvider {

    // // Uri
    // authority
    static final String AUTHORITY = "ru.kartsev.dmitry.todolist";

    // path
    static final String TASKS_PATH = "tasks";

    // Поля
    static final String TASK_ID = "_id";
    static final String TASK_TITLE = "title";
    static final String TASK_DESCRIPTION = "description";

    // Общий Uri
    public static final Uri TASK_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TASKS_PATH);

    // Типы данных
    // набор строк
    static final String TASK_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + TASKS_PATH;

    // одна строка
    static final String TASK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + TASKS_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_TASKS = 1;

    // Uri с указанным ID
    static final int URI_TASK_ID = 2;


    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    public static final String LOG_TAG = "CP_LOG";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TASKS_PATH, URI_TASKS);
        uriMatcher.addURI(AUTHORITY, TASKS_PATH + "/#", URI_TASK_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return false;
    }

    // чтение
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_TASKS: // общий Uri
                Log.d(LOG_TAG, "URI_TASKS");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = TASK_TITLE + " ASC";
                }
                break;
            case URI_TASK_ID: // Uri с ID
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_TASK_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = TASK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + TASK_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TB_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в TASK_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(),
                TASK_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_TASKS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(DBHelper.TB_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(TASK_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_TASKS:
                Log.d(LOG_TAG, "URI_TASKS");
                break;
            case URI_TASK_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_TASK_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = TASK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + TASK_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(DBHelper.TB_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_TASKS:
                Log.d(LOG_TAG, "URI_TASKS");

                break;
            case URI_TASK_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_TASK_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = TASK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + TASK_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(DBHelper.TB_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_TASKS:
                return TASK_CONTENT_TYPE;
            case URI_TASK_ID:
                return TASK_CONTENT_ITEM_TYPE;
        }
        return null;
    }
}
