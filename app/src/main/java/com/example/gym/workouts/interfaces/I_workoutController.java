package com.example.gym.workouts.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Map;

public interface I_workoutController {
    // workouts
    Task<HttpsCallableResult> deleteWorkout(String email, String wo_name);
    Task<HttpsCallableResult> addWorkout(String email, String wo_name);
    // exercises
    Task<HttpsCallableResult> createExercise(Map<String, Object> exe, String email, String wo_name, String exe_name);
    Task<HttpsCallableResult> deleteExercise( String email, String wo_name, String exe_name);
    // content
    Task<HttpsCallableResult> exercises_content(String email, String nameTR);
    Task<HttpsCallableResult> exercise_content(String email, String nameTR, String nameExe);
    Task<HttpsCallableResult> workouts_content(String email);
    Task<HttpsCallableResult> trainees_content();
}
