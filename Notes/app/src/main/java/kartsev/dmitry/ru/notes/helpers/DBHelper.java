package kartsev.dmitry.ru.notes.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "notes";
    public static final String TB_NAME = "notelist";
    public static final String TB_ID_COL_NAME = "_id";
    public static final String TB_TITLE_COL_NAME = "title";
    public static final String TB_NOTE_COL_NAME = "note";
    public static final String QUERY_ID = "_id=?";
    private final String DROP_TABLE = "DROP TABLE IF EXISTS " + TB_NAME;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("CREATE TABLE " + TB_NAME +" (" + TB_ID_COL_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TB_TITLE_COL_NAME + " STRING, " + TB_NOTE_COL_NAME + " STRING);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);*/
    }
}
