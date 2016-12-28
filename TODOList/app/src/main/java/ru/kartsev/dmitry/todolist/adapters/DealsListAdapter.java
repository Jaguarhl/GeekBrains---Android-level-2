package ru.kartsev.dmitry.todolist.adapters;

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

import ru.kartsev.dmitry.todolist.MainActivity;
import ru.kartsev.dmitry.todolist.R;
import ru.kartsev.dmitry.todolist.TaskItem;
import ru.kartsev.dmitry.todolist.UsersCards;
import ru.kartsev.dmitry.todolist.activities.EditTask;
import ru.kartsev.dmitry.todolist.helpers.DBHelper;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Jag on 16.12.2016.
 */

public class DealsListAdapter extends RecyclerView.Adapter<DealsListAdapter.ViewHolder> {

    private List<TaskItem> listItems;
    private List<UsersCards> listInCharge;
    private Context mContext;
    private DBHelper dbHelper;

    public DealsListAdapter(List<TaskItem> listItems, List<UsersCards> listInCharge, Context mContext) {
        this.listItems = listItems;
        this.listInCharge = listInCharge;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final TaskItem itemList = listItems.get(position);
        if (!itemList.isActive()) {
            holder.imgStatus.setImageResource(R.drawable.checked);
        } else {
            holder.imgStatus.setImageResource(R.drawable.unchecked);
        }
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDescription.setText(itemList.getDescription());
        try{
            holder.txtInCharge.setText(listInCharge.get(itemList.getInCharge() - 1).getName());
        } catch (Exception e) {
            holder.txtInCharge.setText(listInCharge.get(0).getName());
            e.printStackTrace();
        }
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext, holder.btnMenu);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnu_item_check_complete:
                                if (listItems.get(position).isActive()) {
                                    listItems.get(position).setActive(false);
                                } else {
                                    listItems.get(position).setActive(true);
                                }
                                setTaskActive(listItems.get(position).getIdInTable(),
                                        listItems.get(position).isActive());
                                notifyDataSetChanged();
                                break;
                            case R.id.mnu_item_delete:
                                dbHelper = new DBHelper(mContext);
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                database.delete(DBHelper.TB_NAME, DBHelper.QUERY_ID,
                                        new String[]{(Integer.toString(listItems.get(position).getIdInTable()) )});
                                dbHelper.close();
                                listItems.remove(position);
                                notifyDataSetChanged();
                                break;
                            case R.id.mnu_item_edit:
                                Intent intent = new Intent(MainActivity.TODOLIST_INTENT_ACTION_EDIT,
                                        null, mContext, EditTask.class);
                                intent.putExtra(MainActivity.TODOLIST_INTENT_VALUE_ITEMPOSITION, position);
                                intent.putExtra(MainActivity.TODOLIST_INTENT_VALUE_ITEMID,
                                        listItems.get(position).getIdInTable());
                                EditTask.openView(intent, mContext, listItems, listInCharge);
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
        holder.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItems.get(position).isActive()) {
                    listItems.get(position).setActive(false);
                } else {
                    listItems.get(position).setActive(true);
                }
                setTaskActive(listItems.get(position).getIdInTable(), listItems.get(position).isActive());
                notifyDataSetChanged();
            }
        });
    }

    private void setTaskActive(int idInTable, boolean active) {
        dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.TB_ACTIVE_COL_NAME, active);
        database.update(DBHelper.TB_NAME, cv, DBHelper.QUERY_ID, new String[]{(Integer.toString(idInTable) )});
        dbHelper.close();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle, txtDescription, txtInCharge, txtDateTo;
        public ImageView imgStatus;
        public ImageButton btnMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageStatus);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtInCharge = (TextView) itemView.findViewById(R.id.txtInCharge);
            txtDateTo = (TextView) itemView.findViewById(R.id.txtDateTo);
            btnMenu = (ImageButton) itemView.findViewById(R.id.imgBtnMenu);
        }
    }
}
