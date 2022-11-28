package com.example.gym.auth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class UserManager {

    private static final String TAG = "DBUserManager";


    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This function if for the manager. He adds user to the db by email address and role
     */
    public void createUser(String email, String role) {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("role", role);

        // Add a new document with user email ID
        db.collection("users").document(email)
            .set(user) //add new user to db
            .addOnSuccessListener(new OnSuccessListener<Void>() { //what happened if the user added successfully
                @Override
                public void onSuccess(Void documentReference) {
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
    // Return the document=contain the role, by email(id= email) from the collection
    public Task<DocumentSnapshot> getUserDoc(String email) {
        DocumentReference u = db.collection("users").document(email);
        Task<DocumentSnapshot> t = u.get();
        return t;
    }
}
