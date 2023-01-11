package com.example.gym.updates;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

// This class have all the actions we can do to update

public class UpdateController {

    protected FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    // This function is for the manager. He adds update to the db by id date and content
    public Task<HttpsCallableResult> createUpdate(Update newUpdate){//String date, String content) {
        // Create a new update
        Map<String, Object> update = new HashMap<>();
        update.put("date", newUpdate.date.getTime());
        update.put("content", newUpdate.content);

        return mFunctions.getHttpsCallable("createUpdate").call(update);
    }

    // Delete update by id
    public Task<HttpsCallableResult> deleteUpdate(String id) {
        HashMap<String, String> data = new HashMap<>();
        data.put("id", id);
        return mFunctions.getHttpsCallable("deleteUpdate").call(data);
    }
    public Task<HttpsCallableResult> getUpdates() {
        return mFunctions.getHttpsCallable("getUpdates").call();
    }

}
