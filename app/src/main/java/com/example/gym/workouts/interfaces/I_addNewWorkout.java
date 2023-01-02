package com.example.gym.workouts.interfaces;

public interface I_addNewWorkout {
    /***
     * this function adds new exercise to the trainee and updates the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we insert in the app
     * @param sets number of sets we insert in the app
     * @param reps number of repetition we insert in the app
     * @param weight_kg weight we insert in the app
     */
    void addExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg, String time);
    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    void makeToast(String s);

}
