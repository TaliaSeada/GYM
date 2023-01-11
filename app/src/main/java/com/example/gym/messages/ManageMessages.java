package com.example.gym.messages;

import com.example.gym.messages.interfaces.I_ManageMessages;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageMessages implements I_ManageMessages {
    protected FirebaseFunctions Functions = FirebaseFunctions.getInstance();
    public Task<HttpsCallableResult> addMessageTrainee(String email, String message, String title) {
        Map<String, Object> message_map = new HashMap<>();
        message_map.put("trainee", email);
        message_map.put("trainer", "all");
        message_map.put("message", message);
        message_map.put("answer", "");
        message_map.put("title", title);
        Date date =  Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(date.getTime());
        message_map.put("date", strDate);
        HashMap<String, Object> data = new HashMap<>();
        data.put("mess", message_map);

        return Functions.getHttpsCallable("addMessageTrainee").call(data);
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