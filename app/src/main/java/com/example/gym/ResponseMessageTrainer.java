package com.example.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponseMessageTrainer extends AppCompatActivity {
    Button send;
    EditText answerTrainer;
    TextView messageTrainee;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addMess(String id, String message, String email) {
        // Update an existing document
        DocumentReference docRef = db.collection("message").document(id);

        // (async) Update one field
        docRef.update("answer", message);
        docRef.update("trainer", email);


    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_message_trainer);
        send = findViewById(R.id.sendResponse);
        answerTrainer = findViewById(R.id.answerTrainer);
        messageTrainee=findViewById(R.id.messageTrainee);
        Intent MessageIntent=getIntent();
        String [] MessageValue=MessageIntent.getStringArrayExtra("key_sender");
        messageTrainee.setText(MessageValue[0]);
        answerTrainer.setText(MessageValue[1]);
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
                String AnswerMess = answerTrainer.getText().toString();
                addMess(stuff, AnswerMess, email);
                startActivity(new Intent(getApplicationContext(), MessageT.class));
            }
        });
    }
}