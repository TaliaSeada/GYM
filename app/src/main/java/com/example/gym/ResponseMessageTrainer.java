package com.example.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    EditText mes;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addMess(String id, String message) {
        // Update an existing document
        DocumentReference docRef = db.collection("message").document(id);

    // (async) Update one field
        docRef.update("answer", message);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_message_trainer);
        send = findViewById(R.id.sendResponse);
        mes = findViewById(R.id.messageResponse);
        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        String stuff = bundle.getString("id");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = mes.getText().toString();
                addMess(stuff, mess);
                startActivity(new Intent(getApplicationContext(), MessageT.class));
            }
        });
    }
}