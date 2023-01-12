package com.example.gym.messages.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.auth.UserController;
import com.example.gym.homePage.view.PraivteAreaShow;
import com.example.gym.messages.controller.messagesController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * This class is the trainer,
 * can response message to the trainee
 **/
public class ResponseMessageTrainer extends AppCompatActivity {
    private final messagesController manageMessages = new messagesController();

    public void addMess(String id, String message, String email) {
        // Update an existing document
        manageMessages.updateMessage(id, message, email);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_message_trainer);
        Button send = findViewById(R.id.buttonAnswer);
        FloatingActionButton addUserButton = findViewById(R.id.fabDetails);
        TextView answerTrainer = findViewById(R.id.answerMessage);
        TextView title = findViewById(R.id.textViewTitle);
        TextView messageTrainee = findViewById(R.id.reciveMessage);
        Intent MessageIntent = getIntent();
        String[] MessageValue = MessageIntent.getStringArrayExtra("key_sender");
        messageTrainee.setText(MessageValue[0]);
        answerTrainer.setText(MessageValue[1]);
        title.setText(MessageValue[2]);
        String email_trainee  = MessageValue[3];
        Log.d("try3", email_trainee);
        addUserButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ResponseMessageTrainer.this);
            Intent senderIntent = new Intent(ResponseMessageTrainer.this, PraivteAreaShow.class);
            senderIntent.putExtra("email", email_trainee);
            startActivity(senderIntent);
            builder.setTitle("user details");
            // Set up the buttons
            builder.show(); //show the add user window
        });
        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        UserController userController = new UserController();
        String email = userController.getConnectedUserMail();
        //Extract the data…
        String stuff = bundle.getString("id");
        send.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ResponseMessageTrainer.this);
            builder.setTitle("Add Response");
            View viewInflated = LayoutInflater.from(ResponseMessageTrainer.this).inflate(R.layout.add_new_response_window, (ViewGroup) null, false);
            final EditText message = (EditText) viewInflated.findViewById(R.id.message);
            builder.setView(viewInflated);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String message_val = message.getText().toString();
//                        // Add the new user to the db
                addMess(stuff, message_val, email);
                answerTrainer.setText(message_val);
                finish();
                startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));

            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                dialog.cancel();
                startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));
            });
            builder.show();
        });
    }

}