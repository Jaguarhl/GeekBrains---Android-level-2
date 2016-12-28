package ru.kartsev.dmitry.todolist.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import ru.kartsev.dmitry.todolist.MainActivity;
import ru.kartsev.dmitry.todolist.R;
import ru.kartsev.dmitry.todolist.TaskItem;
import ru.kartsev.dmitry.todolist.UsersCards;
import ru.kartsev.dmitry.todolist.helpers.DBHelper;

public class EditUser extends Activity {
    public static final String STATE_VALUE_USERNAME = "ru.dmitry.kartsev.todolist.username";
    public static final int MAX_NAME_LENGTH = 45;
    public static final int MIN_NAME_LENGTH = 4;
    private static List<UsersCards> usersCardsList;
    private boolean edit = false;
    private EditText editName;
    private Button btnOk, btnCancel;
    private DBHelper dbHelper;

    private static int userId = 0;
    private static int userPositionInList = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_user_dialog);

        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(MainActivity.LOG_TAG, action);
        if ((!action.isEmpty()) && (action.equals(MainActivity.TODOLIST_INTENT_ACTION_EDIT))) {
            edit = true;
            userId = intent.getIntExtra(MainActivity.TODOLIST_INTENT_VALUE_USERID, 0);
            Log.d(MainActivity.LOG_TAG, "EditUser Intent - userId = " + userId);
            userPositionInList = intent.getIntExtra(MainActivity.TODOLIST_INTENT_VALUE_USERPOSITION, 0);
            Log.d(MainActivity.LOG_TAG, "EditUser Intent - userPositionInList = " + userPositionInList);
        } else {
            edit = false;
            Log.d(MainActivity.LOG_TAG, "EditUser Intent - not edit");
        }

        initViews();
        setButtonBehavior();

        if (savedInstanceState != null) {
            editName.setText(savedInstanceState.getString(STATE_VALUE_USERNAME));
        }
    }

    private void setButtonBehavior() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ManageUsers.openView(EditUser.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    dbHelper = new DBHelper(getBaseContext());
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    Cursor c = database.query(DBHelper.TB_USERS_NAME, null, null, null, null, null, null);
                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.TB_USERNAME_COL_NAME, editName.getText().toString());
                    if (!edit) {
                        c.moveToLast();
                        database.insert(DBHelper.TB_USERS_NAME, null, cv);
                    } else {
                        c.move(userId);
                        database.update(DBHelper.TB_USERS_NAME, cv, DBHelper.QUERY_ID,
                                new String[]{(Integer.toString(userId))});
                    }
                    c.close();
                    dbHelper.close();
                    try {
                        ManageUsers.openView(EditUser.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkFields() {
        if (checkTitleField())
            return true;
        return false;
    }

    private boolean checkTitleField() {
        if (editName.getText().length() > MIN_NAME_LENGTH)
            return true;
        else {
            showToastMessage(R.string.err_name);
        }
        return false;
    }

    private void initViews() {
        editName = (EditText) findViewById(R.id.editName);
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NAME_LENGTH)});
        btnOk = (Button) findViewById(R.id.btnOK);
        if (edit) {
            btnOk.setText(R.string.btn_save);
            editName.setText(usersCardsList.get(userPositionInList).getName());
        }
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    public static void openView(Intent intent, Context context,
                                List<UsersCards> usersCrdsList) {
        usersCardsList = usersCrdsList;
        context.startActivity(intent);
    }

    private void showToastMessage(int msg) {
        Toast message = Toast.makeText(getApplicationContext(),
                msg, Toast.LENGTH_LONG);
        message.setGravity(Gravity.CENTER, 0, 0);
        message.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_VALUE_USERNAME, editName.getText().toString());
    }
}
