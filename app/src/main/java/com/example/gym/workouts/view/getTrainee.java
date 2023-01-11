package com.example.gym.workouts.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_recyclerView;
import com.example.gym.workouts.controller.workoutController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class getTrainee extends AppCompatActivity {
    //set global variable
    // create list to save the content read from firebase
    private final List<String> items = new ArrayList<>();
    // create list to show only the names from content read from firebase
    private RecyclerView rview;
    private myAdapter radapter;
    private final ArrayList<Item> names = new ArrayList<Item>();
    private int dragged;
    // get firebase instance
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final workoutController workoutControllet = new workoutController();


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_trainee);
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        return makeMovementFlags(
                                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                ItemTouchHelper.END
                        );
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        dragged = viewHolder.getAbsoluteAdapterPosition();
                        int targetI = target.getAbsoluteAdapterPosition();
                        Collections.swap(names, dragged, targetI);
                        radapter.notifyItemMoved(dragged, targetI);
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                    }
                }
        );
        // set list adapter
        radapter = new myAdapter(getApplicationContext(), names, new I_recyclerView() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(getTrainee.this, WorkoutList.class);
                i.putExtra("key_ex", new String[]{"trainer", items.get(position)});
//                i.putExtra("role", "trainer");
                startActivity(i);
//                nameTR = items.get(position);
            }
        });
        rview = findViewById(R.id.recyclerView1);
        rview.setLayoutManager(new LinearLayoutManager(this));
        helper.attachToRecyclerView(rview);

        loadContent();
    }

    /***
     * this function load the relevant content from the firebase
     * to the lists we created, sets adapter to the 'names' list
     * in order to show it in the app screen.
     ***/
    public void loadContent() {
        Task<HttpsCallableResult> list = workoutControllet.trainees_content();
        list.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    names.clear();
                    items.clear();
                    ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                    data.forEach(t -> {
                        names.add(new Item((String) t.get("full_name"), R.drawable.login));
                        items.add((String) t.get("id"));
                    });
                    // set adapter
                    radapter.notifyDataSetChanged();
                    rview.setAdapter(radapter);
                } else {
                    Log.w("Get Trainees", task.getException());
                }
            }
        });
    }

}
