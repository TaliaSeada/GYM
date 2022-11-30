package com.example.gym;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MAddNewWorkout extends AppCompatActivity {
    private static final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void removeWorkOut(String email, String wo_name){
        // create exercise
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name).delete();
    }

    public void removeExe(String email, String wo_name, String exe_name){
        // create exercise
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name)
                .collection("exercises").document(exe_name).delete();
    }

    public void addExe(String email, String wo_name, String exe_name, int sets, int rep, double weight_kg){
        // create exercise
        WorkOut.exercise exe = new WorkOut.exercise(exe_name, sets, rep, weight_kg);
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name)
                .collection("exercises").document(exe_name).set(exe).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    public void addWorkOut(String email, String wo_name){
        // create work out
        WorkOut wo = new WorkOut();
        db.collection("user-info").document(email)
                .collection("workouts").document(wo_name)
                .collection("exercises").add(wo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_add_workout);

    }
}