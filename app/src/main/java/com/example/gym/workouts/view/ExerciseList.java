package com.example.gym.workouts.view;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.gym.workouts.controller.workoutController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseList extends AppCompatActivity implements I_exerciseList {
    // set toast
    private Toast t;
    // set fields for data display
    private GridView listView;
    private ImageView add, refresh;
    private List<String> items = new ArrayList<>();
    private final ArrayList<Map<String, String>> show = new ArrayList<Map<String, String>>();
    private SimpleAdapter adap;
    // get firebase instances
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final workoutController workoutControllet = new workoutController();

    @Override
    protected void onResume() {
        super.onResume();
        Intent MessageIntent = getIntent();
        String[] MessageValue = MessageIntent.getStringArrayExtra("key_ex");
        String role = MessageValue[0];
        String email;
        if (role.equals("trainee")) {
            email = email_trainee;
        } else {
            email = MessageValue[1];
        }
        // load content from firebase
        loadContent(email,MessageValue[2]);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent MessageIntent = getIntent();
        String[] MessageValue = MessageIntent.getStringArrayExtra("key_ex");
        setContentView(R.layout.activity_exercise_list);
        String role = MessageValue[0];
        String email;
        if (role.equals("trainee")) {
            email = email_trainee;
        } else {
            email = MessageValue[1];
        }
        // load content from firebase
        loadContent(email,MessageValue[2]);
        // set the exercises list
        listView = findViewById(R.id.grid_exe);
        items = new ArrayList<>();
        // set refresh action
        refresh = findViewById(R.id.refresh2);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContent(email, MessageValue[2]);
            }
        });

        // set add button action
        add = findViewById(R.id.imageMenu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddNewExercise.class);
                i.putExtra("key_ex", new String[]{role,MessageValue[1] ,MessageValue[2]});
//                i.putExtra("role", role);
                startActivity(i);
            }
        });


        // set action for the exercises list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(ExerciseList.this, exeUpdate.class);
//                nameExe = items.get(pos);
                i.putExtra("key_ex", new String[]{role, items.get(pos), MessageValue[2], email});
//                i.putExtra("role", role);
                startActivity(i);
            }
        });
    }

    /***
     * this function load the relevant content from the firebase
     * to the lists we created in order to show it in the app screen.
     ***/
    @Override
    public void loadContent(String email, String nameTR) {
        Task<HttpsCallableResult> exe = workoutControllet.exercises_content(email, nameTR);
        exe.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    items.clear();
                    show.clear();
                    ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
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
                    String[] from = {"exercise", "reps", "sets", "weight", "time"};
                    int[] to = {R.id.exe, R.id.reps_e, R.id.sets_e, R.id.weight_e, R.id.time_e};
                    adap = new SimpleAdapter(ExerciseList.this, show, R.layout.exe_item, from, to);
                    listView.setAdapter(adap);
                } else {
                    Log.w("Get Exercises", task.getException());
                }
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