package com.example.gym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class WorkoutList extends AppCompatActivity {
    // set toast
    Toast t;
    // set fields for data display
    EditText input;
    ImageView add;
    static GridView listView;
    static ListViewGroupW adapter;
    static String nameTR;
    private static final ArrayList<String> items = new ArrayList<String>();
    // get firebase instances
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        // set list adapter
        adapter = new ListViewGroupW(WorkoutList.this, items);
        listView = findViewById(R.id.grid_workout);
        // get text
        input = findViewById(R.id.Input);

        // load content from firebase
        loadContent();

        // set action for the workouts list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(WorkoutList.this, ExerciseList.class);
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
                    AddNewWorkoutTrainee.addWO(user.getEmail(), text);
                    input.setText("");
                    makeToast(text + " Added Successfully");
                    // reload content to show the new workout
                    loadContent();
                } catch (Exception e) {
                    makeToast("Type Workout Name");
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * this function load the relevant content from the firebase
     * to the list we created in order to show it in the app screen.
     ***/
    public void loadContent() {
        String email = Objects.requireNonNull(user.getEmail());
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
    public static void removeItem(int remove) {
        try {
            db.collection("user-info").document(Objects.requireNonNull(user.getEmail()))
                    .collection("workouts").document(items.get(remove)).delete();
            listView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    private void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
}