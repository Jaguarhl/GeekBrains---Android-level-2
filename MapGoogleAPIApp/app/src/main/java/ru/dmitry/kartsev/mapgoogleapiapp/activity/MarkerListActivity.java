package ru.dmitry.kartsev.mapgoogleapiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ru.dmitry.kartsev.mapgoogleapiapp.MainActivity;
import ru.dmitry.kartsev.mapgoogleapiapp.R;
import ru.dmitry.kartsev.mapgoogleapiapp.adapters.MarkersListAdapter;
import ru.dmitry.kartsev.mapgoogleapiapp.data.MarkerItem;
import ru.dmitry.kartsev.mapgoogleapiapp.helpers.DBHelper;

public class MarkerListActivity extends AppCompatActivity {
    private MarkersListAdapter adapter;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private RecyclerView recyclerView;
    private static List<MarkerItem> markersList;
    private Toolbar toolbar;
    private static Context mContext;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers_list);

        initDatabase();
        initViews();
        setButtonBehavior();
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
    }

    private void setButtonBehavior() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MapActivity.class);
                intent.putExtra(MainActivity.SEARCH_ADDRESS_ACTION, "");
                MarkerListActivity.this.startActivity(intent);
            }
        });
    }

    private void initViews() {
        btnClose = (Button) findViewById(R.id.btnClose);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*markersList = new ArrayList<>();
        database = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = database.query(DBHelper.TB_USERS_NAME, null, null, null, null, null, null);

            if(cursor.moveToFirst()) {
                // определеяем номера столцов по имени в выборке
                int idIndex = cursor.getColumnIndex(DBHelper.TB_ID_COL_NAME);
                int unameColIndex = cursor.getColumnIndex(DBHelper.TB_USERNAME_COL_NAME);
                int userActive = cursor.getColumnIndex(DBHelper.TB_ACTIVE_COL_NAME);

                do {
                    usersCardsList.add(new UsersCards(cursor.getString(unameColIndex),
                            cursor.getInt(userActive) != 0, cursor.getInt(idIndex)));
                } while (cursor.moveToNext());
            }

            Log.d(MainActivity.LOG_TAG, "count of rows = " + cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        adapter = new MarkersListAdapter(markersList, MarkerListActivity.this);
        recyclerView.setAdapter(adapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*
        toolbar.setTitle(R.string.hint_markers_list);
         */
    }

    public static void openView(Context context, List<MarkerItem> mrkrsList) {
        markersList = new ArrayList<>();
        markersList = mrkrsList;
        mContext = context;
        Intent intent = new Intent(context, MarkerListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();
    }
}
