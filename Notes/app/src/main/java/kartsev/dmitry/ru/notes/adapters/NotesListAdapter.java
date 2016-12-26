package kartsev.dmitry.ru.notes.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kartsev.dmitry.ru.notes.MainActivity;
import kartsev.dmitry.ru.notes.R;
import kartsev.dmitry.ru.notes.NoteItem;
import kartsev.dmitry.ru.notes.activities.EditNoteActivity;
import kartsev.dmitry.ru.notes.helpers.DBHelper;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Jag on 16.12.2016.
 */

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.ViewHolder> {

    private List<NoteItem> listNotes;
    private Context mContext;
    private DBHelper dbHelper;

    public NotesListAdapter(List<NoteItem> listNotes, Context mContext) {
        this.listNotes = listNotes;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final NoteItem noteList = listNotes.get(position);        
        holder.txtTitle.setText(noteList.getTitle());
        holder.txtDescription.setText(noteList.getNote());
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext, holder.btnMenu);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnu_item_delete:
                                dbHelper = new DBHelper(mContext);
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                database.delete(DBHelper.TB_NAME, DBHelper.QUERY_ID,
                                        new String[]{(Integer.toString(listNotes.get(position).getIdInTable()) )});
                                dbHelper.close();
                                listNotes.remove(position);
                                notifyDataSetChanged();
                                break;
                            case R.id.mnu_item_edit:
                                Intent intent = new Intent(MainActivity.NOTES_INTENT_ACTION_EDIT,
                                        null, mContext, EditNoteActivity.class);
                                intent.putExtra(EditNoteActivity.NOTES_INTENT_VALUE_ITEMPOSITION, position);
                                intent.putExtra(EditNoteActivity.NOTES_INTENT_VALUE_ITEMID,
                                        listNotes.get(position).getIdInTable());
                                EditNoteActivity.openView(intent, mContext, listNotes);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle, txtDescription;
        public ImageButton btnMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            btnMenu = (ImageButton) itemView.findViewById(R.id.imgBtnMenu);
        }
    }
}
