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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class exeUpdate extends AppCompatActivity {
    TextView input_exe;
    EditText input_set;
    EditText input_weight;
    EditText input_reps;
    Button UPDATE;
    static String email;
    String Gworkout;
    DocumentReference docRef;
    static double reps;
    static double sets;
    static double weight;

    private static final String TAG = "DBWorkOut";
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

    public void loadContent() {
        docRef = db.collection("user-info").document(Objects.requireNonNull(user.getEmail()))
                .collection("workouts").document(GroupWorkout.nameTR)
                .collection("exercises").document(newScrennW.nameExe);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    exeUpdate.reps = document.getDouble("reps");
                    exeUpdate.sets = document.getDouble("sets");
                    exeUpdate.weight = document.getDouble("weight");
                    System.out.println(reps);
                    System.out.println(sets);
                    System.out.println(weight);
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    @SuppressLint({"MissingInflatedId", "WrongViewCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exe_update);
        loadContent();
        email = user.getEmail();


        UPDATE = findViewById(R.id.updateExe);

        System.out.println(reps);
        System.out.println(sets);
        System.out.println(weight);

        input_reps = findViewById(R.id.reps);
        input_reps.setText(reps+"");

        input_set = findViewById(R.id.sets);
        input_set.setText(sets+"");

        input_weight = findViewById(R.id.weight);
        input_weight.setText(weight+"");

        input_exe = findViewById(R.id.titleExe);
        input_exe.setText(newScrennW.nameExe);




        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exercise = input_exe.getText().toString();
                double weight = Double.parseDouble(input_weight.getText().toString());
                int reps = Integer.parseInt(input_reps.getText().toString());
                int set = Integer.parseInt(input_set.getText().toString());
                try{
                    Gworkout = GroupWorkout.nameTR;
                    addExe(email, Gworkout, exercise, set, reps, weight);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                finish();

            }
        });
    }
}


// kk@gmail.com