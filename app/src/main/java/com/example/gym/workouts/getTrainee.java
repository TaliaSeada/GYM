package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_recyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class getTrainee extends AppCompatActivity {
    //set global variable
    static String nameTR;
    // create list to save the content read from firebase
    private final List<String> items = new ArrayList<>();
    // create list to show only the names from content read from firebase
    private RecyclerView rview;
    private myAdapter radapter;
    private final ArrayList<Item> names = new ArrayList<Item>();
    private int dragged;
    // get firebase instance
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();


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
                i.putExtra("role", "trainer");
                startActivity(i);
                nameTR = items.get(position);
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
        db.collection("users").
                whereEqualTo("role", "trainee")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        names.clear();
                        items.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            names.add(new Item(snapshot.getString("full_name"), R.drawable.login));
                            items.add(snapshot.getId());
                        }
                        // set adapter
                        radapter.notifyDataSetChanged();
                        rview.setAdapter(radapter);

                    }
                });
    }

}
