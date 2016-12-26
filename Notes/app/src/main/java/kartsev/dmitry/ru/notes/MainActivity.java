package kartsev.dmitry.ru.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import kartsev.dmitry.ru.notes.activities.EditNoteActivity;
import kartsev.dmitry.ru.notes.adapters.NotesListAdapter;
import kartsev.dmitry.ru.notes.helpers.DBHelper;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "DB_LOGS";
    public static final String NOTES_INTENT_ACTION_ADDNEW = "kartsev.dmitry.ru.notes.intent.action.addnew";
    public static final String NOTES_INTENT_ACTION_EDIT = "kartsev.dmitry.ru.notes.intent.action.edit";
    private ImageButton addNoteBtn;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private RecyclerView notesList;
    private List<NoteItem> listNotes;
    private NotesListAdapter adapter;

    String[] title = {"Б. Пастернак", "Б. Пастернак", "Б. Пастернак"};
    String[] description = {"Мело мело по всей земле, во все пределы.\n" +
            "Свеча горела на столе, свеча горела.\nКак летним зноем мошкара летиит на пламя,\n" +
            "Слетались хлопья со двора к оконной раме.",
            "Метель лепила на стекле кружки и стрелы,\n" +
                    "Свеча горела на столе, свеча горела.\n" +
                    "На озарённый потолок ложились тени,\n" +
                    "Скрещенья рук, скрещенья ног... судьбы скрещенья.",
            "И падали два башмачка со стуком на пол\n" +
                    "И воск слезами с ночника на платье капал.\n" +
                    "И всё терялось в снежной мгле, седой и белой,\n" +
                    "Свеча горела на столе, свеча горела."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabase();
        fillDB();
        initViews();
        setButtonsBehavior();
    }

    private void setButtonsBehavior() {
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditActivity(view);
            }
        });
    }

    private void openEditActivity(View view) {
        Intent intent = new Intent(NOTES_INTENT_ACTION_ADDNEW, null, this, EditNoteActivity.class);
        EditNoteActivity.openView(intent, this, listNotes);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
    }

    private void fillDB() {
        Cursor c = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);
        if (c.getCount() == 0) {
            ContentValues cv = new ContentValues();

            //Заполним таблицу
            for (int i = 0; i < 3; i++) {
                cv.put(DBHelper.TB_TITLE_COL_NAME, title[i]);
                cv.put(DBHelper.TB_NOTE_COL_NAME, description[i]);

                long countOfInsert = database.insert(DBHelper.TB_NAME, null, cv);
                Log.d(LOG_TAG, "count of inserted rows = " + countOfInsert);
            }
        }
        c.close();
        dbHelper.close();
    }

    private void initViews() {
        addNoteBtn = (ImageButton) findViewById(R.id.imgBtnAdd);
        notesList = (RecyclerView) findViewById((R.id.recyclerView));
        notesList.setHasFixedSize(true);
        notesList.setLayoutManager(new LinearLayoutManager(this));

        listNotes = new ArrayList<>();
        database = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = database.query(DBHelper.TB_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                // определеяем номера столцов по имени в выборке
                int idIndex = cursor.getColumnIndex(DBHelper.TB_ID_COL_NAME);
                int titleColIndex = cursor.getColumnIndex(DBHelper.TB_TITLE_COL_NAME);
                int noteColIndex = cursor.getColumnIndex(DBHelper.TB_NOTE_COL_NAME);

                do {
                    listNotes.add(new NoteItem(cursor.getString(titleColIndex), cursor.getString(noteColIndex),
                            cursor.getInt(idIndex)));
                } while (cursor.moveToNext());
            }

            Log.d(LOG_TAG, "count of rows = " + cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new NotesListAdapter(listNotes, this);
        notesList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public static void openView(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
