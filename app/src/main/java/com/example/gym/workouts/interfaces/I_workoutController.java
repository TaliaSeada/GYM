package com.example.gym.workouts.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

public interface I_workoutController {
    Task<HttpsCallableResult> deleteWorkout(String email, String wo_name);
    Task<HttpsCallableResult> addWorkout(String email, String wo_name);
}
