package com.example.gym.workouts.controller;

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

    // new and update exercise
    @Override
    public Task<HttpsCallableResult> createExercise(Map<String, Object> exe, String email, String wo_name, String exe_name){
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name_wo", wo_name);
        data.put("name_exe", exe_name);
        data.put("exe", exe);

        return mFunctions.getHttpsCallable("createExercise").call(data);
    }

    // delete exercise
    @Override
    public Task<HttpsCallableResult> deleteExercise( String email, String wo_name, String exe_name){
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name_wo", wo_name);
        data.put("name_exe", exe_name);

        return mFunctions.getHttpsCallable("deleteExercise").call(data);
    }


    // load content
    @Override
    public Task<HttpsCallableResult> exercises_content(String email, String nameTR){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("name", nameTR);
        return mFunctions.getHttpsCallable("getExercisesList").call(data);
    }

    @Override
    public Task<HttpsCallableResult> exercise_content(String email, String nameTR, String nameExe){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("name_wo", nameTR);
        data.put("name_exe", nameExe);

        return mFunctions.getHttpsCallable("getExercise").call(data);
    }

    @Override
    public Task<HttpsCallableResult> workouts_content(String email){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);

        return mFunctions.getHttpsCallable("getWorkoutList").call(data);
    }

    @Override
    public Task<HttpsCallableResult> trainees_content(){
        return mFunctions.getHttpsCallable("getTraineeList").call();
    }
}
