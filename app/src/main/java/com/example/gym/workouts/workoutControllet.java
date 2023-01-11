package com.example.gym.workouts;

import com.example.gym.workouts.interfaces.I_workoutController;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;


public class workoutControllet implements I_workoutController {
    protected FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    private final String TAG = "DBWorkOut";

    // workout list
    @Override
    public Task<HttpsCallableResult> deleteWorkout(String email, String wo_name){
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name_wo", wo_name);

        // delete from firebase
        return mFunctions.getHttpsCallable("deleteWorkout").call(data);
    }

    @Override
    public Task<HttpsCallableResult> addWorkout(String email, String wo_name){
        Map<String, Object> name = new HashMap<>();
        name.put("name", wo_name);

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name_wo", wo_name);
        data.put("name", name);

        return mFunctions.getHttpsCallable("createWorkout").call(data);
    }
}
