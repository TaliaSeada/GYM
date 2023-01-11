package com.example.gym.homePage.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

/***
 * The department for control between the view to the server
 */
public interface I_privateAreaController {

    // get the personal details
    Task<HttpsCallableResult> getPersonalDetails(String email);

    // add birthday
    Task<HttpsCallableResult> addDate(String email,String date);

    //add weight and height
    Task<HttpsCallableResult> addDetails(String email, double height, double weight);

    // add gender
    Task<HttpsCallableResult> addGender(String email,String gender);

    // get user name
    Task<HttpsCallableResult> getName(String email);
}
