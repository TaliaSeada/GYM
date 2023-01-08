package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_exerciseList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseList extends AppCompatActivity implements I_exerciseList {
    // set global variable
    static String nameExe;
    // set toast
    private Toast t;
    // set fields for data display
    private GridView listView;
    private ImageView add;
    private List<String> items = new ArrayList<>();
    private final ArrayList<Map<String, String>> show = new ArrayList<Map<String, String>>();
    private SimpleAdapter adap;
    // get firebase instances
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String email_trainer = getTrainee.nameTR;
    protected FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);
        String role = getIntent().getStringExtra("role");
        String email;
        if (role.equals("trainee")) {
            email = email_trainee;
        } else {
            email = email_trainer;
        }
        // load content from firebase
        loadContent(email);
        // set the exercises list
        listView = findViewById(R.id.grid_exe);
        items = new ArrayList<>();
        loadContent(email_trainee);
        // set add button action
        add = findViewById(R.id.imageMenu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddNewWorkout.class);
                i.putExtra("role", role);
                startActivity(i);
            }
        });

        // set action for the exercises list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(ExerciseList.this, exeUpdate.class);
                i.putExtra("role", role);
                startActivity(i);
                nameExe = items.get(pos);
            }
        });
    }

    /***
     * this function load the relevant content from the firebase
     * to the lists we created in order to show it in the app screen.
     ***/
    @Override
    public void loadContent(String email) {
//        db.collection("user-info").document(email)
//                .collection("workouts").document(WorkoutList.nameTR)
//                .collection("exercises").
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("name", WorkoutList.nameTR);

        Task<HttpsCallableResult> exe = mFunctions.getHttpsCallable("getExercisesList").call(data);
        exe.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    items.clear();
                    show.clear();
                    ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        data.forEach(e -> {
                            final Map<String, String> ex = new HashMap<>();
                            items.add((String) e.get("name"));
                            String name = (String) e.get("name");
                            int reps = (int) e.get("reps");
                            int sets = (int) e.get("sets");
                            Object weight = e.get("weight");
                            String time = (String) e.get("time");
                            String unit = (String) e.get("unit");
                            ex.put("exercise", name);
                            ex.put("reps", "Repetition: " + reps);
                            ex.put("sets", "Sets: " + sets);
                            ex.put("weight", "Weight: " + weight + " kg");
                            ex.put("time", "Time: " + time + " " + unit);
                            show.add(ex);
                        });
                    }
                    String[] from = {"exercise", "reps", "sets", "weight", "time"};
                    int[] to = {R.id.exe, R.id.reps_e, R.id.sets_e, R.id.weight_e, R.id.time_e};
                    adap = new SimpleAdapter(ExerciseList.this, show, R.layout.exe_item, from, to);
                    listView.setAdapter(adap);
                }
                else{
                    Log.w("Get Exercises", task.getException());
                }
            }
        });
//                addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                items.clear();
//                show.clear();
//                for (DocumentSnapshot snapshot : documentSnapshots) {
//                    final Map<String, String> ex = new HashMap<>();
//                    items.add(snapshot.getString("name"));
//                    String name = snapshot.getString("name");
//                    Long reps = snapshot.getLong("reps");
//                    Long sets = snapshot.getLong("sets");
//                    double weight = snapshot.getDouble("weight");
//                    String time = snapshot.getString("time");
//                    String unit = snapshot.getString("unit");
////                            exe_object eo = new exe_object(name, reps, sets, weight);
//                    ex.put("exercise", name);
//                    ex.put("reps", "Repetition: " + String.valueOf(reps));
//                    ex.put("sets", "Sets: " + String.valueOf(sets));
//                    ex.put("weight", "Weight: " + String.valueOf(weight) + " kg");
//                    ex.put("time", "Time: " + time + " " + unit);
//                    show.add(ex);
//                }
//                String[] from = {"exercise", "reps", "sets", "weight", "time"};
//                int[] to = {R.id.exe, R.id.reps_e, R.id.sets_e, R.id.weight_e, R.id.time_e};
////                        adap = new SimpleAdapter(ExerciseList.this, show, R.layout.grid_layout, from, to);
//                adap = new SimpleAdapter(ExerciseList.this, show, R.layout.exe_item, from, to);
//                listView.setAdapter(adap);
//            }
//        });
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