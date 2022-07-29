package com.fanuel.elevatordemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ElevatorButtonAdapter extends BaseAdapter {
    Context context;
    List<String> buttonList;
    LayoutInflater inflater;
    public ItemClicked itemClicked;

    interface ItemClicked{
        void OnItemClicked(int value);
    }
    public ElevatorButtonAdapter(Context context, List<String> buttonList){
        this.context = context;
        this.buttonList = buttonList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return buttonList.size();
    }

    @Override
    public Object getItem(int i) {
        return buttonList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.elevator_button_grid_item, null);
            TextView textView = view.findViewById(R.id.textView2);
            String key = buttonList.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClicked.OnItemClicked(Integer.parseInt(key));
                }
            });

            textView.setText(key);

        }
        return view;
    }
}
