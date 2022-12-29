package com.example.gym.workouts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;

public class myViewHolder extends RecyclerView.ViewHolder {

    ImageView itemImage;
    TextView itemName;

    public myViewHolder(@NonNull View itemView) {
        super(itemView);
        itemImage = itemView.findViewById(R.id.item_image);
        itemName = itemView.findViewById(R.id.item_text);
    }
}
