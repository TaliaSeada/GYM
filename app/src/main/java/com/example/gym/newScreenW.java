package com.example.gym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class newScreenW extends AppCompatActivity {
    // set toast
    Toast t;
    // set fields for data display
    static GridView listView;
    static String nameExe;
    ImageView add;
    private static List<String> items = new ArrayList<>();
    final ArrayList<Map<String, exe_object>> show = new ArrayList<Map<String, exe_object>>();
    SimpleAdapter adap;
    // get firebase instances
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_screen_w);
        // load content from firebase
        loadContent();
        // set the exercises list
        listView = findViewById(R.id.grid_exe);
        items = new ArrayList<>();

        // set add button action
        add = findViewById(R.id.imageMenu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddNewWorkoutTrainee.class));
            }
        });

        // set action for the exercises list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(newScreenW.this, exeUpdate.class);
                startActivity(i);
                nameExe = items.get(pos);
            }
        });
    }

    /***
     * this function load the relevant content from the firebase
     * to the lists we created in order to show it in the app screen.
     ***/
    public void loadContent() {
        db.collection("user-info").document(Objects.requireNonNull(user.getEmail()))
                .collection("workouts").document(GroupWorkout.nameTR)
                .collection("exercises")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        items.clear();
                        show.clear();
                        for(DocumentSnapshot snapshot : documentSnapshots){
                            final Map<String, exe_object> ex = new HashMap<>();
                            items.add(snapshot.getString("name"));
                            String name = snapshot.getString("name");
                            Long reps = snapshot.getLong("reps");
                            Long sets = snapshot.getLong("sets");
                            double weight = snapshot.getDouble("weight");
                            exe_object eo = new exe_object(name, reps, sets, weight);
                            ex.put("exercise", eo);
                            show.add(ex);
                        }
                        String[] from = {"exercise"};
                        int[] to = {R.id.exe};
                        adap = new SimpleAdapter(newScreenW.this, show, R.layout.grid_layout, from, to);
                        listView.setAdapter(adap);
                    }
                });
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