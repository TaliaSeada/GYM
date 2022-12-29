package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorkoutList extends AppCompatActivity implements I_workoutList {
    // set global variable
    static String nameTR;
    // set toast
    Toast t;
    // set fields for data display
    EditText input;
    ImageView add;
    GridView listView;
    ListViewGroupW adapter;
    private final ArrayList<String> items = new ArrayList<String>();
    // get firebase instances
    private static final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String email_trainer = getTrainee.nameTR;

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
        // set list adapter
        adapter = new ListViewGroupW(WorkoutList.this, items);
        listView = findViewById(R.id.grid_workout);
        // get text
        input = findViewById(R.id.Input);

        // load content from firebase
        loadContent(email);

        // set action for the workouts list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(WorkoutList.this, ExerciseList.class);
                i.putExtra("role", role);
                startActivity(i);
                nameTR = items.get(pos);
            }
        });

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
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        items.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            items.add(snapshot.getString("name"));
                        }
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                });
    }

    /***
     * this function removes an item from the display list and updates the firebase
     * @param remove the index of the item we clicked to remove
     */
//    public static void removeItem(int remove) {
//        try {
//            db.collection("user-info").document(Objects.requireNonNull(user.getEmail()))
//                    .collection("workouts").document(items.get(remove)).delete();
//            listView.setAdapter(adapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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