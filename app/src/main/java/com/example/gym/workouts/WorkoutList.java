package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_workoutList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorkoutList extends AppCompatActivity implements I_workoutList {
    // set global variable
    static String nameTR;
    // set toast
    private Toast t;
    // set fields for data display
    private EditText input;
    private ImageView add;
    private RecyclerView rview;
    private myAdapter radapter;
    private final ArrayList<String> items = new ArrayList<String>();
    private final ArrayList<Item> ritems = new ArrayList<Item>();
    // get firebase instances
    private static final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String email_trainer = getTrainee.nameTR;
    private int dragged;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        String role = getIntent().getStringExtra("role");
        String email;
        if(role.equals("trainee")){
            email = email_trainee;
        }
        else {
            email = email_trainer;
        }

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        return makeMovementFlags(
                                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                               ItemTouchHelper.END | ItemTouchHelper.START
                        );
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        dragged = viewHolder.getAbsoluteAdapterPosition();
                        int targetI = target.getAbsoluteAdapterPosition();
                        Collections.swap(ritems, dragged, targetI);
                        radapter.notifyItemMoved(dragged, targetI);
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        switch (direction){
                            case ItemTouchHelper.START:
                                Intent i = new Intent(WorkoutList.this, ExerciseList.class);
                                i.putExtra("role", role);
                                startActivity(i);
                                nameTR = ritems.get(viewHolder.getAbsoluteAdapterPosition()).getName();
                                break;
                            case ItemTouchHelper.END:
                                db.collection("user-info").document(email).collection("workouts")
                                        .document(ritems.get(viewHolder.getAbsoluteAdapterPosition()).getName()).delete();
                                radapter.notifyItemRemoved(viewHolder.getAbsoluteAdapterPosition());
                                makeToast("Item Removed");
                                break;
                        }
                    }
                }
        );
        // set list adapter
        radapter = new myAdapter(getApplicationContext(), ritems);
        rview = findViewById(R.id.recyclerView);
        rview.setLayoutManager(new LinearLayoutManager(this));
        helper.attachToRecyclerView(rview);

        // get text
        input = findViewById(R.id.Input);

        // load content from firebase
        loadContent(email);

        // set add button action
        add = findViewById(R.id.imageMenu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String text = input.getText().toString();
                    addWO(email, text);
                    input.setText("");
                    makeToast(text + " Added Successfully");
                    // reload content to show the new workout
                    loadContent(email);
                } catch (Exception e) {
                    makeToast("Type Workout Name");
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * this function adds new workout to the trainee and updates the firebase
     * @param email trainee email
     * @param wo_name the name we insert in the app
     */
    @Override
    public void addWO(String email, String wo_name) {
        // create workout
        Map<String, Object> name = new HashMap<>();
        name.put("name", wo_name);
        // set in firebase
        db.collection("user-info").document(Objects.requireNonNull(email))
                .collection("workouts").document(wo_name).set(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /***
     * this function load the relevant content from the firebase
     * to the list we created in order to show it in the app screen.
     ***/
    @Override
    public void loadContent(String email) {
        db.collection("user-info").document(email)
                .collection("workouts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        items.clear();
                        ritems.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            items.add(snapshot.getString("name"));
                            ritems.add(new Item(snapshot.getString("name"), R.drawable.im));
                        }
//                        adapter.notifyDataSetChanged();
//                        listView.setAdapter(adapter);
                        radapter.notifyDataSetChanged();
                        rview.setAdapter(radapter);
                    }
                });
    }

    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    @Override
    public void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
}