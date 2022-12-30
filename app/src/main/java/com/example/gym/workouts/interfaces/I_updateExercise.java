package com.example.gym.workouts.interfaces;

public interface I_updateExercise {
    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    void makeToast(String s);
    /***
     * this function delete an exercise from the trainee in the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we clicked in the app
     */
    void DeleteExe(String email, String wo_name, String exe_name);
    /***
     * this function updates an existing exercise to the trainee in the firebase
     * @param email trainee email
     * @param wo_name the workout name we clicked in the app
     * @param exe_name the exercise name we clicked in the app
     * @param sets number of sets we insert in the app
     * @param reps number of repetition we insert in the app
     * @param weight_kg weight we insert in the app
     */
    void updateExe(String email, String wo_name, String exe_name, int sets, int reps, double weight_kg);
    /***
     * this function load the relevant content from the firebase
     * to the fields we created in order to show it in the app screen.
     ***/
    void loadContent(String email);

}
