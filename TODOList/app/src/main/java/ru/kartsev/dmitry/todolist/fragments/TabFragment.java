package ru.kartsev.dmitry.todolist.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import ru.kartsev.dmitry.todolist.R;

/**
 * Created by Jag on 03.01.2017.
 */

public class TabFragment extends Fragment {
    private TextView textView;
    private String text = "";
    private int icon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adding_task_one_tab_fragment, container, false);

        initViews(view);
        setTextViewClickBehavior();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView.setText(text);
    }

    private void initViews(View view) {
        textView = (TextView)view.findViewById(R.id.taskTitle);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIcon(int icn) {
        this.icon = icn;
    }

    public String getTitleText() {
        return text;
    }

    public int getIcon() {
        return icon;
    }

    private void setTextViewClickBehavior() {
        /*final PopupMenu popupMenu = new PopupMenu(getContext(), textView);
        popupMenu.inflate(R.menu.popup_menu);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });*/
    }
}
