package com.example.gym.homePage;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManagePrivateArea {
    protected FirebaseFunctions Functions = FirebaseFunctions.getInstance();


    public Task<HttpsCallableResult> getPersonalDetails(String email) {
        Map<String, Object> email_data = new HashMap<>();
        email_data.put("email", email);
        return Functions.getHttpsCallable("getPersonalDetails").call(email_data);
    }

    public Task<HttpsCallableResult> addDate(String email,String date) {
        Map<String, Object> date_map = new HashMap<>();
        date_map.put("email", email);
        date_map.put("date", date);
        return Functions.getHttpsCallable("addDate").call(date_map);
    }
    public Task<HttpsCallableResult> addDetails(String email, double height, double weight) {
        Map<String, Object> details_map = new HashMap<>();
        details_map.put("email", email);
        details_map.put("height", height);
        details_map.put("weight", weight);
        return Functions.getHttpsCallable("addDetails").call(details_map);
    }
    public Task<HttpsCallableResult> addGender(String email,String gender) {
        Map<String, Object> gender_map = new HashMap<>();
        gender_map.put("email", email);
        gender_map.put("gender", gender);
        return Functions.getHttpsCallable("addGender").call(gender_map);
    }
    public Task<HttpsCallableResult> getName(String email) {
        Map<String, Object> name_map = new HashMap<>();
        name_map.put("email", email);
        return Functions.getHttpsCallable("getName").call(name_map);
    }
}