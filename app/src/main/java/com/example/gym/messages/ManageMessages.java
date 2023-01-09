package com.example.gym.messages;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ManageMessages {
    protected FirebaseFunctions Functions = FirebaseFunctions.getInstance();

    public Task<HttpsCallableResult> addMessage(String email, String message, String title) {
        Map<String, Object> message_map = new HashMap<>();
        message_map.put("trainee", email);
        message_map.put("trainer", "all");
        message_map.put("message", message);
        message_map.put("answer", "");
        message_map.put("title", title);
        message_map.put("date", Calendar.getInstance().getTime());
        return Functions.getHttpsCallable("addMessage").call(message_map);

//        return db.collection("message").document().set(message_map);
    }

    public Task<HttpsCallableResult> getMessageTrainee(String email) {
        Map<String, Object> email_data = new HashMap<>();
        email_data.put("email", email);
        return Functions.getHttpsCallable("getMessageListTrainee").call(email_data);
    }

    public Task<HttpsCallableResult> getMessageTrainer(String email) {
        Map<String, Object> email_data = new HashMap<>();
        email_data.put("email", email);
        return Functions.getHttpsCallable("getMessageListTrainer").call(email_data);
    }

    public Task<HttpsCallableResult> updateMessage(String id, String message, String email) {
        Map<String, Object> message_map = new HashMap<>();
        message_map.put("id", id);
        message_map.put("email", email);
        message_map.put("message", message);
        return Functions.getHttpsCallable("updateMessage").call(message_map);
    }
}