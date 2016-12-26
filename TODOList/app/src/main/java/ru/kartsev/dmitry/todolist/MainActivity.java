package ru.kartsev.dmitry.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kartsev.dmitry.todolist.helpers.DBHelper;
import ru.kartsev.dmitry.todolist.activities.EditTask;
import ru.kartsev.dmitry.todolist.adapters.DealsListAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String TODOLIST_INTENT_ACTION_ADDNEW = "ru.kartsev.dmitry.todolist.intent.action.addnew";
    public static final String TODOLIST_INTENT_ACTION_EDIT = "ru.kartsev.dmitry.todolist.intent.action.edit";
    public static final String TODOLIST_INTENT_VALUE_ITEMPOSITION = "ru.kartsev.dmitry.todolist.intent.value.itemposition";
    public static final String TODOLIST_INTENT_VALUE_ITEMID = "ru.kartsev.dmitry.todolist.intent.value.itemid";
    public static final String LOG_TAG = "BD_LOGS";
    private ImageButton addDeal;
    private RecyclerView dealsList;
    private DealsListAdapter adapter;
    private List<TaskItem> listItems;
    private List<UsersCards> listInCharge;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    String[] title = { "Добавить первую задачу", "Отметить выполненным", "Удалить задачу" };
    String[] users = { "Я", "Друг", "Подруга" };
    String[] description = { "Добьте в список дел первую вашу задачу.", "Отметить данную задачу как выполненную.",
            "Удалите эту задачу из списка." };
    int[] active = { 1, 1, 1 };
    int[] usersInCharge = { 1, 2, 3 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabase();
        fillDB();
        initViews();
        setButtonBehavior();
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
    }

    private void setButtonBehavior() {
        addDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditActivity(view);
            }
        });
    }

    private void initViews() {
        addDeal = (ImageButton) findViewById(R.id.imgBtnAdd);
        dealsList = (RecyclerView) findViewById(R.id.recyclerView);
        dealsList.setHasFixedSize(true);
        dealsList.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
        listInCharge = new ArrayList<>();
        database = dbHelper.getWritableDatabase();
        // filling list data
        try {
            Cursor cursor = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);

            if(cursor.moveToFirst()) {
                // определеяем номера столцов по имени в выборке
                int idIndex = cursor.getColumnIndex(DBHelper.TB_ID_COL_NAME);
                int titleColIndex = cursor.getColumnIndex(DBHelper.TB_TITLE_COL_NAME);
                int descriptionColIndex = cursor.getColumnIndex(DBHelper.TB_DESC_COL_NAME);
                int taskActive = cursor.getColumnIndex(DBHelper.TB_ACTIVE_COL_NAME);
                int inCharge = cursor.getColumnIndex(DBHelper.TB_INCHARGE_COL_NAME);

                do {
                   listItems.add(new TaskItem(cursor.getString(titleColIndex), cursor.getString(descriptionColIndex),
                           cursor.getInt(taskActive) != 0, cursor.getInt(idIndex), cursor.getInt(inCharge)));
                } while (cursor.moveToNext());
            }

            Log.d(LOG_TAG, "count of rows = " + cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Cursor cursor = database.query(DBHelper.TB_USERS_NAME, null, null, null, null, null, null);

            if(cursor.moveToFirst()) {
                // определеяем номера столцов по имени в выборке
                int idIndex = cursor.getColumnIndex(DBHelper.TB_ID_COL_NAME);
                int unameColIndex = cursor.getColumnIndex(DBHelper.TB_USERNAME_COL_NAME);
                int userActive = cursor.getColumnIndex(DBHelper.TB_ACTIVE_COL_NAME);

                do {
                    listInCharge.add(new UsersCards(cursor.getString(unameColIndex),
                            cursor.getInt(userActive) != 0, cursor.getInt(idIndex)));
                } while (cursor.moveToNext());
            }

            Log.d(LOG_TAG, "count of rows = " + cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new DealsListAdapter(listItems, listInCharge, this);
        dealsList.setAdapter(adapter);
    }

    private void fillDB() {
        //Проверка существования записей
        Cursor c = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);
        if(c.getCount() == 0) {
            ContentValues cv = new ContentValues();

            //Заполним таблицу
            for(int i = 0; i < 3; i++) {
                cv.put(DBHelper.TB_TITLE_COL_NAME, title[i]);
                cv.put(DBHelper.TB_DESC_COL_NAME, description[i]);
                cv.put(DBHelper.TB_ACTIVE_COL_NAME, active[i]);
                cv.put(DBHelper.TB_INCHARGE_COL_NAME, usersInCharge[i]);

                long countOfInsert = database.insert(DBHelper.TB_NAME, null, cv);
                Log.d(LOG_TAG, "count of inserted rows = " + countOfInsert);
            }

            cv = new ContentValues();

            for(int i = 0; i < 3; ++i) {
                cv.put(DBHelper.TB_USERNAME_COL_NAME, users[i]);
                cv.put(DBHelper.TB_ACTIVE_COL_NAME, active[i]);
                long countOfInsert = database.insert(DBHelper.TB_USERS_NAME, null, cv);
                Log.d(LOG_TAG, "count of inserted rows in " + DBHelper.TB_USERS_NAME + " = " + countOfInsert);
            }
       }
        c.close();
        dbHelper.close();
    }

    public static void openView(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();
    }

    public void openEditActivity(View view) {
        Intent intent = new Intent(TODOLIST_INTENT_ACTION_ADDNEW, null, this, EditTask.class);
        EditTask.openView(intent, this, listItems, listInCharge);
    }
}
