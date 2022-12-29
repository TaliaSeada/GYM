package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_addNewWorkout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewWorkout extends AppCompatActivity implements I_addNewWorkout {
    // set toast
    private Toast t;
    // set fields for data display
    private EditText input_exe;
    private AppCompatTextView input_set;
    private AppCompatTextView input_weight;
    private AppCompatTextView input_reps;
    private String Gworkout;
    private int minteger_sets;
    private int minteger_reps;
    private double minteger_weight;
    // set button for adding new data to firebase
    private Button ADD;
    // get firebase instances
    private static final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    // get user email
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String email_trainer = getTrainee.nameTR;

    /***
     * this function adds new exercise to the trainee and updates the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we insert in the app
     * @param sets number of sets we insert in the app
     * @param reps number of repetition we insert in the app
     * @param weight_kg weight we insert in the app
     */
    @Override
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

    /***
     * the following functions are for the exercise creation and update
     * they give the user the ability to set the sets, repetitions and weight
     * @param view relevant button (plus or minus)
     */
    @Override
    public void increaseInteger_sets(View view) {
        minteger_sets = minteger_sets + 1;
        display_sets(minteger_sets);
    }
    @Override
    public void decreaseInteger_sets(View view) {
        minteger_sets = minteger_sets - 1;
        display_sets(minteger_sets);
    }
    @Override
    public void display_sets(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_sets);
        displayInteger.setText("" + number);
    }
    @Override
    public void increaseInteger_reps(View view) {
        minteger_reps = minteger_reps + 1;
        display_reps(minteger_reps);
    }
    @Override
    public void decreaseInteger_reps(View view) {
        minteger_reps = minteger_reps - 1;
        display_reps(minteger_reps);
    }
    @Override
    public void display_reps(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_reps);
        displayInteger.setText("" + number);
    }
    @Override
    public void increaseInteger_weight(View view) {
        minteger_weight = minteger_weight + 1;
        display_weight(minteger_weight);
    }
    @Override
    public void decreaseInteger_weight(View view) {
        minteger_weight = minteger_weight - 1;
        display_weight(minteger_weight);
    }
    @Override
    @SuppressLint("DefaultLocale")
    public void display_weight(double number) {
        TextView displayDouble = (TextView) findViewById(R.id.integer_number_weight);
        displayDouble.setText(String.format("%.1f", number));
    }
    @Override
    public void increaseInteger_weight_(View view) {
        minteger_weight = minteger_weight + 0.1;
        display_weight(minteger_weight);
    }
    @Override
    public void decreaseInteger_weight_(View view) {
        minteger_weight = minteger_weight - 0.1;
        display_weight(minteger_weight);
    }


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_workout_trainee);
        String role = getIntent().getStringExtra("role");
        String email;
        if(role.equals("trainee")){
            email = email_trainee;
        }
        else {
            email = email_trainer;
        }
        // set button
        ADD = findViewById(R.id.addWorkout);
        // set texts
        input_reps = findViewById(R.id.integer_number_reps);
        input_set = findViewById(R.id.integer_number_sets);
        input_weight = findViewById(R.id.integer_number_weight);
        input_exe = findViewById(R.id.editWorkout);
        minteger_sets = 0;
        minteger_reps = 0;
        minteger_weight = 0.0;

        // set ADD button action
        ADD.setOnClickListener(new View.OnClickListener() {
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
                    if(exercise.equals("")){
                        makeToast("Type Exercise Name");
                    }
                    else{
                        makeToast("Something Went Wrong");
                    }

                    e.printStackTrace();
                }
                // close the window
                finish();

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