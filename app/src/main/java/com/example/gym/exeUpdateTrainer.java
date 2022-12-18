package com.example.gym;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    TextView input_exe;
    EditText input_set;
    EditText input_weight;
    EditText input_reps;
    Button UPDATE;
    Button DELETE;
    String Gworkout;

    static Long reps;
    static Long sets;
    static double weight;

    private static final String TAG = "DBExercise";
    @SuppressLint("StaticFieldLeak")
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email = Objects.requireNonNull(AddWorkoutTrainer.nameTR);

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadContent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exe_update);
        UPDATE = findViewById(R.id.updateExe);

        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                double weight = Double.parseDouble(input_weight.getText().toString());
                int reps = Integer.parseInt(input_reps.getText().toString());
                int set = Integer.parseInt(input_set.getText().toString());
                try {
                    Gworkout = GroupWorkoutTrainer.nameTR;
                    UpdateExe(email, Gworkout, exercise, set, reps, weight);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                loadContent();
                finish();

            }
        });

        DELETE = findViewById(R.id.delete);

        DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                try {
                    Gworkout = GroupWorkoutTrainer.nameTR;
                    DeleteExe(email, Gworkout, exercise);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                loadContent();
                finish();

            }
        });

    }

    public void loadContent() {
        db.collection("user-info").document(Objects.requireNonNull(email))
                .collection("workouts").document(GroupWorkoutTrainer.nameTR)
                .collection("exercises").document(newScreanTrainer.nameExe).
                addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
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
                        input_reps = findViewById(R.id.reps);
                        input_reps.setText(reps + "");

                        input_set = findViewById(R.id.sets);
                        input_set.setText(sets + "");

                        input_weight = findViewById(R.id.weight);
                        input_weight.setText(weight + "");

                        input_exe = findViewById(R.id.titleExe);
                        input_exe.setText(newScreanTrainer.nameExe);
                    }


                });


    }

    public void UpdateExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg) {
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

    public void DeleteExe(String email, String wo_name, String exe_name) {
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

}
