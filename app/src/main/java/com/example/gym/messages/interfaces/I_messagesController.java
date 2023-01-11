package com.example.gym.messages.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

/***
 * The department for control between the view to the server
 */
public interface I_messagesController {

    //Add a message to the trainee
    Task<HttpsCallableResult> addMessageTrainee(String email, String message, String title);

    //get the messages of the trainee
    Task<HttpsCallableResult> getMessageTrainee(String email);

    //get the messages of the trainer
    Task<HttpsCallableResult> getMessageTrainer(String email) ;

    //update the answer of the trainer
    Task<HttpsCallableResult> updateMessage(String id, String message, String email);
}
