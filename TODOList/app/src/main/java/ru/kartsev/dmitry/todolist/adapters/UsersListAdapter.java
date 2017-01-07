package ru.kartsev.dmitry.todolist.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import ru.kartsev.dmitry.todolist.MainActivity;
import ru.kartsev.dmitry.todolist.R;
import ru.kartsev.dmitry.todolist.UsersCards;
import ru.kartsev.dmitry.todolist.activities.EditTask;
import ru.kartsev.dmitry.todolist.activities.EditUser;
import ru.kartsev.dmitry.todolist.activities.ManageUsers;
import ru.kartsev.dmitry.todolist.helpers.DBHelper;

import static android.support.v4.content.ContextCompat.startActivity;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private List<UsersCards> listUsers;
    private Context mContext;
    private DBHelper dbHelper;

    public UsersListAdapter(List<UsersCards> listUsers, Context mContext) {
        this.listUsers = listUsers;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_users, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final UsersCards userCard = listUsers.get(position);
        /*if (!userCard.isActive()) {
            holder.imgStatus.setImageResource(R.drawable.checked);
        } else {
            holder.imgStatus.setImageResource(R.drawable.unchecked);
        }*/
        holder.txtName.setText(userCard.getName());
        holder.btnUMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext, holder.btnUMenu);
                popupMenu.inflate(R.menu.users_options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnu_item_delete_user:
                                dbHelper = new DBHelper(mContext);
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                Cursor c = database.query(DBHelper.TB_NAME, new String[] {(DBHelper.TB_INCHARGE_COL_NAME)},
                                        String.valueOf(Integer.toString(listUsers.get(position).getIdInTable())),
                                        null, null, null, null);
                                while (c.moveToNext()) {
                                    cv.put(DBHelper.TB_INCHARGE_COL_NAME, 0);
                                    database.update(DBHelper.TB_NAME, cv, DBHelper.QUERY_ID, new String[]{(Integer.toString(listUsers.get(position).getIdInTable()) )});

                                }
                                database.delete(DBHelper.TB_USERS_NAME, DBHelper.QUERY_ID,
                                        new String[]{(Integer.toString(listUsers.get(position).getIdInTable()) )});
                                dbHelper.close();
                                listUsers.remove(position);
                                notifyDataSetChanged();
                                break;
                            case R.id.mnu_item_edit_user:
                                Intent intent = new Intent(MainActivity.TODOLIST_INTENT_ACTION_EDIT,
                                        null, mContext, EditUser.class);
                                intent.putExtra(MainActivity.TODOLIST_INTENT_VALUE_USERPOSITION, position);
                                intent.putExtra(MainActivity.TODOLIST_INTENT_VALUE_USERID,
                                        listUsers.get(position).getIdInTable());
                                EditUser.openView(intent, mContext, listUsers);
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
        /*holder.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listUsers.get(position).isActive()) {
                    listUsers.get(position).setActive(false);
                } else {
                    listUsers.get(position).setActive(true);
                }

                setUserActive(listUsers.get(position).getIdInTable(), listUsers.get(position).isActive());
                notifyDataSetChanged();
            }
        });*/
    }

    /*
    private void setUserActive(int idInTable, boolean active) {
        dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.TB_ACTIVE_COL_NAME, active);
        database.update(DBHelper.TB_USERS_NAME, cv, DBHelper.QUERY_ID, new String[]{(Integer.toString(idInTable) )});
        dbHelper.close();
    }
    */

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        /*
        public ImageView imgStatus;
         */
        public ImageButton btnUMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            //imgStatus = (ImageView) itemView.findViewById(R.id.imageUStatus);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            btnUMenu = (ImageButton) itemView.findViewById(R.id.imgUsersBtnMenu);
        }
    }
}
