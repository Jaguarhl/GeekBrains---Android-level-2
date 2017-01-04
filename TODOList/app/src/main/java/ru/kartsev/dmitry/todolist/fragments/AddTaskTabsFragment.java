package ru.kartsev.dmitry.todolist.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import goldzweigapps.tabs.Builder.EasyTabsBuilder;
import goldzweigapps.tabs.Interface.TabsListener;
import goldzweigapps.tabs.Items.TabItem;
import goldzweigapps.tabs.View.EasyTabs;
import ru.kartsev.dmitry.todolist.R;

/**
 * Created by Jag on 03.01.2017.
 */
public class AddTaskTabsFragment extends Fragment {
    private EasyTabs easyTabs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adding_task_tabs_fragment, container, false);

        initViews(view);
        setTabs();

        return view;
    }

    private void initViews(View view) {
        easyTabs = (EasyTabs)view.findViewById(R.id.easyTabs);
    }

    public interface onSelectTabEventListener {
        public void onSelectTabEvent(int s);
    }

    onSelectTabEventListener selectTabEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            selectTabEventListener = (onSelectTabEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSelectTabEventListener");
        }
    }

    private void setTabs() {
        TabFragment firstFragment = new TabFragment();
        TabFragment secondFragment = new TabFragment();
        TabFragment thirdFragment = new TabFragment();
        firstFragment.setText(getResources().getString(R.string.tabtitle));
        firstFragment.setIcon(R.drawable.usual_task_icon);
        secondFragment.setText(getResources().getString(R.string.tabtitle_urgent));
        secondFragment.setIcon(R.drawable.urgent_task_icon);
        thirdFragment.setText(getResources().getString(R.string.tabtitle_important));
        thirdFragment.setIcon(R.drawable.important_task_icon);

        EasyTabsBuilder.with(easyTabs).addTabs(
                new TabItem(firstFragment, firstFragment.getTitleText()),
                new TabItem(secondFragment, secondFragment.getTitleText()),
                new TabItem(thirdFragment, thirdFragment.getTitleText())
        ).setTabsBackgroundColor(getResources().getColor(R.color.colorPrimary))
                .setIndicatorColor(getResources().getColor(R.color.md_white_1000))
                .addIcons(
                firstFragment.getIcon(),
                secondFragment.getIcon(),
                thirdFragment.getIcon())
                .setIconFading(true)
                .hideAllTitles(true)
                .withListener(new TabsListener() {

                    @Override
                    public void onScreenPosition(int position) {
                        /*
                        getActivity().
                         */
                        selectTabEventListener.onSelectTabEvent(position);
                        Log.d("tag", String.valueOf(position));
                    }
                })
                .Build();
    }
}
