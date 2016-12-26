package kartsev.dmitry.ru.notes.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import kartsev.dmitry.ru.notes.MainActivity;
import kartsev.dmitry.ru.notes.NoteItem;
import kartsev.dmitry.ru.notes.R;
import kartsev.dmitry.ru.notes.helpers.DBHelper;

public class EditNoteActivity extends Activity {
    public static final String STATE_VALUE_NODETITLE = "kartsev.dmitry.ru.nodes.nodetitle";
    public static final String STATE_VALUE_NODETEXT = "kartsev.dmitry.ru.nodes.nodetext";
    public static final String NOTES_INTENT_VALUE_ITEMPOSITION = "kartsev.dmitry.ru.notes.intent.value.itemposition";
    public static final String NOTES_INTENT_VALUE_ITEMID = "kartsev.dmitry.ru.notes.intent.value.itemid";
    private DBHelper dbHelper;
    private EditText edTitle, edNote;
    private Button btnSave, btnCancel;
    private boolean edit = false;
    private int itemId = 0;
    private int itemPositionInList = 0;

    static private List<NoteItem> listNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_activity);

        Intent intent = getIntent();
        String action = intent.getAction();
        if ((!action.isEmpty()) && (action.equals(MainActivity.NOTES_INTENT_ACTION_EDIT))) {
            edit = true;
            itemId = intent.getIntExtra(NOTES_INTENT_VALUE_ITEMID, 0);
            itemPositionInList = intent.getIntExtra(NOTES_INTENT_VALUE_ITEMPOSITION, 0);
        } else {
            edit = false;
        }

        initViews();
        setButtonsBehavior();

        if (savedInstanceState != null) {
            edTitle.setText(savedInstanceState.getString(STATE_VALUE_NODETITLE));
            edNote.setText(savedInstanceState.getString(STATE_VALUE_NODETEXT));
        }
    }

    private void setButtonsBehavior() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.openView(EditNoteActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    dbHelper = new DBHelper(getBaseContext());
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    Cursor c = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);
                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.TB_TITLE_COL_NAME, edTitle.getText().toString());
                    cv.put(DBHelper.TB_NOTE_COL_NAME, edNote.getText().toString());
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
                        MainActivity.openView(EditNoteActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkFields() {
        return true;
    }

    private void initViews() {
        edTitle = (EditText) findViewById(R.id.editTitle);
        edNote = (EditText) findViewById(R.id.editNote);
        btnSave = (Button) findViewById(R.id.btnSave);
        if (edit) {
            btnSave.setText(R.string.btn_save);
            edNote.setText(listNotes.get(itemPositionInList).getNote());
            edTitle.setText(listNotes.get(itemPositionInList).getTitle());
        } else {
            btnSave.setText(R.string.btn_add);
        }
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_VALUE_NODETITLE, edTitle.getText().toString());
        outState.putString(STATE_VALUE_NODETEXT, edNote.getText().toString());
    }

    public static void openView(Intent intent, Context mContext, List<NoteItem> lstNotes) {
        listNotes = lstNotes;
        mContext.startActivity(intent);
    }
}
