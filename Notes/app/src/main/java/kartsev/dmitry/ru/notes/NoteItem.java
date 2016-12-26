package kartsev.dmitry.ru.notes;

/**
 * Created by Jag on 25.12.2016.
 */

public class NoteItem {
    private String title;
    private String note;
    private int idInDB;

    public NoteItem(String title, String note, int idInDB) {
        this.title = title;
        this.note = note;
        this.idInDB = idInDB;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public int getIdInTable() {
        return idInDB;
    }
}
