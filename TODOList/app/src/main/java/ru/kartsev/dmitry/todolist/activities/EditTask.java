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

/**
 * Created by Jag on 18.12.2016.
 */

public class EditTask extends Activity {
    public static final String STATE_VALUE_TASKTITLE = "ru.dmitry.kartsev.todolist.tasktitle";
    public static final String STATE_VALUE_TASKDESC = "ru.dmitry.kartsev.todolist.taskdesc";
    public static final int MAX_TITLE_LENGTH = 25;
    public static final int MAX_DESCRIPTION_LENGTH = 255;
    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MIN_DESC_LENGTH = 5;
    private static List<TaskItem> listItems;
    private static List<UsersCards> listInCharge;
    private boolean edit = false;
    private EditText editTitile, editDesc;
    private Button btnOk, btnCancel;
    private Spinner inCharge;
    private DBHelper dbHelper;

    private static int itemId = 0;
    private static int itemPositionInList = 0;
    private int lastUserSelected = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_task_dialog);

        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(MainActivity.LOG_TAG, action);
        if ((!action.isEmpty()) && (action.equals(MainActivity.TODOLIST_INTENT_ACTION_EDIT))) {
            edit = true;
            itemId = intent.getIntExtra(MainActivity.TODOLIST_INTENT_VALUE_ITEMID, 0);
            Log.d(MainActivity.LOG_TAG, "EditTask Intent - itemId = " + itemId);
            itemPositionInList = intent.getIntExtra(MainActivity.TODOLIST_INTENT_VALUE_ITEMPOSITION, 0);
            Log.d(MainActivity.LOG_TAG, "EditTask Intent - itemPositionInList = " + itemPositionInList);
        } else {
            edit = false;
            Log.d(MainActivity.LOG_TAG, "EditTask Intent - not edit");
        }

        initViews();
        setButtonBehavior();
        setSpinnerBehavior();

        if (savedInstanceState != null) {
            editTitile.setText(savedInstanceState.getString(STATE_VALUE_TASKTITLE));
            editDesc.setText(savedInstanceState.getString(STATE_VALUE_TASKDESC));
        }
    }

    private void setSpinnerBehavior() {
        inCharge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != lastUserSelected) {
                    listItems.get(itemPositionInList).setInCharge(listInCharge.get(position).getIdInTable());
                    lastUserSelected = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lastUserSelected = 0;
            }
        });
    }

    private void setButtonBehavior() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.openView(EditTask.this);
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
                    Cursor c = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);
                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.TB_TITLE_COL_NAME, editTitile.getText().toString());
                    cv.put(DBHelper.TB_DESC_COL_NAME, editDesc.getText().toString());
                    cv.put(DBHelper.TB_ACTIVE_COL_NAME, true);
                    cv.put(DBHelper.TB_INCHARGE_COL_NAME, listInCharge.get(inCharge.getSelectedItemPosition()).getIdInTable());
                    Log.d(MainActivity.LOG_TAG, "Setted ID of user in " + DBHelper.TB_NAME +
                            " to " + inCharge.getSelectedItemPosition());
                    if (!edit) {
                        c.moveToLast();
                        database.insert(DBHelper.TB_NAME, null, cv);
                    } else {
                        c.move(itemId);
                        database.update(DBHelper.TB_NAME, cv, DBHelper.QUERY_ID,
                                new String[]{(Integer.toString(itemId))});
                    }
                    c.close();
                    dbHelper.close();
                    try {
                        MainActivity.openView(EditTask.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkFields() {
        if (checkTitleField() && checkDescriptionField())
            return true;
        return false;
    }

    private boolean checkDescriptionField() {
        if (editDesc.getText().length() > MIN_DESC_LENGTH)
            return true;
        else {
            showToastMessage(R.string.err_description);
        }
        return false;
    }

    private boolean checkTitleField() {
        if (editTitile.getText().length() > MIN_TITLE_LENGTH)
            return true;
        else {
            showToastMessage(R.string.err_title);
        }
        return false;
    }

    private void initViews() {
        editTitile = (EditText) findViewById(R.id.editTitle);
        editTitile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TITLE_LENGTH)});
        editDesc = (EditText) findViewById(R.id.editDescription);
        editDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_DESCRIPTION_LENGTH)});
        btnOk = (Button) findViewById(R.id.btnOK);
        inCharge = (Spinner) findViewById(R.id.spinnerInCharge);
        fillSpinnerData();
        if (edit) {
            btnOk.setText(R.string.btn_save);
            inCharge.setSelection(getIndex(inCharge, listInCharge.get(listItems.get(
                    itemPositionInList).getInCharge() - 1).getName()));
        }
        btnCancel = (Button) findViewById(R.id.btnCancel);

        if (edit) {
            editDesc.setText(listItems.get(itemPositionInList).getDescription());
            editTitile.setText(listItems.get(itemPositionInList).getTitle());
        }
    }

    private void fillSpinnerData() {
        int size = listInCharge.size();
        String users[] = new String[size];
        for (int i = 0; i < size; ++i) {
            users[i] = listInCharge.get(i).getName();
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, users);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inCharge.setAdapter(spinnerArrayAdapter);
    }

    public static void openView(Intent intent, Context context, List<TaskItem> listItms,
                                List<UsersCards> listInChrge) {
        listItems = listItms;
        listInCharge = listInChrge;
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
        outState.putString(STATE_VALUE_TASKTITLE, editTitile.getText().toString());
        outState.putString(STATE_VALUE_TASKDESC, editDesc.getText().toString());
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
