package com.example.gym.messages;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResponseMessageTrainer extends AppCompatActivity {
    /**
     * This class is the trainer,
     * can response message to the trainee
     **/
    private Button send;
    private TextView answerTrainer;
    private TextView Title;
    private TextView messageTrainee;
    private ManageMessages manageMessages = new ManageMessages();

    public void addMess(String id, String message, String email) {
        // Update an existing document
        manageMessages.updateMessage(id, message, email);
    }
    
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_message_trainer);
        send = findViewById(R.id.buttonAnswer);
        answerTrainer = findViewById(R.id.answerMessage);
        Title = findViewById(R.id.textViewTitle);
        messageTrainee = findViewById(R.id.reciveMessage);
        Intent MessageIntent = getIntent();
        String[] MessageValue = MessageIntent.getStringArrayExtra("key_sender");
        messageTrainee.setText(MessageValue[0]);
        answerTrainer.setText(MessageValue[1]);
        Title.setText(MessageValue[2]);
        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        //Extract the data…
        String stuff = bundle.getString("id");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResponseMessageTrainer.this);
                builder.setTitle("Add Response");
                View viewInflated = LayoutInflater.from(ResponseMessageTrainer.this).inflate(R.layout.add_new_response_window, (ViewGroup) null, false);
                final EditText message = (EditText) viewInflated.findViewById(R.id.message);
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String message_val = message.getText().toString();
//                        // Add the new user to the db
                        addMess(stuff, message_val, email);
                        answerTrainer.setText(message_val);
                        finish();
                        startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));
                    }
                });
                builder.show();
            }
        });
    }

}