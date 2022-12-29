package com.example.gym.messages;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ManageMessages {
    private static final String TAG = "DBMessages";

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<Void> addMessage(String email, String message, String title) {

        Map<String, Object> message_map = new HashMap<>();
        message_map.put("trainee", email);
        message_map.put("trainer", "all");
        message_map.put("message", message);
        message_map.put("answer", "");
        message_map.put("title", title);
        message_map.put("date", Calendar.getInstance().getTime());
        return db.collection("message").document().set(message_map);
    }

    public Task<QuerySnapshot> getMessage(String email) {
        return db.collection("message")
                .whereEqualTo("trainee", email)
                .get();
    }
}