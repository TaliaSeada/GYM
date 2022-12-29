package com.example.gym.workouts.interfaces;

public interface I_workoutList {
    /***
     * this function adds new workout to the trainee and updates the firebase
     * @param email trainee email
     * @param wo_name the name we insert in the app
     */
    void addWO(String email, String wo_name);
    /***
     * this function load the relevant content from the firebase
     * to the list we created in order to show it in the app screen.
     ***/
    void loadContent(String email);
    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    void makeToast(String s);

}
