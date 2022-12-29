package com.example.gym.workouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_recyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myViewHolder> {
    I_recyclerView itemClick;
    Context context;
    List<Item> items;

    public myAdapter(Context context, List<Item> items, I_recyclerView itemClick) {
        this.context = context;
        this.items = items;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.itemName.setText(items.get(position).getName());
        holder.itemImage.setImageResource(items.get(position).getImage());

        holder.itemView.setOnClickListener(view -> {
            itemClick.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
