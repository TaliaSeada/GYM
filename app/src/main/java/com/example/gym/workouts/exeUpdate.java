package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_updateExercise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class exeUpdate extends AppCompatActivity implements I_updateExercise {
    // set toast
    private Toast t;
    // set fields for data display
    private EditText input_time, input_unit;
    private TextView input_exe, title, input_set, input_weight, input_reps, workValueS, workValueR, workValueW;
    private String Gworkout;
    private int minteger_sets;
    private int minteger_reps;
    private double minteger_weight;
    private String time, unit;
    private Button DELETE;
    private Button UPDATE, DecreaseS, IncreaseS, DecreaseW, IncreaseW, DecreaseR, IncreaseR;
    protected FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    // get firebase instances
    private static final String TAG = "DBExercise";
    @SuppressLint("StaticFieldLeak")
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String email_trainer = getTrainee.nameTR;


    @SuppressLint({"MissingInflatedId", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_exe_update);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .8));

        String role = getIntent().getStringExtra("role");
        String email;
        if (role.equals("trainee")) {
            email = email_trainee;
        } else {
            email = email_trainer;
        }
        // load content from firebase
        loadContent(email);
        // set button
        UPDATE = findViewById(R.id.addWorkout);
        IncreaseS = findViewById(R.id.ButtonAddS);
        DecreaseS = findViewById(R.id.ButtonRemoveS);
        workValueS = findViewById(R.id.valueWorkoutS);

        IncreaseR = findViewById(R.id.ButtonAddR);
        DecreaseR = findViewById(R.id.ButtonRemoveR);
        workValueR = findViewById(R.id.valueWorkoutR);

        IncreaseW = findViewById(R.id.ButtonAddW);
        DecreaseW = findViewById(R.id.ButtonRemoveW);
        workValueW = findViewById(R.id.valueWorkoutW);

        IncreaseS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger_sets += 1;
                workValueS.setText(minteger_sets + "");
            }
        });
        DecreaseS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger_sets <= 0) {
                    Toast.makeText(getApplicationContext(), "Can't Decrease 0", Toast.LENGTH_SHORT).show();
                } else {
                    minteger_sets -= 1;
                    workValueS.setText(minteger_sets + "");
                }
            }
        });

        IncreaseR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger_reps += 1;
                workValueR.setText(minteger_reps + "");
            }
        });
        DecreaseR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger_reps <= 0) {
                    Toast.makeText(getApplicationContext(), "Can't Decrease 0", Toast.LENGTH_SHORT).show();
                } else {
                    minteger_reps -= 1;
                    workValueR.setText(minteger_reps + "");
                }
            }
        });

        IncreaseW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger_weight += 0.5;
                workValueW.setText(minteger_weight + "");
            }
        });
        DecreaseW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger_weight <= 0) {
                    Toast.makeText(getApplicationContext(), "Can't Decrease 0", Toast.LENGTH_SHORT).show();
                } else {
                    minteger_weight -= 0.5;
                    workValueW.setText(minteger_weight + "");
                }
            }
        });

        // set texts
        title = findViewById(R.id.AddExercise);
        input_exe = findViewById(R.id.NameExercise);
        input_set = findViewById(R.id.valueWorkoutS);
        input_reps = findViewById(R.id.valueWorkoutR);
        input_weight = findViewById(R.id.valueWorkoutW);
        input_time = findViewById(R.id.editTextTime);
        input_unit = findViewById(R.id.unit);


        // set ADD button action
        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                double weight = Double.parseDouble(input_weight.getText().toString());
                int reps = Integer.parseInt(input_reps.getText().toString());
                int set = Integer.parseInt(input_set.getText().toString());
                Gworkout = WorkoutList.nameTR;
                String time = input_time.getText().toString();
                String unit = input_unit.getText().toString();
                try {
                    // add the exercise to firebase
                    updateExe(email, Gworkout, exercise, set, reps, weight, time, unit);
                    makeToast(exercise + " Updated Successfully");
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

        DELETE = findViewById(R.id.delete1);
        DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                try {
                    Gworkout = WorkoutList.nameTR;
                    DeleteExe(email, Gworkout, exercise);
                    makeToast("Deleted Successfully");
                } catch (NullPointerException e) {
                    makeToast("Something Went Wrong");
                    e.printStackTrace();
                }
                loadContent(email);
                finish();

            }
        });

    }

    /***
     * this function updates an existing exercise to the trainee in the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we clicked in the app
     * @param sets number of sets we insert in the app
     * @param reps number of repetition we insert in the app
     * @param weight_kg weight we insert in the app
     */
    @Override
    public void updateExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg, String time, String unit) {
        // create exercise
        Map<String, Object> exe = new HashMap<>();
        exe.put("reps", reps);
        exe.put("sets", sets);
        exe.put("weight", weight_kg);
        exe.put("name", exe_name);
        exe.put("time", time);
        exe.put("unit", unit);
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

    /***
     * this function delete an exercise from the trainee in the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we clicked in the app
     */
    @Override
    public void DeleteExe(String email, String wo_name, String exe_name) {
        // delete from firebase
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name)
                .collection("exercises").document(exe_name).delete()
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
     * to the fields we created in order to show it in the app screen.
     ***/
    @Override
    public void loadContent(String email) {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("name_wo", WorkoutList.nameTR);
        data.put("name_exe", ExerciseList.nameExe);

        Task<HttpsCallableResult> exe = mFunctions.getHttpsCallable("getExercise").call(data);
        exe.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                    data.forEach(e -> {
                        minteger_reps = (int) e.get("reps");
                        minteger_sets =(int) e.get("sets");
                        Object tmp = e.get("weight");
                        assert tmp != null;
                        if(tmp.equals(0)){
                            minteger_weight = (double)((Integer)(tmp));
                        }
                        else{
                            minteger_weight = (double) tmp;
                        }
                        time = (String) e.get("time");
                        unit = (String) e.get("unit");
                    });
                } else {
                    Log.w("Get Exercises", task.getException());
                }

                // set the fields
                title = findViewById(R.id.AddExercise);
                input_exe = findViewById(R.id.NameExercise);
                input_set = findViewById(R.id.valueWorkoutS);
                input_reps = findViewById(R.id.valueWorkoutR);
                input_weight = findViewById(R.id.valueWorkoutW);
                input_time = findViewById(R.id.editTextTime);
                input_unit = findViewById(R.id.unit);

                input_set.setText(minteger_sets + "");
                input_reps.setText(minteger_reps + "");
                input_weight.setText(minteger_weight + "");
                input_exe.setText(ExerciseList.nameExe);
                input_time.setText(time);
                input_unit.setText(unit);
            }
        });
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
//                            minteger_reps = Math.toIntExact(snapshot.getLong("reps"));
//                            minteger_sets = Math.toIntExact(snapshot.getLong("sets"));
//                            minteger_weight = snapshot.getDouble("weight");
//                            time = snapshot.getString("time");
//                            unit = snapshot.getString("unit");
//                            Log.d(TAG, "Current data: " + snapshot.getData());
//                        } else {
//                            Log.d(TAG, "Current data: null");
//                        }
//
//                        // set the fields
//                        title = findViewById(R.id.AddExercise);
//                        input_exe = findViewById(R.id.NameExercise);
//                        input_set = findViewById(R.id.valueWorkoutS);
//                        input_reps = findViewById(R.id.valueWorkoutR);
//                        input_weight = findViewById(R.id.valueWorkoutW);
//                        input_time = findViewById(R.id.editTextTime);
//                        input_unit = findViewById(R.id.unit);
//
//                        input_set.setText(minteger_sets + "");
//                        input_reps.setText(minteger_reps + "");
//                        input_weight.setText(minteger_weight + "");
//                        input_exe.setText(ExerciseList.nameExe);
//                        input_time.setText(time);
//                        input_unit.setText(unit);
//                    }
//                });
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