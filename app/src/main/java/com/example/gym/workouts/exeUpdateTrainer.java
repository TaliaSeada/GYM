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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class exeUpdateTrainer extends AppCompatActivity {
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
    // get relevant trainee email
    String email = Objects.requireNonNull(getTrainee.nameTR);

    /***
     * the following functions are for the exercise creation and update
     * they give the user the ability to set the sets, repetitions and weight
     * @param view relevant button (plus or minus)
     */
    public void increaseInteger_sets(View view) {
        sets = sets + 1;
        display_sets(sets);
    }

    public void decreaseInteger_sets(View view) {
        sets = sets - 1;
        display_sets(sets);
    }

    private void display_sets(Long number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_sets);
        displayInteger.setText("" + number);
    }

    public void increaseInteger_reps(View view) {
        reps = reps + 1;
        display_reps(reps);
    }

    public void decreaseInteger_reps(View view) {
        reps = reps - 1;
        display_reps(reps);
    }

    private void display_reps(Long number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number_reps);
        displayInteger.setText("" + number);
    }

    public void increaseInteger_weight(View view) {
        weight = weight + 1;
        display_weight(weight);
    }

    public void decreaseInteger_weight(View view) {
        weight = weight - 1;
        display_weight(weight);
    }

    @SuppressLint("DefaultLocale")
    private void display_weight(double number) {
        TextView displayDouble = (TextView) findViewById(R.id.integer_number_weight);
        displayDouble.setText(String.format("%.1f", number));
    }

    public void increaseInteger_weight_(View view) {
        weight = weight + 0.1;
        display_weight(weight);
    }

    public void decreaseInteger_weight_(View view) {
        weight = weight - 0.1;
        display_weight(weight);
    }


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // load content from firebase
        loadContent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exe_update);

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
                    Gworkout = WorkoutListTrainer.nameTR;
                    UpdateExe(email, Gworkout, exercise, set, reps, weight);
                    makeToast("Updated Successfully");
                } catch (NullPointerException e) {
                    makeToast("Something Went Wrong");
                    e.printStackTrace();
                }
                loadContent();
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
                    Gworkout = WorkoutListTrainer.nameTR;
                    DeleteExe(email, Gworkout, exercise);
                    makeToast("Deleted Successfully");
                } catch (NullPointerException e) {
                    makeToast("Something Went Wrong");
                    e.printStackTrace();
                }
                loadContent();
                finish();

            }
        });

    }

    /***
     * this function load the relevant content from the firebase
     * to the fields we created in order to show it in the app screen.
     ***/
    public void loadContent() {
        db.collection("user-info").document(Objects.requireNonNull(email))
                .collection("workouts").document(WorkoutListTrainer.nameTR)
                .collection("exercises").document(ExerciseListTrainer.nameExe).
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
                        input_exe.setText(ExerciseListTrainer.nameExe);
                    }
                });
    }

    /***
     * this function updates an existing exercise to the relevant trainee in the firebase
     * @param email relevant trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we clicked in the app
     * @param sets number of sets we insert in the app
     * @param reps number of repetition we insert in the app
     * @param weight_kg weight we insert in the app
     */
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
     * this function delete an exercise from the relevant trainee in the firebase
     * @param email relevant trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we clicked in the app
     */
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
    private void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
}
