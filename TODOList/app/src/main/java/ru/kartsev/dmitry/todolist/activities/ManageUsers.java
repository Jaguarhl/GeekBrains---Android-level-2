package ru.kartsev.dmitry.todolist.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import ru.kartsev.dmitry.todolist.MainActivity;
import ru.kartsev.dmitry.todolist.R;
import ru.kartsev.dmitry.todolist.UsersCards;
import ru.kartsev.dmitry.todolist.adapters.UsersListAdapter;
import ru.kartsev.dmitry.todolist.helpers.DBHelper;

public class ManageUsers extends AppCompatActivity {
    private UsersListAdapter adapter;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private ImageButton addUser;
    private RecyclerView usersList;
    private static List<UsersCards> usersCardsList;
    private Toolbar toolbar;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabase();
        initViews();
        initDrawer();
        setButtonBehavior();
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
    }

    private void setButtonBehavior() {
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditActivity(view);
            }
        });
    }

    private void initViews() {
        addUser = (ImageButton) findViewById(R.id.imgBtnAdd);
        usersList = (RecyclerView) findViewById(R.id.recyclerView);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));

        usersCardsList = new ArrayList<>();        
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
        }

        adapter = new UsersListAdapter(usersCardsList, this);
        usersList.setAdapter(adapter);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_users_title);
    }

    private void initDrawer() {
        new DrawerBuilder().withActivity(this).build();

        PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem();
        primaryDrawerItem.withName(R.string.side_menu_profile_2).withIcon(R.drawable.users);

        SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem();
        secondaryDrawerItem.withName(R.string.side_menu_profile_1).withIcon(R.mipmap.ic_launcher)
                .withIdentifier(2);

        DividerDrawerItem dividerDrawerItem = new DividerDrawerItem();

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(new ProfileDrawerItem().withName(getResources().getString(R.string.side_menu_profile_2))
                                .withIcon(getResources().getDrawable(R.mipmap.ic_launcher)),
                        new ProfileDrawerItem().withName(getResources().getString(R.string.side_menu_profile_1))
                                .withIcon(getResources().getDrawable(R.drawable.users))
                ).build();

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(primaryDrawerItem, secondaryDrawerItem, dividerDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int id = (int)drawerItem.getIdentifier();
                        switch (id) {
                            case 1: {
                                break;
                            }
                            case 2: {
                                MainActivity.openView(ManageUsers.this);
                                break;
                            }
                        }
                        return false;
                    }
                })
                .build();
    }

    public static void openView(Context context, List<UsersCards> usersCrdsList) {
        usersCardsList = usersCrdsList;
        mContext = context;
        Intent intent = new Intent(context, ManageUsers.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();
    }

    public void openEditActivity(View view) {
        Intent intent = new Intent(MainActivity.TODOLIST_INTENT_ACTION_ADDNEW, null, this, EditUser.class);
        EditUser.openView(intent, mContext, usersCardsList);
    }

    public static void openView(Context context) {
        Intent intent = new Intent(context, ManageUsers.class);
        context.startActivity(intent);
    }
}
