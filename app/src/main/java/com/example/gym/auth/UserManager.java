package com.example.gym.auth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

// This class have all the actions we can do to user

public class UserManager {
    private static final String TAG = "DBUserManager";

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_TRAINER = "trainer";
    public static final String ROLE_TRAINEE = "trainee";


    // This function is for the manager. He adds user to the db by email address and role
    public Task<Void> createUser(String email, String role, String full_name) {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("role", role);
        user.put("full_name", full_name);

        email = email.toLowerCase();

        // Add a new document with user email ID
        return db.collection("users").document(email).set(user); //add new user to db
    }

    // Return the document= contain the role&full name, by email(id= email) from the collection
    public Task<DocumentSnapshot> getUserDoc(String email) {
        return db.collection("users").document(email).get();
    }

    // Return the email of the connected user from fb auth
    public String getConnectedUserMail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return "";
        }
        return user.getEmail();
    }

    // Delete user by email
    public Task<Void> deleteUser(String email) {
        return db.collection("users").document(email).delete();
    }

    // Get all the users that exist by specific role
    public Task<QuerySnapshot> getUsersByRole(String role) {
        return db.collection("users")
                .whereEqualTo("role", role)
                .get();
    }

}
