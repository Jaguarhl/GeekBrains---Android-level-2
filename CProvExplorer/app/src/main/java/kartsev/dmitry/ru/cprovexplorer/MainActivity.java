package kartsev.dmitry.ru.cprovexplorer;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends Activity {

    final String LOG_TAG = "myLogs";

    final Uri TASK_URI = Uri
            .parse("content://ru.kartsev.dmitry.todolist/tasks");

    final String TASK_TITLE = "title";
    final String TASK_DESCRIPTION = "description";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cursor cursor = getContentResolver().query(TASK_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { "title", "description" };
        int to[] = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, from, to);

        ListView lvContact = (ListView) findViewById(R.id.lvContact);
        lvContact.setAdapter(adapter);
    }

    public void onClickInsert(View v) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, "Task 5");
        cv.put(TASK_DESCRIPTION, "Description 5");
        Uri newUri = getContentResolver().insert(TASK_URI, cv);
        Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
    }

    public void onClickUpdate(View v) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, "name 105");
        cv.put(TASK_DESCRIPTION, "email 105");
        Uri uri = ContentUris.withAppendedId(TASK_URI, 1);
        int cnt = getContentResolver().update(uri, cv, null, null);
        Log.d(LOG_TAG, "update, count = " + cnt);
    }

    public void onClickDelete(View v) {
        Uri uri = ContentUris.withAppendedId(TASK_URI, 4);
        int cnt = getContentResolver().delete(uri, null, null);
        Log.d(LOG_TAG, "delete, count = " + cnt);
    }

    public void onClickError(View v) {
        Uri uri = Uri.parse("content://ru.kartsev.dmitry.todolist/users");
        try {
            getContentResolver().query(uri, null, null, null, null);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }
    }
}
