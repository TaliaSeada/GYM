package com.example.gym.auth;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

// This class have all the actions we can do to user

public class UserController {
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_TRAINER = "trainer";
    public static final String ROLE_TRAINEE = "trainee";


    // This function is for the manager. He adds user to the db by email address full name and role
    public Task<HttpsCallableResult> createUser(String email, String role, String full_name) {
        // Create a new user with role, full name and email
        Map<String, Object> user = new HashMap<>();
        user.put("role", role);
        user.put("full_name", full_name);
        user.put("email", email);

        return mFunctions.getHttpsCallable("createUser").call(user);
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
    public Task<HttpsCallableResult> deleteUser(String email) {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        return mFunctions.getHttpsCallable("deleteUser").call(data);
    }

    // Get all the users that exist by specific role
    public Task<HttpsCallableResult> getUsersByRole(String role) {
        HashMap<String, String> data = new HashMap<>();
        data.put("role", role);
        return mFunctions.getHttpsCallable("getAllUsers").call(data);
    }

}
