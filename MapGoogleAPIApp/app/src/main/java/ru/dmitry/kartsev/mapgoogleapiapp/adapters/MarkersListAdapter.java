package ru.dmitry.kartsev.mapgoogleapiapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.dmitry.kartsev.mapgoogleapiapp.R;
import ru.dmitry.kartsev.mapgoogleapiapp.activity.MapActivity;
import ru.dmitry.kartsev.mapgoogleapiapp.data.MarkerItem;
import ru.dmitry.kartsev.mapgoogleapiapp.helpers.DBWork;

import static android.R.id.message;

public class MarkersListAdapter extends RecyclerView.Adapter<MarkersListAdapter.ViewHolder> {

    private List<MarkerItem> markersList;
    private Context mContext;
    private DBWork dbWork;

    public MarkersListAdapter(List<MarkerItem> markersList, Context mContext) {
        this.markersList = markersList;
        this.mContext = mContext;
        this.dbWork = new DBWork(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_marker_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final MarkerItem item = markersList.get(position);
        holder.txtMarkerTitle.setText(item.getMarkerName());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);

                mDialog
                        .setTitle(mContext.getResources().getString(R.string.delete_marker_title))
                        .setMessage(mContext.getResources().getString(R.string.delete_marker_message))
                        .setCancelable(false)
                        .setPositiveButton(mContext.getResources().getString(R.string.btn_delete_marker),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dbWork.deleteMarker(markersList.get(position).getMarkerIdInDB());
                                        markersList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton(mContext.getResources().getString(R.string.btn_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialog.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return markersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtMarkerTitle;
        public ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMarkerTitle = (TextView) itemView.findViewById(R.id.txtMarketTitle);
            btnDelete = (ImageButton) itemView.findViewById(R.id.imgUsersBtnMenu);
        }
    }
}
