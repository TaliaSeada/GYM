package com.example.gym;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ListViewGroupW extends ArrayAdapter<String> {
    ArrayList<String> list;
    Context context;

    public ListViewGroupW(Context context, ArrayList<String> items) {
        super(context, R.layout.list_row, items);
        this.context = context;
        list = items;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @NonNull View converView, @NonNull ViewGroup parent) {
        if (converView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            converView = layoutInflater.inflate(R.layout.list_row, null);
            TextView number = converView.findViewById(R.id.number);
            number.setText(position + 1 + ".");
            TextView name = converView.findViewById(R.id.name);
            name.setText(list.get(position));
            ImageView remove = converView.findViewById(R.id.remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GroupWorkout.removeItem(position);
                }
            });

        }
        return converView;
    }
}
