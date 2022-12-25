package com.example.gym;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddNewWorkoutTrainer extends AppCompatActivity {
    EditText input_exe;
    AppCompatTextView input_set;
    AppCompatTextView input_weight;
    AppCompatTextView input_reps;
    Button ADD;
    String Gworkout;
    String email = Objects.requireNonNull(AddWorkoutTrainer.nameTR);


    private static final String TAG = "DBWorkOut";
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg) {
        // create exercise
        Map<String, Object> exe = new HashMap<>();
        exe.put("reps", reps);
        exe.put("sets", sets);
        exe.put("weight", weight_kg);
        exe.put("name", exe_name);

        Map<String, Object> name = new HashMap<>();
        name.put("name", wo_name);

        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name).set(name);

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

    public void increaseInteger_sets(View view) {
        AddNewWorkoutTrainee.minteger_sets = AddNewWorkoutTrainee.minteger_sets + 1;
        display_sets(AddNewWorkoutTrainee.minteger_sets);
    }
    public void decreaseInteger_sets(View view) {
        AddNewWorkoutTrainee.minteger_sets = AddNewWorkoutTrainee.minteger_sets - 1;
        display_sets(AddNewWorkoutTrainee.minteger_sets);
    }
    private void display_sets(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_sets);
        displayInteger.setText("" + number);
    }
    public void increaseInteger_reps(View view) {
        AddNewWorkoutTrainee.minteger_reps = AddNewWorkoutTrainee.minteger_reps + 1;
        display_reps(AddNewWorkoutTrainee.minteger_reps);
    }
    public void decreaseInteger_reps(View view) {
        AddNewWorkoutTrainee.minteger_reps = AddNewWorkoutTrainee.minteger_reps - 1;
        display_reps(AddNewWorkoutTrainee.minteger_reps);
    }
    private void display_reps(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_reps);
        displayInteger.setText("" + number);
    }
    public void increaseInteger_weight(View view) {
        AddNewWorkoutTrainee.minteger_weight = AddNewWorkoutTrainee.minteger_weight + 1;
        display_weight(AddNewWorkoutTrainee.minteger_weight);
    }
    public void decreaseInteger_weight(View view) {
        AddNewWorkoutTrainee.minteger_weight = AddNewWorkoutTrainee.minteger_weight - 1;
        display_weight(AddNewWorkoutTrainee.minteger_weight);
    }
    @SuppressLint("DefaultLocale")
    private void display_weight(double number) {
        TextView displayDouble = (TextView) findViewById(R.id.integer_number_weight);
        displayDouble.setText(String.format("%.1f" ,number));
    }
    public void increaseInteger_weight_(View view) {
        AddNewWorkoutTrainee.minteger_weight = AddNewWorkoutTrainee.minteger_weight + 0.1;
        display_weight(AddNewWorkoutTrainee.minteger_weight);
    }
    public void decreaseInteger_weight_(View view) {
        AddNewWorkoutTrainee.minteger_weight = AddNewWorkoutTrainee.minteger_weight - 0.1;
        display_weight(AddNewWorkoutTrainee.minteger_weight);
    }


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_workout_trainee);
        ADD = findViewById(R.id.addWorkout);
        input_reps = findViewById(R.id.integer_number_reps);
        input_set = findViewById(R.id.integer_number_sets);
        input_weight = findViewById(R.id.integer_number_weight);
        input_exe = findViewById(R.id.editWorkout);
        AddNewWorkoutTrainee.minteger_sets = 0;
        AddNewWorkoutTrainee.minteger_reps = 0;
        AddNewWorkoutTrainee.minteger_weight = 0.0;


        ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                double weight = Double.parseDouble(input_weight.getText().toString());
                int reps = Integer.parseInt(input_reps.getText().toString());
                int set = Integer.parseInt(input_set.getText().toString());
                try{
                    Gworkout = GroupWorkoutTrainer.nameTR;
                    addExe(email, Gworkout, exercise, set, reps, weight);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                finish();

            }
        });

    }
}