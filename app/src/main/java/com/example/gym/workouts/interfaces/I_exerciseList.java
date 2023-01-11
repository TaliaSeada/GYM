package com.example.gym.workouts.interfaces;

public interface I_exerciseList {
    /***
     * this function load the relevant content from the firebase
     * to the lists we created in order to show it in the app screen.
     ***/
    void loadContent(String email, String nameTR);
    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    void makeToast(String s);

}
