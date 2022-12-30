package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_addNewWorkout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.util.HashMap;
import java.util.Map;

public class AddNewWorkout extends AppCompatActivity implements I_addNewWorkout {
    // set toast
    private Toast t;
    // set fields for data display
    private EditText input_exe;
    private TextView  input_set,input_weight,input_reps, workValueS,workValueR,workValueW;
    private int integer_sets;
    private int integer_reps;
    private double double_weight ,weight;
    private Long reps,sets;

    // set button for adding new data to firebase
    private Button START_NOW,RemoveS,AddS,RemoveW,AddW ,RemoveR,AddR  ;
    // get firebase instances
    private static final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    // get user email
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String email_trainer = getTrainee.nameTR;
    private String Gworkout;


    /***
     * this function adds new exercise to the trainee and updates the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we insert in the app
     * @param sets number of sets we insert in the app
     * @param reps number of repetition we insert in the app
     * @param weight_kg weight we insert in the app
     *
     */


//    @Override
    public void addExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg) {
        // create exercise
        Map<String, Object> exe = new HashMap<>();
        exe.put("reps", reps);
        exe.put("sets", sets);
        exe.put("weight", weight_kg);
        exe.put("name", exe_name);
        Map<String, Object> name = new HashMap<>();
        name.put("name", wo_name);
        // if the workout is new insert it first
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name).set(name);
        // set in firebase
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name)
                .collection("exercises").document(exe_name).set(exe)
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


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_excrcise);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.9), (int) (height*.8));

        String role = getIntent().getStringExtra("role");
        String email;
        if(role.equals("trainee")){
            email = email_trainee;
        }
        else {
            email = email_trainer;
        }
        // load content from firebase
//        loadContent(email);
        // set button
        START_NOW = findViewById(R.id.addWorkout);
        AddS = findViewById(R.id.ButtonAddS);
        RemoveS = findViewById(R.id.ButtonRemoveS);
        workValueS = findViewById(R.id.valueWorkoutS);

        AddR = findViewById(R.id.ButtonAddR);
        RemoveR = findViewById(R.id.ButtonRemoveR);
        workValueR = findViewById(R.id.valueWorkoutR);

        AddW = findViewById(R.id.ButtonAddW);
        RemoveW = findViewById(R.id.ButtonRemoveW);
        workValueW = findViewById(R.id.valueWorkoutW);

        AddS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integer_sets += 1;
                workValueS.setText(integer_sets + "");
            }
        });
        RemoveS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (integer_sets <= 0) {
                    Toast.makeText(getApplicationContext(), "Sorry!", Toast.LENGTH_SHORT).show();
                } else {
                    integer_sets -= 1;
                    workValueS.setText(integer_sets + "");
                }
            }
        });


//Repetition
        AddR = findViewById(R.id.ButtonAddR);
        RemoveR = findViewById(R.id.ButtonRemoveR);
        workValueR = findViewById(R.id.valueWorkoutR);

        AddR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integer_reps += 1;
                workValueR.setText(integer_reps + "");
            }
        });
        RemoveR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (integer_reps <= 0) {
                    Toast.makeText(getApplicationContext(), "Sorry!", Toast.LENGTH_SHORT).show();
                } else {
                    integer_reps -= 1;
                    workValueR.setText(integer_reps + "");
                }
            }
        });


        AddW = findViewById(R.id.ButtonAddW);
        RemoveW = findViewById(R.id.ButtonRemoveW);
        workValueW = findViewById(R.id.valueWorkoutW);

        AddW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double_weight += 0.5;
                workValueW.setText(double_weight + "");
            }
        });
        RemoveW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (double_weight <= 0) {
                    Toast.makeText(getApplicationContext(), "Sorry!", Toast.LENGTH_SHORT).show();
                } else {
                    double_weight -= 0.5;
                    workValueW.setText(double_weight + "");
                }
            }
        });

        // set texts
        input_exe = findViewById(R.id.NameExercise);
        input_set = findViewById(R.id.valueWorkoutS);
        input_reps = findViewById(R.id.valueWorkoutR);
        input_weight = findViewById(R.id.valueWorkoutW);


        // set ADD button action
        START_NOW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                double weight = Double.parseDouble(input_weight.getText().toString());
                int reps = Integer.parseInt(input_reps.getText().toString());
                int set = Integer.parseInt(input_set.getText().toString());
                Gworkout = WorkoutList.nameTR;
                try {
                    // add the exercise to firebase
                    addExe(email, Gworkout, exercise, set, reps, weight);
                    makeToast(exercise + " Added Successfully");
                } catch (Exception e) {
                    if (exercise.equals("")) {
                        makeToast("Type Exercise Name");
                    } else {
                        makeToast("Something Went Wrong");
                    }

                    e.printStackTrace();
                }
                // close the window
                finish();

            }
        });

    }

//    public void loadContent(String email) {
//        db.collection("user-info").document(email)
//                .collection("workouts").document(WorkoutList.nameTR)
//                .collection("exercises").document(ExerciseList.nameExe).
//                addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
//                    @SuppressLint({"SetTextI18n", "WrongViewCast"})
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
//                            return;
//                        }
//
//                        if (snapshot != null && snapshot.exists()) {
//                            reps = snapshot.getLong("reps");
//                            sets = snapshot.getLong("sets");
//                            weight = snapshot.getDouble("weight");
//                            Log.d(TAG, "Current data: " + snapshot.getData());
//                        } else {
//                            Log.d(TAG, "Current data: null");
//                        }
//
//                        // set the fields
//                        input_exe = findViewById(R.id.NameExercise);
//                        input_set = findViewById(R.id.valueWorkoutS);
//                        input_reps = findViewById(R.id.valueWorkoutR);
//                        input_weight = findViewById(R.id.valueWorkoutW);
//
//                        input_set.setText(sets + "");
//                        input_reps.setText(reps + "");
//                        input_weight.setText(weight + "");
//                        input_exe.setText(ExerciseList.nameExe);
//                    }
//                });
//    }

    //Extracts the data from Firebase to activity_private_area.xml


    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
//    @Override
    public void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
    }