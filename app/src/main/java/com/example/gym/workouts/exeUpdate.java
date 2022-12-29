package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_updateExercise;
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

public class exeUpdate extends AppCompatActivity implements I_updateExercise {
    // set toast
    Toast t;
    // set fields for data display
    TextView input_exe;
    AppCompatTextView input_set;
    AppCompatTextView input_weight;
    AppCompatTextView input_reps;
    String Gworkout;
    Long reps;
    Long sets;
    double weight;
    // set buttons for updating and deleting data in firebase
    Button UPDATE;
    Button DELETE;
    // get firebase instances
    private static final String TAG = "DBExercise";
    @SuppressLint("StaticFieldLeak")
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String email_trainer = getTrainee.nameTR;

    /***
     * the following functions are for the exercise creation and update
     * they give the user the ability to set the sets, repetitions and weight
     * @param view relevant button (plus or minus)
     */
    @Override
    public void increaseInteger_sets(View view) {
        sets = sets + 1;
        display_sets(sets);
    }
    @Override
    public void decreaseInteger_sets(View view) {
        sets = sets - 1;
        display_sets(sets);
    }
    @Override
    public void display_sets(Long number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_sets);
        displayInteger.setText("" + number);
    }
    @Override
    public void increaseInteger_reps(View view) {
        reps = reps + 1;
        display_reps(reps);
    }
    @Override
    public void decreaseInteger_reps(View view) {
        reps = reps - 1;
        display_reps(reps);
    }
    @Override
    public void display_reps(Long number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_reps);
        displayInteger.setText("" + number);
    }
    @Override
    public void increaseInteger_weight(View view) {
        weight = weight + 1;
        display_weight(weight);
    }
    @Override
    public void decreaseInteger_weight(View view) {
        weight = weight - 1;
        display_weight(weight);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void display_weight(double number) {
        TextView displayDouble = (TextView) findViewById(R.id.integer_number_weight);
        displayDouble.setText(String.format("%.1f", number));
    }
    @Override
    public void increaseInteger_weight_(View view) {
        weight = weight + 0.1;
        display_weight(weight);
    }
    @Override
    public void decreaseInteger_weight_(View view) {
        weight = weight - 0.1;
        display_weight(weight);
    }


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exe_update);
        String role = getIntent().getStringExtra("role");
        String email;
        if(role.equals("trainee")){
            email = email_trainee;
        }
        else {
            email = email_trainer;
        }
        // load content from firebase
        loadContent(email);

        // set UPDATE button action
        UPDATE = findViewById(R.id.updateExe);
        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                double weight = Double.parseDouble(input_weight.getText().toString());
                int reps = Integer.parseInt(input_reps.getText().toString());
                int set = Integer.parseInt(input_set.getText().toString());
                try {
                    Gworkout = WorkoutList.nameTR;
                    UpdateExe(email, Gworkout, exercise, set, reps, weight);
                    makeToast("Updated Successfully");
                } catch (NullPointerException e) {
                    makeToast("Something Went Wrong");
                    e.printStackTrace();
                }
                loadContent(email);
                finish();

            }
        });
        // set DELETE button action
        DELETE = findViewById(R.id.delete);
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
     * this function load the relevant content from the firebase
     * to the fields we created in order to show it in the app screen.
     ***/
    @Override
    public void loadContent(String email) {
        db.collection("user-info").document(email)
                .collection("workouts").document(WorkoutList.nameTR)
                .collection("exercises").document(ExerciseList.nameExe).
                addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                    @SuppressLint({"SetTextI18n", "WrongViewCast"})
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            reps = snapshot.getLong("reps");
                            sets = snapshot.getLong("sets");
                            weight = snapshot.getDouble("weight");
                            Log.d(TAG, "Current data: " + snapshot.getData());
                        } else {
                            Log.d(TAG, "Current data: null");
                        }

                        // set the fields
                        input_reps = findViewById(R.id.integer_number_reps);
                        input_reps.setText(reps + "");

                        input_set = findViewById(R.id.integer_number_sets);
                        input_set.setText(sets + "");

                        input_weight = findViewById(R.id.integer_number_weight);
                        input_weight.setText(weight + "");

                        input_exe = findViewById(R.id.titleExe);
                        input_exe.setText(ExerciseList.nameExe);
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
    public void UpdateExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg) {
        // create exercise
        Map<String, Object> exe = new HashMap<>();
        exe.put("reps", reps);
        exe.put("sets", sets);
        exe.put("weight", weight_kg);
        exe.put("name", exe_name);
        Map<String, Object> name = new HashMap<>();
        name.put("name", wo_name);
        // set name
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
